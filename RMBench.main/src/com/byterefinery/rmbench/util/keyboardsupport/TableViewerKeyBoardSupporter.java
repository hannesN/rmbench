/*
  * created 28-Feb-2006
  *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
  *
  * $Id: TableViewerKeyBoardSupporter.java 279 2006-03-03 14:01:59Z hannesn $
  */
package com.byterefinery.rmbench.util.keyboardsupport;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;

/**
 * Well ... the JFace <CODE>TableViewer<CODE> class does not provide
 * text celleditor keyboard support like if TAB is pressed in a column,
 * the focus does not go into the next column etc.,
 * 
 * After observing JDT method signature refactoring widget which does the
 * desired keyboard support, a generic utility listener has been developed
 * on the same lines.
 * 
 * Usage goes as ...
 * 
 * <P><PRE><CODE>
 * 
 *              TableViewer tblViewer = new TableViewer(SWT.SINGLE | SWT.FULL_SELECTION | SWT.| SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER)
 *              ...
 *              CellEditor[] cellEditors = ...
 *              ...
 *              tblViewer.setCellEditors(cellEditors);
 *              
 *              TableViewerKeyBoardSupporter supporter = new TableViewerKeyBoardSupporter(tblViewer);
 *              supporter.startSupport();
 *  
 * </CODE></PRE></P>
 * @author venkataramana (newsgroup: eclipse-platform)
 */
public class TableViewerKeyBoardSupporter {

    protected TableViewer tableViewer;

    public TableViewerKeyBoardSupporter(TableViewer tableViewer) {
        super();
        this.tableViewer = tableViewer;
    }

    /**
     * After the cell editors have been set on tableviewer, this method
     * should be called to start giving keyboard support.
     */
    public void startSupport() {
        tableViewer.getTable().addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == SWT.F2 && e.stateMask == SWT.NONE) {
                    editColumnOrNextPossible(0);
                    e.doit = false;
                }
            }
        });

        tableViewer.getTable().addTraverseListener(new TraverseListener() {
            public void keyTraversed(TraverseEvent e) {
                if (e.detail == SWT.TRAVERSE_RETURN && e.stateMask == SWT.NONE) {
                    editColumnOrNextPossible(0);
                    e.detail = SWT.TRAVERSE_NONE;
                }
            }
        });

        /* add table-textcelleditors key and traverse listeners */
        CellEditor[] cellEditors = tableViewer.getCellEditors();
        if (cellEditors != null) {
            for (int colIndex = 0; colIndex < cellEditors.length; colIndex++) {
                CellEditor cellEditor = cellEditors[colIndex];
                if (cellEditor != null) {
                    if (cellEditor.getControl()!=null) {
                        /*won't work with combo boxes, so we let it out.. at the moment
                        cellEditor.getControl().addKeyListener(
                                new CellEditorKeyListener(cellEditor, colIndex));
                        */
                        cellEditor.getControl().addTraverseListener(
                                new CellEditorTraverseListener(cellEditor, colIndex));
                    }
                }
            }
        }
    }

    protected int nextColumn(int column) {
        int col = (column >= tableViewer.getTable().getColumnCount() - 1) ? 0 : column + 1;
        
        //we got to switch to the next row
        if (col==0) {
            int nextRow = tableViewer.getTable().getSelectionIndex() + 1;
            
            if (nextRow >= tableViewer.getTable().getItemCount())
                return column;
            
            tableViewer.getTable().setSelection(nextRow);
        }
        
        return col;
    }

    protected int prevColumn(int column) {
        int col = (column <= 0) ? tableViewer.getTable().getColumnCount() - 1 : column - 1;
        
//      we got to switch to the previous row
        if (col==tableViewer.getTable().getColumnCount()-1) {
            int nextRow = tableViewer.getTable().getSelectionIndex() - 1;
            
            if (nextRow < 0)
                return column;
            
            tableViewer.getTable().setSelection(nextRow);
        }
        
        return col;
    }

    protected void editColumnOrNextPossible(final int column) {
        Object selectedElem = getSelectedElement();
        if (selectedElem == null)
            return;

        int nextColumn = column;
        do {
            tableViewer.editElement(selectedElem, nextColumn);
            if (tableViewer.isCellEditorActive())
                return;
            nextColumn = nextColumn(nextColumn);
        } while (nextColumn != column);
    }

    protected void editColumnOrPrevPossible(int column) {
        Object selectedElem = getSelectedElement();
        if (selectedElem == null)
            return;

        int prevColumn = column;
        do {
            tableViewer.editElement(selectedElem, prevColumn);
            if (tableViewer.isCellEditorActive())
                return;
            prevColumn = prevColumn(prevColumn);
        } while (prevColumn != column);
    }

    private Object getSelectedElement() {
        IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
        if (selection == null || selection.isEmpty())
            return null;

        return selection.getFirstElement();
    }

    class CellEditorKeyListener extends KeyAdapter {
        private int column;

        public CellEditorKeyListener(CellEditor editor, int column) {
            super();
            this.column = column;
        }

        public void keyPressed(KeyEvent e) {
            if (e.stateMask == SWT.MOD1) {
                switch (e.keyCode) {
                    case SWT.ARROW_DOWN:
                        e.doit = false;
                        int nextRow = tableViewer.getTable().getSelectionIndex() + 1;
                        if (nextRow >= tableViewer.getTable().getItemCount())
                            break;
                        tableViewer.getTable().setSelection(nextRow);
                        editColumnOrPrevPossible(column);
                        break;
    
                    case SWT.ARROW_UP:
                        e.doit = false;
                        int prevRow = tableViewer.getTable().getSelectionIndex() - 1;
                        if (prevRow < 0)
                            break;
                        tableViewer.getTable().setSelection(prevRow);
                        editColumnOrPrevPossible(column);
                        break;
                    case SWT.ARROW_RIGHT:
                        editColumnOrNextPossible(nextColumn(column));
                        break;

                    case SWT.ARROW_LEFT:
                        editColumnOrPrevPossible(prevColumn(column));
                        break;
                }
            }
        }
    }

    class CellEditorTraverseListener extends Object implements TraverseListener {
        private CellEditor editor;

        private int column;

        public CellEditorTraverseListener(CellEditor editor, int column) {
            super();
            this.editor = editor;
            this.column = column;
        }

        public void keyTraversed(TraverseEvent e) {
            switch (e.detail) {
                case SWT.TRAVERSE_TAB_NEXT:
                    editColumnOrNextPossible(nextColumn(column));
                    e.detail = SWT.TRAVERSE_NONE;
                    break;

                case SWT.TRAVERSE_TAB_PREVIOUS:
                    editColumnOrPrevPossible(prevColumn(column));
                    e.detail = SWT.TRAVERSE_NONE;
                    break;

                case SWT.TRAVERSE_ESCAPE:
                    tableViewer.cancelEditing();
                    e.detail = SWT.TRAVERSE_NONE;
                    break;

                case SWT.TRAVERSE_RETURN:
                    editor.deactivate();
                    e.detail = SWT.TRAVERSE_NONE;
                    break;

                default:
                    break;
            }
        }
    }
}
