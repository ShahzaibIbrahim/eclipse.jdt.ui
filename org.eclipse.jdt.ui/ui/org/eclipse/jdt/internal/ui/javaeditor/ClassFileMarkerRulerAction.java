package org.eclipse.jdt.internal.ui.javaeditor;

/*
 * Licensed Materials - Property of IBM,
 * WebSphere Studio Workbench
 * (c) Copyright IBM Corp 1999, 2000
 */

import java.util.Map;
import java.util.ResourceBundle;

import org.eclipse.jface.text.source.IVerticalRuler;

import org.eclipse.core.resources.IResource;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.MarkerRulerAction;

import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import org.eclipse.jdt.internal.ui.IResourceLocator;


class ClassFileMarkerRulerAction extends MarkerRulerAction {
	
	
	public ClassFileMarkerRulerAction(ResourceBundle bundle, String prefix, IVerticalRuler ruler, ITextEditor editor, String markerType, boolean askForLabel) {
		super(bundle, prefix, ruler, editor, markerType, askForLabel);
	}
	
	/**
	 * @see MarkerRulerAction#getResource()
	 */
	protected IResource getResource() {
		
		IResource resource= null;
		
		IEditorInput input= getTextEditor().getEditorInput();
		if (input instanceof ClassFileEditorInput) {
			IClassFile c= ((ClassFileEditorInput) input).getClassFile();
			IResourceLocator locator= (IResourceLocator) c.getAdapter(IResourceLocator.class);
			if (locator != null) {
				try {
					resource= locator.getContainingResource(c);
				} catch (JavaModelException x) {
					// ignore but should inform
				}
			}
		}
		
		return resource;
	}
	
	
	/**
	 * @see MarkerRulerAction#getInitialAttributes()
	 */
	protected Map getInitialAttributes() {
		
		Map attributes= super.getInitialAttributes();
		
		IEditorInput input= getTextEditor().getEditorInput();
		if (input instanceof ClassFileEditorInput) {
			IClassFile classFile= ((ClassFileEditorInput) input).getClassFile();
			JavaCore.addJavaElementMarkerAttributes(attributes, classFile);
		}
		
		return attributes;
	}
}