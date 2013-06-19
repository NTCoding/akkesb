package akkesb.host

import akka.kernel.Bootable
import akkesb.dbus.{AkkesbDBusConnection, TestableDBusConnection, DBusMessageSender, DBusMessageHandler}
import org.freedesktop.DBus
import org.freedesktop.dbus.DBusConnection
import com.typesafe.config.{Config, ConfigFactory}
import scala.collection.immutable.IndexedSeq
import java.io.File
import java.net.URLDecoder


class AkkesbStarter extends Bootable {

    def startup() {
        val pathToFile = getClass.getProtectionDomain.getCodeSource.getLocation.getPath
        val decoded = URLDecoder.decode(pathToFile, "UTF-8")
        val confPath = new File(new File(decoded).getParentFile.getParentFile.getPath, "akkesb.conf")
        val file = new File(confPath.getPath)
        if (!file.exists()) throw new UnsupportedOperationException("akkesb.conf does not exist. It should live in the \"akkesb\" folder")

        val config = ConfigFactory.load(ConfigFactory.parseFile(file))
        val hostName = config.getString("akkesb.hostname")
        val port = config.getString("akkesb.port")
        val application = config.getString("akkesb.systemName")
        val service = config.getString("akkesb.serviceName")
        val commands = parseCommands(config)
        val otherNodes = parseOtherNodes(config)
        val connection =  new AkkesbDBusConnection(DBusConnection.getConnection(DBusConnection.SESSION))
        val handler = new DBusMessageHandler(application, service, connection)

        val bus = BusHost(hostName, port, application, service, handler ,new DBusMessageSender, connection, new RemoteActorSystemCreator)
        bus willHandleCommands(commands)
        bus joinCluster(otherNodes)
    }

    def parseCommands(config: Config): List[String] = {
        val x = config.getStringList("akkesb.commandsToHandle")
        (0 to (x.size -1)).map(x.get(_)).toList
    }

    def parseOtherNodes(config: Config) = {
        val x = config.getStringList("akkesb.otherServices")
        (0 to (x.size - 1))
            .map(x.get(_).split(","))
            .map(array => (array(0), array(1), array(2)))
            .toList
    }

    def shutdown() {
        sys.exit()
    }
}
