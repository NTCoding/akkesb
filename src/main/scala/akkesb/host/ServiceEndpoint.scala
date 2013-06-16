package akkesb.host

import akka.actor.{ActorRef, InvalidMessageException, Actor}

class ServiceEndpoint extends Actor {

    def sendRemoteMessage(tuple: (ActorRef, AnyRef)) {
        val recipient = tuple._1
        val message = tuple._2
        println(s"Sending remote message: $message to $recipient")
        recipient ! message
    }

    def receive = {
        case SendRemoteMessages(toSend) => toSend foreach(x => sendRemoteMessage(x))
        case any => throw new UnsupportedOperationException(s"Service endpoint not configured to handle: $any")
    }
}
