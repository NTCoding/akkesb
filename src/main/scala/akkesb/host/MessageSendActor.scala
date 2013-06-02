package akkesb.host

import akka.actor.Actor

class MessageSendActor extends Actor {

    def receive: MessageSendActor#Receive =  {
        case _ => println("message send actor not implemented yet")
    }

}
