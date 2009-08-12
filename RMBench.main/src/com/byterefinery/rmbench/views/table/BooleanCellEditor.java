/*
 * created 28-Feb-2006
 *
 * Copyright 2006, DynaBEAN Consulting
 *
 * $Id: BooleanCellEditor.java 306 2006-03-16 21:29:22Z hannesn $
 */
package com.byterefinery.rmbench.views.table;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.util.ImageConstants;

public class BooleanCellEditor extends CellEditor {
    
    private CheckBoxWidget checkBox;
    private int columnNumber;
    
    private boolean selection = false;
    
    private Image checkedImage = null;
    private Image uncheckedImage = null;
    
    public BooleanCellEditor() {
        super();
    }
    
    public BooleanCellEditor(Composite parent, int columnNumber) {
        setStyle(SWT.NONE);
        this.columnNumber = columnNumber;
        create(parent);
    }

    public BooleanCellEditor(Composite parent, int columnNumber, Image checkedImage, Image uncheckedImage) {
        setStyle(SWT.NONE);
        this.columnNumber = columnNumber;
        this.uncheckedImage = uncheckedImage;
        this.checkedImage = checkedImage;
        create(parent);
    }

    protected Control createControl(Composite parent) {
        checkBox = new CheckBoxWidget(parent, SWT.NONE, checkedImage, uncheckedImage); 

        checkBox.addKeyListener(new KeyAdapter() {
           public void keyPressed(KeyEvent e) {
               if (e.character==' ') {
                   toggle();
               }
           } 
        });
        
        checkBox.addFocusListener(new FocusAdapter() {
           public void focusLost(FocusEvent e) {
                BooleanCellEditor.this.focusLost();
            } 
        });
        
        checkBox.addMouseListener(new MouseAdapter() {

            public void mouseDown(MouseEvent e) {
                toggle();
            }            
        });
        
        return checkBox;
    }
    
    protected Object doGetValue() {
        return new Boolean(selection);
    }

    protected void doSetFocus() {
        getControl().setFocus();
    }
    
    
    protected void focusLost() {
        if (isActivated()) {
            fireApplyEditorValue();
            deactivate();
        }
    }
    
    private void toggle() {
        selection= !selection;
        checkBox.setSelected(selection);
        valueChanged(true, true);
    }
        
    protected void doSetValue(Object value) {
        if (value instanceof Boolean) {
            selection = ((Boolean)value).booleanValue();
            checkBox.setSelected(selection);
        }
    }
        
    /**
     * This class emulates a check box. In default mode it uses the both Images for the constants EMPTY_CHECKBOX and CHECKED_CHECKBOX.<br/>
     * If you choose to use other icons be sure there size is 16x16.
     * @author Hannes Niederhausen
     *
     */
    private class CheckBoxWidget extends Composite {
        private Image checkedImage = null;
        private Image uncheckedImage = null;
        
        private boolean checked;
        
        CheckBoxWidget(Composite parent, int style) {
            super(parent, style);
            createControl();
        }
                
        /**
         * Creates the widget using the images given as parameter. Be aware, if the widget is disposed, the images also will be.
         * @param parent
         * @param style
         * @param checkedImage
         * @param uncheckedImage
         */
        CheckBoxWidget(Composite parent, int style, Image checkedImage, Image uncheckedImage) {
            super(parent, style);
            this.uncheckedImage = uncheckedImage;
            this.checkedImage = checkedImage;
            createControl();
        }
        
        private void createControl() {
            if ((uncheckedImage==null) && (checkedImage==null)){
                uncheckedImage = RMBenchPlugin.getImageDescriptor(ImageConstants.EMPTY_CHECKBOX).createImage();
                checkedImage = RMBenchPlugin.getImageDescriptor(ImageConstants.CHECKED_CHECKBOX).createImage();
            }
            
            addPaintListener(new PaintListener() {

                public void paintControl(PaintEvent e) {
                    Composite w = (Composite) e.widget;
                                        
                    int y = (w.getBounds().height-16)/2;
                    if (checked) {
                        if (checkBox!=null)
                            e.gc.drawImage(checkedImage, 0, y);
                    }
                    else {
                        if (uncheckedImage!=null)
                            e.gc.drawImage(uncheckedImage, 0, y);
                    }
                    
                    //caclulating position of editor, need to do this to hide icon of table
                    Table table= (Table) w.getParent();
                    int x = 0;
                    x = table.getSelection()[0].getBounds(columnNumber).x;
                    y = table.getSelection()[0].getBounds(columnNumber).y;
                    
                    setLocation(x+1, y+1);
                }
                
            });
        }
        
        public void dispose() {
            checkedImage.dispose();
            uncheckedImage.dispose();
            super.dispose();
        }
        
        public Point computeSize(int wHint, int hHint) {
            return new Point (16,16);
        }
        
        public Point getSize() {
            return new Point (16,16);
        }
        
        public Point computeSize(int wHint, int hHint, boolean changed) {
            wHint=16;
            hHint=16;
            return super.computeSize(wHint, hHint, changed);
        }
        
        public void setSelected(boolean value) {
            checked = value;
            redraw();
        }
        
    }
}
