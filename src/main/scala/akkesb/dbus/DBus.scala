package akkesb.dbus

import org.freedesktop.dbus._

trait MessageSender extends DBusInterface {

   def send(name: String, keys: Array[String], values: Array[String])
}

class DBusMessageSender extends MessageSender {

    def isRemote: Boolean = false

    def send(name: String, keys: Array[String], values: Array[String]) {
        // find the service that owns this command

        // send the command to the remote actor named service_actor
    }
}

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





