package akkesb.host

import akkesb.dbus.{DBusMessageSender, TestableDBusConnection, MessageHandler, MessageSender}
import akka.actor.{ActorRef, Props, ActorSystem}

object BusHost {

    def apply(hostName: String, port: String, application: String, serviceName: String, handler: MessageHandler, sender: MessageSender,
              connection: TestableDBusConnection, actorSystemCreator: ActorSystemCreator) =  {

        connection.requestBusName(f"akkesb.$application.$serviceName")

        val actorSystem = actorSystemCreator.create(application, hostName, port)
        // TODO - these will all be created from the top level actor, not directly from the actor system
        val registrationsActor = actorSystem.actorOf(new Props(classOf[MessageRegistrar]))
        val endpoint = actorSystem.actorOf(new Props(classOf[ServiceEndpoint]), serviceName)
        val addressBook = actorSystem.actorOf(Props(() => new AddressBookActor(application)))
        val messageSendActor = actorSystem.actorOf(new Props(() => new MessageSendActor(registrationsActor, endpoint, addressBook)))

        if(endpoint == null) throw new AkkesbStartupFailed("service endpoint could not be created")

        sender
            .asInstanceOf[DBusMessageSender]  // don't like the cast, but severely constrained by dbus's lack of testability
            .setActor(messageSendActor)

        connection.exportObject("/messages/incoming", handler)
        connection.exportObject("/messages/outgoing", sender)

        new BusHost(hostName, port, application, serviceName, addressBook, actorSystem.actorOf(Props(() => new ConfigDistributor(addressBook, endpoint))))
    }
}

class BusHost(val hostName: String, val port: String, val application: String, val service: String, addressBook: ActorRef, configDistributor: ActorRef) {

    def willSendCommands (commands: List[String]) {

    }

    def willHandleCommands (commands: List[String]) {
        configDistributor ! DistributeCommandOwnership(service, commands)
    }

    def joinCluster(nodes: List[(String, String, String)]) {
        nodes foreach(n => addressBook ! AddAddress(n._1, n._2, n._3))
    }
}

class AkkesbStartupFailed(message: String) extends Exception(message)
