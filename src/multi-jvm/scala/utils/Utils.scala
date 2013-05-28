package utils

object Testing {

    def sendCommand(command: (String,List[(String, _)])) = {
        new Command(command)
    }

    def assertService(service: String) = {
        new Service(service)
    }

}

class Command(val command: (String, List[(String, _)])) {

    def via(service: String) {

    }
}

class Service(val name: String) {

    def receivedCommand(message: (String, List[(String, _)])) {

    }

}


