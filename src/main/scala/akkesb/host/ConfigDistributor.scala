package akkesb.host

import akka.actor.{ActorRef, Actor}

class ConfigDistributor(addressBook: ActorRef, serviceEndpoint: ActorRef) extends Actor {

    def receive = {

        case DistributeCommandOwnership(owner, commands) => {
            println("Config distributor looking up address of all services")
            addressBook ! GiveMeAReferenceToAllServiceAddresses((owner, commands))
        }

        case AllServices(services, (owner: String, commands: List[String])) => {
            println("Config distributor about to send configs to all remote services")
            serviceEndpoint ! SendRemoteMessages(messagesFor(services, owner, commands))
        }
    }

    def messagesFor(services: List[ActorRef], commandOwner: String, commandNames: List[String]): List[(ActorRef, AnyRef)] = {
        services map(s => (s, CommandHandlerRegistrations(commandOwner, commandNames)))
    }
}
