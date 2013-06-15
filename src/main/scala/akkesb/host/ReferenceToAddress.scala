package akkesb.host

import akka.actor.ActorRef

case class ReferenceToAddress(reference: ActorRef, command: (String, Array[String], Array[String]))
