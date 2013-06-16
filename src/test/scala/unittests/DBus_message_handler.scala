package unittests

import akkesb.dbus.{MessageHandler, TestableDBusConnection, DBusMessageHandler}
import org.mockito.Mockito.{when, verify}

class DBus_message_handler extends TestBaseClassWithJunitRunnerAndTestKit {

    "When the DBus message handler is told to handle a command" - {

        val connection = mock[TestableDBusConnection]
        val handlerProxy = mock[MessageHandler]
        val keys = Array("shopId")
        val values = Array("34")
        val application = "dbushandlertest"
        val service = "diggiwhizz_service"

        when(connection.getRemoteObject(s"akkesb.$application.$service.Client", "/commands/receiving", classOf[MessageHandler]))
        .thenReturn(handlerProxy, null)

        new DBusMessageHandler(application, service, connection).handle("command", keys, values)


        "It sends the message over dbus to the client library" in {
            verify(handlerProxy) handle("command", keys, values)
        }
    }
}
