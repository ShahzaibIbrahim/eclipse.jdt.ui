package org.eclipse.jdt.ui.wizards;
/*
 * Licensed Materials - Property of IBM,
 * WebSphere Studio Workbench
 * (c) Copyright IBM Corp 1999, 2000
 */

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.WizardPage;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;

import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.dialogs.IStatusInfoChangeListener;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.BuildPathsBlock;

/**
 * Standard wizard page for creating new Java projects. This page can be used in 
 * project creation wizards for projects and will configure the project with the 
 * Java nature. This page also allows the user to configure the Java project's 
 * output location for class files generated by the Java builder.
 * <p>
 */
public class NewJavaProjectWizardPage extends WizardPage {
	
	private static final String PAGE_NAME= "NewJavaProjectWizardPage";

	private WizardNewProjectCreationPage fMainPage;
	private IPath fCurrProjectPath;
	
	private BuildPathsBlock fBuildPathsBlock;

	private StatusInfo fCurrStatus;
	
	/**
	 * Creates a Java project wizard creation page.
	 * <p>
	 * The Java project wizard reads project name and location from the main page.
	 * </p>
	 *
	 * @param root the workspace root
	 * @param mainpage the main page of the wizard
	 */	
	public NewJavaProjectWizardPage(IWorkspaceRoot root, WizardNewProjectCreationPage mainpage) {
		super(PAGE_NAME);
		
		setTitle(JavaPlugin.getResourceString(PAGE_NAME + ".title"));
		setDescription(JavaPlugin.getResourceString(PAGE_NAME + ".description"));
		
		fMainPage= mainpage;
		IStatusInfoChangeListener listener= new IStatusInfoChangeListener() {
			public void statusInfoChanged(StatusInfo status) {
				updateStatus(status);
			}
		};

				
		fBuildPathsBlock= new BuildPathsBlock(root, listener, true);
		fCurrStatus= new StatusInfo();
	}		
	
	/**
	 * Sets the default output location to be used for the new Java project.
	 * This is the path of the folder (with the project) into which the Java builder 
	 * will generate binary class files corresponding to the project's Java source
	 * files.
	 * <p>
	 * The wizard will create this folder if required.
	 * </p>
	 *
	 * @param path the folder to be taken as the default output path
	 */
	public void setDefaultOutputFolder(IPath path) {
		fBuildPathsBlock.setDefaultOutputFolder(path);
	}	

	/**
	 * Sets the default classpath to be used for the new Java project.
	 * <p>
	 * The caller of this method is responsible for creating the classpath entries 
	 * for the <code>IJavaProject</code> that corresponds to created the project.
	 * Secondary source for JAR files have to be set manually, after the wizard
	 * has finished. The caller is also responsible for creating any new folders 
	 * that might be mentioned on the classpath.
	 * </p>
	 * <p>
	 * [Issue: It is slightly unfortunate to make appeal to a preference in an API 
	 *  method but not expose that preference as API.
	 * ]
	 * </p>
	 *
	 * @param entries the default classpath entries
	 * @param appendDefaultJDK <code>true</code> if the standard Java library 
	 *    specified in the preferences should be added to the classpath
	 */
	public void setDefaultClassPath(IClasspathEntry[] entries, boolean appendDefaultJDK) {
		fBuildPathsBlock.setDefaultClassPath(entries, appendDefaultJDK);
	}

	/**
	 * Gets the project handle from the main page.
	 * Overwrite this method if you do not have a main page
	 */
	protected IProject getProjectHandle() {
		return fMainPage.getProjectHandle();
	}

	/**
	 * Returns the Java project handle corresponding to the project defined in
	 * in the main page.
	 *
	 * @returns the Java project
	 */	
	public IJavaProject getNewJavaProject() {
		return JavaCore.create(getProjectHandle());
	}	

	/* (non-Javadoc)
	 * @see WizardPage#createControl
	 */	
	public void createControl(Composite parent) {
		Control control= fBuildPathsBlock.createControl(parent);
		setControl(control);
	}	

	/**
	 * Extend this method to set a user defined default class path or output location
	 * @see IDialogPage#setVisible
	 */	
	public void setVisible(boolean visible) {
		if (visible) {
			updateStatus(fCurrStatus);	
			fBuildPathsBlock.init(getProjectHandle(), false);		
		}
		super.setVisible(visible);
	}

	/**
	 * Returns the runnable that will create the Java project. 
	 * The runnable will create and open the project if needed. The runnable will
	 * add the Java nature to the project, and set the project's classpath and
	 * output location. 
	 * <p>
	 * To create the new java project, execute this runnable
	 * </p>
	 *
	 * @return the runnable
	 */		
	public IRunnableWithProgress getRunnable() {
		fBuildPathsBlock.init(getProjectHandle(), false);
		return fBuildPathsBlock.getRunnable();
	}
	
	/* (non-Javadoc)
	 * Updates the status line
	 */	
	private void updateStatus(StatusInfo status) {
		fCurrStatus= status;
		if (isCurrentPage()) {
			setPageComplete(!fCurrStatus.isError());
			if (fCurrStatus.isOK()) {
				setErrorMessage(null);
				setMessage(null);
			} else if (fCurrStatus.isWarning()) {
				setErrorMessage(null);
				setMessage(fCurrStatus.getMessage());
			} else {
				setMessage(null);
				setErrorMessage(fCurrStatus.getMessage());
			}
		}
	}
		
}