package utils

import akkesb.queries.Get
import org.freedesktop.dbus.{Tuple, Variant, DBusInterface, DBusConnection}

object Testing {

    def sendCommand(command: (String,List[(String, _)])) = {
        new Command(command)
    }

    def assertService(application: String, service: String) = {
        new Service(application, service)
    }

}

class Command(val command: (String, List[(String, _)])) {

    def via(service: String, application: String) {
    }
}

class Service(val application: String, val name: String) {

    def receivedCommand(command: (String, List[(String, _)])) {

        // TODO - the second argument could be one of those type alias things (lol!)
        val lastCommand = DBus.invoke[Get, Tuple](f"akkesb.$application.$name", "/queries", classOf[Get], _.nextCommand)

        lastCommand match {
            case anyCommand: Tuple => assertIdenticalCommand(anyCommand, command)
            case _  => fail(command, "Did not get a command back from dbus")
        }

    }

    def assertIdenticalCommand(actual: Tuple, expected: (String, List[(String, _)])) {
        val actualName = actual.getParameters()(0).toString
        val commandData = actual.getParameters()(1).asInstanceOf[Array[AnyRef]]

        if (actualName != expected._1) fail(expected, f"actual name was wrong: $actualName") // TODO - do comparison with actual message

        for (i <- 0 to expected._2.length -1) {
            val expectedKey = expected._2(i)_1
            val actualKey = commandData(i).asInstanceOf[Tuple].getParameters()(0).toString

            val expectedValue = expected._2(i)_2
            val actualValue  = commandData(i).asInstanceOf[Tuple].getParameters()(1)

            if (!expectedKey.equals(actualKey)) fail(expected, f"Item: $i has wrong key: $actualKey. Should have been: $expectedKey")
            if (!expectedValue.equals(actualValue)) fail(expected, f"Item $i has wrong value: $actualValue. Should have been: $expectedValue")
                                }

    }

    def fail(command: (String, List[(String, _)]), message: String) {
        throw new Exception(f"command: '${command._1}' was not received by $application.$name. Message: $message")
    }

}

object DBus {

    def invoke[A <: DBusInterface, B](dbusService: String, path: String, interface: Class[A], result: A => B) = {

         val connection = DBusConnection.getConnection(DBusConnection.SESSION)

         connection.getRemoteObject(dbusService, path, interface) match {
             case remoteObject: A => result(remoteObject)
             case _ => println("failed to get remote object - how should this be handled?")
         }
    }
}


