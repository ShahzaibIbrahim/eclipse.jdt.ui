package org.eclipse.jdt.internal.ui.text;

/*
 * Licensed Materials - Property of IBM,
 * WebSphere Studio Workbench
 * (c) Copyright IBM Corp 2000
 */
 
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jdt.ui.text.IJavaColorConstants;

/**
 * Java color manager.
 */
public class JavaColorManager implements IColorManager {	
	
	protected Map fColorTable= new HashMap(10);
	protected Map fKeyTable= new HashMap(10);
	
	
	public JavaColorManager() {
		initializeColorMapping();
	}
	
	protected void initializeColorMapping() {
		put(IJavaColorConstants.JAVA_MULTI_LINE_COMMENT, new RGB(42, 127, 170));
		put(IJavaColorConstants.JAVA_SINGLE_LINE_COMMENT, new RGB(42, 127, 170));
		put(IJavaColorConstants.JAVA_KEYWORD, new RGB(127, 0, 85));
		put(IJavaColorConstants.JAVA_TYPE, new RGB(127, 0, 85));
		put(IJavaColorConstants.JAVA_STRING, new RGB(42, 0, 255));
		put(IJavaColorConstants.JAVA_DEFAULT, new RGB(0, 0, 0));
		
		put(IJavaColorConstants.JAVADOC_KEYWORD, new RGB(127, 159, 95));
		put(IJavaColorConstants.JAVADOC_TAG, new RGB(127, 159, 95));
		put(IJavaColorConstants.JAVADOC_LINK, new RGB(159, 191, 95));
		put(IJavaColorConstants.JAVADOC_DEFAULT, new RGB(63, 127, 95));
	}
	
	public void put(String key, RGB rgb) {
		fKeyTable.put(key, rgb);
	}
		
	/**
	 * @see IColorManager#getColor(RGB)
	 */
	public Color getColor(RGB rgb) {
		
		if (rgb == null)
			return null;
			
		Color color= (Color) fColorTable.get(rgb);
		if (color == null) {
			color= new Color(Display.getCurrent(), rgb);
			fColorTable.put(rgb, color);
		}
		return color;
	}
	
	/**
	 * @see IColorManager#dispose
	 */
	public void dispose() {
		Iterator e= fColorTable.values().iterator();
		while (e.hasNext())
			((Color) e.next()).dispose();
	}

	/**
	 * @see IColorManager#getColor(String)
	 */
	public Color getColor(String key) {
		
		if (key == null)
			return null;
			
		RGB rgb= (RGB) fKeyTable.get(key);
		return getColor(rgb);
	}
}
