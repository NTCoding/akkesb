package akkesb.host

import akka.actor.Actor
import scala.collection.convert.Wrappers.DictionaryWrapper
import java.util.Dictionary
import scala.collection.mutable

class MessageRegistrar extends Actor {

    var registrations: Map[String, String] = Map[String, String]()

    def receive = {
        case registration: RegisterCommandHandler => {
            registrations = registrations  + (registration.command -> registration.handlingService)
        }
        case whoHandles: WhoHandlesCommand => {
            registrations.get(whoHandles.name) match {
                case handler: Some[String] =>  sender ! CommandHandledBy(handler.head, whoHandles.name, whoHandles.keys, whoHandles.values)
                case None => throw new UnRegisteredCommand(s"${whoHandles.name} is not registered. Currently have commands: ${registrations.keys.mkString(",")}")
            }
        }
    }
}

class UnRegisteredCommand(message: String) extends Exception(message)