package unittests

import akka.testkit.{TestActorRef, TestKit}
import akka.actor.{ActorRef, Props, ActorSystem}
import org.scalatest.FreeSpecLike
import akkesb.host._
import org.scalatest.mock.MockitoSugar
import akkesb.host.CommandOwnedBy
import akkesb.host.WhoHandlesCommandRequest
import akkesb.host.SendCommand


class Message_send_actor_spec extends TestKit(ActorSystem("TestActorSystem")) with FreeSpecLike with StopSystemAfterAll
                              with MockitoSugar {

    "when the message send actor receives a 'send command' message" - {

        val unusedInThisTestActor = mock[ActorRef]
        val keys = Array("1", "2")
        val values = Array("3", "4")

        val messageSendActor = system.actorOf(new Props(() => new MessageSendActor(testActor, unusedInThisTestActor)))

        messageSendActor ! SendCommand("eat_the_doughnuts", keys, values)


        "it sends a message to the message registration actor asking who handles the command" in {
            expectMsg(WhoHandlesCommandRequest("eat_the_doughnuts", keys, values))
        }
    }


    "when the message send actor receives an 'owned by' message" - {

        val unusedInThisTestActor = mock[ActorRef]
        val keys = Array("1", "2")
        val values = Array("3", "4")

        val messageSendActor = system.actorOf(new Props(() => new MessageSendActor(unusedInThisTestActor, testActor)))

        messageSendActor ! CommandOwnedBy("marketing_service", "xyzcommand", keys, values)


        "it sends a 'send command' message to the service actor" in {
            expectMsg(SendCommandToService("marketing_service", "xyzcommand", keys, values))
        }
    }

}
