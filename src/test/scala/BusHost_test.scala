

import akkesb.commands.{DBusMessageSender, ActorDelegatingMessageHandler, MessageHandler, MessageSender}
import Host.BusHost
import org.freedesktop.dbus.DBusConnection
import org.junit._
import junit.framework.Assert._

import org.scalatest.FreeSpec

class BusHost_test {

    @Before
    def when_starting_up_the_bus_host {

    }

    @Test
    def command_inbox_interface_is_exported_onto_dbus_with_reference_to_a_command_handler_actor {
        assertFalse(true)
    }

    @Test
    def command_sender_interface_is_exported_onto_dbus_with_reference_to_a_command_sender_actor {
        assertFalse(true)
    }
}

class Bus_host_startup_spec extends FreeSpec  {

    val hostName = "funnyhostname"
    val port = "2892"
    val application = "funnyapplication"
    val service = "funnyservice"
    val handler = new ActorDelegatingMessageHandler()
    val sender = new DBusMessageSender()
    val connection = DBusConnection.getConnection(DBusConnection.SESSION)


    "When starting up the bus host" - {

          "it registers as a service on dbus using the supplied application and service name" in {

          }

          "it exports the supplied command handler on dbus in the messages/incoming path" in {

          }

          "it exports the supplied command sender on dbus in the messages/outgoing path" in {

          }

          BusHost(hostName, port, application, service, handler, sender, connection);
    }
}
