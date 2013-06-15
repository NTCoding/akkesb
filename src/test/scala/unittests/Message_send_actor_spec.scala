package unittests

import akka.testkit.{TestActorRef, TestKit}
import akka.actor.{ActorRef, Props, ActorSystem}
import org.scalatest.FreeSpecLike
import akkesb.host._
import org.scalatest.mock.MockitoSugar
import akkesb.host.CommandHandledBy
import akkesb.host.WhoHandlesCommand
import akkesb.host.SendCommand


class Message_send_actor_spec extends TestKit(ActorSystem("TestActorSystem")) with FreeSpecLike with StopSystemAfterAll
                              with MockitoSugar {

    "when the message send actor receives a 'send command' message" - {

        val unusedInThisTestActor = mock[ActorRef]
        val keys = Array("1", "2")
        val values = Array("3", "4")
        val registrations = testActor

        val messageSender = system.actorOf(new Props(() => new MessageSendActor(registrations, unusedInThisTestActor, unusedInThisTestActor)))
        messageSender ! SendCommand("eat_the_doughnuts", keys, values)

        "it sends a message to the message registration actor asking who handles the command" in {
            expectMsg(WhoHandlesCommand("eat_the_doughnuts", keys, values))
        }
    }

    "when the message send actor receives an 'owned by' message" - {

        val unusedInThisTestActor = mock[ActorRef]
        val keys = Array("1", "2")
        val values = Array("3", "4")
        val addressBook = testActor

        val messageSender = system.actorOf(new Props(() => new MessageSendActor(unusedInThisTestActor, unusedInThisTestActor, addressBook)))
        messageSender ! CommandHandledBy("marketing_service", "xyzcommand", keys, values)

        "it asks the address book for a reference to the address of the service who owns the command" in {
             expectMsg(WhatIsTheAddressFor("marketing_service", ("xyzcommand", keys, values)))
        }
    }

    "when the message send actor receives a reference to an address" - {

        val unusedInThisTestActor = mock[ActorRef]
        val serviceFacade = testActor
        val keys = Array("password")
        val values =  Array("cheese")
        val addressRef = TestActorRef[ServiceEndpoint]

        val messageSender = system.actorOf(new Props(() => new MessageSendActor(unusedInThisTestActor, serviceFacade, unusedInThisTestActor)))
        messageSender ! ReferenceToAddress(addressRef, ("command", keys, values))

        "it tells the service facade to send the command to the address reference" in {
            expectMsg(SendCommandToService(addressRef,("command", keys, values)))
        }
    }

}
