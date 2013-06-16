package unittests

import akka.testkit.{TestActorRef, TestKit}
import akka.actor.{Actor, Props, ActorSystem}
import org.scalatest.FreeSpecLike
import akkesb.host._
import scala.concurrent.duration.Duration
import org.scalatest.MustMatchers
import akkesb.host.ReferenceToAddress
import akkesb.host.AddAddress
import akkesb.host.WhatIsTheAddressFor
import com.typesafe.config.ConfigFactory
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class Address_book_actor_spec extends TestKit(ActorSystem("TestActorSystem989", ConfigFactory.load(ConfigFactory.parseString("""akka.actor { provider = "akka.remote.RemoteActorRefProvider" }"""))))
                                with FreeSpecLike with MustMatchers with StopSystemAfterAll{

    val keys = Array("1", "2", "3")
    val values = Array("4", "5", "6")

    "When telling the address book to store the address of some existing actors" - {
        val rs = new RemoteActorSystemCreator().create("7dizzo", "127.0.0.1", "6612")
        val sevDigActor = rs.actorOf(new Props(() => new Actor{ def receive = {case _ => println("hello")}}), "7digital")
        val badAssActor = rs.actorOf(new Props(() => new Actor{ def receive = {case _ => println("hello")}}), "badAss")

        val addressBook = TestActorRef(Props(() => new AddressBookActor("7dizzo")))
        addressBook ! AddAddress("7digital", "127.0.0.1", "6612")
        addressBook ! AddAddress("badAss", "127.0.0.1", "6612")

        "Future requests for an address return an actor ref pointing to the requested address (also forwarding the received state)" in {
            addressBook.tell(WhatIsTheAddressFor("7digital", ("command", keys, values)), testActor)
            receiveOne(Duration(200, "ms")) match {
                case ref: ReferenceToAddress => {
                    ref.reference.path.toString must equal("akka://7dizzo@127.0.0.1:6612/user/7digital")
                    ref.state must equal(("command", keys, values))
                }
            }
        }

        "Future requests for all addresses return an actor ref pointing to each address (also forwarding the received state)" in {
            val inputState = ("randomstate", 99112, "cheesecake")

            addressBook.tell(GiveMeAReferenceToAllServiceAddresses(inputState), testActor)

            receiveOne(Duration(100, "ms")) match {
                case AllServices(serviceAddresses, state) => {
                     serviceAddresses.length must be(2)
                     serviceAddresses.count(s => s.path.toString.equals("akka://7dizzo@127.0.0.1:6612/user/7digital")) must be(1)
                     serviceAddresses.count(s => s.path.toString.equals("akka://7dizzo@127.0.0.1:6612/user/badAss")) must be(1)
                     state must equal(inputState)
                }
            }
        }

        rs.shutdown()
    }
}
