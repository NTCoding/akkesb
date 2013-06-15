package unittests

import akka.testkit.{TestActorRef, TestKit}
import akka.actor.{Props, ActorSystem}
import org.scalatest.FreeSpecLike
import akkesb.host.{ReferenceToAddress, WhatIsTheAddressFor, AddAddress, AddressBookActor}
import scala.concurrent.duration.Duration
import org.scalatest.MustMatchers

class Address_book_actor_spec extends TestKit(ActorSystem("TestActorSystem989")) with FreeSpecLike with MustMatchers with StopSystemAfterAll{

    val keys = Array(1, 2, 3)
    val values = Array(1, 2, 3)

    "When telling the address book to store an address" - {

        val addressBook = TestActorRef(Props(()  => new AddressBookActor("testApplication")))
        addressBook ! AddAddress("7digital", "456.289.33.33", "8876")
        addressBook.tell(WhatIsTheAddressFor("7digital", ("command", keys, values)), testActor)

        "Future requests for that address return an actor ref pointing to the remote actor with the original command data" in {
            receiveOne(Duration(200, "ms")) match {
                case ref: ReferenceToAddress => {
                    ref.reference.path.address must equal("akka://testApplicatione@456.289.33.33:8876/user/7digital")
                    ref.
                }
            }
        }
    }
}
