package akkesb.host

case class RegisterCommandHandler(command: String, handlingService: String)

case class RegisterMultipleCommandsHandler(commands: List[String], handlingService: String)

