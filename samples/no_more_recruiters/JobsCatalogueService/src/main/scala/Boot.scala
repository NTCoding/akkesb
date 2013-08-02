import akka.actor.{ActorRefFactory, Actor, Props, ActorSystem}
import akka.io.IO
import spray.can.Http
import spray.routing
import spray.routing.HttpService

object Boot extends App {

    implicit val system = ActorSystem.create("JobsCatalogueService")

    val service = system.actorOf(Props[SearchServiceActor])

    IO(Http) ! Http.Bind(service, "localhost", 8081)
}

class SearchServiceActor extends Actor with HttpService {

    def routes = {
        path("") {
            get {
                complete("hello")
            }
        }
    }

    def receive = runRoute(routes)

    implicit def actorRefFactory = context
}

