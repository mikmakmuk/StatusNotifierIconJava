package org.kde;

import org.freedesktop.dbus.DBusInterface;

public interface StatusNotifierWatcher extends DBusInterface {
	public void RegisterStatusNotifierItem(String service);
}
