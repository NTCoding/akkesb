package Host

import org.freedesktop.dbus.DBusConnection
import akkesb.commands.{HostSender, HostInbox, Inbox}

object BusHost {

    def apply(hostName: String, port: String, application: String, serviceName: String) =  {

        val connection = DBusConnection.getConnection(DBusConnection.SESSION)

        connection.requestBusName(f"akkesb.$application.$serviceName")

        connection.exportObject("/commands/INCOMING", new HostInbox())
        connection.exportObject("/commands/OUTGOING", new HostSender())

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
