package Host

import org.freedesktop.dbus.DBusConnection

object BusHost {

    def apply(hostName: String, port: String, application: String, serviceName: String) =  {
        val connection = DBusConnection.getConnection(DBusConnection.SESSION)
        connection.requestBusName(f"akkesb.$application.$serviceName")

        new BusHost(hostName, port, application, serviceName)
    }
}

class BusHost(val hostName: String, val port: String, val application: String, val serviceName: String) {


    def willSendCommands (commands: List[String]) {

    }

    def willHandleCommands (commands: List[String]) {

    }


    def joinCluster {

    }
}
