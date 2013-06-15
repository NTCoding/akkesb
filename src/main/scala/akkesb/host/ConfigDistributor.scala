package akkesb.host

import akka.actor.Actor

class ConfigDistributor extends Actor {

    def receive = {
        case _ => println("Config distributor net set up to handle messages")
    }
}
