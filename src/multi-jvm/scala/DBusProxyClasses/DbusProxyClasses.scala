
package akkesb.commands  {

    import org.freedesktop.dbus.{Variant, Tuple, DBusInterface, DBusMap}

    trait Inbox extends DBusInterface {

        def addCommand(command: ThreeTuple[String, Array[String], Array[String]])

        def nextMessage : ThreeTuple[String, Array[String], Array[String]]
    }

    final class ThreeTuple[A, B, C](val first: A, val second: B, val third: C) extends Tuple { }
}




