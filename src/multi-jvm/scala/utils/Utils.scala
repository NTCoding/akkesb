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
        DBus.send(f"akkesb.$application")
    }
}

class Service(val application: String, val name: String) {

    def receivedCommand(command: (String, List[(String, _)])) {
         throw new Exception(f"command: '${command._1}' was not received by $application.$name")
    }

}


