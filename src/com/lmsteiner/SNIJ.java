package com.lmsteiner;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.freedesktop.DBus.Properties;
import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.UInt32;
import org.freedesktop.dbus.Variant;
import org.freedesktop.dbus.exceptions.DBusException;
import org.kde.StatusNotifierItem;
import org.kde.StatusNotifierWatcher;

public class SNIJ implements StatusNotifierItem, Properties {
	public static final String PROP_ID = "Id";
	public static final String PROP_TITLE = "Title";
	public static final String PROP_CATEGORY = "Category";
	public static final String PROP_STATUS = "Status";
	public static final String PROP_WINDOWID = "WindowId";
	public static final String PROP_ICONNAME = "IconName";
	public static final String PROP_ICONPIXMAP = "IconPixmap";
	public static final String PROP_OVERLAYICONNAME = "OverlayIconName";
	public static final String PROP_OVERLAYICONPIXMAP = "OverlayIconPixmap";
	public static final String PROP_ATTENTIONICONNAME = "AttentionIconName";
	public static final String PROP_ATTENTIONICONPIXMAP = "AttentionIconPixmap";
	public static final String PROP_ATTENTIONMOVIENAME = "AttentionMovieName";
	public static final String PROP_TOOLTIP = "ToolTip";
	
	public static final String CAT_APPLICATIONSTATUS = "ApplicationStatus";
	public static final String CAT_COMMUNICATIONS = "Communications";
	public static final String CAT_SYSTEMSERVICES = "SystemServices";
	public static final String CAT_HARDWARE = "Hardware";
	
	public static final String STATUS_ACTIVE = "Active";
	public static final String STATUS_PASSIVE = "Passive";
	public static final String STATUS_NEEDSATTENTION = "NeedsAttention";

	
	private final String WATCHER_BUSNAME = "org.kde.StatusNotifierWatcher";
	private final String WATCHER_OBJECTPATH = "/StatusNotifierWatcher";
	private final String SNIJ_BUSNAME = "com.lmsteiner.SNIJ";
	private final String SNIJ_OBJECTPATH = "/StatusNotifierItem";
	
	private String m_serviceName;
	private String m_objectPath;
	private Map<String, Variant> m_properties;
	private Map<String, StatusIcon> m_icons;
	private String m_currentIcon;
	private StatusIconCallback m_cb;
	private DBusConnection m_conn;
	private static int m_id = 0;
	
	public SNIJ(String id, StatusIcon defaultIcon, String category) throws SNIJException {
		m_serviceName = SNIJ_BUSNAME + "-" + getPID() + "-" + ++m_id;
		m_objectPath = SNIJ_OBJECTPATH;
		
		m_properties = new HashMap<String,Variant>();
		m_properties.put(PROP_ID, new Variant<String>(id));
		m_properties.put(PROP_CATEGORY, new Variant<String>(category));
		m_properties.put(PROP_TITLE, new Variant<String>(id));
		m_properties.put(PROP_STATUS, new Variant<String>(STATUS_ACTIVE));
		
		m_icons = new Hashtable<String, StatusIcon>();
		m_icons.put(defaultIcon.getIconIdentifier(), defaultIcon);
		m_properties.put(PROP_ICONNAME, new Variant<String>(defaultIcon.getIconfile().getAbsolutePath()));
		m_currentIcon = defaultIcon.getIconIdentifier();
		
		try {
			m_conn = DBusConnection.getConnection(DBusConnection.SESSION);
			m_conn.requestBusName(m_serviceName);
			m_conn.exportObject(m_objectPath, this);	
			
			StatusNotifierWatcher watcher = 
					m_conn.getRemoteObject(WATCHER_BUSNAME, 
				WATCHER_OBJECTPATH, StatusNotifierWatcher.class);
			
				watcher.RegisterStatusNotifierItem(m_serviceName);
		}
		catch(DBusException e) {
			String msg = "Dbus error.\nReason: " + e.getCause();
			throw new SNIJException(msg);
		}
	}
	
	public static long getPID() {
	    String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
	    return Long.parseLong(processName.split("@")[0]);
	}
	
	public static int getInstanceID() {
		return m_id;
	}

	
	@Override
	public boolean isRemote() {
		return false;
	}
	
	public void addIcon(StatusIcon icon) {
		m_icons.put(icon.getIconIdentifier(), icon);
	}
	
	public void setIcon(String identifier) throws SNIJException {
		StatusIcon icon = m_icons.get(identifier);
		
		Variant<String> oldval = m_properties.get(PROP_ICONNAME);
		m_properties.put(PROP_ICONNAME, new Variant<String>(icon.getIconfile().getAbsolutePath()));
		try {
			m_conn.sendSignal(new NewIcon(m_objectPath));
			m_currentIcon = identifier;
		}
		catch(DBusException e) {
			m_properties.put(PROP_ICONNAME, oldval);
			throw new SNIJException("Icon could not be set.\nReason: " + e.getCause());
		}
	}
	
	public void setTitle(String s) throws SNIJException {
		Variant<String> oldval = m_properties.get(PROP_TITLE);
		m_properties.put(PROP_TITLE, new Variant<String>(s));
		
		try {
			m_conn.sendSignal(new StatusNotifierItem.NewTitle(m_objectPath));
		}
		catch(DBusException e) {
			m_properties.put(PROP_TITLE, oldval);
			throw new SNIJException("Title could not be set.\nReason: " + e.getCause());
		}
	}
	
	
	public void setState(String s) throws SNIJException {
		Variant<String> oldval = m_properties.get(PROP_STATUS);
		m_properties.put(PROP_STATUS, new Variant<String>(s));
		
		try {
			m_conn.sendSignal(new StatusNotifierItem.NewStatus(m_objectPath, s));
		}
		catch(DBusException e) {
			m_properties.put(PROP_STATUS, oldval);
			throw new SNIJException("State could not be set.\nReason: " + e.getCause());
		}
	}
	
	public void setWindowId(long id) {
		m_properties.put(PROP_WINDOWID, new Variant<UInt32>(new UInt32(id)));
	}
	
	public String getCurrentIcon() {
		return m_currentIcon;
	}
	
	public void setIconCallBack(StatusIconCallback cb) {
		m_cb = cb;
	}
	
	public void cleanup() throws IOException {
		File tmppath = m_icons.get(m_currentIcon).getTmpDir();
		
		for(Map.Entry<String, StatusIcon> entry : m_icons.entrySet()) {
			StatusIcon icon = entry.getValue();
			Files.delete(icon.getIconfile().toPath());
		}
		
		Files.delete(tmppath.toPath());
	}
	
	
	//METHODS
	@Override
	public void ContextMenu(int x, int y) {
		m_cb.ContextMenu(x, y);	
	}

	@Override
	public void Activate(int x, int y) {
		m_cb.Activate(x, y);
	}

	@Override
	public void SecondaryActivate(int x, int y) {
		m_cb.SecondaryActivate(x, y);		
	}

	@Override
	public void Scroll(int delta, String orientation) {
		m_cb.Scroll(delta, orientation);
	}

	@Override
	public Variant Get(String arg0, String arg1) {
		return m_properties.get(arg1);
	}

	@Override
	public Map<String, Variant> GetAll(String arg0) {
		return m_properties;
	}

	@Override
	public <A> void Set(String arg0, String arg1, A arg2) {
		// TODO Auto-generated method stub
		
	}
}
