
package akkesb.commands  {

    import org.freedesktop.dbus.{Variant, Tuple, DBusInterface, DBusMap}

    trait Inbox extends DBusInterface {

        def addCommand(command: TwoTuple[String, String])

        def nextMessage : TwoTuple[String, String]
    }

    final class TwoTuple[A, B](val first: A, val second: B) extends Tuple { }
}




