package utils

import akkesb.commands.{Inbox, TwoTuple}
import org.freedesktop.dbus.{DBusConnection, Tuple, Position}
import scala.Exception

object Command {

    def apply(command: (String, List[(String, Any)])) = new Command(command)
}

class Command(val command: (String, List[(String, Any)])) {

    def sendFrom(service: String, application: String) {
        val connection = DBusConnection.getConnection(DBusConnection.SESSION)

        connection.getRemoteObject(f"akkesb.$application.$service", "/commands", classOf[Inbox]) match {
            case inbox: Inbox => inbox.addCommand(DBusTuple(command))
            case _ => throw new Exception(f"failed to get remote object: $application.$service /commands Inbox")
        }
    }

    def assertIdenticalTo(actual: Tuple) {

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
       val receivedCommand = connection.getRemoteObject(f"akkesb.$application.$name", "/commands", classOf[Inbox]) match {
           case inbox: Inbox => inbox.nextMessage
           case _ => throw new Exception(f"failed to get remote object: $application.$name /commands Inbox")
       }

       receivedCommand match {
            case received: Tuple => Command(command).assertIdenticalTo(received)
            case _  => throw new Exception("Did not get a tuple back from dbus")
        }
    }
}

object DBusTuple {

    def apply(command: (String, List[(String, Any)])) = new TwoTuple("fuck", "off")
}


