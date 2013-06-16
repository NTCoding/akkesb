package akkesb.dbus

import org.freedesktop.dbus.DBusInterface

trait MessageHandler extends DBusInterface {

    // TODO - make a type def for this message, it's used in a few places
    def handle(name: String, keys: Array[String], values: Array[String])
}

class DBusMessageHandler extends MessageHandler {

    def handle(name: String, keys: Array[String], values: Array[String]) {
        println("Message handler does nothing with messages")
    }

    def isRemote = false
}

