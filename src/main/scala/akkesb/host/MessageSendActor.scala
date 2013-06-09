package akkesb.host

import akka.actor.{ActorRef, Actor}

// would love to make these actor refs completely private, but can't test it otherwise
class MessageSendActor(val messageRegistrationsActor: ActorRef, val serviceActor: ActorRef) extends Actor {

    def receive =  {
        case send: SendCommand => messageRegistrationsActor ! WhoHandlesCommand(send.name, send.keys, send.data)
        case ownedBy: CommandHandledBy => serviceActor ! SendCommandToService(ownedBy.handler, ownedBy.commandName, ownedBy.keys, ownedBy.values)
    }
}
