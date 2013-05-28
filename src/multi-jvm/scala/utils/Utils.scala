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
        val lastCommand = DBus.invoke[Get, Tuple[String, Array[Tuple[String, Variant]]]](f"akkesb.$application.$name", "/queries", _.nextCommand)

        lastCommand match {
            case anyCommand: Tuple[String, Array[Tuple[String, Variant]]] => assertIdenticalCommand(anyCommand, command)
            case _  => fail(command)
        }

    }

    def assertIdenticalCommand(actual: Tuple[String, Array[Tuple[String, Variant]]], expected: (String, List[(String, _)])) {
        val actualName = actual.getParameters()(0).toString
        val commandData = actual.getParameters()(1).asInstanceOf[Array[Tuple[String, Variant]]]

        if (actualName != expected._1) fail(expected) // TODO - do comparison with actual message
        for (i <- 0 to expected._2.length -1) {
              if (expected._2(i) != commandData(i).getParameters()(0)) fail(expected)
        }

    }

    def fail(command: (String, List[(String, _)])) {
        throw new Exception(f"command: '${command._1}' was not received by $application.$name")
    }

}

object DBus {

    def invoke[A <: DBusInterface, B](dbusService: String, path: String, result: A => B) = {

         val connection = DBusConnection.getConnection(DBusConnection.SESSION)

         connection.getRemoteObject(dbusService, path, classOf[A]) match {
             case remoteObject: A => result(remoteObject)
             case _ => println("failed to get remote object - how should this be handled?")
         }
    }
}


