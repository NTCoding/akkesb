package akkesb.host

import akka.actor.{ActorRef, InvalidMessageException, Actor}
import akka.event.Logging
import akkesb.dbus.MessageHandler

class ServiceEndpoint(registrar: ActorRef, handler: MessageHandler) extends Actor {

    val log = Logging(context.system, this)

    def receive = {
        case SendRemoteMessages(toSend) => toSend foreach(x => sendRemoteMessage(x))

        case CommandHandlerRegistrations(service, commands) => registrar ! RegisterMultipleCommandsHandler(commands, service)

        case SendCommandToService(service, command) => service ! ProcessCommand(command)

        case ProcessCommand(command) => handler handle(command._1, command._2, command._3)

        case any => throw new UnsupportedOperationException(s"Service endpoint not configured to handle: $any")
    }

    def sendRemoteMessage(tuple: (ActorRef, AnyRef)) {
        val recipient = tuple._1
        val message = tuple._2
        log.info(s"Sending remote message: $message to $recipient")
        recipient ! message
    }
}
