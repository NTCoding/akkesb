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

trait MessageHandler extends DBusInterface {

    // TODO - make a type def for this message, it's used in a few places
    def handle(name: String, keys: Array[String], values: Array[String])
}

class ActorDelegatingMessageHandler extends MessageHandler {

    def handle(name: String, keys: Array[String], values: Array[String]) {
        println("Message handler does nothing with messages")
    }

    def isRemote = false
}

/*
    Didn't want to create this wrapper, but DBus is not test/mock-friendly
 */
trait TestableDBusConnection {

    var conn: Option[DBusConnection]

    def setConnection(c: DBusConnection) { conn = Option(c) }

    def requestBusName(name: String) = conn match{
        case Some(c) => c.requestBusName(name)
        case None => println("no DBus connection")
    }

    def exportObject(path: String, interface: DBusInterface) = conn match {
        case Some(c) => c.exportObject(path, interface)
        case None => println("no DBus connection")
    }
}

class AkkesbDBusConnection(connection: DBusConnection) extends TestableDBusConnection {
    var conn: Option[DBusConnection] = Some(connection)
}





