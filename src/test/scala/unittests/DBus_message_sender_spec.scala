package unittests

import akka.actor.ActorSystem
import akka.testkit.{TestActorRef, TestKit}
import akkesb.dbus.DBusMessageSender
import org.scalatest.{FreeSpecLike, fixture, WordSpec}
import akkesb.host.{SendCommand, MessageSendActor}

class DBus_message_sender_spec extends TestKit(ActorSystem("testsystemt")) with FreeSpecLike with StopSystemAfterAll {


    "when the sender has been configured with an actor ref" - {

        val sender = new DBusMessageSender()
        val actorRef =  TestActorRef[MessageSendActor]
        sender.setActor(actorRef)
        sender.send("test_message", Array("1", "2", "3"), Array("4", "5", "6"))

        "messages sending is handled by forwarding the message to the message sending actor" in {
            expectMsg(SendCommand("test_message", Array("1", "2", "3"), Array("4", "5", "6")))
        }

    }


    "when the sender has **NOT** been configured with an actor ref" - {

        "attempts to handle messages will always throw a descriptive exception" in {
            fail("blah")
        }

    }

}



