package utils

import org.freedesktop.dbus.{DBusConnection, Tuple}
import scala.Exception
import akkesb.dbus.MessageSender


object Command {

    def apply(command: (String, List[(String, Any)])) = new Command(command)
}

class Command(val command: (String, List[(String, Any)])) {

    def sendFrom(service: String, application: String) {
        val connection = DBusConnection.getConnection(DBusConnection.SESSION)

        connection.requestBusName("commands_are_sent_test.testing_and_assertions_jvm")

        connection.getRemoteObject(f"akkesb.$application.$service", "/messages/sending", classOf[MessageSender]) match {
            case sender: MessageSender => sender.send(command._1, command._2.map(_._1).toArray, command._2.map(_._2.toString).toArray)
            case _ => throw new Exception(f"failed to get remote object: $application.$service /akkesb Inbox")
        }
    }

    def fail(message: String) {
         throw new Exception(message)
    }
}



