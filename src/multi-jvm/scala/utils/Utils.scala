package utils

import org.freedesktop.dbus.{DBusConnection, Tuple}
import scala.Exception
import akkesb.commands.{DBusAkkesbCommand, Sender, Inbox}

object Command {

    def apply(command: (String, List[(String, Any)])) = new Command(command)
}

class Command(val command: (String, List[(String, Any)])) {

    def sendFrom(service: String, application: String) {
        val connection = DBusConnection.getConnection(DBusConnection.SESSION)

        connection.requestBusName("commands_are_sent_test.testing_and_assertions_jvm")

        connection.getRemoteObject(f"akkesb.$application.$service", "/commands/outgoing", classOf[Sender]) match {
            case sender: Sender => sender.send(DBusTuple(command))
            case _ => throw new Exception(f"failed to get remote object: $application.$service /commands Inbox")
        }
    }

    def assertIdenticalTo(actual: DBusAkkesbCommand) {

        /*
        // TODO - could cast this to a dbus tuple and do comparison - probably prefer not to
        val actualName = actual.getParameters()(0).toString
        val commandData = actual.getParameters()(1).asInstanceOf[Array[AnyRef]]

        if (actualName != command._1) fail(f"actual name was wrong: $actualName") // TODO - do comparison with actual message

        // TODO - call in junit or an assertions library?
        for (i <- 0 to command._2.length -1) {
            val expectedKey = command._2(i)_1
            val actualKey = commandData(i).asInstanceOf[Tuple].getParameters()(0).toString

            val expectedValue = command._2(i)_2
            val actualValue  = commandData(i).asInstanceOf[Tuple].getParameters()(1)

            if (!expectedKey.equals(actualKey)) fail(f"Item: $i has wrong key: $actualKey. Should have been: $expectedKey")
            if (!expectedValue.equals(actualValue)) fail(f"Item $i has wrong value: $actualValue. Should have been: $expectedValue")
        }
        */

        fail("blah")
    }

    def fail(message: String) {
         throw new Exception(message)
    }
}

object Service {

    def apply(application: String, name: String) = new Service(application, name)
}

class Service(val application: String, val name: String) {

    def assertReceivedLastCommand(command: (String, List[(String, _)])) {
       val connection = DBusConnection.getConnection(DBusConnection.SESSION)
       val receivedCommand = connection.getRemoteObject(f"akkesb.$application.$name", "/commands/incoming", classOf[Inbox]) match {
           case inbox: Inbox => inbox.nextMessage
           case _ => throw new Exception(f"failed to get remote object: $application.$name /commands/INCOMING    Inbox")
       }

       receivedCommand match {
            case received: DBusAkkesbCommand => Command(command).assertIdenticalTo(received)
            case _  => throw new Exception("Did not get a tuple back from dbus")
        }
    }
}

object DBusTuple {

    def apply(command: (String, List[(String, Any)])) = {
        new DBusAkkesbCommand()
    }
}


