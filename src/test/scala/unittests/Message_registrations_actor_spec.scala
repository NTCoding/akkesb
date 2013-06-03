package unittests

import akka.actor.ActorSystem
import org.scalatest.FreeSpecLike
import org.scalatest.mock.MockitoSugar
import akka.testkit.TestKit

class Message_registrations_actor_spec extends TestKit(ActorSystem("TestActorSystem")) with FreeSpecLike with StopSystemAfterAll {

    "When the message registrations actor has been supplied registration details" - {

        "And a 'who owns command' request is received for a command that has been registered" in {

            "It responds with a 'command handled by' message showing who owns the command" in {
                fail("blah")
            }

        }

        "But a 'who owns command' request is received for an un-registered command" in {

            "It does not respond ** this behaviour will be changed at some point **" in {
               fail("blah")
            }
        }

    }


}
