package akkesb.host

import akka.actor.ActorSystem

trait ActorSystemCreator {

    def create(name: String, hostName: String, port: String) : ActorSystem
}
