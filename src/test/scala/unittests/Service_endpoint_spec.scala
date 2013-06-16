package unittests

import akka.testkit.{TestActorRef, TestProbe}
import akkesb.host._
import akka.actor.Props
import akkesb.host.CommandHandlerRegistrations
import akkesb.host.SendRemoteMessages

class Service_endpoint_spec extends TestBaseClassWithJunitRunnerAndTestKit {


    "When told to send a collection of messages to remote addresses" - {
        val remoteActor1 = TestProbe()
        val remoteActor2 = TestProbe()

        val endpoint = TestActorRef(Props(() => new ServiceEndpoint(TestProbe().ref)))
        endpoint ! SendRemoteMessages(List((remoteActor1.ref, "hello fatboy"), (remoteActor2.ref, (1, 9909))))

        "Each message is sent to the remote address" in {
            remoteActor1.expectMsg("hello fatboy")
            remoteActor2.expectMsg((1, 9909))
        }
    }


    "When receiving command handler registrations" - {
        val messageRegistrar = TestProbe()
        val commands = List("update_price", "change_ya_clothes", "go_crazy_ebeneezer")

        val endpoint = TestActorRef(Props(() => new ServiceEndpoint(messageRegistrar.ref)))

        endpoint ! CommandHandlerRegistrations("crazee_service", commands)

        "It forwards them to the message registrar" in {
            messageRegistrar.expectMsg(RegisterMultipleCommandsHandler(commands, "crazee_service"))
        }
    }


    "When told to send a command to a service" - {
        val remoteService = TestProbe()
        val command = ("go_for_a_swimg", Array("swimming_trunks_type"), Array("speedo"))
        val endpoint = TestActorRef(Props(() => new ServiceEndpoint(TestProbe().ref)))

        endpoint ! SendCommandToService(remoteService.ref, command)

        "It sends the command to the service" in {
            remoteService.expectMsg(ProcessCommand(command))
        }
    }
}
