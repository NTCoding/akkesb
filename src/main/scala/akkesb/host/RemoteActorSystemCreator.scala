package akkesb.host

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

class RemoteActorSystemCreator extends ActorSystemCreator {

    def create(name: String, hostName: String, port: String) = {

        ActorSystem.create(name, ConfigFactory.parseString(
            s"""akka {
                actor {
                    provider = "akka.remote.RemoteActorRefProvider"
                }
                remote {
                    enabled-transports = ["akka.remote.netty.tcp"]
                    netty.tcp {
                        hostname = "$hostName"
                        port = $port
                    }
                }
            }"""
        ))
    }

}
