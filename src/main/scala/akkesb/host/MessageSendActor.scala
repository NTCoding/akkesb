package akkesb.host

import akka.actor.{ActorRef, Actor}

// would love to make these actor refs completely private, but can't test it otherwise
class MessageSendActor(val messageRegistrationsActor: ActorRef, val serviceActor: ActorRef) extends Actor {

    // TODO - passing the command around keeps messages immutable and avoids saving state. Is that too inefficient though?
    def receive =  {
        case send: SendCommand => messageRegistrationsActor ! WhoHandlesCommand(send.name, send.keys, send.data)
        // lookup the owners address in the address book
        // send the message to the sender
        case ownedBy: CommandHandledBy => serviceActor ! SendCommandToService(ownedBy.handler, ownedBy.commandName, ownedBy.keys, ownedBy.values)
    }
}
