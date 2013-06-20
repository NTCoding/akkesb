package multijvm.commands

import akkesb.host.{RemoteActorSystemCreator, BusHost}
import utils._
import org.junit.Assert
import org.freedesktop.dbus.{DBusInterfaceName, DBusInterface, DBusConnection}
import akkesb.dbus._
import akka.actor.ActorSystem
import org.scalatest.{FreeSpec, FreeSpecLike}
import org.scalatest.MustMatchers
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object CommandsAreSentToRegisteredHandlersMultiJvmMarketingServiceHost {

    def main(args: Array[String]) {
        val connection = new AkkesbDBusConnection(DBusConnection.getConnection(DBusConnection.SESSION))
        val h = new DBusMessageHandler("commandsAreSentTest","marketing_service", connection)
        val host = BusHost("127.0.0.1", "3051","commandsAreSentTest", "marketing_service",h, new DBusMessageSender, connection, new RemoteActorSystemCreator)
        host joinCluster List(("catalogue_service", "127.0.0.1", "3052"), ("payments_service", "127.0.0.1", "3053"))
        host willSendCommands List("update_price")

        Thread.sleep(10000)
        sys.exit()
    }
}

object CommandsAreSentToRegisteredHandlersMultiJvmMarketingServiceClient {

    def main(args: Array[String]) {
       Thread.sleep(10000) // wait for services to start up and share configs - might be a better way than sleeping

       Command(("update_price", List(("productId", 1), ("price", 50))))
                .sendFrom("marketing_service", "commandsAreSentTest")

        sys.exit()
    }
}

object CommandsAreSentToRegisteredHandlersMultiJvmCatalogueServiceHost extends MustMatchers {

    def main(args: Array[String]) {
        val connection = new AkkesbDBusConnection(DBusConnection.getConnection(DBusConnection.SESSION))
        val h = new DBusMessageHandler("commandsAreSentTest", "catalogue_service", connection)
        val host = BusHost("127.0.0.1", "3052", "commandsAreSentTest", "catalogue_service", h, new DBusMessageSender,connection, new RemoteActorSystemCreator)

        host joinCluster List(("marketing_service", "127.0.0.1", "3051"), ("payments_service", "127.0.0.1", "3053"))
        host willSendCommands List("stop_taking_payments_for_product")
        host willHandleCommands List("update_price")

        Thread.sleep(10000) // wait for messages to be sent from the client library simulation

        sys.exit()
    }
}

object CommandsAreSentToRegisteredHandlersMultiJvmCatalogueServiceClient extends MustMatchers {

    def main(args: Array[String]) {
        val messages = new ListBuffer[(String, Array[String], Array[String])]
        val handler = new TestDBusHandler(messages)

        val connection = DBusConnection.getConnection(DBusConnection.SESSION)
        connection.requestBusName("akkesb.commandsAreSentTest.catalogue_service.Client")
        connection.exportObject("/commands/receiving", handler)

        Thread.sleep(10000) // wait for services to share configs

        Command(("stop_taking_payments_for_product", List(("productId", 1))))
            .sendFrom("catalogue_service", "commandsAreSentTest")

        Thread.sleep(6000)

        messages.head._1 must equal("update_price")
        messages.head._2 must equal(Array("productId", "price"))
        messages.head._3 must equal(Array("1", "50"))

        sys.exit()
    }
}

object CommandsAreSentToRegisteredHandlersMultiJvmPaymentsServiceHost {

    def main(args: Array[String]) {
        val connection = new AkkesbDBusConnection(DBusConnection.getConnection(DBusConnection.SESSION))
        val h = new DBusMessageHandler("commandsAreSentTest", "payments_service", connection)
        val host = BusHost("127.0.0.1", "3053", "commandsAreSentTest", "payments_service", h, new DBusMessageSender, connection, new RemoteActorSystemCreator)
        host joinCluster List(("marketing_service", "127.0.0.1", "3051"), ("catalogue_service", "127.0.0.1", "3052"))
        host willHandleCommands List("stop_taking_payments_for_product")

        Thread.sleep(10000) // wait for messages to be sent from the client library simulation

        sys.exit()
    }
}

object CommandsAreSentToRegisteredHandlersMultiJvmPaymentsServiceClient extends MustMatchers {

    def main(args: Array[String]) {
        val messages = new ListBuffer[(String, Array[String], Array[String])]
        val handler = new TestDBusHandler(messages)

        val connection = DBusConnection.getConnection(DBusConnection.SESSION)
        connection.requestBusName("akkesb.commandsAreSentTest.payments_service.Client")
        connection.exportObject("/commands/receiving", handler)

        Thread.sleep(11000)

        messages.head._1 must equal("stop_taking_payments_for_product")
        messages.head._2 must equal(Array("productId"))
        messages.head._3 must equal(Array("1"))

        sys.exit()
    }
}

@DBusInterfaceName("akkesb.dbus.MessageHandler")
class TestDBusHandler(private var messages: ListBuffer[(String, Array[String], Array[String])]) extends DBusInterface {

    def handle(name: String, keys: Array[String], values: Array[String]) {
        println(s"**********     Received message: $name     **********")
        messages.append((name, keys, values))
    }

    def isRemote: Boolean = false
}
