package akkesb.host

import akka.actor.ActorRef

case class WhoHandlesCommand(name: String, keys: Array[String], values: Array[String])
