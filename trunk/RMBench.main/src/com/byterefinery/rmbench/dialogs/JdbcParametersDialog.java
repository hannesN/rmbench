/*
 * created 31.07.2006
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *  $Id: JdbcParametersDialog.java 666 2007-10-01 19:32:51Z cse $
 */
package com.byterefinery.rmbench.dialogs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.util.ImageConstants;

/**
 * The Dialog to add some additional options to a jdbc url.
 * @author Hannes Niederhausen
 */
public class JdbcParametersDialog extends Dialog {

	private final static Pattern OPTIONPATTERN = Pattern.compile("((\\p{Alnum}+|_)+)=(\\p{Alnum}+)");
	
    private static final String PROP_CELL_DEL = "delete";
    private static final String PROP_CELL_KEY = "name";
    private static final String PROP_CELL_VALUE = "value";
    
    private final List<Option> options;

    private TableViewer viewer;
    private Button addButton;
    
    private String nameValue, valueValue;

    private static class Option {
    	String key;
    	String value;
    	
    	Option(String key, String value) {
    		this.key = key;
    		this.value = value;
    	}

		public boolean isEmpty() {
			return key.length() == 0;
		}
    }
    
    public JdbcParametersDialog(Shell parentShell,String options) {
        super(parentShell);
        this.options = new ArrayList<Option>();
        setShellStyle(getShellStyle() | SWT.RESIZE);
        splitOptions(options);
    }

    protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.JdbcParametersDialog_Title);
	}

	private void splitOptions(String options) {
        Matcher matcher = OPTIONPATTERN.matcher(options);
        while (matcher.find()) {
            String name = matcher.group(1);
            String value = matcher.group(3);
            
            this.options.add(new Option(name, value));
        }
    }
    
    protected Control createDialogArea(Composite parent) {
        
        Composite composite = new Composite((Composite) super.createDialogArea(parent), SWT.NONE);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        viewer = new TableViewer(composite, SWT.FULL_SELECTION);

        viewer.setContentProvider(new OptionContentProvider());
        viewer.setLabelProvider(new OptionLabelProvider());
        
        Table table = viewer.getTable();
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.heightHint = convertHeightInCharsToPixels(7);
        table.setLayoutData(gd);
        table.setHeaderVisible(true);
        
        viewer.setCellModifier(new OptionCellModifier());
        viewer.setCellEditors(new CellEditor[]{
        		new TextCellEditor(table), 
        		new TextCellEditor(table),
        		new CheckboxCellEditor(table)});
        viewer.setColumnProperties(new String[]{PROP_CELL_KEY, PROP_CELL_VALUE, PROP_CELL_DEL});
        
        TableColumn tc = new TableColumn(table, SWT.NONE);
        tc.setText(Messages.JdbcParametersDialog_Col_Name);
        tc.setWidth(120);
        tc = new TableColumn(table, SWT.NONE);
        tc.setText(Messages.JdbcParametersDialog_Col_Value);
        tc.setWidth(120);
        tc = new TableColumn(table, SWT.NONE);
        tc.setWidth(20);

        final Composite fieldGroup = new Composite(composite, SWT.NONE);
        fieldGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
        fieldGroup.setLayout(new GridLayout(3, false));
        
        Label nameLabel = new Label(fieldGroup, SWT.NONE);
        nameLabel.setText(Messages.JdbcParametersDialog_Col_Name+":");
        final Text nameField = new Text(fieldGroup, SWT.BORDER);
        gd = new GridData(SWT.FILL, SWT.NONE, true, false);
        nameField.setLayoutData(gd);
        nameField.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				nameValue = nameField.getText();
				addButton.setEnabled(addButtonEnabled());
			}
        });
        
        Composite addComposite = new Composite(fieldGroup, SWT.NONE);
        gd = new GridData(SWT.LEFT, SWT.TOP, false, true);
        gd.verticalSpan = 2;
        addComposite.setLayoutData(gd);
        addComposite.setLayout(new GridLayout());
        
        Label valueLabel = new Label(fieldGroup, SWT.NONE);
        valueLabel.setText(Messages.JdbcParametersDialog_Col_Value+":");
        final Text valueField = new Text(fieldGroup, SWT.BORDER);
        gd = new GridData(SWT.FILL, SWT.NONE, true, false);
        valueField.setLayoutData(gd);
        valueField.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				valueValue = valueField.getText(); 
				addButton.setEnabled(addButtonEnabled());
			}
        });
        
        addButton = new Button(addComposite, SWT.PUSH);
        addButton.setImage(RMBenchPlugin.getImage(ImageConstants.ADD));
        gd = new GridData(SWT.CENTER, SWT.CENTER, false, false);
        addButton.setLayoutData(gd);
		addButton.setEnabled(false);
        addButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Option option = findOption(nameValue);
				if(option != null) {
					option.value = valueValue;
				}
				else {
					options.add(new Option(nameValue, valueValue));
				}
                nameField.setText("");
                valueField.setText("");
                viewer.refresh();
			}
        });
        
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				StructuredSelection sel = (StructuredSelection)event.getSelection();
				Option option = (Option)sel.getFirstElement();
				if(option != null) {
					nameField.setText(option.key);
					valueField.setText(option.value);
				}
				else {
					nameField.setText("");
					valueField.setText("");
				}
			}
        });
        viewer.setInput(options);

        return composite;
    }
    
	protected boolean addButtonEnabled() {
		return (nameValue != null && nameValue.length() > 0) && (valueValue != null && valueValue.length() > 0);
	}

	private Option findOption(String key) {
		for (Option option : options) {
			if(option.key.equals(nameValue))
				return option;
		}
		return null;
	}
	
	/**
	 * @return a string representation of the current options, separated by &amp;
	 */
	public String getOptionString() {
        StringBuffer buffer=new StringBuffer();
        
        for (Iterator<Option> it=options.iterator(); it.hasNext();) {
            Option option = it.next();
            if(!option.isEmpty()) {
	            buffer.append(option.key+"="+option.value);
	            if (it.hasNext())
	                buffer.append("&");
            }
        }
        
        return buffer.toString();
    }
    
    private class OptionContentProvider implements IStructuredContentProvider {

        public Object[] getElements(Object inputElement) {
            return options.toArray();
        }

        public void dispose() {
        }
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }
    
    private class OptionLabelProvider implements ITableLabelProvider {

        public String getColumnText(Object element, int columnIndex) {
        	
        	switch(columnIndex) {
        	case 0: return ((Option)element).key;
        	case 1: return ((Option)element).value;
        	}
            return null;
        }

        public Image getColumnImage(Object element, int columnIndex) {
            return columnIndex == 2 ? ImageConstants.DELETE_IMG : null;
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

    private class OptionCellModifier implements ICellModifier {

		public boolean canModify(Object element, String property) {
            return property.equals(PROP_CELL_DEL);
        }

        public Object getValue(Object element, String property) {
        	return Boolean.TRUE;
        }

        public void modify(Object element, String property, Object value) {
        	
        	Option option;
        	if(element instanceof Item)
            	option = (Option)((Item)element).getData();
        	else
        		option = (Option)element;
        	
            if(property.equals(PROP_CELL_DEL)) {
        		options.remove(option);
            }
            viewer.refresh();
        }
    }
}
