import akka.actor._
import akka.io.IO
import akka.util.Timeout
import java.util.concurrent.TimeUnit
import scala.concurrent.Future
import scala.Some
import scala.util.parsing.json.JSON
import spray.can.Http
import akka.pattern.ask
import spray.http._
import spray.http.HttpHeaders.RawHeader
import spray.http.HttpMethods._


object Boot extends App {

    implicit val system = ActorSystem.create("JobsCatalogueService")

    val searcher = system.actorOf(Props[Searcher])
    val service = system.actorOf(Props(classOf[SearchService], searcher))

    IO(Http) ! Http.Bind(service, "localhost", 8081)
}


class SearchService (searcher: ActorRef) extends Actor {

    implicit val timeout =  Timeout(20, TimeUnit.SECONDS)
    implicit val executionContext = context.dispatcher

    def receive = {

        case _: Http.Connected => sender ! Http.Register(self)

        case HttpRequest(GET, Uri.Path("/"), _, _, _) => {
            sender ! HttpResponse(entity = "Try jobs/q={searchTerm}")
        }

        case HttpRequest(GET, uri,  _, _, _) => {
            val endpoint = sender // close over the sender now, not when the future completes
            handleGetRequestFor(uri) map {response => endpoint ! response }
        }
    }

    def handleGetRequestFor(uri: Uri) = {
        uri.path match {
            case Uri.Path("/jobs") => handleJobsRequest(uri.query.get("q"))
            case Uri.Path(path) => {
                println(s"No handler for path: $path")
                Future {HttpResponse(400, s"No handler for path: $path")}
            }
        }
    }

    def handleJobsRequest(q: Option[String]) = {
        q match {
            case None => Future{ HttpResponse(400, "Missing value for \"q\" parameter") }
            case Some(searchTerm) => {
                    (searcher ?  SearchFor(searchTerm)).mapTo[String] map {result =>
                        println(s"Returning 200 response with entity: $result")
                        HttpResponse(200, entity = result, headers = List(RawHeader("Content-Type", "application/json")))
                    }
            }
        }
    }
}


case class SearchFor(searchTerm: String)


class Searcher extends Actor {

    def receive = {
        case SearchFor(searchTerm) => {
            println(s"Searcher received search request for: $searchTerm")
            sender ! """{ "result"="yippee" }"""
        }
    }

}

