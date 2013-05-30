package akkesb.commands

import org.freedesktop.dbus.{Variant, Tuple, DBusInterface, DBusMap}

trait Inbox extends DBusInterface {

    def nextMessage : ThreeTuple[String, Array[String], Array[String]]
}

final class ThreeTuple[A, B, C](val first: A, val second: B, val third: C) extends Tuple { }

trait Sender extends DBusInterface {

    def send(command: ThreeTuple[String, Array[String], Array[String]])

}

class HostInbox extends Inbox {

    def addCommand(command: ThreeTuple[String, Array[String], Array[String]]) {
        println("received request to add command - but not implemented yet")
    }

    def nextMessage : ThreeTuple[String, Array[String], Array[String]] = {
        throw new Exception("Not implemented next message yet")
    }

    def isRemote: Boolean = false
}



class HostSender extends Sender {

    def isRemote: Boolean = false

    def send(command: ThreeTuple[String, Array[String], Array[String]]) {
        throw new Exception("Host sender send command not implemented")
    }
}




