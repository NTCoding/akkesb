package akkesb.host

import akka.actor.ActorRef

case class AllServices(references: List[ActorRef], state: AnyRef)
