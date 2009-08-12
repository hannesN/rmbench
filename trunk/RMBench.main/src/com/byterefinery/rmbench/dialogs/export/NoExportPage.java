/*
 * created 15.05.2006
 * 
 * $Id$
 */
package com.byterefinery.rmbench.dialogs.export;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;

import com.byterefinery.rmbench.RMBenchMessages;
import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.dialogs.Messages;

/**
 * a page that only shows a messages that export is not available
 * 
 * @author cse
 */
class NoExportPage extends WizardPage {

	public static NoExportPage noLicense() {
		return new NoLicensePage();
	}
	public static NoExportPage noExport() {
		return new NoExportPage();
	}
	
    private NoExportPage() {
        super(NoExportPage.class.getName());
        setTitle(Messages.ExportWizard_mainTitle);
        setDescription(Messages.ExportWizard_selectionDescription);
    }

    public void createControl(Composite parent) {
        Composite control = new Composite(parent, SWT.NONE);
        control.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        control.setLayout(new GridLayout());
        
        addDescriptionWidgets(control);
        
        setPageComplete(false);
        setControl(control);
    }

	protected void addDescriptionWidgets(Composite control) {
        Label noExport = new Label(control, SWT.CENTER | SWT.WRAP);
        GridData gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
        gd.widthHint = 300; // Windows: force initial width
        noExport.setLayoutData(gd);
        
        noExport.setText(RMBenchMessages.Export_noExportAvailable);
	}
	
	private static final class NoLicensePage extends NoExportPage {

		protected void addDescriptionWidgets(Composite control) {
	        Link noLicense = new Link(control, SWT.CENTER | SWT.WRAP);
	        GridData gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
	        gd.widthHint = 300; // Windows: force initial width
	        noLicense.setLayoutData(gd);
	        
	        String text = RMBenchMessages.License_FeatureNotAvailable.replaceFirst(
	                RMBenchPlugin.RMBENCH_HOME, RMBenchPlugin.RMBENCH_HOME_HREF);
	        noLicense.setText(text);
	        noLicense.addSelectionListener(new SelectionAdapter() {
	            public void widgetSelected(SelectionEvent e) {
	                RMBenchPlugin.externalBrowseHomepage(getShell());
	            }
	        });
		}
	}
}
