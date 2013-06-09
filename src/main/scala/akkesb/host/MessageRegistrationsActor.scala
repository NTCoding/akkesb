package akkesb.host

import akka.actor.Actor
import scala.collection.convert.Wrappers.DictionaryWrapper
import java.util.Dictionary
import scala.collection.mutable

class MessageRegistrationsActor extends Actor {

    var registrations: Map[String, String] = Map[String, String]()

    def receive = {
        case registration: RegisterCommandHandler => {
            registrations = registrations  + (registration.command -> registration.handlingService)
        }
        case whoHandles: WhoHandlesCommand => {
            sender ! CommandHandledBy(registrations.get(whoHandles.name).head, whoHandles.name, whoHandles.keys, whoHandles.values)
        }
    }
}
