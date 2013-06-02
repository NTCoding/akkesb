package akkesb.host

import akka.actor.{ActorRef, Actor}

class MessageSendActor(private val messageRegistrationsActor: ActorRef, private val serviceActor: ActorRef) extends Actor {

    def receive =  {
        case send: SendCommand => messageRegistrationsActor ! WhoHandlesCommandRequest(send.name, send.keys, send.data)
        case ownedBy: CommandOwnedBy => serviceActor ! SendCommandToService(ownedBy.owner, ownedBy.commandName, ownedBy.keys, ownedBy.data)
    }

}
