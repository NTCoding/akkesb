package Host

import org.freedesktop.dbus.DBusConnection
import akkesb.commands._

object BusHost {

    def apply(hostName: String, port: String, application: String, serviceName: String, handler: MessageHandler, sender: MessageSender) =  {

        val connection = DBusConnection.getConnection(DBusConnection.SESSION)

        connection.requestBusName(f"akkesb.$application.$serviceName")

        connection.exportObject("/messages/handling", handler)
        connection.exportObject("/messages/sending", sender)

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
