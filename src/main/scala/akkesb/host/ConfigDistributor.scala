package akkesb.host

import akka.actor.{ActorRef, Actor}

class ConfigDistributor(addressBook: ActorRef, serviceEndpoint: ActorRef) extends Actor {

    def receive = {
        case _ => println("Config distributor net set up to handle messages")
    }
}
