package org.eclipse.jdt.internal.ui.search;

/*
 * Licensed Materials - Property of IBM,
 * WebSphere Studio Workbench
 * (c) Copyright IBM Corp 1999, 2000
 */
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import org.eclipse.search.ui.IGroupByKeyComputer;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.util.ExceptionHandler;

class GroupByKeyComputer implements IGroupByKeyComputer {

	IJavaElement fLastJavaElement= null;;
	String fLastHandle= null;;

	public Object computeGroupByKey(IMarker marker) {
		// no help from JavaModel to rename yet
		// return getJavaElement(marker);
		return getJavaElementHandleId(marker);
	}

	private String getJavaElementHandleId(IMarker marker) {
		try {
			return (String)marker.getAttribute(IJavaSearchUIConstants.ATT_JE_HANDLE_ID);
		} catch (CoreException ex) {
			ExceptionHandler.handle(ex, JavaPlugin.getResourceBundle(), "Search.Error.markerAttributeAccess.");
			return null;
		}
	}
	
	private IJavaElement getJavaElement(IMarker marker) {
		String handle= getJavaElementHandleId(marker);
		if (handle != null && !handle.equals(fLastHandle)) {
			fLastHandle= handle;
			fLastJavaElement= JavaCore.create(handle);
			IResource handleResource= null;
			try {
				if (fLastJavaElement != null)
					handleResource= fLastJavaElement.getCorrespondingResource();
			} catch (JavaModelException  ex) {
				ExceptionHandler.handle(ex, JavaPlugin.getResourceBundle(), "Search.Error.javaElementAccess.");
				// handleResource= null;
			}
			if (fLastJavaElement != null && marker.getResource().equals(handleResource)) {
				// need to get and set new handle here
			}
		}
		return fLastJavaElement;
	}
}