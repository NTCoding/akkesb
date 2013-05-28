package utils

import akkesb.queries.Get
import org.freedesktop.dbus.{Variant, Tuple, DBusInterface, DBusConnection}

object Testing {

    def sendCommand(application: String, command: (String,List[(String, _)])) = {
        new Command(application, command)
    }

    def assertService(application: String, service: String) = {
        new Service(application, service)
    }

}

class Command(val application: String, val command: (String, List[(String, _)])) {

    def via(service: String) {
    }
}

class Service(val application: String, val name: String) {

    def receivedCommand(command: (String, List[(String, _)])) {

        val lastCommand = DBus.invoke[Get, Tuple[String, Array[Tuple[String, Variant]]] ](f"akkesb.$application.$name", "/queries",)

        lastCommand match {
            case anyCommand: (String, List[(String, _)]) => assertIdenticalCommand(anyCommand, command)
            case _  => fail(command)
        }

    }

    def assertIdenticalCommand(actual: (String, List[(String, _)]), expected: (String, List[(String, _)])) {
        // TODO - convert the actual from dbus types back to tuples
        if (actual != expected) fail(expected)
    }

    def fail(command: (String, List[(String, _)])) {
        throw new Exception(f"command: '${command._1}' was not received by $application.$name")
    }

}

object DBus {

    def invoke[A <: DBusInterface, B](dbusService: String, path: String, interface: A, result: A => B) = {
         val connection = DBusConnection.getConnection(DBusConnection.SESSION)
         connection.getRemoteObject(dbusService, path, classOf[A]) match {
             case remoteObject: A => result(remoteObject)
         }
    }
}


