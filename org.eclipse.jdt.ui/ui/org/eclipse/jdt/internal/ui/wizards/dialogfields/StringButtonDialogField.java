/*
 * Licensed Materials - Property of IBM,
 * WebSphere Studio Workbench
 * (c) Copyright IBM Corp 1999, 2000
 */
package org.eclipse.jdt.internal.ui.wizards.dialogfields;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import org.eclipse.jdt.internal.ui.wizards.swt.MGridData;

public class StringButtonDialogField extends StringDialogField {
		
	private Button fBrowseButton;
	private String fBrowseButtonLabel;
	private IStringButtonAdapter fStringButtonAdapter;
	
	private boolean fButtonEnabled;
	
	public StringButtonDialogField(IStringButtonAdapter adapter) {
		super();
		fStringButtonAdapter= adapter;
		fBrowseButtonLabel= "!Browse...!";
		fButtonEnabled= true;
	}
	
	public void setButtonLabel(String label) {
		fBrowseButtonLabel= label;
	}
	
	// ------ adapter communication
	
	public void changeControlPressed() {
		fStringButtonAdapter.changeControlPressed(this);
	}
	
	// ------- layout helpers
		
	public Control[] doFillIntoGrid(Composite parent, int nColumns) {
		assertEnoughColumns(nColumns);
		
		Label label= getLabelControl(parent);
		label.setLayoutData(gridDataForLabel(1));
		Text text= getTextControl(parent);
		text.setLayoutData(gridDataForText(nColumns - 2));
		Control button= getChangeControl(parent);
		button.setLayoutData(gridDataForControl(1));
		
		return new Control[] { label, text, button };
	}	
	
	public int getNumberOfControls() {
		return 3;	
	}
	
	protected static MGridData gridDataForControl(int span) {
		MGridData gd= new MGridData();
		gd.horizontalAlignment= gd.FILL;
		gd.grabExcessHorizontalSpace= false;
		gd.horizontalSpan= span;
		return gd;
	}		
	
	// ------- ui creation	
	
	public Control getChangeControl(Composite parent) {
		if (fBrowseButton == null) {
			assertCompositeNotNull(parent);
			
			fBrowseButton= new Button(parent, SWT.PUSH);
			fBrowseButton.setText(fBrowseButtonLabel);
			fBrowseButton.setEnabled(isEnabled() && fButtonEnabled);
			fBrowseButton.addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {
					changeControlPressed();
				}
				public void widgetSelected(SelectionEvent e) {
					changeControlPressed();
				}
			});	
			
		}
		return fBrowseButton;
	}
	
	// ------ enable / disable management
	
	public void enableButton(boolean enable) {
		if (isOkToUse(fBrowseButton)) {
			fBrowseButton.setEnabled(isEnabled() && enable);
		}
		fButtonEnabled= enable;
	}
	
	protected void updateEnableState() {
		super.updateEnableState();
		if (isOkToUse(fBrowseButton)) {
			fBrowseButton.setEnabled(isEnabled() && fButtonEnabled);
		}
	}	
	
	
}