/*
 * created 08.05.2005
 * 
 * $Id: JdbcConnectionWizardPage3.java 657 2007-08-31 23:20:24Z cse $
 */
package com.byterefinery.rmbench.dialogs;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.exceptions.ExceptionMessages;
import com.byterefinery.rmbench.exceptions.SystemException;
import com.byterefinery.rmbench.external.IMetaDataAccess;
import com.byterefinery.rmbench.external.database.jdbc.IJdbcConnectAdapter;
import com.byterefinery.rmbench.model.dbimport.DBModel;
import com.byterefinery.rmbench.util.ImageConstants;
import com.byterefinery.rmbench.util.UIRunnableWithProgress;


/**
 * wizard page that allows to select the schemas to be imported from the 
 * database connection
 * 
 * @author cse
 */
public class JdbcConnectionWizardPage3 extends WizardPage {

    private final JdbcConnectionWizard wizard;
    
    private CheckboxTableViewer tableViewer;
    private Button allSchemasRadio;
    private Button selectedSchemasRadio;
    
    private Button deselectButton;
    private Button selectButton;

    private String[] schemaNames = {};
    
    protected JdbcConnectionWizardPage3(JdbcConnectionWizard wizard) {
        super("JdbcConnectionWizardPage3"); //$NON-NLS-1$
        this.wizard = wizard;
        setTitle(wizard.getPageTitle());
        setDescription(Messages.JdbcConnectionWizardPage3_description);
    }

    public void createControl(Composite parent) {

        Composite mainComposite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        mainComposite.setLayout(layout);
        mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Group radioGroup = new Group(mainComposite, SWT.NONE);
        radioGroup.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
        radioGroup.setLayout(new GridLayout(1, false));
        radioGroup.setText(Messages.JdbcConnectionWizardPage3_schemaRule);
        
        allSchemasRadio = new Button(radioGroup, SWT.RADIO);
        allSchemasRadio.setText(Messages.JdbcConnectionWizardPage3_allSchemas);
        allSchemasRadio.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        allSchemasRadio.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                if(allSchemasRadio.getSelection()) {
                    wizard.setSchemaRule(DBModel.ALL_SCHEMAS);
                    updateSchemaRule();
                }
            }
        });
        
        selectedSchemasRadio = new Button(radioGroup, SWT.RADIO);
        selectedSchemasRadio.setText(Messages.JdbcConnectionWizardPage3_selectedSchemas);
        selectedSchemasRadio.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        selectedSchemasRadio.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                if(selectedSchemasRadio.getSelection()) {
                    wizard.setSchemaRule(new DBModel.SelectedSchemasRule());
                    updateSchemaRule();
                }
            }
        });
        Table table = new Table(mainComposite, SWT.CHECK | SWT.BORDER);
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        tableViewer = new CheckboxTableViewer(table);
        tableViewer.getTable().setLayoutData(
                new GridData(SWT.FILL, SWT.FILL, true, true));
        tableViewer.setLabelProvider(new TableLabelProvider());
        tableViewer.setContentProvider(new TableContentProvider());
        tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                setSelectedSchemas();
            }
            
        });
        
        
        createTableButtons(mainComposite);

        updateSchemaRule();
        setControl(mainComposite);
        
    }
    
    private void setSelectedSchemas() {
        Object list[] = tableViewer.getCheckedElements();
        String elements[] = new String[list.length];
        System.arraycopy(list, 0, elements, 0, list.length);
        wizard.setSelectedSchemas(elements);
    }

    public void setVisible(boolean visible) {
        if (visible)
            updateViewer();
        
        super.setVisible(visible);
    }
    
    private void createTableButtons(Composite parent) {
        
        Composite buttonArea = new Composite(parent, SWT.NONE);
        buttonArea.setLayout(new GridLayout(2, true));
        buttonArea.setLayoutData(new GridData(SWT.CENTER, SWT.NONE, true, false));
        
        selectButton = new Button(buttonArea, SWT.NONE);
        selectButton.setText(Messages.JdbcConnectionWizardPage3_select_all_button_label);
        selectButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                tableViewer.setAllChecked(true);
                setSelectedSchemas();
            }
        });
        
        deselectButton = new Button(buttonArea, SWT.NONE);
        deselectButton.setText(Messages.JdbcConnectionWizardPage3_deselect_all_button_label);
        deselectButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                tableViewer.setAllChecked(false);
                setSelectedSchemas();
            }
        });
        
    }
    
    private void updateSchemaRule() {
        boolean allSchemas = wizard.getSchemaRule() == DBModel.ALL_SCHEMAS;
        
        allSchemasRadio.setSelection(allSchemas);
        boolean selectionEnabled = !allSchemas;

        selectedSchemasRadio.setSelection(selectionEnabled);
      
        selectButton.setEnabled(selectionEnabled);
        deselectButton.setEnabled(selectionEnabled);
        
        tableViewer.getControl().setEnabled(selectionEnabled);
    }
    
    private List<String> loadSchemas() {
        String password = wizard.getPassword();
        if(wizard.getPrompt()) {
            PasswordInputDialog inputDialog = new PasswordInputDialog(
                    getShell(),
                    Messages.JdbcConnectionWizardPage3_passwordTitle,
                    Messages.JdbcConnectionWizardPage3_passwordMessage,
                    null,
                    null);
            if (inputDialog.open() != Window.OK)
                return Collections.emptyList();
            
            password = inputDialog.getValue();
        }
        
        IJdbcConnectAdapter connection = null;
        List<String> allSchemas = new ArrayList<String>();
        try {
            connection = wizard.getConnectionAdapter(password);
            IMetaDataAccess.Factory factory = wizard.getDriverInfo().getMetaDataFactory();
            IMetaDataAccess metaData = factory.createMetaData(connection.getConnection(), false);
            
            IMetaDataAccess.ResultSet resultSet = metaData.getSchemas();
            while(resultSet.next()) {
                String catalogName = resultSet.getString(2);
                String schemaName = resultSet.getString(1);
                allSchemas.add(catalogName != null ? catalogName+"."+schemaName : schemaName); //$NON-NLS-1$
            }
        }
        catch(SQLException e) {
            connectError(e, SystemException.getStatus(e, ExceptionMessages.cantEstablishConnection));
            return Collections.emptyList();
        }
        catch(SystemException e) {
            connectError(e.getCause(), e.getStatus(ExceptionMessages.cantEstablishConnection));
            return Collections.emptyList();
        }
        finally {
            try {
                if(connection != null) connection.release();
            } catch (SQLException e) {
                RMBenchPlugin.logError(e);
            }
        }
        
        return allSchemas;
    }

    private void updateViewer() {
        SchemaLoader loader = new SchemaLoader();
        loader.start();
        schemaNames = (String[]) loader.getSchemas().toArray(new String[loader.getSchemas().size()]);
        
        Arrays.sort(schemaNames);
        tableViewer.setInput(schemaNames);
        try {
            String[] selSchemas = wizard.getSelectedSchemas();
            for (int i=0; i<selSchemas.length; i++){
                for (int j=0; j<schemaNames.length; j++) {
                    if (schemaNames[j].equals(selSchemas[i])) {
                        tableViewer.setChecked(schemaNames[j], true);
                        break;
                    }
                }
            }
        }
        catch (IllegalStateException e) {
            //do nothing, we just have no selected schemas
        }
    }
    
    private void connectError(Throwable error, IStatus status) {
        RMBenchPlugin.logError(error.getMessage(), error);
        ExceptionDialog.openError(
                getShell(),
                status.getMessage(),
                status);
    }
    
    private class SchemaLoader extends UIRunnableWithProgress {
        List<String> allSchemas;
        
        public void run(IProgressMonitor monitor) {
            monitor.beginTask(Messages.JdbcConnectionWizardPage3_loading_schemas, IProgressMonitor.UNKNOWN);
            try {
                allSchemas = loadSchemas();
            }
            finally {
                monitor.done();
            }
        }
        
        public List<String> getSchemas() {
            return allSchemas;
        }
    }

   
    class TableContentProvider implements IStructuredContentProvider {
        public Object[] getElements(Object inputElement) {
            return schemaNames;
        }
        public void dispose() {
        }
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }
    
    class TableLabelProvider implements ITableLabelProvider {

        public Image getColumnImage(Object element, int columnIndex) {
            if (columnIndex == 0) {
                return RMBenchPlugin.getImage(ImageConstants.SCHEMA);
            } else {
                return null;
            }
        }
        public String getColumnText(Object element, int columnIndex) {
            if (columnIndex == 0) {
                return (String) element;
            } else {
                return null;
            }
        }
        public void addListener(ILabelProviderListener listener) {
        }
        public void dispose() {
        }
        public boolean isLabelProperty(Object element, String property) {
            return false;
        }
        public void removeListener(ILabelProviderListener listener) {
        }
    }
}
