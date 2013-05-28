package utils

object Testing {

    def sendCommand(application: String, command: (String,List[(String, _)])) = {
        new Command(application, command)
    }

    def assertService(application: String, service: String) = {
        new Service(application, service)
    }

}

class Command(val application: String, val command: (String, List[(String, _)])) {

    def via(service: String) {
        DBus.invoke(f"akkesb.$application")
    }
}

class Service(val application: String, val name: String) {

    def receivedCommand(command: (String, List[(String, _)])) {

        val lastCommand = DBus.invoke(f"akkesb.$application.$name", "/queries", "akkesb.queries.Get", "lastReceivedCommand")

        lastCommand match {
            case anyCommand: (String, List[(String, _)]) => assertIdenticalCommand(anyCommand, command)
            case _  => fail(command)
        }

    }

    def assertIdenticalCommand(actual: (String, List[(String, _)]), expected: (String, List[(String, _)])) {
        if (actual != expected) fail(expected)
    }

    def fail(command: (String, List[(String, _)])) {
        throw new Exception(f"command: '${command._1}' was not received by $application.$name")
    }

}

object DBus {

    def invoke(dbusService: String, path: String, interface: String, method: String) = {

    }
}


