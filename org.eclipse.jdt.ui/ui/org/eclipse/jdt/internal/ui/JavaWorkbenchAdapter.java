package org.eclipse.jdt.internal.ui;
/*
 * Licensed Materials - Property of IBM,
 * WebSphere Studio Workbench
 * (c) Copyright IBM Corp 1999, 2000
 */


import org.eclipse.jface.resource.ImageDescriptor;

import org.eclipse.ui.model.IWorkbenchAdapter;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IParent;
import org.eclipse.jdt.core.JavaModelException;

import org.eclipse.jdt.internal.ui.util.ArrayUtility;
import org.eclipse.jdt.internal.ui.util.JdtPortingFinder;

/**
 * An imlementation of the IWorkbenchAdapter for IJavaElements.
 */
  
public class JavaWorkbenchAdapter implements IWorkbenchAdapter {
	
	public Object[] getChildren(Object o) {
		if (o instanceof IParent) {
			try {
				return ((IParent)o).getChildren();
			} catch(JavaModelException e) {
				// fall through
			}
		}
		return ArrayUtility.getEmptyArray();
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		JdtPortingFinder.toBeDone("1G5XYUD: ITPUI:WIN2000 - DCR - Need a generic way to access the label and image of an element");
		return null;
	}

	public String getLabel(Object o) {
		if (o instanceof IJavaElement)
			return ((IJavaElement)o).getElementName();
		return null;
	}

	public Object getParent(Object o) {
		if (o instanceof IJavaElement)
			return ((IJavaElement)o).getParent();
		return null;
	}
}