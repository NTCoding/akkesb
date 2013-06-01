package test

import akkesb.dbus._
import akkesb.host.BusHost
import org.scalatest.{OneInstancePerTest, FreeSpec}
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

        "it registers as a service on dbus using the supplied application and service name" in {
            verify(connection) requestBusName f"akkesb.$application.$service"
        }

        "it exports the supplied command handler on dbus in the messages/incoming path" in {
            verify(connection) exportObject("/messages/incoming", handler)
        }

        "it exports the supplied command sender on dbus in the messages/outgoing path" in {
            verify(connection) exportObject("/messages/outgoing", sender)
        }
    }
}


