package akkesb.host

import akka.actor.{ActorRef, Actor}

// would love to make these actor refs completely private, but can't test it otherwise
class MessageSendActor(val registrar: ActorRef, val serviceFacade: ActorRef, val addressBook: ActorRef) extends Actor {

    // TODO - passing the command around keeps messages immutable and avoids saving state. Is that too inefficient though?
    def receive =  {
        case send: SendCommand => registrar ! WhoHandlesCommand(send.name, send.keys, send.data)
        case ownedBy: CommandHandledBy => addressBook ! WhatIsTheAddressFor(ownedBy.handler, (ownedBy.commandName, ownedBy.keys, ownedBy.values))
        case address: ReferenceToAddress => serviceFacade ! SendCommandToService(address.reference, address.state)
    }
}
