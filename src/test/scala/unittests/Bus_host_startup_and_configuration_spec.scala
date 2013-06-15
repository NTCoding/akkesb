package unittests

import akkesb.dbus._
import akkesb.host._
import org.scalatest._
import scala.language.postfixOps
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import akka.actor._
import akka.testkit.{TestProbe, TestKit, TestActorRef}
import org.mockito.Matchers._
import org.hamcrest.{Description, BaseMatcher}
import scala.util.Random
import akkesb.host.AddAddress
import org.scalatest.exceptions.NotAllowedException
import akkesb.host.DistributeCommandOwnership
import akkesb.host.AddAddress

class Bus_host_startup_and_configuration_spec extends TestBaseClassWithJunitRunnerAndTestKit  {

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
    val creator = mock[ActorSystemCreator]
    val configDistributor = new TestProbe(system)


    "When the bus host starts up" - {

        when(creator.create(application, hostName, port)).thenReturn(actorSystem)

        when(actorSystem
            .actorOf(new Props(classOf[ServiceEndpoint])))
            .thenReturn(serviceFacadeActor)

        when(actorSystem
            .actorOf(new Props(classOf[MessageRegistrationsActor])))
            .thenReturn(registrationsActor)

        when(actorSystem
            .actorOf(argThat(new IsValidPropsToCreateActor(classOf[MessageSendActor]))))
            .thenReturn(messageSendingActor)

        when(actorSystem
            .actorOf(argThat(new IsValidPropsToCreateActor(classOf[AddressBookActor]))))
            .thenReturn(testActor)

        when(actorSystem
            .actorOf(new Props(classOf[ConfigDistributor])))
            .thenReturn(configDistributor.ref)

        val host = BusHost(hostName, port, application, service, handler, sender, connection, creator)


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

        "it forwards addresses to the address book actor when joining a cluster" in {
            host joinCluster(List(("funny_service", "127.0.0.1", "1111"), ("crazy_service", "243.122.56.23", "4454")))
            expectMsg(AddAddress("funny_service", "127.0.0.1", "1111"))
            expectMsg(AddAddress("crazy_service", "243.122.56.23", "4454"))
        }

        "when handler registrations are updated it tells the config distributor to distribute them" in {
            val commandNames = List("im_alive_command", "time_for_breakfast_command")
            host willHandleCommands(commandNames)
            configDistributor.expectMsg(DistributeCommandOwnership(service, commandNames))
        }

    }
}

class IsValidPropsToCreateActor(actorType: Class[_]) extends BaseMatcher[Props]{

    val random = new Random()

    implicit val actorSystem = ActorSystem.create("test" + random.nextInt(99999))

    def matches(item: Any): Boolean = {
        item match {
            case props: Props => TestActorRef(props, f"$actorType${random.nextInt(999999)}").underlyingActor.getClass.equals(actorType)
            case null => false
        }
    }
    def randomName = "random_name_" + random.nextInt(99999)

    def describeTo(description: Description) {}

}

