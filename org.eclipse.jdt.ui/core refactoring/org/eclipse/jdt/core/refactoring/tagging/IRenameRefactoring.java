/*
 * Licensed Materials - Property of IBM,
 * WebSphere Studio Workbench
 * (c) Copyright IBM Corp 1999, 2000
 */
package org.eclipse.jdt.core.refactoring.tagging;

import org.eclipse.jdt.core.refactoring.RefactoringStatus;

/**
 * Represents a refactoring that renames an <code>IJavaElement</code>.
 * <p>
 * <bf>NOTE:<bf> This class/interface is part of an interim API that is still under development 
 * and expected to change significantly before reaching stability. It is being made available at 
 * this early stage to solicit feedback from pioneering adopters on the understanding that any 
 * code that uses this API will almost certainly be broken (repeatedly) as the API evolves.</p>
 */
public interface IRenameRefactoring {
	
	/**
	 * Sets new name for the entity that this refactoring is working on.
	 * This name is then validated in <code>checkNewName</code>.
	 */
	public void setNewName(String newName);
	
	
	/**
	 * Checks if the new name (set in <code>setNewName</code>) is valid for
	 * the entity that this refactoring renames.
	 */
	public RefactoringStatus checkNewName();
}