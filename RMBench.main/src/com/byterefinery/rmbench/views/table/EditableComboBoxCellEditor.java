/*
 * created 27-Feb-2006
 *
 * Copyright 2006, DynaBEAN Consulting
 *
 * $Id: EditableComboBoxCellEditor.java 666 2007-10-01 19:32:51Z cse $
 */
package com.byterefinery.rmbench.views.table;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class EditableComboBoxCellEditor extends CellEditor {

    /** itemlist of all items*/
    private String[] completeItems;
    
    /** current itemlist rendered by the combobox*/
    private List<String> renderedItems;

    private CCombo comboBox;
    
    private int selection;
    
    private String inputString;
    
    private boolean listenKeys = false;
    
    public EditableComboBoxCellEditor() {
        super();
    }

    public EditableComboBoxCellEditor(Composite parent, String[] items) {
        super(parent);
        setItems(items);
    }

    public EditableComboBoxCellEditor(Composite parent, String[] items, int style) {
        super(parent, style);
        setItems(items);
    }
    
    protected Control createControl(Composite parent) {
        comboBox = new CCombo(parent, getStyle());
        comboBox.setFont(parent.getFont());

        comboBox.addKeyListener(new KeyAdapter() {
            // hook key pressed - see PR 14201  
            public void keyPressed(KeyEvent e) {
                keyReleaseOccured(e);
            }
        });
        
        comboBox.addSelectionListener(new SelectionAdapter() {
            public void widgetDefaultSelected(SelectionEvent event) {
                applyEditorValueAndDeactivate();
            }

            public void widgetSelected(SelectionEvent event) {
                selection = getSelectedIndex();
                markDirty();
            }
        });

        
        comboBox.addTraverseListener(new TraverseListener() {
            public void keyTraversed(TraverseEvent e) {
                if (e.detail == SWT.TRAVERSE_ESCAPE
                        || e.detail == SWT.TRAVERSE_RETURN) {
                    e.doit = false;
                }
            }
        });

        comboBox.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                EditableComboBoxCellEditor.this.focusLost();
            }
        });
        
        comboBox.addModifyListener(new ModifyListener() {
           public void modifyText(ModifyEvent e) {
               if (listenKeys) {
                   inputString = comboBox.getText().toUpperCase();
                   setIndex();
               }
            } 
        });
        
        comboBox.setEditable(true);
        
        return comboBox;
    }
    
    public void setItems(String[] items) {
        completeItems = items;
        listenKeys = false;
        inputString = "";
        renderedItems = new ArrayList<String>(completeItems.length);
        for (int i=0; i<completeItems.length; i++) {
            renderedItems.add(items[i]);
        }
        
        populateComboBoxItems();
    }

    public String[] getItems() {
        return completeItems;
    }
    
    protected Object doGetValue() {
        return new Integer(selection);
    }

    private void setIndex() {
        if (comboBox.getText().length()==0) {
            selection=0;
            return;
        }
        
        listenKeys=false;
        for (int i=0; i<completeItems.length; i++) {
            if (completeItems[i].startsWith(inputString)) {
                selection = i;
                return;
            }
        }
        
        selection = 0;
        
    }
    
    /* (non-Javadoc)
     * Method declared on CellEditor.
     */
    protected void doSetFocus() {
        comboBox.setFocus();
    }

    /**
     * The <code>ComboBoxCellEditor</code> implementation of
     * this <code>CellEditor</code> framework method sets the 
     * minimum width of the cell.  The minimum width is 10 characters
     * if <code>comboBox</code> is not <code>null</code> or <code>disposed</code>
     * eles it is 60 pixels to make sure the arrow button and some text is visible.
     * The list of CCombo will be wide enough to show its longest item.
     */
    public LayoutData getLayoutData() {
        LayoutData layoutData = super.getLayoutData();
        if ((comboBox == null) || comboBox.isDisposed())
            layoutData.minimumWidth = 90;
        else {
            // make the comboBox 10 characters wide
            GC gc = new GC(comboBox);
            layoutData.minimumWidth = (gc.getFontMetrics()
                    .getAverageCharWidth() * 15) + 10;
            gc.dispose();
        }
        return layoutData;
    }

    /**
     * The <code>ComboBoxCellEditor</code> implementation of
     * this <code>CellEditor</code> framework method
     * accepts a zero-based index of a selection.
     *
     * @param value the zero-based index of the selection wrapped
     *   as an <code>Integer</code>
     */
    protected void doSetValue(Object value) {
        Assert.isTrue(comboBox != null && (value instanceof Integer));
        selection = ((Integer) value).intValue();
        comboBox.select(selection);
    }

    /**
     * Updates the list of choices for the combo box for the current control.
     */
    private void populateComboBoxItems() {
        if (comboBox != null && renderedItems != null) {
            comboBox.removeAll();
            for (int i = 0; i < renderedItems.size(); i++)
                comboBox.add((String) renderedItems.get(i), i);
            setValueValid(true);
            selection = 0;
        }
    }

    public int getSelectedIndex() {
        int comboSelection = comboBox.getSelectionIndex();
        if (comboSelection== -1) {
            setValueValid(false);
            return (selection=-1);
        }
        
        String selected = (String) renderedItems.get(comboSelection);

        for (int i=0; i<completeItems.length; i++) {
            if (completeItems[i].equals(selected)) {
                selection = i;
                comboBox.setText(selected);
                setValueValid(true);
                return selection;
            }
        }
        selection=-1;
        setValueValid(false);
        return selection;
    }
    
    /**
     * Applies the currently selected value and deactiavates the cell editor
     */
    private void applyEditorValueAndDeactivate() {
        if (!isValueValid()) {
            // try to insert the current value into the error message.
            setErrorMessage(MessageFormat.format(getErrorMessage(),
                    new Object[] { comboBox.getText() }));
        }
        comboBox.select(selection);
        fireApplyEditorValue();
        deactivate();
    }

    /*
     *  (non-Javadoc)
     * @see org.eclipse.jface.viewers.CellEditor#focusLost()
     */
    protected void focusLost() {
        if (isActivated()) {
            applyEditorValueAndDeactivate();
        }
    }

    /*
     *  (non-Javadoc)
     * @see org.eclipse.jface.viewers.CellEditor#keyReleaseOccured(org.eclipse.swt.events.KeyEvent)
     */
    protected void keyReleaseOccured(KeyEvent keyEvent) {
        
        if ((keyEvent.stateMask==SWT.NONE) && (keyEvent.doit)) {   
            if (keyEvent.character == '\u001b') { // Escape character
                fireCancelEditor();
            } else if (keyEvent.character == SWT.TAB) { // tab key
                applyEditorValueAndDeactivate();
            } else if ((keyEvent.character == SWT.DEL) || (keyEvent.character == SWT.BS)
                    || (Character.isLetter(keyEvent.character))) {
                listenKeys = true;
            } else if ( (keyEvent.keyCode==SWT.ARROW_RIGHT) ) {
                comboBox.select(selection);
                inputString="";
            }
        } else
            keyEvent.doit = false;
    }
    
}
