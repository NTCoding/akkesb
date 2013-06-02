package unittests

import akkesb.dbus._
import akkesb.host.{MessageSendActor, BusHost}
import org.scalatest.{OneInstancePerTest, FreeSpec}
import scala.language.postfixOps
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import akka.actor.{Props, ActorRef, ActorSystem}
import akka.testkit.TestActorRef

class Bus_host_startup_spec extends FreeSpec with OneInstancePerTest with MockitoSugar  {

    val hostName = "funnyhostname"
    val port = "2892"
    val application = "funnyapplication"
    val service = "funnyservice"
    val handler = new ActorDelegatingMessageHandler()
    val sender = mock[DBusMessageSender]
    val connection = mock[TestableDBusConnection]
    implicit val actorSystem = mock[ActorSystem]
    val messageSendingActor = mock[ActorRef]


    "When the bus host starts up" - {

        when(actorSystem.actorOf(new Props(classOf[MessageSendActor]))).thenReturn(messageSendingActor)

        BusHost(hostName, port, application, service, handler, sender, connection, actorSystem)

        "it registers as a service on dbus using the supplied application and service name" in {
            verify(connection) requestBusName f"akkesb.$application.$service"
        }

        "it passes a reference to the message sending actor to the message sender" in {
            verify(sender) setActor messageSendingActor
        }

        "it exports the supplied command handler on dbus in the messages/incoming path" in {
            verify(connection) exportObject("/messages/incoming", handler)
        }

        "it exports the supplied command sender on dbus in the messages/outgoing path" in {
            verify(connection) exportObject("/messages/outgoing", sender)
        }
    }
}


