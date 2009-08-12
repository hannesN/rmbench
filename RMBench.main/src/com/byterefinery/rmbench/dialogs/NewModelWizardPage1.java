package com.byterefinery.rmbench.dialogs;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.extension.DatabaseExtension;
import com.byterefinery.rmbench.extension.NameGeneratorExtension;

/**
 * wizard page for specifying the model name an database 
 */
public class NewModelWizardPage1 extends WizardPage {
    
    private Text modelNameText;
    private DatabaseExtension databaseExtension = RMBenchPlugin.getStandardDatabaseExtension();
    private NameGeneratorExtension generatorExtension = RMBenchPlugin.getDefaultNameGeneratorExtension();
    
	public NewModelWizardPage1() {
		super("NewModelWizardPage1");
		setTitle(Messages.NewModelWizard1_Title);
		setDescription(Messages.NewModelWizard1_Description);
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(2, false);
        layout.verticalSpacing = 9;
		container.setLayout(layout);
        
        Label label = new Label(container, SWT.NULL);
        label.setText(Messages.NewModelWizard1_ModelName);
        
        modelNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
        modelNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        modelNameText.setText(Messages.NewModelWizard1_newModel_name);
        modelNameText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                dialogChanged();
            }
        });
        
        label = new Label(container, SWT.NONE);
        label.setText(Messages.ModelPropertiesDialog_Database);
        label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        
        final ComboViewer dbCombo = new ComboViewer(container, SWT.READ_ONLY | SWT.DROP_DOWN);
        dbCombo.getCombo().setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
        dbCombo.setContentProvider(new ArrayContentProvider());
        dbCombo.setLabelProvider(new LabelProvider() {
            public String getText(Object element) {
                DatabaseExtension dbext = (DatabaseExtension)element;
                return dbext.getName();
            }
        });
        dbCombo.setInput(RMBenchPlugin.getExtensionManager().getDatabaseExtensions());
        dbCombo.setSelection(new StructuredSelection(databaseExtension));
        dbCombo.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = (IStructuredSelection)event.getSelection();
                databaseExtension = (DatabaseExtension)selection.getFirstElement();
                dialogChanged();
            }
        });
        
        label = new Label(container, SWT.NONE);
        label.setText(Messages.ModelPropertiesDialog_NameGenerator);
        label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        
        final ComboViewer generatorCombo = new ComboViewer(container, SWT.READ_ONLY | SWT.DROP_DOWN);
        generatorCombo.getCombo().setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
        generatorCombo.setContentProvider(new ArrayContentProvider());
        generatorCombo.setLabelProvider(new LabelProvider() {
            public String getText(Object element) {
                NameGeneratorExtension genext = (NameGeneratorExtension)element;
                return genext.getName();
            }
        });
        generatorCombo.setInput(RMBenchPlugin.getExtensionManager().getNameGeneratorExtensions());
        generatorCombo.setSelection(new StructuredSelection(generatorExtension));
        generatorCombo.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = (IStructuredSelection)event.getSelection();
                generatorExtension = (NameGeneratorExtension)selection.getFirstElement();
                dialogChanged();
            }
        });
        
		dialogChanged();
		setControl(container);
	}

	private void dialogChanged() {
        if (getModelName().length() == 0) {
            setErrorMessage(Messages.NewModelWizard1_ErrModelName);
            setPageComplete(false);
        }
        else {
        	//clears error message
        	setErrorMessage(null);
            setPageComplete(true);
            //setting new filename to next page
            NewModelWizardPage2 filePage = (NewModelWizardPage2)getNextPage();
            char name[]=getModelName().toCharArray();
            for (int i=0; i<name.length; i++) {
                if (name[i]==' ') {
                    name[i]='_';
                }
            }
            
            filePage.setFileName(new String(name)+".rmb");
        }
	}

    public DatabaseExtension getDatabaseExtension() {
        return databaseExtension;
    }

    public NameGeneratorExtension getNameGeneratorExtension() {
        return generatorExtension;
    }

    public String getModelName() {
        return modelNameText.getText();
    }
    
    
}