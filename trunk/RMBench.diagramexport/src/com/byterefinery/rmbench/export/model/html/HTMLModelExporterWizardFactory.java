/*
 * created 20.05.2006
 * 
 * $Id$
 */
package com.byterefinery.rmbench.export.model.html;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.byterefinery.rmbench.external.IModelExporter;
import com.byterefinery.rmbench.external.IModelExporterWizardFactory;

/**
 * factory for creating a HTML exporter configuration wizard page
 * 
 * @author cse
 */
public class HTMLModelExporterWizardFactory implements IModelExporterWizardFactory {

    public IWizardPage[] getWizardPages(IModelExporter exporter) {
        return new IWizardPage[] {new HTMLModelExporterWizardPage(exporter)};
    }

    private static class HTMLModelExporterWizardPage extends WizardPage {

    	private Text widthText;
    	private Text heightText;
    	private IModelExporter exporter;
    	
        protected HTMLModelExporterWizardPage(IModelExporter exporter) {
            super(HTMLModelExporterWizardPage.class.getName());
            setTitle(Messages.HTMLExporter_Title);
            setDescription(Messages.HTMLExporter_Description);
            this.exporter = exporter;
        }

        public void createControl(Composite parent) {
            Composite control = new Composite(parent, SWT.NONE);
            control.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, false, true));
            control.setLayout(new GridLayout());
            
            Group sizeGroup = new Group(control, SWT.NONE);
            sizeGroup.setText(Messages.HTMLExporter_LabelImageSize);
            sizeGroup.setLayout(new GridLayout());
            sizeGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
           
            //radiobuttons for size
            Button button = new Button(sizeGroup, SWT.RADIO);
            button.setText("800x600");
            button.setSelection(true);
            button.addSelectionListener(new SizeButtonSelectionListener(800, 600));
            
            button = new Button(sizeGroup, SWT.RADIO);
            button.setText("1024x768");
            button.addSelectionListener(new SizeButtonSelectionListener(1024, 768));
            
            button = new Button(sizeGroup, SWT.RADIO);
            button.setText("1280x1024");
            button.addSelectionListener(new SizeButtonSelectionListener(1280, 1024));
            
            button = new Button(sizeGroup, SWT.RADIO);
            button.setText("1600x1200");
            button.addSelectionListener(new SizeButtonSelectionListener(1600, 1200));
            
            button = new Button(sizeGroup, SWT.RADIO);
            button.setText(Messages.HTMLExporter_ImageSizeCustom);
            button.addSelectionListener(new SelectionListener() {

				public void widgetSelected(SelectionEvent e) {
					Button b=(Button) e.getSource();
				
					widthText.setEnabled(b.getSelection());
					heightText.setEnabled(b.getSelection());
				}

				public void widgetDefaultSelected(SelectionEvent e) {
				}
            	
            });
            
            Composite customGroup = new Composite(sizeGroup, SWT.NONE);
            customGroup.setLayout(new GridLayout(2, false));
            GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
            gd.horizontalIndent = 20;
            customGroup.setLayoutData(gd);
            
            Label label = new Label(customGroup, SWT.NONE);
            label.setText(Messages.HTMLExporter_LabelWidth);
            
            widthText = new Text(customGroup, SWT.BORDER);
            widthText.setEnabled(false);
            gd = new GridData(SWT.BEGINNING, SWT.CENTER, true, false);
            gd.minimumWidth = 35;
            gd.widthHint = 35;
            widthText.setLayoutData(gd);
            widthText.setTextLimit(4);
            widthText.addVerifyListener(new NumberVerifyListener());
            widthText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					setCustomSize();					
				}          	
            });
            
            label = new Label(customGroup, SWT.NONE);
            label.setText(Messages.HTMLExporter_LabelHeight);
            
            heightText = new Text(customGroup, SWT.BORDER);
            heightText.setEnabled(false);
            gd = new GridData(SWT.BEGINNING, SWT.CENTER, true, false);
            gd.minimumWidth = 35;
            gd.widthHint = 35;
            heightText.setLayoutData(gd);
            heightText.setTextLimit(4);
            heightText.addVerifyListener(new NumberVerifyListener());
            heightText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					setCustomSize();					
				}          	
            });

            Group templateGroup = new Group(control, SWT.NONE);
            templateGroup.setText(Messages.HTMLExporter_Group_UseTemplate);
            templateGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
            templateGroup.setLayout(new GridLayout());
            
            button = new Button(templateGroup, SWT.RADIO);
            button.setText(Messages.HTMLExporter_Button_2Frame);
            button.setSelection(true);
            button.addSelectionListener(new SelectionListener() {
            	public void widgetSelected(SelectionEvent e) {
            		if ( ((Button)e.widget).getSelection() )
            			((HTMLModelExporter) exporter).setTemplateType(HTMLModelExporter.TWO_FRAME_TEMPLATE);
            	}
            	
            	public void widgetDefaultSelected(SelectionEvent e) {
            	}
            });
            
            button = new Button(templateGroup, SWT.RADIO);
            button.setText(Messages.HTMLExporter_Button_3Frame);
            button.addSelectionListener(new SelectionListener() {
            	public void widgetSelected(SelectionEvent e) {
            		if ( ((Button)e.widget).getSelection() )
            			((HTMLModelExporter) exporter).setTemplateType(HTMLModelExporter.THREE_FRAME_TEMPLATE);
            	}
            	
            	public void widgetDefaultSelected(SelectionEvent e) {
            	}
            });
            
            setControl(control);
        }
        
        protected void setCustomSize() {
			if ( (widthText.getText().length()>0) && (heightText.getText().length()>0) ) {
	        	int w = Integer.parseInt(widthText.getText());
				int h = Integer.parseInt(heightText.getText());
				setImageSize(w, h);
			}
		}

		private void setImageSize(int width, int height) {
        	if (exporter instanceof HTMLModelExporter) {
        		((HTMLModelExporter) exporter).setImageSize(width, height);
        	}
        }
        
        private class SizeButtonSelectionListener implements SelectionListener {
        	int width;
        	int height;
        	
        	
        	public SizeButtonSelectionListener(int width, int height) {
				super();
				this.width = width;
				this.height = height;
			}

			public void widgetSelected(SelectionEvent e) {
				Button b=(Button) e.getSource();
				if (b.getSelection())
					setImageSize(width, height);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}  
        }

        /**
         * Checks if the input typed in a textfield contains only digits
         * @author Hannes Niederhausen
         *
         */
        private class NumberVerifyListener implements VerifyListener {
			public void verifyText(VerifyEvent e) {
				char c[] = e.text.toCharArray();
				
				for (int i=0; i<c.length; i++) {
					if (!Character.isDigit(c[i])) {
						e.doit = false;
					}
				}
			}
        	
        }
    }
}
