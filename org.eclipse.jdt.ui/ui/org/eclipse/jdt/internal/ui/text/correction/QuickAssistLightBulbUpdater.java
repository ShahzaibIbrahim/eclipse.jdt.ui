/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.ui.text.correction;

import java.util.Iterator;

import org.eclipse.core.resources.IFile;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IPostSelectionProvider;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.MarkerAnnotation;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;

import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.PreferenceConstants;

import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.javaeditor.IJavaAnnotation;
import org.eclipse.jdt.internal.ui.javaeditor.JavaAnnotationIterator;

/**
 *
 */
public class QuickAssistLightBulbUpdater {

	private static class AssistAnnotation extends Annotation {
		
		private Image fImage;
		
		public AssistAnnotation() {
			setLayer(MarkerAnnotation.PROBLEM_LAYER + 1);
		}
		
		private Image getImage() {
			if (fImage == null) {
				fImage= JavaPluginImages.get(JavaPluginImages.IMG_OBJS_QUICK_ASSIST);
			}
			return fImage;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.text.source.Annotation#paint(org.eclipse.swt.graphics.GC, org.eclipse.swt.widgets.Canvas, org.eclipse.swt.graphics.Rectangle)
		 */
		public void paint(GC gc, Canvas canvas, Rectangle r) {
			drawImage(getImage(), gc, canvas, r, SWT.CENTER, SWT.TOP);
		}
		
	}
	
	private Annotation fAnnotation;
	private boolean fIsAnnotationShown;
	private IEditorPart fEditor;
	private ITextViewer fViewer;
	
	private ISelectionChangedListener fListener;
	private IPropertyChangeListener fPropertyChangeListener;
	
	public QuickAssistLightBulbUpdater(IEditorPart part, ITextViewer viewer) {
		fEditor= part;
		fViewer= viewer;
		fAnnotation= new AssistAnnotation();
		fIsAnnotationShown= false;
		fPropertyChangeListener= null;
	}
	
	public boolean isSetInPreferences() {
		return PreferenceConstants.getPreferenceStore().getBoolean(PreferenceConstants.APPEARANCE_QUICKASSIST_LIGHTBULB);
	}
	
	private void installSelectionListener() {
		ISelectionProvider provider= fViewer.getSelectionProvider();
		if (provider instanceof IPostSelectionProvider) {
			fListener= new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent event) {
					doSelectionChanged();
				}
			};
			((IPostSelectionProvider) provider).addPostSelectionChangedListener(fListener);
		}		
	}
	
	private void uninstallSelectionListener() {
		if (fListener != null) {
			IPostSelectionProvider provider= (IPostSelectionProvider) fViewer.getSelectionProvider();
			provider.removePostSelectionChangedListener(fListener);
			fListener= null;
			if (fIsAnnotationShown) {
				getAnnotationModel().removeAnnotation(fAnnotation);
				fIsAnnotationShown= false;
			}
		}
	}	
	
	public void install() {
		if (isSetInPreferences()) {
			installSelectionListener();
		}
		if (fPropertyChangeListener == null) {
			fPropertyChangeListener= new IPropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent event) {
					doPropertyChanged(event.getProperty());
				}
			};
			PreferenceConstants.getPreferenceStore().addPropertyChangeListener(fPropertyChangeListener);		
		}
	}
	
	public void uninstall() {
		uninstallSelectionListener();
		if (fPropertyChangeListener != null) {
			PreferenceConstants.getPreferenceStore().removePropertyChangeListener(fPropertyChangeListener);		
			fPropertyChangeListener= null;
		}
	}
	
	protected void doPropertyChanged(String property) {
		if (property.equals(PreferenceConstants.APPEARANCE_QUICKASSIST_LIGHTBULB)) {
			if (isSetInPreferences()) {
				installSelectionListener();
				doSelectionChanged();
			} else {
				uninstallSelectionListener();
			}			
		}
	}	
	
	private ICompilationUnit getCompilationUnit(IEditorInput input) {
		if (input instanceof FileEditorInput) {
			IFile file= ((FileEditorInput) input).getFile();
			ICompilationUnit cu= JavaCore.createCompilationUnitFrom(file);
			if (cu != null) {
				return JavaModelUtil.toWorkingCopy(cu);
			}
		}
		return null;
	}
	
	private IAnnotationModel getAnnotationModel() {
		return JavaUI.getDocumentProvider().getAnnotationModel(fEditor.getEditorInput());
	}
	
	private IDocument getDocument() {
		return JavaUI.getDocumentProvider().getDocument(fEditor.getEditorInput());
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	private void doSelectionChanged() {
		Point point= fViewer.getSelectedRange();
		int offset= point.x;
		int length= point.y;
		
		final IAnnotationModel model= getAnnotationModel();
		boolean hasQuickFix= hasQuickFixLightBulb(model, offset);
		ICompilationUnit cu= getCompilationUnit(fEditor.getEditorInput());

		if (hasQuickFix || cu == null) {
			if (fIsAnnotationShown) {
				model.removeAnnotation(fAnnotation);
			}
			return;			
		}
		final IAssistContext context= new AssistContext(cu, offset, length);
		calculateLightBulb(model, context);
		//Runnable runnable= new Runnable() {
		//	public void run() {
		//		calculateLightBulb(model, context);
		//	}
		//};
		//runnable.run();
	}
		
	
	private void calculateLightBulb(IAnnotationModel model, IAssistContext context) {
		boolean needsAnnotation= JavaCorrectionProcessor.hasAssists(context);
		if (fIsAnnotationShown) {
			model.removeAnnotation(fAnnotation);
		}
		if (needsAnnotation) {
			model.addAnnotation(fAnnotation, new Position(context.getSelectionOffset(), context.getSelectionLength()));
		}
		fIsAnnotationShown= needsAnnotation;
	}
	
	/*
	 * Tests if there is already a quick fix light bulb on the current line
	 */	
	private boolean hasQuickFixLightBulb(IAnnotationModel model, int offset) {
		try {
			IDocument document= getDocument();
			int currLine= document.getLineOfOffset(offset);
			
			Iterator iter= new JavaAnnotationIterator(model, true);
			while (iter.hasNext()) {
				IJavaAnnotation annot= (IJavaAnnotation) iter.next();
				Position pos= model.getPosition((Annotation) annot);
				int startLine= document.getLineOfOffset(pos.getOffset());
				if (startLine == currLine && JavaCorrectionProcessor.hasCorrections(annot)) {
					return true;
				}
			}
		} catch (BadLocationException e) {
			JavaPlugin.log(e);
		}
		return false;
	}


}
