package akkesb.host

import akkesb.dbus.{TestableDBusConnection, MessageHandler, MessageSender}

object BusHost {

    def apply(hostName: String, port: String, application: String, serviceName: String, handler: MessageHandler, sender: MessageSender, connection: TestableDBusConnection) =  {

        connection.requestBusName(f"akkesb.$application.$serviceName")

        connection.exportObject("/messages/incoming", handler)
        connection.exportObject("/messages/outgoing", sender)

        new BusHost(hostName, port, application, serviceName)
    }
}

class BusHost(val hostName: String, val port: String, val application: String, val service: String) {


    def willSendCommands (commands: List[String]) {

    }

    def willHandleCommands (commands: List[String]) {

    }


    def joinCluster {

    }
}
