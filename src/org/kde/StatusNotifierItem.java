package org.kde;


import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.DBusSignal;
import org.freedesktop.dbus.exceptions.DBusException;

public interface StatusNotifierItem extends DBusInterface {
	
	//METHODS
	public void ContextMenu(int x, int y);
	public void Activate(int x, int y);
	public void SecondaryActivate(int x, int y);
	public void Scroll(int delta, String orientation);
	
	//SIGNALS
	public static class NewTitle extends DBusSignal {
		public NewTitle(String path) throws DBusException {
			super(path);
	    }
	}
	
	public static class NewIcon extends DBusSignal {
		public NewIcon(String path) throws DBusException {
			super(path);
	    }
	}
	   
	public static class NewAttentionIcon extends DBusSignal {
		public NewAttentionIcon(String path) throws DBusException {
			super(path);
	    }
	}
	   
	public static class NewOverlayIcon extends DBusSignal {
		public NewOverlayIcon(String path) throws DBusException {
	         super(path);
	    }
	}
	   
	public static class NewToolTip extends DBusSignal {
		public NewToolTip(String path) throws DBusException {
	         super(path);
	    }
	}
	
	public static class NewStatus extends DBusSignal {
		public final String status;
	      
		public NewStatus(String path, String status) throws DBusException {
	         super(path, status);
	         this.status = status;
	    }
	}
}
