/*
 * Licensed Materials - Property of IBM,
 * WebSphere Studio Workbench
 * (c) Copyright IBM Corp 1999, 2000
 */
package org.eclipse.jdt.internal.ui.refactoring.actions;

import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.wizard.WizardDialog;

import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.IUpdate;

import org.eclipse.jdt.core.ICompilationUnit;

import org.eclipse.jdt.internal.core.CompilationUnit;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.actions.JavaUIAction;
import org.eclipse.jdt.internal.ui.refactoring.RefactoringWizardDialog;
import org.eclipse.jdt.internal.ui.refactoring.code.ExtractMethodWizard;
import org.eclipse.jdt.internal.ui.util.JdtHackFinder;

/**
 * Extracts a new method from the text editor's text selection by using the
 * extract method refactoing.
 */
public class ExtractMethodAction extends JavaUIAction implements IUpdate {
	
	private ITextEditor fEditor;
	
	/**
	 * Creates a new extract method action for the given text editor. The text
	 * editor's selection marks the set of statements to be extracted into a new
	 * method.
	 * @param editor the text editor.
	 */
	public ExtractMethodAction(ITextEditor editor) {
		super("Extract Method");
		fEditor= editor;
		Assert.isNotNull(fEditor);
	}

	/* (non-JavaDoc)
	 * Method declared in IUpdate.
	 */
	public void update() {
		setEnabled(canOperateOn());
	}
	
	/* (non-JavaDoc)
	 * Method declared in IAction.
	 */
	public void run() {
		ICompilationUnit o= getCompilationUnit();
		JdtHackFinder.fixme("");
		if (o instanceof CompilationUnit) {
			CompilationUnit cu= (CompilationUnit)o;
			ITextSelection selection= getTextSelection();
			ExtractMethodWizard wizard= new ExtractMethodWizard(cu, selection, fEditor.getDocumentProvider());
			WizardDialog dialog= new RefactoringWizardDialog(JavaPlugin.getActiveWorkbenchShell(), wizard);
			dialog.open();			
		}
	}
	
	private boolean canOperateOn() {
		return true;
	}
	
	private ICompilationUnit getCompilationUnit() {
		return JavaPlugin.getDefault().getWorkingCopyManager().getWorkingCopy(fEditor.getEditorInput());
	}
	
	private ITextSelection getTextSelection() {
		return (ITextSelection)fEditor.getSelectionProvider().getSelection();
	}	
}