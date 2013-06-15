package unittests

import akka.testkit.{TestProbe, TestActorRef, TestKit}
import akka.actor.{ActorRef, Props, ActorSystem}
import org.scalatest.{FreeSpecLike, path, MustMatchers}
import akkesb.host._
import akkesb.host.DistributeCommandOwnership
import akkesb.host.AllServices
import akkesb.host.GiveMeAReferenceToAllServiceAddresses
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class Config_distributor_spec extends TestBaseClassWithJunitRunnerAndTestKit {


    "when the config distributor is told to distribute commands" - {
        val command = ("fubu_service", List("change_conventions", "remove_old_wiki_page"))
        val addressBook = TestProbe()
        val serviceEndpoint = TestProbe()
        val distributor = TestActorRef(Props(() => new ConfigDistributor(addressBook.ref, serviceEndpoint.ref)))

        distributor ! DistributeCommandOwnership("fubu_service", List("change_conventions", "remove_old_wiki_page"))

        "it gets the address of all services" in {
            addressBook.expectMsg(GiveMeAReferenceToAllServiceAddresses(command))
        }

        "when the reply comes back with the list of addresses" - {
            val service1 = TestProbe()
            val service2 = TestProbe()
            distributor ! AllServices(List(service1.ref, service2.ref), command)

            "it tells the service endpoint to send the registrations to them" in {
                val expectedMessages = List[(ActorRef, AnyRef)](
                    (service1.ref, CommandHandlerRegistrations("fubu_service", List("change_conventions", "remove_old_wiki_page"))),
                    (service2.ref, CommandHandlerRegistrations("fubu_service", List("change_conventions", "remove_old_wiki_page")))
                )
                serviceEndpoint.expectMsg(SendRemoteMessages(expectedMessages))
            }
        }
    }
}
