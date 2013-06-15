package unittests

import org.scalatest.FreeSpec
import org.scalatest.MustMatchers
import akkesb.host.RemoteActorSystemCreator

class RemoteActorSystemCreatorSpec extends FreeSpec with MustMatchers {

    "Actor systems created by the Remote Actor System Creator" - {

        val system = new RemoteActorSystemCreator().create("tangyniknaks", "127.0.0.1", "9909")

        "Have remote configurations using specified hostname and port" in {
            system.settings.config.getString("akka.actor.provider") must equal("akka.remote.RemoteActorRefProvider")
            system.settings.config.getList("akka.remote.enabled-transports").get(0).render must equal("\"akka.remote.netty.tcp\"")
            system.settings.config.getString("akka.remote.netty.tcp.hostname") must equal("127.0.0.1")
            system.settings.config.getInt("akka.remote.netty.tcp.port") must equal(9909)
        }

        "Have default configurations also using the specified host name and port" in {
            system.settings.config.getString("akka.remote.netty.hostname") must equal("127.0.0.1")
            system.settings.config.getInt("akka.remote.netty.port") must equal(9909)
        }

        "Have the supplied name" in {
            system.name must equal("tangyniknaks")
        }

        system.shutdown()
    }

}
