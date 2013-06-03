package unittests

import akkesb.dbus._
import akkesb.host.{MessageRegistrationsActor, ServiceFacadeActor, MessageSendActor, BusHost}
import org.scalatest.{OneInstancePerTest, FreeSpec}
import scala.language.postfixOps
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import akka.actor.{Actor, Props, ActorRef, ActorSystem}
import akka.testkit.{TestKit, TestActorRef}
import org.mockito.Matchers._
import org.hamcrest.{Description, BaseMatcher}
import scala.util.Random

class Bus_host_startup_spec extends FreeSpec with OneInstancePerTest with MockitoSugar  {

    "When the bus host starts up" - {

        when(actorSystem
            .actorOf(new Props(classOf[ServiceFacadeActor])))
            .thenReturn(serviceFacadeActor)

        when(actorSystem
            .actorOf(new Props(classOf[MessageRegistrationsActor])))
            .thenReturn(registrationsActor)

        when(actorSystem
            .actorOf(argThat(new IsValidPropsToCreateActor(classOf[MessageSendActor]))))
            .thenReturn(messageSendingActor)

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


    val hostName = "funnyhostname"
    val port = "2892"
    val application = "funnyapplication"
    val service = "funnyservice"
    val handler = new ActorDelegatingMessageHandler()
    val sender = mock[DBusMessageSender]
    val connection = mock[TestableDBusConnection]
    implicit val actorSystem = mock[ActorSystem]
    val messageSendingActor = mock[ActorRef]
    val serviceFacadeActor = mock[ActorRef]
    val registrationsActor = mock[ActorRef]
}

class IsValidPropsToCreateActor(actorType: Class[_]) extends BaseMatcher[Props]{

    implicit val actorSystem = ActorSystem.create("test")

    def matches(item: Any): Boolean = {
        val props = item.asInstanceOf[Props]
        TestActorRef(props, randomName).underlyingActor.getClass.equals(actorType)
    }

    def randomName = "random_name_" + new Random().nextInt(99999)

    def describeTo(description: Description) {}
}

