package akkesb.host

import akka.actor.Actor

class AddressBookActor extends Actor {

    def receive = {
        case _ => throw new Exception("Address book actor not configured to handle any messages")
    }
}
