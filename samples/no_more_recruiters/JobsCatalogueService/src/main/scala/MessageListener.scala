import org.freedesktop.dbus.{DBusConnection, DBusInterfaceName, DBusInterface}
import scala.collection.mutable.ListBuffer
import scala.concurrent.{Future, ExecutionContext}
import ExecutionContext.Implicits.global

object MessageListener {

    def main(args: Array[String]) {
        val messages = new ListBuffer[(String, List[(String, String)])]()

        Future {
           listenForMessages(messages)
        }

        checkForMessages(messages)
    }

    def listenForMessages(messages: ListBuffer[(String, List[(String, String)])]) {
        // TODO - dbus stuff here
        val handler = new MessageHandler(messages)
        val bus = DBusConnection.getConnection(DBusConnection.SESSION)
        bus.requestBusName("akkesb.NoMoreRecruiters.JobsCatalogueService.Client")
        bus.exportObject("/commands/receiving", handler)

        def listen {
            Thread.sleep(1000)
            listen
        }

        listen
        // TODO - setup configs and create a distribution for the application
        // TODO - test on command line first
    }

    def checkForMessages(messages: ListBuffer[(String, List[(String, String)])]) {
        Thread.sleep(3000)
        if (!messages.isEmpty) {
            println(s"Messages: ${messages.mkString(",")}")
            messages.clear()
        }
        checkForMessages(messages)
    }
}

@DBusInterfaceName("akkesb.dbus.MessageHandler")
class MessageHandler(private val messages: ListBuffer[(String, List[(String, String)])]) extends DBusInterface {

    def isRemote: Boolean = false

    def handle(name: String, keys: Array[String], values: Array[String]) {
        println(s"Received message: ${name}")
        val tupes = for(i <- (0 to keys.length)) yield (keys(i), values(i))
        messages.append((name, tupes.toList))
    }
}