package multijvm.commands

import Host.BusHost
import utils._
import akkesb.commands.{ActorDelegatingMessageHandler, DBusMessageSender, MessageHandler}
import org.junit.Assert
import org.freedesktop.dbus.DBusConnection


/* Each of these tests represents a separate host process - this is to emulate a distributed cluster
   where each service is running on a different machine

   It is being considered whether services on the same machine might share a host process - this could be
   1 host per OS, 1 host per app, or option to choose
 */


object CommandsAreSentToRegisteredHandlers_MultiJvm_MarketingServiceHost {

    def main(args: Array[String]) {

        // TODO hostname and port should be passed in as args - come back to this later
        val host = BusHost("127.0.0.1", "3051","commands_are_sent_test", "marketing_service",
            new ActorDelegatingMessageHandler(), new DBusMessageSender(), DBusConnection.getConnection(DBusConnection.SESSION))

        // TODO - if these are not set at startup - they will need to be sent via dbus instead
        host willSendCommands List(("update_price"))
        host joinCluster
    }

}

object CommandsAreSentToRegisteredHandlers_MultiJvm_CatalogueServiceHost {

    def main(args: Array[String]) {

        var receivedMessages : List[(String, Array[String], Array[String])] = List()
        val tmh = new TestMessageHandler((name, keys, values) => receivedMessages = receivedMessages :+ (name, keys, values))

        // TODO - first 3 params can group as a data structure
        val host = BusHost("127.0.0.1", "3052", "commands_are_sent_test", "catalogue_service", tmh,
            new DBusMessageSender(), DBusConnection.getConnection(DBusConnection.SESSION))

        host willSendCommands List("stop_taking_payments_for_product")
        host willHandleCommands List("update_price")
        host joinCluster

        Thread.sleep(500) // wait for messages to be sent from the client library simulation

        Assert.assertTrue("catalogue service did not receive the update price command", receivedMessages contains ("update_price", List(("productId", 1), ("price", 50))))
    }
}


object CommandsAreSentToRegisteredHandlers_MultiJvm_PaymentsServiceHost {


    def main(args: Array[String]) {

        var receivedMessages : List[(String, Array[String], Array[String])] = List()
        val tmh = new TestMessageHandler((name, keys, values) => receivedMessages = receivedMessages :+ (name, keys, values))


        val host = BusHost("127.0.0.1", "3053", "commands_are_sent_test", "payments_service", tmh,
            new DBusMessageSender(), DBusConnection.getConnection(DBusConnection.SESSION))

        host willHandleCommands List("stop_taking_payments_for_product")
        host joinCluster

        Thread.sleep(500) // wait for messages to be sent from the client library simulation

        Assert.assertTrue("payments service did not receive stop taking payments command", receivedMessages contains ("stop_taking_payments_for_product", List(("productId", 1))))
    }
}

object CommandsAreSentToRegisteredHandlers_MultiJvm_ClientLibrarySimulation {

    /*
        Messages are sent from a separate JVM replicating how a real system work - client
        libraries will send messages via dbus from separate processes
     */
    def main(args: Array[String]) {

        // wait for services to start up - might be a better way than sleeping
        Thread.sleep(500)

        Command(("update_price", List(("productId", 1), ("price", 50))))
               .sendFrom("marketing_service", "commands_are_sent_test")

        Command(("stop_taking_payments_for_product", List(("productId", 1))))
               .sendFrom("catalogue_service", "commands_are_sent_test")

    }
}

class TestMessageHandler(handler: (String, Array[String], Array[String]) => Unit) extends MessageHandler {

    def handle(name: String, keys: Array[String], values: Array[String]) {
         handler(name, keys, values)
    }

    def isRemote: Boolean = false
}
