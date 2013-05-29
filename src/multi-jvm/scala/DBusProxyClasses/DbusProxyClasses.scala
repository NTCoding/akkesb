package akkesb.queries {

    import org.freedesktop.dbus.{Tuple, DBusInterface}

    trait Get extends DBusInterface {

          def nextCommand : Tuple
    }

}

package akkesb.inbound {

    import org.freedesktop.dbus.{Tuple, DBusInterface}

    trait Inbox extends DBusInterface {

        def addCommand(command: Tuple)
    }
}



