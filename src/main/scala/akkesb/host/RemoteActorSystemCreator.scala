package akkesb.host

import akka.actor.ActorSystem
import com.typesafe.config.{Config, ConfigFactory}

class RemoteActorSystemCreator extends ActorSystemCreator {

    def create(name: String, hostName: String, port: String) = {

        val string: Config = ConfigFactory.parseString(
            s"""akka.remote {
                    enabled-transports = ["akka.remote.netty.tcp"]
                    netty {
                        hostname = "$hostName"
                        port = $port
                        tcp {
                            hostname = "$hostName"
                            port = $port
                        }
                    }
                }
                akka.actor {
                    provider = "akka.remote.RemoteActorRefProvider"
                }"""
        )
        ActorSystem.create(name, ConfigFactory.load(string))
    }
}
