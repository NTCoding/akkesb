package akkesb.commands;

import org.freedesktop.dbus.Position;
import org.freedesktop.dbus.Struct;
import java.lang.String;

public final class DBusAkkesbCommand extends Struct
{

    public DBusAkkesbCommand() {
        super();
        this.data = "data";
        this.name = "name";
    }

    @Position(0)
    public final String name;

    @Position(1)
    public final String data;
}