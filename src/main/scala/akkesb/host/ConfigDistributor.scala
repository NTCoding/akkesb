package akkesb.host

import akka.actor.{ActorRef, Actor}

class ConfigDistributor(addressBook: ActorRef, serviceEndpoint: ActorRef) extends Actor {

    def receive = {
        case DistributeCommandOwnership(owner, commands) => addressBook ! GiveMeAReferenceToAllServiceAddresses((owner, commands))
        case AllServices(services, (owner: String, commands: List[String])) => serviceEndpoint ! SendRemoteMessages(messagesFor(services, owner, commands))
    }

    def messagesFor(services: List[ActorRef], commandOwner: String, commandNames: List[String]): List[(ActorRef, AnyRef)] = {
        services map(s => (s, CommandHandlerRegistrations(commandOwner, commandNames)))
    }
}
