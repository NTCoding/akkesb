package akkesb.commands

import org.freedesktop.dbus._

trait Inbox extends DBusInterface {

    def nextMessage : DBusAkkesbCommand
}

trait Sender extends DBusInterface {

   def send(name: String, keys: Array[String], values: Array[String])
}

class HostInbox extends Inbox {

    def nextMessage : DBusAkkesbCommand = {
        throw new Exception("Not implemented next message yet")
    }

    def isRemote: Boolean = false
}

class HostSender extends Sender {

    def isRemote: Boolean = false

    def send(name: String, keys: Array[String], values: Array[String]) {
        // find the service that owns this command

        // send the command to the remote actor named service_actor
    }
}




