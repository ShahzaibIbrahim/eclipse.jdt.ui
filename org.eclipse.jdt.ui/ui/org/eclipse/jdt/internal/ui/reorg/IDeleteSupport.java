/*
 * Licensed Materials - Property of IBM,
 * WebSphere Studio Workbench
 * (c) Copyright IBM Corp 1999, 2000
 */

package org.eclipse.jdt.internal.ui.reorg;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.jdt.core.JavaModelException;

/**
 * Abstraction layer for deleting.
 */
public interface IDeleteSupport {
	boolean canDelete(Object o);
	void delete(Object o, IProgressMonitor pm) throws JavaModelException, CoreException;
	/**
	 * @returns the segment count of the path of the underlying resource (or 0 if the
	 		path length can't be determined.
	 */
	int getPathLength(Object o);
}