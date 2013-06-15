package akkesb.host

import akka.actor.Actor

class ServiceEndpoint extends Actor {
    def receive: ServiceEndpoint#Receive = {
        case _ => println("Service facade actor receive not implemented")
    }
}
