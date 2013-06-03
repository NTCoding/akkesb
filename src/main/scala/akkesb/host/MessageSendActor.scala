package akkesb.host

import akka.actor.{ActorRef, Actor}

// would love to make these actor refs completely private, but can't test it otherwise
class MessageSendActor(val messageRegistrationsActor: ActorRef, val serviceActor: ActorRef) extends Actor {

    def receive =  {
        case send: SendCommand => messageRegistrationsActor ! WhoHandlesCommandRequest(send.name, send.keys, send.data)
        case ownedBy: CommandOwnedBy => serviceActor ! SendCommandToService(ownedBy.owner, ownedBy.commandName, ownedBy.keys, ownedBy.data)
    }
}
