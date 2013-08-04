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
import spray.util.SprayActorLogging


object Boot extends App {

    implicit val system = ActorSystem.create("JobsCatalogueService")

    val searcher = system.actorOf(Props[Searcher])
    val service = system.actorOf(Props(classOf[SearchService], searcher))

    IO(Http) ! Http.Bind(service, "localhost", 8081)
}


class SearchService (searcher: ActorRef) extends Actor with SprayActorLogging {

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
            case Uri.Path("/jobs") => handleJobsRequest(HandleJobsRequest(uri.query))
            case Uri.Path(path) => {
                println(s"No handler for path: $path")
                Future {HttpResponse(400, s"No handler for path: $path")}
            }
        }
    }

    def handleJobsRequest(request:  HandleJobsRequest) = {
        request match {
            case HandleJobsRequest(None, _, _) => {
                Future{ HttpResponse(400, "Missing value for \"q\" parameter") }
            }
            case HandleJobsRequest(Some(searchTerm), minSalary, maxSalary) => {
                    (searcher ?  SearchFor(searchTerm, minSalary, maxSalary)).mapTo[String] map {result =>
                        println(s"Returning 200 response")
                        HttpResponse(200, entity = HttpEntity(ContentTypes.`application/json`, result))
                    }
            }
        }
    }
}


case class SearchFor(searchTerm: String, minSalary: Option[Int], maxSalary: Option[Int])

object HandleJobsRequest {

    def apply(queryString: Uri.Query): HandleJobsRequest = {
        val query = queryString get "q"
        val minSalary = asInt(queryString get "minSalary")
        val maxSalary = asInt(queryString get "maxSalary")
        HandleJobsRequest(query, minSalary, maxSalary)
    }

    private def asInt(param: Option[String]) = param match {
        case None => None
        case Some(str) => Option(Integer.parseInt(str))

    }
}

case class HandleJobsRequest(query: Option[String], minSalary: Option[Int], maxSalary: Option[Int])

