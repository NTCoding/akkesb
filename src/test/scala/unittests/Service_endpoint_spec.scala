package unittests

import akka.testkit.{TestActorRef, TestProbe}
import akkesb.host.{SendRemoteMessages, ServiceEndpoint}

class Service_endpoint_spec extends TestBaseClassWithJunitRunnerAndTestKit {

    "When told to send a collection of messages to remote addresses" - {

        val remoteActor1 = TestProbe()
        val remoteActor2 = TestProbe()

        val endpoint = TestActorRef[ServiceEndpoint]
        endpoint ! SendRemoteMessages(List((remoteActor1.ref, "hello fatboy"), (remoteActor2.ref, (1, 9909))))

        "Each message is sent to the remote address" in {
            remoteActor1.expectMsg("hello fatboy")
            remoteActor2.expectMsg((1, 9909))
        }
    }

}
