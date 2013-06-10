package akkesb.host

import akkesb.dbus.{DBusMessageSender, TestableDBusConnection, MessageHandler, MessageSender}
import akka.actor.{ActorRef, Props, ActorSystem}

object BusHost {

    def apply(hostName: String, port: String, application: String, serviceName: String, handler: MessageHandler, sender: MessageSender,
              connection: TestableDBusConnection, actorSystem: ActorSystem) =  {

        connection.requestBusName(f"akkesb.$application.$serviceName")

        val registrationsActor = actorSystem.actorOf(new Props(classOf[MessageRegistrationsActor]))
        val serviceFacadeActor = actorSystem.actorOf(new Props(classOf[ServiceFacadeActor]))
        val messageSendActor = actorSystem.actorOf(new Props(() => new MessageSendActor(registrationsActor, serviceFacadeActor)))

        sender
            .asInstanceOf[DBusMessageSender]  // don't like the cast, but severely constrained by dbus's lack of testability
            .setActor(messageSendActor)

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
