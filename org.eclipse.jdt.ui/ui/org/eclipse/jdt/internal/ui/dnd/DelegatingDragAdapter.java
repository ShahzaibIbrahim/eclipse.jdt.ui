package org.eclipse.jdt.internal.ui.dnd;
/*
 * Licensed Materials - Property of IBM,
 * WebSphere Studio Workbench
 * (c) Copyright IBM Corp 1999, 2000
 */


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;

import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.ISelection;

public abstract class DelegatingDragAdapter implements DragSourceListener {

	private TransferDragSourceListener[] fPossibleListeners;
	private List fActiveListeners;
	private TransferDragSourceListener fFinishListener;
	
	public DelegatingDragAdapter(TransferDragSourceListener[] listeners) {
		Assert.isNotNull(listeners);
		fPossibleListeners= listeners;
	}

	/**
	 * Returns the selection forwarded to the managed drag source listeners.
	 * The <code>event.data</code> slot is used to pass the selection.
	 */
	public abstract ISelection getSelection();
	
	public void dragStart(DragSourceEvent event) {
		fFinishListener= null;
		boolean saveDoit= event.doit;
		Object saveData= event.data;
		boolean doIt= false;
		List transfers= new ArrayList(fPossibleListeners.length);
		fActiveListeners= new ArrayList(fPossibleListeners.length);
		event.data= getSelection();
		
		for (int i= 0; i < fPossibleListeners.length; i++) {
			TransferDragSourceListener listener= fPossibleListeners[i];
			event.doit= saveDoit;
			listener.dragStart(event);
			if (event.doit) {
				transfers.add(listener.getTransfer());
				fActiveListeners.add(listener);
			}
			doIt= doIt || event.doit;
		}
		if (doIt) {
			((DragSource)event.widget).setTransfer((Transfer[])transfers.toArray(new Transfer[transfers.size()]));
		}
		event.data= saveData;
		event.doit= doIt;
	}

	public void dragSetData(DragSourceEvent event){
		fFinishListener= getListener(event.dataType);
		if (fFinishListener != null)
			fFinishListener.dragSetData(event);
	}
	
	public void dragFinished(DragSourceEvent event){
		if (fFinishListener != null) {
			fFinishListener.dragFinished(event);
		} else {
			// If the user presses Escape then we get a dragFinished without
			// getting a dragSetDate before.
			fFinishListener= getListener(event.dataType);
			if (fFinishListener != null)
				fFinishListener.dragFinished(event);
		}
	}
	
	private TransferDragSourceListener getListener(TransferData type) {
		if (type == null)
			return null;
			
		for (Iterator iter= fActiveListeners.iterator(); iter.hasNext();) {
			TransferDragSourceListener listener= (TransferDragSourceListener)iter.next();
			if (listener.getTransfer().isSupportedType(type)) {
				return listener;
			}
		}
		return null;
	}	
}