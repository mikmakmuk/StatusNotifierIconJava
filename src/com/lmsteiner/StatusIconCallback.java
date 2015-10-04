package com.lmsteiner;

public interface StatusIconCallback {
	public void ContextMenu(int x, int y);
	public void Activate(int x, int y);
	public void SecondaryActivate(int x, int y);
	public void Scroll(int delta, String orientation);
	
}
