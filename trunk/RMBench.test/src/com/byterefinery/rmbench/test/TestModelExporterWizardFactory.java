/*
 * created 20.05.2006
 * 
 * $Id$
 */
package com.byterefinery.rmbench.test;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.byterefinery.rmbench.external.IModelExporter;
import com.byterefinery.rmbench.external.IModelExporterWizardFactory;

/**
 * @author cse
 */
public class TestModelExporterWizardFactory implements IModelExporterWizardFactory {

    public IWizardPage[] getWizardPages(IModelExporter exporter) {
        return new IWizardPage[] {new TestModelExporterWizardPage((TestModelExporter)exporter)};
    }
    
    private static class TestModelExporterWizardPage extends WizardPage {

        private final TestModelExporter exporter;
        
        protected TestModelExporterWizardPage(TestModelExporter exporter) {
            super(TestModelExporterWizardPage.class.getName());
            
            this.exporter = exporter;
            setTitle("Test Model Exporter");
            setDescription("Configure the Test Model Exporter");
        }

        public void createControl(Composite parent) {
            Composite control = new Composite(parent, SWT.NONE);
            control.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, false, true));
            control.setLayout(new GridLayout(2, false));
            
            Label label = new Label(control, SWT.NONE);
            label.setText("text:");
            label.setLayoutData(new GridData(SWT.RIGHT, SWT.NONE, false, false));
            
            final Text text = new Text(control, SWT.BORDER);
            text.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
            text.addModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent e) {
                    String val = text.getText();
                    exporter.setText(val);
                    setPageComplete(val.length() > 0);
                }
            });
            
            setControl(control);
        }
    }
}
