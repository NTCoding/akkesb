package akkesb.dbus

import org.freedesktop.dbus.DBusInterface

trait MessageHandler extends DBusInterface {

    // TODO - make a type def for this message, it's used in a few places
    def handle(name: String, keys: Array[String], values: Array[String])
}

class DBusMessageHandler(private val application: String, private val service: String, private val conn: TestableDBusConnection) extends MessageHandler {

    def handle(name: String, keys: Array[String], values: Array[String]) {
        conn.getRemoteObject(s"akkesb.$application.$service.Client", "/commands/receiving", classOf[MessageHandler]) match {
            case handler: MessageHandler => handler.handle(name, keys, values)
        }
    }

    def isRemote = false
}

