package akkesb.host

import akkesb.dbus.{DBusMessageSender, TestableDBusConnection, MessageHandler, MessageSender}
import akka.actor.{Props, ActorSystem}

object BusHost {

    def apply(hostName: String, port: String, application: String, serviceName: String, handler: MessageHandler, sender: MessageSender,
              connection: TestableDBusConnection, actorSystem: ActorSystem) =  {

        connection.requestBusName(f"akkesb.$application.$serviceName")

        // don't like the cast, but severley constrained by dbus's lack of testability
        sender.asInstanceOf[DBusMessageSender].setActor(actorSystem.actorOf(new Props(classOf[MessageSendActor])))

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
