package unittests

import akka.actor.{Props, ActorSystem}
import org.scalatest.FreeSpecLike
import org.scalatest.mock.MockitoSugar
import akka.testkit.{TestActorRef, TestActor, TestKit}
import akkesb.host._
import akka.testkit
import scala.concurrent.duration.Duration
import org.scalatest.MustMatchers
import akkesb.host.CommandHandledBy
import akkesb.host.WhoHandlesCommand
import akkesb.host.RegisterCommandHandler

class Message_registrations_actor_spec extends TestBaseClassWithJunitRunnerAndTestKit {

    // TODO - test cases for un-registered commands and duplicate registrations

    "When the message registrations actor has been supplied registration details" - {

        val registrationsActor = TestActorRef[MessageRegistrationsActor]
        registrationsActor ! RegisterCommandHandler("send_tweet_command", "ntcoding_service")

        "And a 'who handles command' request is received for a command that has been registered" - {

            val keys = Array("userId", "userName")
            val values = Array("123", "MikeSlim")
            registrationsActor.tell(WhoHandlesCommand("send_tweet_command", keys, values), testActor)

            val response = receiveOne(Duration(100, "ms")).asInstanceOf[CommandHandledBy]

            "A 'command handled by' response is received showing who owns the command" in {
               response.handler must equal("ntcoding_service")
            }

            "The response also has the original command and data - so that no state is stored anywhere" in {
                response.commandName must equal("send_tweet_command")
                response.keys must equal(keys)
                response.values must equal(values)
            }
        }

        "But when a request is made for an un-registered command an exception is thrown stating that no service is registered to handle this command" in {
            intercept[UnRegisteredCommand] { registrationsActor.receive(WhoHandlesCommand("unregistered", Array("blah"), Array("blah"))) }
        }
    }


}
