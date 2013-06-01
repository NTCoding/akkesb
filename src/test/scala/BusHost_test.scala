


import akkesb.dbus.{TestableDBusConnection, DBusMessageSender, ActorDelegatingMessageHandler}
import akkesb.host.BusHost
import org.freedesktop.dbus.{DBusInterface, DBusAsyncReply, DBusConnection}
import org.scalamock.scalatest.MockFactory
import org.scalatest.FreeSpec
import scala.language.postfixOps


class Bus_host_startup_spec extends FreeSpec with MockFactory  {

    val hostName = "funnyhostname"
    val port = "2892"
    val application = "funnyapplication"
    val service = "funnyservice"
    val handler = new ActorDelegatingMessageHandler()
    val sender = new DBusMessageSender()
    val connection = mock[TestableDBusConnection]


    "When starting up the bus akkesb.host" - {

        BusHost(hostName, port, application, service, handler, sender, connection);


        "it registers as a service on akkesb using the supplied application and service name" in {
            connection.requestBusName _ verify f"akkesb.$application.$service"
        }

        "it exports the supplied command handler on akkesb in the messages/incoming path" in {

        }

        "it exports the supplied command sender on akkesb in the messages/outgoing path" in {

        }

    }
}


