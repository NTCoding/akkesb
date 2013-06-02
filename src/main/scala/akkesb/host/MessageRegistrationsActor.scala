package akkesb.host

import akka.actor.Actor

class MessageRegistrationsActor extends Actor {

    def receive: MessageRegistrationsActor#Receive = {
        case _ => println("message registrations actor not implemented yet")
    }
}
