/*
 * created 19.08.2006
 *
 * Copyright 2006, DynaBEAN Consulting
 *  $Id$
 */
package com.byterefinery.rmbench.database.derby;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ListDialog;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.exceptions.SystemException;
import com.byterefinery.rmbench.external.IURLSetupGroup;
import com.byterefinery.rmbench.external.database.jdbc.IJdbcConnectAdapter;
import com.byterefinery.rmbench.external.database.ui.URLSetupGroup;
import com.byterefinery.rmbench.util.UIRunnableWithProgress;

/**
 * setup widget group for the derby embedded driver
 * 
 * @author Hannes Niederhausen
 */
public class EmbeddedURLSetupGroup extends URLSetupGroup {
	
	public static class Factory implements IURLSetupGroup.Factory {

        public IURLSetupGroup createSetupGroup(String connectionUrl, IURLSetupGroup.Context context) {
            return new EmbeddedURLSetupGroup(connectionUrl, context);
        }
	}
	
    final static private String jdbcPart="jdbc:derby:"; //$NON-NLS-1$
    final static Pattern pattern = Pattern.compile("jdbc:derby:([^;]*)(;create=(false|true))?"); //$NON-NLS-1$
    
    private String filepath;
    private String databaseName;
    
    private boolean complete;
    
    private Text filepathText;
    private Text databaseNameText;
    private Button createButton;
    private Button browseButton;
    private Label jdbcUrl;
    
    
    public EmbeddedURLSetupGroup(String connectionUrl, IURLSetupGroup.Context context) {
        super(context);
        filepath=""; //$NON-NLS-1$
        databaseName=""; //$NON-NLS-1$
        if (connectionUrl!=null)
            parseConnection(connectionUrl);
    }
    
    private void parseConnection(String connectionUrl) {
        Matcher matcher = pattern.matcher(connectionUrl);
        StringBuffer buffer = new StringBuffer();
        if (matcher.find()) {
            filepath=matcher.group(1);
            String splitPath[] = matcher.group(1).split("\\"+File.separatorChar);
        
            for (int i=0; i<splitPath.length-1; i++) {
                buffer.append(splitPath[i]);
                if (i<splitPath.length-2)
                    buffer.append(File.separator);
            }
            filepath = buffer.toString();
            databaseName = splitPath[splitPath.length-1];
        }
    }

    protected void createEditArea(Composite parent) {
        GridLayout layout = new GridLayout(3, false);
        layout.marginWidth = 0;
        
        parent.setLayout(layout);
        
        Label label = new Label(parent, SWT.NONE);
        label.setText(Messages.EmbeddedURLSetupGroup_database_path);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        
        filepathText = new Text(parent, SWT.BORDER);
        filepathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        filepathText.setText(filepath);
        filepathText.addModifyListener(new ModifyListener() {
           public void modifyText(ModifyEvent e) {
                filepath=filepathText.getText();
                validatePath();
                updateUrl();
            } 
        });
        
                
        Button button = new Button(parent, SWT.NONE);
        button.setText(Messages.EmbeddedURLSetupGroup_browse_button_label);
        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                 DirectoryDialog dlg = new DirectoryDialog(Display.getCurrent().getActiveShell());
                 
                 String path = dlg.open();
                 if (path!=null) {
                     filepath = path;
                     filepathText.setText(path);
                     validatePath();
                     updateUrl();
                 }
             } 
         });
        
        label = new Label(parent, SWT.NONE);
        label.setText(Messages.EmbeddedURLSetupGroup_database_name);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        
        databaseNameText = new Text(parent, SWT.BORDER);
        databaseNameText.setText(databaseName);
        databaseNameText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                databaseName = databaseNameText.getText();
                validatePath();
                updateUrl();
            }
        });
        databaseNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        
        createDatabaseButtons(parent);
        
        jdbcUrl = new Label(parent, SWT.NONE);
        GridData gridData = new GridData(SWT.FILL, SWT.NONE, true, false);
        gridData.horizontalSpan = 3;
        gridData.verticalIndent = 20;
        jdbcUrl.setLayoutData(gridData);
        jdbcUrl.setAlignment(SWT.CENTER);
        jdbcUrl.setText(getConnectionURL());
        
        validatePath();
    }
    
    private void createDatabaseButtons(Composite parent) {
        Composite buttonField = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        buttonField.setLayout(layout);
        
        browseButton = new Button(buttonField, SWT.NONE);
        browseButton.setText(Messages.EmbeddedURLSetupGroup_browse_button_label);
        browseButton.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false, false));
        browseButton.addSelectionListener(new SelectionAdapter() {
           public void widgetSelected(SelectionEvent e) {
               ListDialog dlg = new ListDialog(Display.getCurrent().getActiveShell());
               
               dlg.setTitle(Messages.EmbeddedURLSetupGroup_browse_button_label);
               dlg.setContentProvider(new BrowseContentProvider());
               dlg.setLabelProvider(new BrowseLabelProvider());
               dlg.setInput("");    //needed for initial filling the list, god knows why
               
               if (dlg.open()==Dialog.OK) {
                   databaseName = (String) dlg.getResult()[0];
                   databaseNameText.setText(databaseName);
               }
            } 
        });
        
        createButton = new Button(buttonField, SWT.NONE);
        createButton.setText(Messages.EmbeddedURLSetupGroup_create_button_label); //$NON-NLS-1$
        createButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                createDatabase();
            }
        });
        GridData gridData = new GridData(SWT.FILL, SWT.NONE, false, false);
        gridData.verticalIndent = 5;
        createButton.setLayoutData(gridData);
    }
    
    /**
     * Checks if the current filepath is a valid derby database path.
     * If the creation flag is false, it is essentially that the choosen directory
     * contains a valid derby database
     * 
     */
    private void validatePath() {
        createButton.setEnabled(false);
        
        if (filepath.length()==0) {
            fireCompleteEvent(false);
            fireErrorEvent(Messages.EmbeddedURLSetupGroup_error_db_path_needed);
            browseButton.setEnabled(false);
            return;
        }
        
        File file = new File(filepath);
        if ( (!file.exists()) || (!file.isDirectory())) {
            fireCompleteEvent(false);
            fireErrorEvent(Messages.EmbeddedURLSetupGroup_invalid_path);
            return;
        }
        
        browseButton.setEnabled(true);
        
        if (databaseName.length()==0) {
            fireCompleteEvent(false);
            fireErrorEvent(Messages.EmbeddedURLSetupGroup_error_db_name_needed);
            return;
        }
        String completePath=filepath+File.separator+databaseName;
        file = new File(completePath);
        if (!file.exists()) {
            createButton.setEnabled(true);
            fireCompleteEvent(false);
            fireErrorEvent(Messages.EmbeddedURLSetupGroup_error_db_not_exists);
            return;
        }
        
        if (!isValidDatabase(getConnectionURL())) {
            fireCompleteEvent(false);
            fireErrorEvent(Messages.EmbeddedURLSetupGroup_invalid_database_name);
            return;
        }
        
        fireCompleteEvent(true);
        fireErrorEvent(null);
    }

    protected void fireCompleteEvent(boolean completed) {
        complete=completed;
        super.fireCompleteEvent(completed);
    }
    
    public String getConnectionURL() {
        String result = jdbcPart+filepath+File.separator+databaseName;
        
        return result;
    }

    public boolean isComplete() {
        return complete;
    }
    
    private void updateUrl() {
        jdbcUrl.setText(getConnectionURL());
    }
    
    private void createDatabase() {
        UIRunnableWithProgress job = new UIRunnableWithProgress() {
            public void run (IProgressMonitor monitor) {
                monitor.beginTask(Messages.EmbeddedURLSetupGroup_info_creating_db, IProgressMonitor.UNKNOWN);
                IJdbcConnectAdapter conn = null;
                try {
                    Properties properties = new Properties();
                    properties.setProperty("create", "true"); //$NON-NLS-1$ //$NON-NLS-2$
                    
                    //establish connection, driver creates db
                    conn = context.getConnectionAdapter(properties, getConnectionURL());
                    validatePath();
                }
                catch (SystemException e){
                    RMBenchPlugin.logError(e);
                }
                finally {
                    monitor.done();
                    if(conn != null) {
                        try {
                            conn.release();
                        } catch (SQLException e) {
                            RMBenchPlugin.logError(e);
                        }
                    }
                }
            }
        };
        job.start();
    }
    
    private boolean isValidDatabase(String url) {
        try {
            DerbyEmbeddedConnectAdapter connectionAdapter = null;
            connectionAdapter = (DerbyEmbeddedConnectAdapter) context.getConnectionAdapter(new Properties(), url);
            if (connectionAdapter!=null)
                connectionAdapter.release();
            else
                return false;
        } catch (SystemException e) {
            return false;
        } catch (SQLException e) {
            RMBenchPlugin.logError(e);
        }
        return true;
    }
    
    private String[] getValidDatabases(String path) {
        File file = new File(path);
        String pathStart="jdbc:derby:"+path+File.separator;
        ArrayList<String> list = new ArrayList<String>();
        if (file.exists()) {
            File[] files = file.listFiles();
            for (int i=0; i<files.length; i++) {
                if (files[i].isDirectory()) {
                    if (isValidDatabase(pathStart+files[i].getName()))
                        list.add(files[i].getName());
                }
            }
        }
        return (String[]) list.toArray(new String[list.size()]);
    }
    
    private class BrowseContentProvider implements IStructuredContentProvider {

        public Object[] getElements(Object inputElement) {
            return getValidDatabases(filepath);
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
        
    }
    
    private class BrowseLabelProvider implements ILabelProvider {

        public Image getImage(Object element) {
            return null;
        }

        public String getText(Object element) {
            return (String) element;
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
