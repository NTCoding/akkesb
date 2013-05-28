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

    def via(application: String, service: String) {
        DBus.Send(f"akkesb.$application", )
    }
}

class Service(val application: String, val name: String) {

    def receivedCommand(message: (String, List[(String, _)])) {

    }

}


