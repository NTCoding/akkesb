package multijvm.commands

import akkesb.host.{RemoteActorSystemCreator, BusHost}
import utils._
import org.junit.Assert
import org.freedesktop.dbus.DBusConnection
import akkesb.dbus.{AkkesbDBusConnection, ActorDelegatingMessageHandler, MessageHandler, DBusMessageSender}
import akka.actor.ActorSystem
import org.scalatest.{FreeSpec, FreeSpecLike}


/* Each of these tests represents a separate akkesb.host process - this is to emulate a distributed cluster
   where each service is running on a different machine

   It is being considered whether services on the same machine might share a akkesb.host process - this could be
   1 akkesb.host per OS, 1 akkesb.host per app, or option to choose
 */


object CommandsAreSentToRegisteredHandlersMultiJvmMarketingServiceHost extends FreeSpec {

    def main(args: Array[String]) {

        "startup a marketing service that registers itself as a sender of update_price commands " - {

            val connection = new AkkesbDBusConnection(DBusConnection.getConnection(DBusConnection.SESSION))
            // TODO hostname and port should be passed in as args - come back to this later
            val host = BusHost("127.0.0.1", "3051","commandsAreSentTest", "marketing_service",
                new ActorDelegatingMessageHandler(), new DBusMessageSender(), connection, new RemoteActorSystemCreator)
            host joinCluster(List(("catalogue_service", "127.0.0.1", "3052"), ("payments_service", "127.0.0.1", "3053")))
            host willSendCommands List(("update_price"))
        }
    }
}

object CommandsAreSentToRegisteredHandlersMultiJvmCatalogueServiceHost extends FreeSpec {

    def main(args: Array[String]) {

        "startup a catalogue service that sends stop_taking_payments_for_product commands and handles update_price commands" - {

            var receivedMessages : List[(String, Array[String], Array[String])] = List()
            val tmh = new TestMessageHandler((name, keys, values) => receivedMessages = receivedMessages :+ (name, keys, values))

            val connection = new AkkesbDBusConnection(DBusConnection.getConnection(DBusConnection.SESSION))
            val host = BusHost("127.0.0.1", "3052", "commandsAreSentTest", "catalogue_service", tmh, new DBusMessageSender(),connection, new RemoteActorSystemCreator)

            host joinCluster(List(("marketing_service", "127.0.0.1", "3051"),("payments_service", "127.0.0.1", "3053")))
            host willSendCommands List("stop_taking_payments_for_product")
            host willHandleCommands List("update_price")

            "the catalogue service will have received the update_price command sent from the client" in {
                Thread.sleep(1000) // wait for messages to be sent from the client library simulation
                Assert.assertTrue("catalogue service did not receive the update price command", receivedMessages contains ("update_price", List(("productId", 1), ("price", 50))))
            }
        }
    }
}

object CommandsAreSentToRegisteredHandlersMultiJvmPaymentsServiceHost extends FreeSpec {

    def main(args: Array[String]) {

        "Startup a payments service that handles stop_taking_payments_for_product commands" - {

            var receivedMessages : List[(String, Array[String], Array[String])] = List()
            val tmh = new TestMessageHandler((name, keys, values) => receivedMessages = receivedMessages :+ (name, keys, values))
            val connection = new AkkesbDBusConnection(DBusConnection.getConnection(DBusConnection.SESSION))

            val host = BusHost("127.0.0.1", "3053", "commandsAreSentTest", "payments_service", tmh,
                new DBusMessageSender(), connection, new RemoteActorSystemCreator)

            host joinCluster(List(("marketing_service", "127.0.0.1", "3051"),("catalogue_service", "127.0.0.1", "3052")))
            host willHandleCommands List("stop_taking_payments_for_product")

            "the payments service will have recieved the stop_taking_paments_for_product command that was sent from the client" in {
                Thread.sleep(1000) // wait for messages to be sent from the client library simulation
                Assert.assertTrue("payments service did not receive stop taking payments command", receivedMessages contains ("stop_taking_payments_for_product", List(("productId", 1))))
            }
        }
    }
}

object CommandsAreSentToRegisteredHandlersMultiJvmClientLibrarySimulation extends FreeSpec {
    /*
        Messages are sent from a separate JVM replicating how a real system work - client
        libraries will send messages via akkesb from separate processes
     */
    def main(args: Array[String]) {
        Thread.sleep(8000) // wait for services to start up and share configs - might be a better way than sleeping

        Command(("update_price", List(("productId", 1), ("price", 50))))
               .sendFrom("marketing_service", "commandsAreSentTest")

        Command(("stop_taking_payments_for_product", List(("productId", 1))))
               .sendFrom("catalogue_service", "commandsAreSentTest")
    }
}

class TestMessageHandler(handler: (String, Array[String], Array[String]) => Unit) extends MessageHandler {

    def handle(name: String, keys: Array[String], values: Array[String]) {
         handler(name, keys, values)
    }

    def isRemote: Boolean = false
}
