package akkesb.dbus

import org.freedesktop.dbus.{DBusInterface, DBusConnection}

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
