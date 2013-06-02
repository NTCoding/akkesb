package akkesb.host

import akka.actor.Actor

class ServiceFacadeActor extends Actor {
    def receive: ServiceFacadeActor#Receive = {
        case _ => println("Service facade actor receive not implemented")
    }
}
