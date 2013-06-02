package akkesb.host

case class CommandOwnedBy(owner: String, commandName: String, keys: Array[String], data: Array[String])
