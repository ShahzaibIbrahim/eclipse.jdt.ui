package org.eclipse.jdt.internal.ui.wizards.dialogfields;/*
 * Licensed Materials - Property of IBM,
 * WebSphere Studio Workbench
 * (c) Copyright IBM Corp 1999, 2000
 */


public interface IListAdapter {
	
	void customButtonPressed(DialogField field, int index);
	void selectionChanged(DialogField field);

}