package akkesb.host

import akka.actor.{ActorRef, InvalidMessageException, Actor}
import akka.event.Logging

class ServiceEndpoint extends Actor {

    val log = Logging(context.system, this)

    def sendRemoteMessage(tuple: (ActorRef, AnyRef)) {
        val recipient = tuple._1
        val message = tuple._2
        log.info(s"Sending remote message: $message to $recipient")
        recipient ! message
    }

    def receive = {
        case SendRemoteMessages(toSend) => toSend foreach(x => sendRemoteMessage(x))
        case any => throw new UnsupportedOperationException(s"Service endpoint not configured to handle: $any")
    }
}
