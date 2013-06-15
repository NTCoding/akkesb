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

class Address_book_actor_spec extends TestKit(ActorSystem("TestActorSystem989", ConfigFactory.load(ConfigFactory.parseString("""akka.actor { provider = "akka.remote.RemoteActorRefProvider" }"""))))
                                with FreeSpecLike with MustMatchers with StopSystemAfterAll{

    val keys = Array("1", "2", "3")
    val values = Array("4", "5", "6")

    "When telling the address book to store an address for an actor that already exists" - {

        val rs = new RemoteActorSystemCreator().create("7dizzo", "127.0.0.1", "6612")
        val act = rs.actorOf(new Props(() => new Actor{ def receive = {case _ => println("hello")}}), "7digital")

        val addressBook = TestActorRef(Props(() => new AddressBookActor("7dizzo")))
        addressBook ! AddAddress("7digital", "127.0.0.1", "6612")
        addressBook.tell(WhatIsTheAddressFor("7digital", ("command", keys, values)), testActor)


        "Future requests for that address return an actor ref pointing to the remote actor with the original command data" in {
            receiveOne(Duration(200, "ms")) match {
                case ref: ReferenceToAddress => {
                    ref.reference.path.toString must equal("akka://7dizzo@127.0.0.1:6612/user/7digital")
                    ref.command must equal(("command", keys, values))
                }
            }
        }

        rs.shutdown()
    }
}
