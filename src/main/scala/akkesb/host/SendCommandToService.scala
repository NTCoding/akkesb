package akkesb.host

case class SendCommandToService(service: String, commandName: String, keys: Array[String], values: Array[String])
