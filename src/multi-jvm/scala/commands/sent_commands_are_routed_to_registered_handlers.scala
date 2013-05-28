package commands

import Host.BusHost
import utils._

object CommandsAreSentToRegisteredHandlers_MultiJvm_MarketingServiceHost {

    def main(args: Array[String]) {
        // TODO hostname and port should be passed in as args - come back to this later
        val host = new BusHost("127.0.0.1", "3051","commands_are_sent_test", "marketing_service")
        host willSendCommands List( ("update_price") )
        host joinCluster
    }

}

object CommandsAreSentToRegisteredHandlers_MultiJvm_CatalogueServiceHost {

    def main(args: Array[String]) {
        val host = new BusHost("127.0.0.1", "3052", "commands_are_sent_test", "catalogue_service")
        host willSendCommands List("stop_taking_payments_for_product")
        host willHandleCommands List("update_price")
        host joinCluster
    }
}


object CommandsAreSentToRegisteredHandlers_MultiJvm_PaymentsServiceHost {

    def main(args: Array[String]) {
        val host = new BusHost("127.0.0.1", "3053", "commands_are_sent_test", "payments_service")
        host willHandleCommands List("stop_taking_payments_for_product")
        host joinCluster
    }
}

object CommandsAreSentToRegisteredHandlers_MultiJvm_TestsAndAssertions {

    def main(args: Array[String]) {

        Testing.sendCommand("commands_are_sent_test", ("update_price", List(("productId", 1), ("price", 50))))
               .via("marketing_service")

        Testing.assertService("commands_are_sent_test","catalogue_service")
               .receivedCommand(("update_price", List(("productId", 1), ("price", 50))))

        Testing.sendCommand("commands_are_sent_test", ("stop_taking_payments_for_product", List(("productId", 1))))
               .via("catalogue_service")

        Testing.assertService("commands_are_sent_test","payments_service")
               .receivedCommand(("stop_taking_payments_for_product", List(("productId", 1))))
    }

}
