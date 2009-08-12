/*
 * created 08.05.2006
 * 
 * Copyright 2008, ByteRefinery
 * 
 * $Id$
 */
package com.byterefinery.rmbench.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.byterefinery.rmbench.EventManager;
import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.EventManager.Event;
import com.byterefinery.rmbench.EventManager.Listener;
import com.byterefinery.rmbench.model.dbimport.DBModel;
import com.byterefinery.rmbench.util.ImageConstants;

/**
 * The dialog fpr managing the JDBC connections
 * 
 * @author Hannes Niederhausen
 *
 */
public class ConnectionDialog extends Dialog {

	private Button addButton;
	private Button editButton;
	private Button removeButton;
	
	private TableViewer viewer;
	
	private EventManager.Listener listener = new Listener() {

		public void eventOccurred(int eventType, Event event) {
			viewer.setInput(RMBenchPlugin.getDefault().getDBModels());
		}
		
		public void register() {
			RMBenchPlugin.getEventManager().addListener(DBMODELS_ADDED|DBMODELS_CHANGED|DBMODELS_REMOVED, this);
		}
	};
	
	public ConnectionDialog(Shell parentShell) {
		super(parentShell);
	}

	protected Control createDialogArea(Composite parent) {
		//parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(2, false));
		comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		viewer = new TableViewer(comp, SWT.BORDER);
		viewer.setContentProvider(new DBModelContentProvider());
		viewer.setLabelProvider(new DBModelLabelProvider());
		
		Table table = viewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		table.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (viewer.getSelection().isEmpty()) {
					editButton.setImage(RMBenchPlugin.getImage(ImageConstants.EDIT_disabled));
					editButton.setEnabled(false);
					removeButton.setImage(ImageConstants.DELETE_DISABLED_IMG);
					removeButton.setEnabled(false);
					return;
				}
				editButton.setImage(RMBenchPlugin.getImage(ImageConstants.EDIT));
				editButton.setEnabled(true);
				removeButton.setImage(ImageConstants.DELETE_IMG);
				removeButton.setEnabled(true);	
			}
		});
		TableLayout layout = new TableLayout();
		table.setLayout(layout);
		table.setHeaderVisible(true);
		TableColumn tc = new TableColumn(table, SWT.NONE);
		tc.setText(Messages.ConnectionDialog_connection_name);
		layout.addColumnData(new ColumnWeightData(50));
		
		
		tc = new TableColumn(table, SWT.NONE);
		tc.setText(Messages.ConnectionDialog_database_name);
		layout.addColumnData(new ColumnWeightData(50));
		
		
		Composite tableButtonComposite = createTableButtonBar(comp);
		GridData gridData = new GridData(SWT.NONE, SWT.FILL, false, true);
		gridData.verticalAlignment = SWT.CENTER;
		tableButtonComposite.setLayoutData(gridData);
		
		viewer.setInput(RMBenchPlugin.getDefault().getDBModels());
		
		listener.register();
		return comp;
	}
	
	private Composite createTableButtonBar(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		comp.setLayout(layout);
		
		//GridData gridData = new GridData();
		
		addButton = new Button(comp, SWT.PUSH);
		addButton.setImage(RMBenchPlugin.getImage(ImageConstants.ADD));
		addButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				JdbcConnectionWizard wizard = new JdbcConnectionWizard();
				WizardDialog dlg = new WizardDialog(addButton.getShell(), wizard);
				 if(dlg.open() == Window.OK) {
			            DBModel dbmodel = wizard.getDBModel();
			            RMBenchPlugin.addDBModel(dbmodel);
			     }
			}
		});
		
		editButton = new Button(comp, SWT.PUSH);
		editButton.setImage(RMBenchPlugin.getImage(ImageConstants.EDIT_disabled));
		editButton.setEnabled(false);
		editButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DBModel dbModel = (DBModel) ((IStructuredSelection) viewer
						.getSelection()).getFirstElement(); 
				JdbcConnectionWizard wizard = new JdbcConnectionWizard(dbModel);
				WizardDialog dlg = new WizardDialog(addButton.getShell(), wizard);
				 if(dlg.open() == Window.OK) {
			            DBModel newDbModel = wizard.getDBModel();
			            RMBenchPlugin.dbModelChanged(dbModel, newDbModel);
			     }
			}
		});
		
		removeButton = new Button(comp, SWT.PUSH);
		removeButton.setImage(ImageConstants.DELETE_DISABLED_IMG);
		removeButton.setEnabled(false);
		removeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DBModel dbModel = (DBModel) ((IStructuredSelection) viewer
						.getSelection()).getFirstElement(); 

				RMBenchPlugin.removeDBModel(dbModel);
			}
		});
		
		return comp;
	}
	
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.ConnectionDialog_dialog_title);
		newShell.setSize(330, 400);
	}
	

	public boolean close() {
		RMBenchPlugin.getEventManager().removeListener(listener);
		return super.close();
	}
	
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CLOSE_ID, IDialogConstants.CLOSE_LABEL,
				true);
	}
	
	private class DBModelContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object inputElement) {
			return RMBenchPlugin.getDefault().getDBModels();
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}
	
	protected void buttonPressed(int buttonId) {
		if (buttonId==IDialogConstants.CLOSE_ID)
			close();
	}
	
	
	private class DBModelLabelProvider implements ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			DBModel dbModel = (DBModel) element;
			if (columnIndex==0) {
				return dbModel.getName();
			} else {
				return RMBenchPlugin.getExtensionManager().getDatabaseExtension(dbModel.getDatabaseInfo()).getName();
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
