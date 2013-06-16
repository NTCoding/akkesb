package akkesb.dbus

import org.freedesktop.dbus._
import akka.actor.ActorRef
import akkesb.host.SendCommand

trait MessageSender extends DBusInterface {
   def send(name: String, keys: Array[String], values: Array[String])
}

class DBusMessageSender extends MessageSender {

    def isRemote: Boolean = false
    // TODO - rename this to message send actor
    private var actorRef : Option[ActorRef] = None

    def send(name: String, keys: Array[String], values: Array[String])  {
        actorRef match {
            case Some(actor) => actor ! SendCommand(name, keys, values)
            case None => throw new MessageSendActorHasNotBeenSet()
        }
    }

    def setActor(actor: ActorRef) {
        actorRef = Some(actor)
    }
}

class MessageSendActorHasNotBeenSet extends Exception










