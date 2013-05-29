package akkesb.queries

import org.freedesktop.dbus.{Tuple, DBusInterface}

trait Get extends DBusInterface {

      def nextCommand : Tuple
}



