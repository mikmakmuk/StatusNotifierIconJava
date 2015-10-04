package com.lmsteiner;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.lmsteiner.StatusIcon;

public class StatusIconTest {
	private static File m_dest;
	
	@BeforeClass
	public static void runBeforeClass() {
		try {
			File src = new File(StatusIconTest.class.getResource("/data/test.png").toURI());
			m_dest = new File(System.getProperty("java.io.tmpdir") + File.separatorChar + src.getName());
			Files.copy(src.toPath(), m_dest.toPath());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@AfterClass
	public static void runAfterClass() {
		m_dest.delete();	
	}
	

	@Test
	public void testStatusIconCorrectCopyFile() {
		StatusIcon si = null;
		try {
			si = new StatusIcon("testA", m_dest);
		} 
		catch(IOException e1) {
			e1.printStackTrace();
			fail();
		}
		
		File src = m_dest;
		File dest = si.getIconfile();
		InputStream insrc = null;
		InputStream indest = null;
		
		assertTrue(dest.exists());
		assertTrue(src.length() == dest.length());
		
		try {
			insrc = new BufferedInputStream(new FileInputStream(src));
			indest = new BufferedInputStream(new FileInputStream(dest));
			
			int v1, v2;
			do {
				v1 = insrc.read();
				v2 = indest.read();
				assertTrue(v1 == v2);
			} while(v1 >= 0);
			
		} 
		catch(FileNotFoundException e) {
			e.printStackTrace();
			fail();
		} 
		catch(IOException e) {
			e.printStackTrace();
			fail();
		}
		finally {
			if(insrc != null) {
				try {
					insrc.close();
				} 
				catch(IOException e) {
					e.printStackTrace();
					fail();
				}
			}
			
			if(indest != null) {
				try {
					indest.close();
				} 
				catch(IOException e) {
					e.printStackTrace();
					fail();
				}
			}
		}
		
		assertTrue(si.getIconfile().delete());
		assertTrue(si.getTmpDir().delete());
	}
	
	@Test
	public void testStatusIconCorrectCopyStream() {
		FileInputStream fi = null;
		
		try {
			fi = new FileInputStream(m_dest);
		} 
		catch(FileNotFoundException e1) {
			e1.printStackTrace();
			fail();
		}
		
		StatusIcon si = null;
		try {
			si = new StatusIcon("testA", "png", fi);
		} 
		catch(IOException e1) {
			e1.printStackTrace();
			fail();
		}
		
		File src = m_dest;
		File dest = si.getIconfile();
		InputStream insrc = null;
		InputStream indest = null;
		
		assertTrue(dest.exists());
		assertTrue(src.length() == dest.length());
		
		try {
			insrc = new BufferedInputStream(new FileInputStream(src));
			indest = new BufferedInputStream(new FileInputStream(dest));
			
			int v1, v2;
			do {
				v1 = insrc.read();
				v2 = indest.read();
				assertTrue(v1 == v2);
			} while(v1 >= 0);
			
		} 
		catch(FileNotFoundException e) {
			e.printStackTrace();
			fail();
		} 
		catch(IOException e) {
			e.printStackTrace();
			fail();
		}
		finally {
			if(insrc != null) {
				try {
					insrc.close();
				} 
				catch(IOException e) {
					e.printStackTrace();
					fail();
				}
			}
			
			if(indest != null) {
				try {
					indest.close();
				} 
				catch(IOException e) {
					e.printStackTrace();
					fail();
				}
			}
		}
		
		assertTrue(si.getIconfile().delete());
		assertTrue(si.getTmpDir().delete());
	}

	@Test
	public void testGetIconIdentifier() {
		StatusIcon si = null;
		try {
			si = new StatusIcon("testA", m_dest);
		} 
		catch(IOException e) {
			e.printStackTrace();
			fail();
		}
		assertEquals("testA", si.getIconIdentifier());
		
		assertTrue(si.getIconfile().delete());
		assertTrue(si.getTmpDir().delete());
	}
	
	@Test(expected = IOException.class)
	public void testStatusIconFiledoesnotexist() throws IOException {
		File f = new File("doesnotexist");
		StatusIcon si = new StatusIcon("testA", f);
	}
}
