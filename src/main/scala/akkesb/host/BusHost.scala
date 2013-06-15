package akkesb.host

import akkesb.dbus.{DBusMessageSender, TestableDBusConnection, MessageHandler, MessageSender}
import akka.actor.{ActorRef, Props, ActorSystem}

object BusHost {

    def apply(hostName: String, port: String, application: String, serviceName: String, handler: MessageHandler, sender: MessageSender,
              connection: TestableDBusConnection, actorSystemCreator: ActorSystemCreator) =  {

        connection.requestBusName(f"akkesb.$application.$serviceName")

        val actorSystem = actorSystemCreator.create(application, hostName, port)
        // TODO - these will all be created from the top level actor, not directly from the actor system
        val registrationsActor = actorSystem.actorOf(new Props(classOf[MessageRegistrationsActor]))
        val serviceFacadeActor = actorSystem.actorOf(new Props(classOf[ServiceFacadeActor]))
        val messageSendActor = actorSystem.actorOf(new Props(() => new MessageSendActor(registrationsActor, serviceFacadeActor)))

        sender
            .asInstanceOf[DBusMessageSender]  // don't like the cast, but severely constrained by dbus's lack of testability
            .setActor(messageSendActor)

        connection.exportObject("/messages/incoming", handler)
        connection.exportObject("/messages/outgoing", sender)

        new BusHost(hostName, port, application, serviceName, actorSystem.actorOf(new Props(classOf[AddressBookActor])))
    }
}

class BusHost(val hostName: String, val port: String, val application: String, val service: String, addressBook: ActorRef) {

    def willSendCommands (commands: List[String]) {

    }

    def willHandleCommands (commands: List[String]) {

    }

    def joinCluster(nodes: List[(String, String, String)]) {
        nodes foreach(n => addressBook ! AddAddress(n._1, n._2, n._3))
    }
}
