package akkesb.commands;

import org.freedesktop.dbus.Position;
import org.freedesktop.dbus.Struct;
import org.freedesktop.dbus.Tuple;

import java.lang.String;

public final class DBusAkkesbCommand extends Struct
{

    @Position(0)
    public final String name;

    @Position(1)
    public final String[] keys;

    @Position(2)
    public final String[] values;

    public DBusAkkesbCommand(String name, String[] keys, String[] values) {
        super();
        this.name = name;
        this.keys = keys;
        this.values = values;
    }
}