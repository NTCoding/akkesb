import akka.actor.Actor
import com.sksamuel.elastic4s._
import com.sksamuel.elastic4s.ElasticDsl._
import com.codahale.jerkson.Json._
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.common.settings.ImmutableSettings
import org.elasticsearch.node.NodeBuilder
import org.elasticsearch.node.NodeBuilder._
import org.elasticsearch.search.SearchHits
import scala.util.Failure
import scala.util.Success
import scala.util.{Failure, Success}
import spray.util.SprayActorLogging
import collection.JavaConversions._
import collection.JavaConverters._

class Searcher extends Actor with SprayActorLogging {

    lazy val client: ElasticClient = {
        val node = nodeBuilder().clusterName("homersimpson").client(true).node()
        val client = node.client()
        ElasticClient.fromClient(client)
    }
    implicit val executionContext = context.dispatcher

    def receive = {
        case SearchFor(searchTerm) => {
            println(s"Searcher received search request for: $searchTerm")

            val endpoint = sender // close over the sender for this message, not when the future completes

            client.execute{ search in "jobs" query searchTerm limit 10 } onComplete { searchResult =>
                searchResult match {
                    case Success(sr) => {
                        val hits: SearchHits = sr.getHits
                        val jobs =  hits map { hit =>
                            val src = hit.getSource
                            val title = (src get "title").asInstanceOf[String]
                            val location = (src get "location").asInstanceOf[String]
                            val date = (src get "datePosted").asInstanceOf[String]
                            val description = (src get "description").asInstanceOf[String]
                            val requirements = (src get "requirements").asInstanceOf[String]
                            val minSalary = (src get "minSalary").toString.toDouble
                            val maxSalary = (src get "maxSalary").toString.toDouble
                            Job(title, location, date, description, requirements, minSalary, maxSalary)
                        }

                        val json = generate(JobSearchResult(hits.totalHits, 1, 10, jobs.toSeq))
                        println("Searcher sending json back to endpoint: ")
                        println(json)

                        endpoint ! json
                    }
                    case Failure(blah) => {
                        println("Searcher sending error response back to endpoint: ")
                        val json = s""" { "error": "$blah" } """
                        println(json)
                        endpoint ! json
                    }
                }
           }
        }

    }

}

case class JobSearchResult(totalResults: Long, page: Int, pageSize: Int, jobs: Seq[Job])

case class Job(title: String, location: String, dateAdded: String, description: String, requirements: String,
               minSalary: Double, maxSalary: Double)