package akkesb.queries

import org.freedesktop.dbus.{Variant, Tuple, DBusInterface}

trait Get extends DBusInterface {

      def nextCommand : Tuple[String, Array[Tuple[String, Variant]]]
}



