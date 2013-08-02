import akka.actor.ActorSystem
import com.fasterxml.jackson.core.io.JsonStringEncoder
import java.nio.charset.Charset
import org.jsoup.Jsoup
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.matching.Regex
import scala.util.matching.Regex.MatchIterator
import scala.io.Source._
import com.codahale.jerkson.Json._
import spray.client.pipelining._
import spray.http.StatusCode._


object Indexer {

    implicit val system = ActorSystem.create("please")
    import system.dispatcher

    def main(args: Array[String]) {
        Importer.categories foreach (f => {
            val text = fromFile(s"/home/nick/jobsdata/${f}jobs.json") mkString
            val tuples = parse[Seq[Job]](text)
            val js = tuples map (t => {
                val payRange = extractPay(t._4)
                    val json =
                        s"""{
                    | "title": "${cleanup(t._1)}",
                    | "company": "${cleanup(t._2)}",
                    | "location": "${cleanup(t._3)}",
                    | "minSalary": ${payRange._1},
                    | "maxSalary": ${payRange._2},
                    | "latitude": ${t._5},
                    | "longitude": ${t._6},
                    | "datePosted": "${t._7}",
                    | "description": "x${cleanup(t._8)}",
                    | "requirements": "x${cleanup(t._9)}"
                | }""".stripMargin
                json
            })
            js foreach uploadToElastic
        })
    }

    def cleanup(json: String) = {
        JsonStringEncoder.getInstance.quoteAsString(Jsoup.parse(json.trim.replace("&nbsp;", " ").replace("&reg;", "").replace("®", "").replaceAll("\\p{C}", "").replaceAll("\\p{Cntrl}", "")).text()).mkString
    }

    def uploadToElastic(jsonBlob: String) {
        val pipeline = sendReceive
        println(s"Sending put request for: $jsonBlob")
        val response = Await.result(pipeline(Post("http://localhost:9200/jobs/job", jsonBlob)), Duration(60, "s"))
        response.status.intValue match {
            case 400 => {
                println(response.entity.asString)
                sys.exit()
            }
            case _ => ;
        }
    }

    def extractPay(pay: String): (Int, Int) = {
        val moneys: MatchIterator = new Regex("\\d+") findAllIn pay
        val ls = moneys.toList
        ls.length match {
            case 2 => {
                val first = Integer.parseInt(ls(0))
                val second = Integer.parseInt(ls(1))
                (first, second)
            }
            case _ => (0, 0)
        }
    }
}

case class Job(_1: String, _2: String, _3: String, _4: String, _5: String, _6: String, _7: String, _8: String, _9: String )
