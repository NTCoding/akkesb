package akkesb.commands

import org.freedesktop.dbus._

trait Inbox extends DBusInterface {

    def nextMessage : DBusAkkesbCommand
}

trait Sender extends DBusInterface {

   // def send(command: ThreeTuple[String, Array[String], Array[String]])

   def send(command: DBusAkkesbCommand)
}

class HostInbox extends Inbox {

    def addCommand(command: DBusAkkesbCommand) {
        println("received request to add command - but not implemented yet")
    }

    def nextMessage : DBusAkkesbCommand = {
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

    def send(command: DBusAkkesbCommand) {println("command sent")}
}




