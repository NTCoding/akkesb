package akkesb.host

import akka.actor.ActorRef

case class SendCommandToService(service: ActorRef, command: (String, Array[String], Array[String]))
