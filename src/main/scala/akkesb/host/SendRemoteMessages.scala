package akkesb.host

import akka.actor.ActorRef

case class SendRemoteMessages(toSend: List[(ActorRef, AnyRef)])
