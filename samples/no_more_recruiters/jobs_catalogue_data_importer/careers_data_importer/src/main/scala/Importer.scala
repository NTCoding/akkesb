import akka.actor.ActorSystem
import java.io.{PrintWriter, File}
import org.jsoup.Jsoup
import scala.collection.immutable.Seq
import scala.concurrent.duration.{FiniteDuration, Duration}
import scala.concurrent.{Await, Future}
import scala.xml.{NodeSeq, XML}
import spray.client.pipelining._
import spray.http.{HttpResponse, HttpRequest}
import com.codahale.jerkson.Json._
import spray.httpx.encoding.Gzip
import sys.process._

object Importer {

    implicit val system = ActorSystem.create("please")
    import system.dispatcher

    //val devKey = "WDHS3JK6GXB007PHY0DC"
    val devKey = "WDHS1SM6RJB7C4JBKL80"

    val categories = Array("scala", "erlang", "accounting", "nutritionist", "C#", "java", "node.js", "finance", "psychology", "fitness", "chef", "doctor")

    def main(args: Array[String]) {
       categories foreach (kw => {
            val jobs = for (page <- 1 to Math.min(totalPagesFor(kw), 20)) yield grabPageOfResults(kw, page)
            val json = generate(jobs flatten)
            val writer = new PrintWriter(s"/home/nick/jobsdata/${kw}jobs.json")
            writer write json
            writer flush()
            writer close()
        })
        println("finished")
        sys.exit()
    }

    def totalPagesFor(keyword: String) = {
        val body = responseBody(s"http://api.careerbuilder.com/v1/jobsearch?DeveloperKey=$devKey&keywords=$keyword")

        val root = (XML loadString body) \\ "ResponseJobSearch" head
        val totalPages =  (root \ "TotalPages").head.text
        println(s"Total pages for $keyword = $totalPages")
        Integer.parseInt(totalPages)
    }

    def grabPageOfResults(keyword: String, page: Int) = {
        println(s"Grabbing page of results for: $keyword - page: $page")
        val pipeline = sendReceive

        val url = s"http://api.careerbuilder.com/v1/jobsearch?DeveloperKey=$devKey&keywords=$keyword&pageNumber=$page"
        println(s"About to hit: $url")
        val response = pipeline(Get(url))
        val body = Await.result(response map (r => r.entity.asString), Duration(60, "s"))
        println(s"Got page of results for: $keyword - page: $page")

        (XML loadString body) \\ "JobSearchResult" map (jsr => {
                val title = jsr \ "JobTitle" text
                val company = jsr \ "Company" text
                val location = jsr \ "Location" text
                val pay = jsr \ "Pay" text
                val latitude = jsr \ "LocationLatitude" text
                val longitude = jsr \ "LocationLongitude" text
                val date = jsr \ "PostedDate" text
                val did = jsr \ "DID" text
                val url = jsr \ "JobDetailsURL" text
                val details = detailsFor(url)
                val job = (title, company, location, pay, latitude, longitude, date, details._1, details._2)
                job
         })
    }

    def responseBody(url: String): String = {
        val pipeline = sendReceive
        val response = pipeline(Get(url))
        val body = Await.result(response.map(r => {
            r.status.intValue match {
                case 301 => {
                    responseBody(r.headers.find(h => h.name == "Location").get.value)
                }
                case _ => r.entity.asString
            }
        }), Duration(60, "s"))
        body
    }

    def printJobs(jobs: Future[Seq[(String, String, String, String, String, String, String, String, String)]]) {
        jobs map (js => {
            js foreach (job => {
                println(s"Job: ${job._1}")
                println(s"    Company: ${job._2}")
                println(s"    Location: ${job._3}")
                println(s"    Pay: ${job._4}")
                println(s"    Latitude: ${job._5}")
                println(s"    Longitude: ${job._6}")
                println(s"    Date: ${job._7}")
                println(s"    Description: ${job._8}")
                println(s"    Requirements: ${job._9}")
                println()
            })
        })
    }

    def detailsFor(url: String): (String, String) = {
        println(s"About to scrape: $url for job details")
        val body = responseBody(url)
        val doc = Jsoup.parse(body)
        val descriptions = doc select "div.job_desc"
        val requirements = doc select "div.job_req"
        descriptions.size match {
            case 0 => {
                println("No job descriptions found")
                ("", "")
            }
            case 1 => {
                println("1 Job description found")
                (descriptions.`val`(), requirements.`val`())
            }
            case n => {
                println("2 job descriptions found")
                (descriptions.get(0).html, descriptions.get(1).html)
            }
        }
    }

    /*
    def detailsFor(did: String) = {
        val pipeline = sendReceive

        val detailsUrl = s"http://api.careerbuilder.com/v1/job?DeveloperKey=WDHS3JK6GXB007PHY0DC&DID=$did"
        println(s"About to get details: $detailsUrl")
        val response = pipeline(Get(detailsUrl))
        Await.result(response map(r => r.entity.asString), Duration(60, "s")) match {
            case body: String => {
                val responsejob = (XML loadString body) \\ "ResponseJob"
                val job = responsejob \ "Job"
                val description = job \ "JobDescription" text
                val requirements = job \ "JobRequirements" text
                val dets = (description, requirements)
                dets
            }
            case _ => ("error", "error")
        }
    }
    */
}


case class ResponseJobSearch(totalPages: Int, Results: Results)

case class Results(jobSearchResults: List[JobSearchResult])

case class JobSearchResult(Company: String, DID: String, EmploymentType: String, Location: String,LocationLatitude: Float,
                           LocationLongitude: Float, pay: String, jobTitle: String)