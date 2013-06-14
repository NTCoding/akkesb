package unittests

import org.scalatest.FreeSpec
import org.scalatest.MustMatchers
import akkesb.host.RemoteActorSystemCreator

class RemoteActorSystemCreatorSpec extends FreeSpec with MustMatchers {

    "Actor systems created by the Remote Actor System Creator" - {

        val system = new RemoteActorSystemCreator().create("tangy_nik_naks", "cheesey_wotsits", "9909")

        "Have remote configurations using specified hostname and port" in {
            system.settings.config.root.render must equal(
                """akka {
                actor {
                    provider = "akka.remote.RemoteActorRefProvider"
                }
                remote {
                    enabled-transports = ["akka.remote.netty.tcp"]
                    netty.tcp {
                        hostname = "cheesey_wotsits"
                        port = 9909
                    }
                }
            }""")
        }

        "Have the supplied name" in {
            system.name must equal("tangy_nik_naks")
        }
    }

}
