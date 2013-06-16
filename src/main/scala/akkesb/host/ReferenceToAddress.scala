package akkesb.host

import akka.actor.ActorRef

case class ReferenceToAddress(reference: ActorRef, state: (String, Array[String], Array[String]))
