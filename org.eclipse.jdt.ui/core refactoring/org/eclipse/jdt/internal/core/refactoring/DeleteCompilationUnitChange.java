/*
 * Licensed Materials - Property of IBM,
 * WebSphere Studio Workbench
 * (c) Copyright IBM Corp 2000
 */
package org.eclipse.jdt.internal.core.refactoring;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.refactoring.IChange;

public class DeleteCompilationUnitChange extends CompilationUnitChange {

	private String fCUHandle;
	
	public DeleteCompilationUnitChange(ICompilationUnit cu){
		super((IPackageFragment)cu.getParent(), cu.getElementName());
		fCUHandle= cu.getHandleIdentifier();
	}
	
	public String getName(){
		return "Delete Compilation Unit " + getCUName() + " from " + getPackageName();
	}
	
	public IJavaElement getCorrespondingJavaElement(){
		return JavaCore.create(fCUHandle);
	}
	
	public void perform(IProgressMonitor pm) throws JavaModelException {
		if (!isActive())
			return;
		pm.beginTask("deleting resource:" + getCUName(), 1);
		ICompilationUnit cu= (ICompilationUnit)JavaCore.create(fCUHandle);
		Assert.isNotNull(cu);
		Assert.isTrue(cu.exists());
		Assert.isTrue(!cu.isReadOnly());
		setSource(cu.getSource());
		cu.delete(true, pm);
		pm.done();
	}

	public IChange getUndoChange() {
		if (!isActive())
			return new NullChange();
		else	
			return new CreateCompilationUnitChange(getPackage(), getSource(), getCUName());
	}

}
