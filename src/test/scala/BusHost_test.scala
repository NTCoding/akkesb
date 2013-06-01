package test

import akkesb.dbus._
import akkesb.host.BusHost
import org.freedesktop.dbus.{DBusInterface, DBusAsyncReply, DBusConnection}
import org.scalatest.{OneInstancePerTest, BeforeAndAfter, BeforeAndAfterAll, FreeSpec}
import scala.language.postfixOps
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._

class Bus_host_startup_spec extends FreeSpec with OneInstancePerTest with MockitoSugar  {

    val hostName = "funnyhostname"
    val port = "2892"
    val application = "funnyapplication"
    val service = "funnyservice"
    val handler = new ActorDelegatingMessageHandler()
    val sender = new DBusMessageSender()
    val connection = mock[TestableDBusConnection]

    "When starting up the bus host" - {

        BusHost(hostName, port, application, service, handler, sender, connection)

        "it registers as a service on akkesb using the supplied application and service name" in {
            verify(connection) requestBusName f"akkesb.$application.$service"
        }

        "it exports the supplied command handler on akkesb in the messages/incoming path" in {

        }

        "it exports the supplied command sender on akkesb in the messages/outgoing path" in {

        }
    }


}


