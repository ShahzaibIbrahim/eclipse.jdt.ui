/*
 * Licensed Materials - Property of IBM,
 * WebSphere Studio Workbench
 * (c) Copyright IBM Corp 2000
 */
package org.eclipse.jdt.internal.core.refactoring;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.refactoring.Change;
import org.eclipse.jdt.core.refactoring.IChange;
import org.eclipse.jdt.core.refactoring.ICompositeChange;

/**
 * Represents a composite change.
 */
public class CompositeChange extends Change implements ICompositeChange {

	private List fChanges;
	private IChange fUndoChange;
	
	public CompositeChange(){
		this(new ArrayList(5));
	}
	
	public CompositeChange(int initialCapacity){
		this(new ArrayList(initialCapacity));
	}
	
	private CompositeChange(List changes){
		fChanges= changes;
	}
	
	/* (Non-Javadoc)
	 * Method declared in IChange.
	 */
	public void aboutToPerform() {
		for (Iterator iter= fChanges.iterator(); iter.hasNext(); ) {
			((IChange)iter.next()).aboutToPerform();
		}
	}
	 
	/* (Non-Javadoc)
	 * Method declared in IChange.
	 */
	public void performed() {
		for (Iterator iter= fChanges.iterator(); iter.hasNext(); ) {
			((IChange)iter.next()).performed();
		}
	} 
	
	/**
	 * @see IChange#getUndoChange
	 */
	public final IChange getUndoChange(){
		return fUndoChange;
	}
	
	protected void setUndoChange(IChange change){
	   /*
	 	* subclasses may want to perform the undo 
	 	* smarter than the default implementation
	 	*/ 
		fUndoChange= change;
	}
	
	public void addChange(IChange change){
		fChanges.add(change);
	}
	
	public IChange[] getChildren() {
		if (fChanges == null)
			return null;
		return (IChange[])fChanges.toArray(new IChange[fChanges.size()]);
	}
	
	protected final List getChanges() {
		return fChanges;
	}
	
	/**
	 * to reverse a composite means reversing all changes in reverse order
	 */ 
	private List createUndoList(IProgressMonitor pm) throws JavaModelException{
		List undoList= new ArrayList(fChanges.size());
		Iterator iter= fChanges.iterator();
		pm.beginTask("", fChanges.size());
		while (iter.hasNext()){
			IChange each= (IChange)iter.next();
			each.perform(new SubProgressMonitor(pm, 1, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK));
			undoList.add(each.getUndoChange());
		};
		pm.done();
		Collections.reverse(undoList);
		return undoList;
	}

	/**
	 * @see IChange#perform
	 */
	public void perform(IProgressMonitor pm) throws JavaModelException{
		if (!isActive()){
			fUndoChange= new NullChange();
		} else{
			fUndoChange= new CompositeChange(createUndoList(pm));
		}	
	}
	
	public String toString(){
		StringBuffer buff= new StringBuffer();
		Iterator iter= fChanges.iterator();
		while (iter.hasNext()){
			buff.append("\t").append(iter.next().toString()).append("\n");
		};	
		return buff.toString();
	}
	
	
	public String getName(){
		return "Composite Change";
	}
	
	public IJavaElement getCorrespondingJavaElement(){
		return null;
	}
	
	/**
	 * @see IChange#setActive
	 * Apart setting the active/non-active status on itself 
	 * this method also activates/disactivates all subchanges of this change.
	 */
	public void setActive(boolean active){
		super.setActive(active);
		Iterator iter= fChanges.iterator();
		while (iter.hasNext()){
			((IChange)iter.next()).setActive(active);
		}
	}	
}