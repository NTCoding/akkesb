package unittests

import akka.actor.ActorSystem
import akka.testkit.TestKit
import akkesb.dbus.{MessageSendActorHasNotBeenSet, DBusMessageSender}
import org.scalatest.FreeSpecLike
import akkesb.host.SendCommand
import scala.concurrent.duration.FiniteDuration

class DBus_message_sender_spec extends TestBaseClassWithJunitRunnerAndTestKit {


    "when the sender has been configured with an actor ref" - {

        val sender = new DBusMessageSender()
        val keys = Array("1", "2", "3")
        val values = Array("4", "5", "6")

        sender.setActor(testActor)
        sender.send("test_message", keys, values)

        "messages sending is handled by forwarding the message to the message sending actor" in {
            expectMsg(new SendCommand("test_message", keys, values))
        }

    }


    "when the sender has **NOT** been configured with an actor ref" - {

        val sender = new DBusMessageSender()

        "attempts to handle messages will always throw a descriptive exception" in {
             intercept[MessageSendActorHasNotBeenSet] {
                sender.send("should_blow_up", Array("1"), Array("2"))
             }
        }

    }

}



