/*
 * created 7.11.2006
 * 
 * Copyright 2006, ByteRefinery
 * 
 * $Id$
 */
package com.byterefinery.rmbench.database.mysql;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.util.ImageConstants;

/**
 * This is the dialog used for entering the list elements of the ENUM and SET datatypes
 * 
 * @author Hannes Niederhausen
 */
public class EnumAndSetDialog extends Dialog {

	protected String title;
	protected Text inputText;
	protected Button addButton;
	protected Button deleteButton;
	protected Button upButton;
	protected Button downButton;
	protected MySQLListDatatype dataType;
	
	/** List of enum/set definitions)*/
	protected List<String> types = new ArrayList<String>();
	
	private ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
	private TableViewer	typesViewer;

	
	protected EnumAndSetDialog(Shell parentShell, String title, MySQLListDatatype datatype) {
		super(parentShell);
		dataType = datatype;
		setShellStyle(SWT.TITLE | SWT.RESIZE);
		this.title = title;
	}
	
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
        newShell.setText(title);
	}

	protected Control createDialogArea(Composite parent) {
		Composite mainComposite = (Composite) super.createDialogArea(parent);
        
		mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        mainComposite.setLayout(new GridLayout(2, false));
        
        inputText = new Text(mainComposite, SWT.BORDER);
        inputText.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
        inputText.addVerifyListener(new VerifyListener() {

			public void verifyText(VerifyEvent e) {
				if (e.character==',')
					e.doit = false;
			}
        	
        });
        inputText.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				updateAddButton();
			}
        });
        
        addButton = new Button(mainComposite, SWT.PUSH);
        addButton.setImage(RMBenchPlugin.getImage(ImageConstants.ADD));
        addButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
            	String input = inputText.getText().trim();
                inputText.setText("");
                inputText.setFocus();
                
            	types.add(input);
            	typesViewer.refresh();
            	updateAddButton();
            	getButton(IDialogConstants.OK_ID).setEnabled(true);
            }
        });
        
        createListViewer(mainComposite);
        
        Composite buttonComp = new Composite(mainComposite, SWT.NONE);
        GridData gd = new GridData(SWT.NONE, SWT.FILL, false, true);
        gd.verticalAlignment = SWT.CENTER;
        buttonComp.setLayoutData(gd);
        GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = 0;
        buttonComp.setLayout(layout);
        
        deleteButton = new Button(buttonComp, SWT.PUSH);
        deleteButton.setImage(sharedImages.getImage(ISharedImages.IMG_TOOL_DELETE));
		deleteButton.setEnabled(false);
        deleteButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                IStructuredSelection sel = (IStructuredSelection) typesViewer.getSelection();
                int index = types.indexOf(sel.getFirstElement());
                types.remove(index);
                typesViewer.refresh();
                if(index > 0)
                	typesViewer.setSelection(new StructuredSelection(types.get(index-1)));
                else if(types.size() > 0)
                	typesViewer.setSelection(new StructuredSelection(types.get(0)));
                
                inputText.setText("");
                if (types.size()==0)
                	getButton(IDialogConstants.OK_ID).setEnabled(false);
                else
                	getButton(IDialogConstants.OK_ID).setEnabled(true);
                
            }
        });
        upButton = new Button(buttonComp, SWT.PUSH);
        upButton.setImage(RMBenchPlugin.getImage(ImageConstants.UP));
        upButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                IStructuredSelection sel = (IStructuredSelection) typesViewer.getSelection();
                int index = types.indexOf(sel.getFirstElement());
                moveElement(index, index-1);
                typesViewer.refresh();
            }
        });
        downButton = new Button(buttonComp, SWT.PUSH);
        downButton.setImage(RMBenchPlugin.getImage(ImageConstants.DOWN));
        downButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
            	IStructuredSelection sel = (IStructuredSelection) typesViewer.getSelection();
                int index = types.indexOf(sel.getFirstElement());
                moveElement(index, index+1);
                typesViewer.refresh();
            }
        });
        
        String typesArray[] = dataType.getElements();
        for (int i=0; i<typesArray.length; i++) {
        	types.add(typesArray[i]);
        }
        
        typesViewer.setInput("");
        
        return mainComposite;
	}

	protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        getButton(IDialogConstants.OK_ID).setEnabled(false);
    }

    /**
	 * enable the add button if the contents of the edit field do not correspond to an item
	 * already in the list
	 */
	protected void updateAddButton() {
		addButton.setEnabled(types.indexOf(inputText.getText()) < 0);
	}

	protected void okPressed() {
		dataType.setElements((String[]) types.toArray(new String[types.size()]));
		super.okPressed();
	}

	/**
	 * @return the array of new types  
	 */
	public String[] getTypes() {
		return (String[]) types.toArray(new String[types.size()]);
	}
	
	private void moveElement(int oldIndex, int newIndex) {
		if ( (newIndex>=types.size()) || (newIndex<0))
			return;
		
		String element = (String) types.get(oldIndex);
		types.remove(oldIndex);
		types.add(newIndex, element);
	}
	
	private void createListViewer(Composite parent) {
		typesViewer = new TableViewer(parent, SWT.BORDER);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.verticalSpan = 4;
		typesViewer.getControl().setLayoutData(gd);
		typesViewer.setLabelProvider(new TypeLabelProvider());
		typesViewer.setContentProvider(new TypesContentProvider());
		typesViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection sel = (IStructuredSelection) event.getSelection();
				if (sel.isEmpty()) {
					deleteButton.setEnabled(false);
					return;
				}
				
				inputText.setText((String) sel.getFirstElement());
				deleteButton.setEnabled(true);
			}
		});		
	}
	
	protected Point getInitialSize() {
		return new Point(300, 400);
	}
	
	private class TypesContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object inputElement) {
			return types.toArray(new String[types.size()]);
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}
	
	private class TypeLabelProvider extends LabelProvider {

		public String getText(Object element) {
			return (String) element;
		}
	}
}
