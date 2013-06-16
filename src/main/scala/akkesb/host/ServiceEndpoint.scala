package akkesb.host

import akka.actor.Actor

class ServiceEndpoint extends Actor {

    def receive = {
        case SendRemoteMessages(toSend) => toSend foreach(x => x._1 ! x._2)
    }
}
