package akkesb.host

import akka.actor.{Address, Actor}

class AddressBookActor(val application: String) extends Actor {

    var addressBook = Map[String, (String, String)]()
    // TODO - what about when this service goes down?
    // TODO - should the address book always be in memory if the system grows massive?
    def receive = {
        case AddAddress(service, hostname, port) => addressBook = addressBook + (service -> (hostname, port))

        case WhatIsTheAddressFor(service, command) =>  sender ! ReferenceToAddress(actorRefTo(service), command)

        case GiveMeAReferenceToAllServiceAddresses(state) => sender !  AllServices(addressBook.keys.map(actorRefTo).toList, state)
    }


    def actorRefTo(service: String) = {
        val address = addressBook.get(service).get
        val hostname = address._1
        val port = address._2
        context.actorFor(s"akka://$application@$hostname:$port/user/$service")
    }


}
