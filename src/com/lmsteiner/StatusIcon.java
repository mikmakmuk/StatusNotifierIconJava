package com.lmsteiner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

public class StatusIcon {
	private String m_id;
	private File m_tmppath;
	private File m_tmpiconpath;
	
	public StatusIcon(String identifier, File iconfile) throws IOException {
		m_id = identifier;
		
		if(!iconfile.exists()) {
			String msg = "File does not exist: " + iconfile.getPath();
			throw new FileNotFoundException(msg);
		}
		
		createTmpPath();
			
		m_tmpiconpath = new File(m_tmppath, iconfile.getName());
		Files.copy(iconfile.toPath(), m_tmpiconpath.toPath());
	
	}
	
	public StatusIcon(String identifier, String extension, InputStream is) throws IOException {
		m_id = identifier;
		OutputStream os = null;
		
		if(is == null) {
			String msg = "Icon resource does not exist.";
			throw new FileNotFoundException(msg);
		}
		
		createTmpPath();
		
		
		m_tmpiconpath = new File(m_tmppath, identifier + "." + extension);
		os = new FileOutputStream(m_tmpiconpath);
			
		int read = 0;
		byte[] bytes = new byte[1024];
		
		try {
			while((read = is.read(bytes)) != -1) {
				os.write(bytes, 0, read);
			}
		}
		catch(IOException e) {
			String msg = "Could not copy icon file from stream.\n" +  
					"Destination: " + m_tmpiconpath.getPath() + "\n" +
					"Reason: " + e.getCause();
			
			throw new IOException(msg);
		}
		finally {
			if(os != null) {
				try {
					os.close();
				}
				catch(IOException e) {
					String msg = "Could not close output stream.\nReason" + e.getCause();
					throw new IOException(msg);
				}
			}
			
			try {
				is.close();
			}
			catch(IOException e) {
				String msg = "Could not close input stream.\nReason" + e.getCause();
				throw new IOException(msg);
			}
		}
	}
	
	public String getIconIdentifier() {
		return m_id;
	}
	
	public File getIconfile() {
		return m_tmpiconpath;
	}
	
	public File getTmpDir() {
		return m_tmppath;
	}
	
	private void createTmpPath() throws IOException {
		String tmpdir = System.getProperty("java.io.tmpdir");
		m_tmppath = new File( tmpdir + File.separatorChar + "snij-" + SNIJ.getPID() + "-" + SNIJ.getInstanceID());
		
		if(!m_tmppath.exists())
			Files.createDirectory(m_tmppath.toPath());
	}
}
