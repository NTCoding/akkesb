package akkesb.commands

import org.freedesktop.dbus._

trait Inbox extends DBusInterface {

    def nextMessage : ThreeTuple[String, Array[String], Array[String]]
}

final class ThreeTuple[A, B, C](f: A, s: B, t: C) extends Tuple {

    @Position(0)
    val first = f

    @Position(1)
    val second = s

    @Position(2)
    val third = t

}

trait Sender extends DBusInterface {

   // def send(command: ThreeTuple[String, Array[String], Array[String]])

   def send(command: ThreeTuple[String, String, String])
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

    /*
    def send(command: ThreeTuple[String, Array[String], Array[String]]) {
        println("received request to send message - not implemented yet")
    }
    */

    def send(command: ThreeTuple[String, String, String]) {println("command sent")}
}




