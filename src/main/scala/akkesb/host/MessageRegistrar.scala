package akkesb.host

import akka.actor.Actor
import scala.collection.convert.Wrappers.DictionaryWrapper
import java.util.Dictionary
import scala.collection.mutable

class MessageRegistrar extends Actor {

    var registrations: Map[String, String] = Map[String, String]()

    def receive = {
        case RegisterCommandHandler(command, service) => register(command, service)

        case RegisterMultipleCommandsHandler(commands, service) => commands foreach(c => register(c, service))

        case WhoHandlesCommand(name, keys, values) => {
            registrations.get(name) match {
                case handler: Some[String] =>  sender ! CommandHandledBy(handler.head, name, keys, values)
                case None => throw new UnRegisteredCommand(s"$name is not registered. Currently have commands: ${registrations.keys.mkString(",")}")
            }
        }
    }

    def register(command: String, service: String) {
        registrations = registrations + (command -> service)
    }
}

class UnRegisteredCommand(message: String) extends Exception(message)