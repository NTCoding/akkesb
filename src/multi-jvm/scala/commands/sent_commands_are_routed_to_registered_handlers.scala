package commands

import org.junit._
import junit.framework.Assert._


object CommandsAreSentToRegisteredHandlers_MultiJvm_MarketingServiceHost {

    def main(args: Array[String]) {
        // TODO hostname and port should be passed in as args - come back to this later
        val host = new BusHost("127.0.0.1", "3051", "marketing_service")
        host willSendCommands List( ("update_price") )
        host joinCluster
    }

}

object CommandsAreSentToRegisteredHandlers_MultiJvm_CatalogueServiceHost {

    def main(args: Array[String]) {
        val host = new BusHost("127.0.0.1", "3052", "catalogue_service")
        host willSendCommands List( ("stop_taking_payments_for_product") )
        host willHandleCommands List ( ("update_price") )
        host joinCluster
    }
}


object CommandsAreSentToRegisteredHandlers_MultiJvm_PaymentsServiceHost {

    def main(args: Array[String]) {
        val host = new BusHost("127.0.0.1", "3053", "payments_service")
        host willHandleCommands List ( ("stop_taking_payments_for_product") )
        host joinCluster
    }
}

class Commands_are_sent_to_registered_handlers_only_tests {

    @Test
    def marketing_service_update_price_commands_are_handled_by_the_catalogue_service {
        // register a handler with dbus for catalogue service messages

        // send the message through marketing service

        // wait 5 seconds

        // assert dbus sent the message
        assertTrue(false)
    }

    @Test
    def catalogue_service_stop_taking_payments_commands_are_handled_by_the_payments_service {
        assertTrue(false)
    }
}
