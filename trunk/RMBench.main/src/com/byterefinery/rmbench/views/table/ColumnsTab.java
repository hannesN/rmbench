/*
 * created 06.04.2005
 * 
 * $Id:ColumnsTab.java 2 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.views.table;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.CellEditorActionHandler;

import com.byterefinery.rmbench.EventManager;
import com.byterefinery.rmbench.RMBenchMessages;
import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.extension.DatabaseExtension;
import com.byterefinery.rmbench.external.model.IDataType;
import com.byterefinery.rmbench.model.Model;
import com.byterefinery.rmbench.model.schema.Column;
import com.byterefinery.rmbench.model.schema.Table;
import com.byterefinery.rmbench.operations.AddColumnOperation;
import com.byterefinery.rmbench.operations.ColumnCommentOperation;
import com.byterefinery.rmbench.operations.ColumnDataTypeOperation;
import com.byterefinery.rmbench.operations.ColumnDefaultOperation;
import com.byterefinery.rmbench.operations.ColumnNameOperation;
import com.byterefinery.rmbench.operations.ColumnNullableOperation;
import com.byterefinery.rmbench.operations.ColumnPrecisionOperation;
import com.byterefinery.rmbench.operations.ColumnPrimaryKeyOperation;
import com.byterefinery.rmbench.operations.ColumnScaleOperation;
import com.byterefinery.rmbench.operations.DeleteColumnOperation;
import com.byterefinery.rmbench.operations.ModifyDatatypeExtraDataOperation;
import com.byterefinery.rmbench.operations.MoveColumnOperation;
import com.byterefinery.rmbench.preferences.PreferenceHandler;
import com.byterefinery.rmbench.util.ImageConstants;
import com.byterefinery.rmbench.util.UpdateableAction;
import com.byterefinery.rmbench.util.keyboardsupport.TableViewerKeyBoardSupporter;

/**
 * a tab that displays table column definitions and allows editing them
 * in place
 * 
 * @author cse
 */
public class ColumnsTab extends DetailsTabGroup.DetailTab {

    //we are using the message constants as editor properties, too
    private static final String[] COLUMN_PROPERTIES = new String[] {
            RMBenchMessages.ColumnsTab_Column_Name, RMBenchMessages.ColumnsTab_Column_DataType,
            RMBenchMessages.ColumnsTab_Column_DatatypeDialog,
            RMBenchMessages.ColumnsTab_Column_Precision, RMBenchMessages.ColumnsTab_Column_Scale,
            RMBenchMessages.ColumnsTab_Column_Default, RMBenchMessages.ColumnsTab_Column_NotNull,
            RMBenchMessages.ColumnsTab_Column_Primkey, RMBenchMessages.ColumnsTab_Column_Comment};

    private EventManager.Listener eventListener = new EventManager.Listener() {

        public void eventOccurred(int eventType, EventManager.Event event) {
            if ( (event.owner != table) && (event.element != table) && !(event.element instanceof Model) ) 
                return;

            switch (eventType) {
                case FOREIGNKEY_ADDED: {
                    Column[] columns = (Column[]) event.info;
                    if (columns != null) {
                        for (int i = 0; i < columns.length; i++) {
                            tableViewer.add(columns[i]);
                        }
                    }
                    break;
                }
                case FOREIGNKEY_DELETED: {
                    Column[] columns = (Column[]) event.info;
                    if (columns != null) {
                        for (int i = 0; i < columns.length; i++) {
                            tableViewer.remove(columns[i]);
                        }
                    }
                    break;
                }
                case FOREIGNKEY_MODIFIED: {
                    tableViewer.refresh();
                    break;
                }
                case COLUMN_MODIFIED: {
                    refreshColumn(event.info, (Column) event.element);
                    break;
                }
                case COLUMNS_MODIFIED: {
                    Column[] columns = (Column[]) event.element;
                    for (int i = 0; i < columns.length; i++) {
                        refreshColumn(event.info, columns[i]);
                    }
                    break;
                }
                case COLUMN_SELECTED: {
                    tableViewer.setSelection(new StructuredSelection(event.element));
                    activate();
                    break;
                }
                case COLUMN_OPENED: {
                    tableViewer.setSelection(new StructuredSelection(event.element));
                    activate();
                    selectedColumnEditable = true;
                    break;
                }
                case COLUMN_ADDED: {
                    setSelectedColumn((Column) event.element);
                    tableViewer.add(selectedColumn);
                    tableViewer.setSelection(new StructuredSelection(selectedColumn));
                    selectedColumnEditable = true;
                    tableViewer.editElement(event.element, 0);
                    dataTypeColumn.pack();
                    break;
                }
                case COLUMN_DELETED: {
                    Column removedColumn = (Column) event.element;
                    if (removedColumn == selectedColumn) {
                        if (table.getColumns().size() == 0) {
                            setSelectedColumn(null);
                        }
                        else {
                            int index = tableViewer.getTable().getSelectionIndex();
                            if (index > 0)
                                index--;
                            setSelectedColumn(table.getColumn(index));
                            tableViewer.getTable().setSelection(index);
                        }
                    }
                    tableViewer.remove(removedColumn);
                    break;
                }
                case TABLE_MODIFIED: {
                	if (event.info.equals(EventManager.Properties.COLUMN_ORDER)) {
                		tableViewer.refresh();
                		tableViewer.setSelection(new StructuredSelection(selectedColumn));
                	}
                	break;
                }
                case MODEL_PROPERTIES_CHANGED: {
                	tableViewer.refresh();
                	break;
                }
            }
        }

        private void refreshColumn(Object info, Column column) {
            tableViewer.refresh(column);
        }

        public void register() {
            RMBenchPlugin.getEventManager().addListener(
                    FOREIGNKEY_ADDED | FOREIGNKEY_DELETED | COLUMN_SELECTED | COLUMN_OPENED
                            | COLUMN_ADDED | COLUMN_DELETED | COLUMN_MODIFIED | COLUMNS_MODIFIED
                            | FOREIGNKEY_MODIFIED | TABLE_MODIFIED | MODEL_PROPERTIES_CHANGED,
                    this);
        }
    };

    private class MessageEraser implements Runnable {

        public void schedule() {
            Display.getCurrent().timerExec(4500, this);
        }

        public void run() {
            if (actionBars != null) {
                actionBars.getStatusLineManager().setErrorMessage(null);
            }
        }
    }

    private MessageEraser messageEraser = new MessageEraser();

    private Column selectedColumn;

    private boolean selectedColumnEditable;

    private TableViewer tableViewer;

    private IActionBars actionBars;

    private CellEditorActionHandler cellEditorActionHandler;

    private UpdateableAction[] actions = new UpdateableAction[4];

    private CellEditor[] cellEditors = new CellEditor[COLUMN_PROPERTIES.length];

    private EditableComboBoxCellEditor dataTypeCellEditor;

    private TableColumn dataTypeColumn;

    public void createControl(Composite parent, IActionBars actionBars) {

        this.actionBars = actionBars;

        final org.eclipse.swt.widgets.Table table = new org.eclipse.swt.widgets.Table(parent,
                SWT.FULL_SELECTION | SWT.SINGLE | SWT.BORDER);

        TableLayout layout = new TableLayout();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        TableColumn column = new TableColumn(table, SWT.NONE);
        column.setText(RMBenchMessages.ColumnsTab_Column_Name);
        layout.addColumnData(new ColumnWeightData(30));

        dataTypeColumn = new TableColumn(table, SWT.NONE);
        dataTypeColumn.setText(RMBenchMessages.ColumnsTab_Column_DataType);
        layout.addColumnData(new ColumnWeightData(20));
        
        column = new TableColumn(table, SWT.NONE);
        column.setText("");
        column.setResizable(false);
        layout.addColumnData(new ColumnPixelData(21, false));

        column = new TableColumn(table, SWT.NONE);
        column.setText(RMBenchMessages.ColumnsTab_Column_Precision);
        column.pack();
        layout.addColumnData(new ColumnPixelData(column.getWidth()));

        column = new TableColumn(table, SWT.NONE);
        column.setText(RMBenchMessages.ColumnsTab_Column_Scale);
        column.pack();
        layout.addColumnData(new ColumnPixelData(column.getWidth()));

        column = new TableColumn(table, SWT.NONE);
        column.setText(RMBenchMessages.ColumnsTab_Column_Default);
        layout.addColumnData(new ColumnWeightData(15));

        column = new TableColumn(table, SWT.NONE);
        column.setText(RMBenchMessages.ColumnsTab_Column_NotNull);
        column.pack();
        layout.addColumnData(new ColumnPixelData(column.getWidth()));

        column = new TableColumn(table, SWT.NONE);
        column.setText(RMBenchMessages.ColumnsTab_Column_Primkey);
        column.pack();
        layout.addColumnData(new ColumnPixelData(column.getWidth()));
        
        column = new TableColumn(table, SWT.NONE);
        column.setText(RMBenchMessages.ColumnsTab_Column_Comment);
        layout.addColumnData(new ColumnWeightData(25));

        
        
        table.setLayout(layout);
        table.setLayoutData(new GridData(GridData.FILL_BOTH));

        cellEditorActionHandler = new CellEditorActionHandler(actionBars);
        cellEditors[0] = new TextCellEditor(table);
        cellEditors[0].setValidator(new ColumnNameValidator());
        cellEditorActionHandler.addCellEditor(cellEditors[0]);

        dataTypeCellEditor = new EditableComboBoxCellEditor(table, new String[0], SWT.NONE);
        cellEditors[1] = dataTypeCellEditor;
        
        cellEditors[2] = new ExtendableDatatypeDialogCellEditor(table, SWT.NONE, 2);
        cellEditorActionHandler.addCellEditor(cellEditors[2]);
        
        cellEditors[3] = new TextCellEditor(table);
        cellEditors[3].setValidator(new SizeScaleValidator(
                RMBenchMessages.ColumnsTab_Column_Precision));
        cellEditorActionHandler.addCellEditor(cellEditors[3]);

        cellEditors[4] = new TextCellEditor(table);
        cellEditors[4]
                .setValidator(new SizeScaleValidator(RMBenchMessages.ColumnsTab_Column_Scale));
        cellEditorActionHandler.addCellEditor(cellEditors[4]);

        cellEditors[5] = new TextCellEditor(table);
        cellEditorActionHandler.addCellEditor(cellEditors[5]);

        cellEditors[6] = new BooleanCellEditor(table, 6, RMBenchPlugin.getImageDescriptor(
                ImageConstants.EMPTY_CHECKBOX).createImage(), RMBenchPlugin.getImageDescriptor(
                ImageConstants.CHECKED_CHECKBOX).createImage());

        cellEditors[7] = new BooleanCellEditor(table, 7, RMBenchPlugin.getImageDescriptor(
                ImageConstants.KEY).createImage(), null);

        cellEditors[8] = new TextCellEditor(table);
        cellEditorActionHandler.addCellEditor(cellEditors[8]);
        
        tableViewer = new TableViewer(table);
        tableViewer.setLabelProvider(new TableViewerLabelProvider());
        tableViewer.setContentProvider(new TableViewerContentProvider());
        tableViewer.setCellEditors(cellEditors);
        tableViewer.setColumnProperties(COLUMN_PROPERTIES);
        tableViewer.setCellModifier(new CellModifier());

        tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                Column column = (Column) selection.getFirstElement();
                setSelectedColumn(column);
            }
        });
        tableViewer.addDoubleClickListener(new IDoubleClickListener() {

            public void doubleClick(DoubleClickEvent event) {
                selectedColumnEditable = true;
                tableViewer.refresh(selectedColumn);
            }
        });
        
        tableViewer.getControl().addMouseListener(new MouseListener(){

			public void mouseDoubleClick(MouseEvent e) {			
			}

			public void mouseDown(MouseEvent e) {
			}

			public void mouseUp(MouseEvent e) {
				org.eclipse.swt.widgets.Table table = tableViewer.getTable();
				IStructuredSelection sel = (IStructuredSelection) tableViewer.getSelection();
				if (sel.isEmpty())
					return;
				Rectangle rec = table.getSelection()[0].getBounds(2);
				if (rec.contains(e.x, e.y)) {
					openDatatypeExtraDialog();
					return;
				}
			}
        	
        });
        
        actions[0] = new MoveColumnAction(false);
        actions[1] = new MoveColumnAction(true);
        actions[2] = new AddColumnAction();
        actions[3] = new DeleteColumnAction();

        MenuManager menuMngr = new MenuManager();
        menuMngr.setRemoveAllWhenShown(true);
        menuMngr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				manager.add(actions[2]);
				manager.add(actions[3]);
			}
        });
        
        Menu menu = menuMngr.createContextMenu(tableViewer.getControl());
        tableViewer.getControl().setMenu(menu);
        
        setTable(this.table);
        eventListener.register();

        //adding keyboardsupport
        TableViewerKeyBoardSupporter support = new TableViewerKeyBoardSupporter(tableViewer);
        support.startSupport();
    }

    private void openDatatypeExtraDialog() {
    	DatabaseExtension extension = RMBenchPlugin.getExtensionManager().getDatabaseExtension(ColumnsTab.this.table.getDatabaseInfo());
		IDataType dataType = selectedColumn.getDataType();
    	IDataType copy = dataType.concreteInstance();
		
    	if (copy.hasExtra())
    		copy.setExtra(dataType.getExtra());
    	if (copy.acceptsScale())
    		copy.setScale(copy.getScale());
    	if (copy.acceptsSize())
    		copy.setSize(dataType.getSize());
		
    	if (extension.openTypeEditor(tableViewer.getControl().getDisplay().getActiveShell(), copy)) {		
			ModifyDatatypeExtraDataOperation op = new ModifyDatatypeExtraDataOperation(selectedColumn, copy);
			op.execute(this);
		}
    }
    
    private void setPrimaryKey(boolean value) {
    	Table table = selectedColumn.getTable();

        if (value == selectedColumn.belongsToPrimaryKey())
            return;

        if ((table.getPrimaryKey() != null) && (table.getPrimaryKey().size() == 1)
                && (table.getReferences().size() > 0)) {
            //check if selected column is part of the pk
            if (table.getPrimaryKey().contains(selectedColumn)) {
                IPreferenceStore store = RMBenchPlugin.getDefault().getPreferenceStore();
                if (!store.getBoolean(PreferenceHandler.PREF_HIDE_PK_DELETION_DIALOG)) {
                    MessageDialogWithToggle msgDialog = MessageDialogWithToggle
                            .openOkCancelConfirm(getShell(),
                                    RMBenchMessages.ColumnsTab_PK_Deletion_DialogTitle,
                                    RMBenchMessages.ColumnsTab_PK_Deletion_Message,
                                    RMBenchMessages.ColumnsTab_KeyMsgToggleText, false,
                                    null, null);
                    if (msgDialog.getReturnCode() != MessageDialog.OK)
                        return;
                    store.setValue(PreferenceHandler.PREF_HIDE_PK_DELETION_DIALOG,
                            msgDialog.getToggleState());
                }
            }
        }

        ColumnPrimaryKeyOperation operation = new ColumnPrimaryKeyOperation(selectedColumn);
        operation.execute(ColumnsTab.this);
    }
    
    public void setTable(Table table) {
        super.setTable(table);
        if (this.tableViewer != null) {
            if (table != null) {
                String[] typeNames = this.table.getSchema().getDatabaseInfo().getPrimaryTypeNames();
                this.dataTypeCellEditor.setItems(typeNames);
                this.tableViewer.setInput(table);
            }
            else {
                this.tableViewer.setInput(null);
            }
            updateActions();
        }
    }

    public Action[] getActions() {
        return actions;
    }

    public Image getImage() {
        return RMBenchPlugin.getImage(ImageConstants.COL_VIEW);
    }

    public String getTitle() {
        return RMBenchMessages.ColumnsTab_Title;
    }

    public String getDescription() {
        return RMBenchMessages.ColumnsTab_Description;
    }

    public void dispose() {
        setErrorMessage(null);
        if (cellEditorActionHandler != null) {
            cellEditorActionHandler.dispose();
        }
        eventListener.unregister();
        this.actionBars = null;
    }

    private void setSelectedColumn(Column column) {
        if (column == selectedColumn)
            return;
        selectedColumn = column;
        updateActions();
    }

    private String setErrorMessage(String message) {
        actionBars.getStatusLineManager().setErrorMessage(message);
        if (message != null)
            messageEraser.schedule();
        return message;
    }

    private void updateActions() {
        for (int i = 0; i < actions.length; i++) {
            actions[i].update();
        }
    }

    private static String asString(long size) {
        return size != IDataType.UNSPECIFIED_SIZE ? String.valueOf(size) : ""; //$NON-NLS-1$
    }

    private static String asString(int scale) {
        return scale != IDataType.UNSPECIFIED_SCALE ? String.valueOf(scale) : ""; //$NON-NLS-1$
    }

    private String formatDBMessage(String err, Column column, Object value) {
        DatabaseExtension dbext = RMBenchPlugin.getExtensionManager().getDatabaseExtension(table.getDatabaseInfo());
        if (dbext.getMessageFormatter() != null) {
            return dbext.getMessageFormatter().getMessage(err, column, value);
        }
        return err;
    }

    private class TableViewerContentProvider implements IStructuredContentProvider {

        public Object[] getElements(Object inputElement) {
            Table table = (Table)inputElement;
            return table != null ? table.getColumns().toArray() : new Object[0];
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }
    
    private static class TableViewerLabelProvider implements ITableLabelProvider {

        public Image getColumnImage(Object element, int columnIndex) {
        	Column column = (Column) element;
        	if (columnIndex == 6) {
                return column.getNullable() ? 
                        RMBenchPlugin.getImage(ImageConstants.EMPTY_CHECKBOX) : 
                            RMBenchPlugin.getImage(ImageConstants.CHECKED_CHECKBOX);
            }

            if (columnIndex == 7) {
                return column.belongsToPrimaryKey() ? 
                        RMBenchPlugin.getImage(ImageConstants.KEY) : null;
            }
            
            if (columnIndex == 2) {
            	return (column.getDataType().hasExtra()) ? RMBenchPlugin.getImage(ImageConstants.COLUMNTTAB_BUTTON) : null;
            }
            return null;
        }

        public String getColumnText(Object element, int columnIndex) {
            Column column = (Column) element;
            switch (columnIndex) {
                case 0:
                    return column.getName();
                case 1:
                    return column.getDataType().getPrimaryName();
                case 3:
                    return asString(column.getSize());
                case 4:
                    return asString(column.getScale());
                case 5:
                    return column.getDefault();
                case 6:
                    return null; //see image
                case 7:
                    return null; //see image
                case 8:
                    return column.getComment();
                default:
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

    private class CellModifier implements ICellModifier {
        public boolean canModify(Object element, String property) {
            //called before selectionChanged event, so we need to check
            //the column element, too
            if (element == selectedColumn && selectedColumnEditable) {
                if (property == RMBenchMessages.ColumnsTab_Column_Precision) {
                    return selectedColumn.getDataType().acceptsSize();
                }
                else if (property == RMBenchMessages.ColumnsTab_Column_Scale) {
                    return selectedColumn.getDataType().acceptsScale();
                }
                else if (property == RMBenchMessages.ColumnsTab_Column_DatatypeDialog) {
                	return ((Column)element).getDataType().hasExtra();
                }
                return true;
            }
            return false;
        }

        public Object getValue(Object element, String property) {
            Column column = (Column) element;
            if (property == RMBenchMessages.ColumnsTab_Column_Name) {
                return column.getName();
            }
            if (property == RMBenchMessages.ColumnsTab_Column_DataType) {
                int index = table.getSchema().getDatabaseInfo().getPrimaryNameIndex(
                        selectedColumn.getDataType());
                return new Integer(index);
            }
            if (property == RMBenchMessages.ColumnsTab_Column_Precision) {
                return asString(column.getSize());
            }
            if (property == RMBenchMessages.ColumnsTab_Column_Scale) {
                return asString(column.getScale());
            }
            if (property == RMBenchMessages.ColumnsTab_Column_Default) {
                String val = column.getDefault();
                return val != null ? val : ""; //$NON-NLS-1$
            }
            if (property == RMBenchMessages.ColumnsTab_Column_NotNull) {
                return new Boolean(column.getNullable());
            }
            if (property == RMBenchMessages.ColumnsTab_Column_Primkey) {
                return new Boolean(column.belongsToPrimaryKey());
            }
            if (property == RMBenchMessages.ColumnsTab_Column_Comment) {
                String val = column.getComment();
                return val != null ? val : ""; //$NON-NLS-1$
            }
            if (property == RMBenchMessages.ColumnsTab_Column_DatatypeDialog) {
            	return element;
            }
            return null;
        }

        public void modify(Object element, String property, Object value) {
        	if (value == null) {
                return; //invalid value
            }
            if (property == RMBenchMessages.ColumnsTab_Column_Name) {
                //no changes made, no operation needed
                if (((String) value).equals(selectedColumn.getName()))
                    return;

                ColumnNameOperation operation = new ColumnNameOperation(selectedColumn,
                        (String) value);
                operation.execute(ColumnsTab.this);
            }
            else if (property == RMBenchMessages.ColumnsTab_Column_DataType) {
                int index = ((Integer) value).intValue();
                IDataType type = table.getSchema().getDatabaseInfo().getDataType(index);

                /* no operation needed if datatype primary not changed */
                if (selectedColumn.getDataType().getPrimaryName().equals(type.getPrimaryName()))
                    return;

                if (selectedColumn.belongsToForeignKey()) {
                    MessageDialog.openInformation(getShell(),
                            RMBenchMessages.ColumnsTab_FKMsgDialogTitle,
                            RMBenchMessages.ColumnsTab_FKMsgMessage);
                    return;
                }
                if (selectedColumn.belongsToPrimaryKey()) {
                    IPreferenceStore store = RMBenchPlugin.getDefault().getPreferenceStore();
                    if (!store.getBoolean(PreferenceHandler.PREF_HIDE_PK_DATATYPE_DIALOG)) {
                        MessageDialogWithToggle msgDialog = MessageDialogWithToggle
                                .openOkCancelConfirm(getShell(),
                                        RMBenchMessages.ColumnsTab_PKMsgDialogTitle,
                                        RMBenchMessages.ColumnsTab_PKMsgMessage,
                                        RMBenchMessages.ColumnsTab_KeyMsgToggleText, false, null,
                                        null);
                        if (msgDialog.getReturnCode() != MessageDialog.OK) {
                            return;
                        }
                        store.setValue(PreferenceHandler.PREF_HIDE_PK_DATATYPE_DIALOG, msgDialog
                                .getToggleState());
                    }

                }

                ColumnDataTypeOperation operation = new ColumnDataTypeOperation(selectedColumn, type);
                operation.execute(ColumnsTab.this);
            }
            if (property == RMBenchMessages.ColumnsTab_Column_Precision) {
                String sval = (String) value;
                long newValue = sval.length() > 0 ? Long.parseLong(sval)
                        : IDataType.UNSPECIFIED_SIZE;

                if (newValue == selectedColumn.getDataType().getSize())
                    return;

                if (selectedColumn.belongsToForeignKey()) {
                    MessageDialog.openInformation(getShell(),
                            RMBenchMessages.ColumnsTab_FKMsgDialogTitle,
                            RMBenchMessages.ColumnsTab_FKMsgMessage);
                    return;
                }
                if (selectedColumn.belongsToPrimaryKey()) {
                    IPreferenceStore store = RMBenchPlugin.getDefault().getPreferenceStore();
                    if (!store.getBoolean(PreferenceHandler.PREF_HIDE_PK_DATATYPE_DIALOG)) {
                        MessageDialogWithToggle msgDialog = MessageDialogWithToggle
                                .openOkCancelConfirm(getShell(),
                                        RMBenchMessages.ColumnsTab_PKMsgDialogTitle,
                                        RMBenchMessages.ColumnsTab_PKMsgMessage,
                                        RMBenchMessages.ColumnsTab_KeyMsgToggleText, false, null,
                                        null);
                        if (msgDialog.getReturnCode() != MessageDialog.OK) {
                            return;
                        }
                        store.setValue(PreferenceHandler.PREF_HIDE_PK_DATATYPE_DIALOG, msgDialog
                                .getToggleState());
                    }

                }
                
                ColumnPrecisionOperation operation = new ColumnPrecisionOperation(selectedColumn,
                        newValue);
                operation.execute(ColumnsTab.this);
            }
            else if (property == RMBenchMessages.ColumnsTab_Column_Scale) {
                String sval = (String) value;
                int newValue = sval.length() > 0 ? Integer.parseInt(sval) : IDataType.UNSPECIFIED_SCALE;
                
                if (newValue == selectedColumn.getDataType().getScale())
                    return;
                
                if (selectedColumn.belongsToForeignKey()) {
                    MessageDialog.openInformation(getShell(),
                            RMBenchMessages.ColumnsTab_FKMsgDialogTitle,
                            RMBenchMessages.ColumnsTab_FKMsgMessage);
                            
                    return;
                }
                if (selectedColumn.belongsToPrimaryKey()) {
                    IPreferenceStore store = RMBenchPlugin.getDefault().getPreferenceStore();
                    if (!store.getBoolean(PreferenceHandler.PREF_HIDE_PK_DATATYPE_DIALOG)) {
                        MessageDialogWithToggle msgDialog = MessageDialogWithToggle
                                .openOkCancelConfirm(getShell(),
                                        RMBenchMessages.ColumnsTab_PKMsgDialogTitle,
                                        RMBenchMessages.ColumnsTab_PKMsgMessage,
                                        RMBenchMessages.ColumnsTab_KeyMsgToggleText, false, null,
                                        null);
                        if (msgDialog.getReturnCode() != MessageDialog.OK) {
                            return;
                        }
                        store.setValue(
                                PreferenceHandler.PREF_HIDE_PK_DATATYPE_DIALOG, msgDialog.getToggleState());
                    }

                }
                
                ColumnScaleOperation operation = new ColumnScaleOperation(selectedColumn, newValue);
                operation.execute(ColumnsTab.this);
            }
            else if (property == RMBenchMessages.ColumnsTab_Column_Primkey) {
                setPrimaryKey(((Boolean)value).booleanValue());
            }
            else if (property == RMBenchMessages.ColumnsTab_Column_NotNull) {
                if (((Boolean) value).booleanValue() == selectedColumn.getNullable())
                    return;
                
                if (selectedColumn.belongsToPrimaryKey()) {
                    MessageDialog.openInformation(getShell(),
                            RMBenchMessages.ColumnsTab_NullableInfo_Title,
                            RMBenchMessages.ColumnsTab_NullableInfo_Message);
                    return;
                }
                ColumnNullableOperation operation = new ColumnNullableOperation(selectedColumn);
                operation.execute(ColumnsTab.this);
            }
            else if (property == RMBenchMessages.ColumnsTab_Column_Default) {
                if (((String) value).equals(selectedColumn.getDefault()))
                    return;

                ColumnDefaultOperation operation = new ColumnDefaultOperation(selectedColumn,
                        (String) value);
                operation.execute(ColumnsTab.this);
            }
            else if (property == RMBenchMessages.ColumnsTab_Column_Comment) {
                if (((String) value).equals(selectedColumn.getComment()))
                    return;

                ColumnCommentOperation operation = new ColumnCommentOperation(selectedColumn,
                        (String) value);
                operation.execute(ColumnsTab.this);
            }
        }
    }

    /*
     * a validator to prevent duplicate column names
     */
    private class ColumnNameValidator implements ICellEditorValidator {

        public String isValid(Object value) {
            for (Column column : table.getColumns()) {
                if (column != selectedColumn && column.getName().equals(value)) {
                    return setErrorMessage(RMBenchMessages.ColumnsTab_Err_DuplicateName);
                }
            }
            return null;
        }
    }

    /*
     * validator that ensures numeric input and applicability for precision 
     * or scale
     */
    private class SizeScaleValidator implements ICellEditorValidator {

        private String property;

        public SizeScaleValidator(String property) {
            this.property = property;
        }

        public String isValid(Object value) {
            String stringVal = (String) value;
            if (stringVal.length() > 0) {
                long val = IDataType.UNSPECIFIED_SIZE;
                try {
                    val = Long.parseLong(stringVal);
                }
                catch (NumberFormatException x) {
                    return setErrorMessage(RMBenchMessages.ColumnsTab_Err_Numeric);
                }
                if (val < 0) {
                    return setErrorMessage(RMBenchMessages.ColumnsTab_Err_Numeric);
                }
                if (property.equals(RMBenchMessages.ColumnsTab_Column_Precision)) {
                    String err = selectedColumn.getDataType().validateSize(val);
                    if (err != null) {
                        String msg = formatDBMessage(err, selectedColumn, new Long(val));
                        return setErrorMessage(msg);
                    }
                }
                else if (property.equals(RMBenchMessages.ColumnsTab_Column_Scale)) {
                    String err = selectedColumn.getDataType().validateScale((int) val);
                    if (err != null) {
                        String msg = formatDBMessage(err, selectedColumn, new Integer((int) val));
                        return setErrorMessage(msg);
                    }
                }
            }
            else {
                if (property.equals(RMBenchMessages.ColumnsTab_Column_Precision)) {
                    if (selectedColumn.getDataType().requiresSize()) {
                        return setErrorMessage(RMBenchMessages.ColumnsTab_Err_PrecisionRequired);
                    }
                }
                else if (property.equals(RMBenchMessages.ColumnsTab_Column_Scale)) {
                    if (selectedColumn.getDataType().requiresScale()) {
                        return setErrorMessage(RMBenchMessages.ColumnsTab_Err_ScaleRequired);
                    }
                }
            }
            setErrorMessage(null);
            return null;
        }
    }

    private class AddColumnAction extends UpdateableAction {
        AddColumnAction() {
            super();
            setText(RMBenchMessages.ColumnsTab_AddColumn_Text);
            setToolTipText(RMBenchMessages.ColumnsTab_AddColumn_Description);
            setImageDescriptor(RMBenchPlugin.getImageDescriptor(ImageConstants.ADD));
            setDisabledImageDescriptor(RMBenchPlugin
                    .getImageDescriptor(ImageConstants.ADD_disabled));
        }

        public boolean isEnabled() {
            return table != null;
        }

        public void run() {
            //need to save old selection, so we can reset it after adding a new column
            IStructuredSelection oldSelection = (IStructuredSelection) tableViewer.getSelection();
            if (tableViewer.isCellEditorActive()) {
            	if (!oldSelection.isEmpty()) {
            		// we pretend to edit the same row again, so the previouse editing will be saved 
            		tableViewer.editElement(oldSelection.getFirstElement(), 0);
            		// cancle the new edit state, without saving state
            		tableViewer.cancelEditing();
            	}
            }
            AddColumnOperation operation = new AddColumnOperation(table);
            operation.execute(ColumnsTab.this);
        }
    }
    
    private class MoveColumnAction extends UpdateableAction {
    	private boolean up;
    	
    	public MoveColumnAction(boolean up) {
    		super();
    		this.up = up;
    		if (up) {
    			setText("Move Column up");
    			setImageDescriptor(RMBenchPlugin.getImageDescriptor(ImageConstants.UP));
    		} else {
    			setText("Move Column down");
    			setImageDescriptor(RMBenchPlugin.getImageDescriptor(ImageConstants.DOWN));
    		}
		}
    	
    	public boolean isEnabled() {
    		return (selectedColumn!=null);
    	}
    	
    	public void run() {
    		MoveColumnOperation op;
    		if (up) {
    			op = new MoveColumnOperation(selectedColumn, -1);
    		} else {
    			op = new MoveColumnOperation(selectedColumn, 1);
    		}
    		op.execute(ColumnsTab.this);
    	}
    }

    private class DeleteColumnAction extends UpdateableAction {
        DeleteColumnAction() {
            super();
            setText(RMBenchMessages.ColumnsTab_DeleteColumn_Text);
            setToolTipText(RMBenchMessages.ColumnsTab_DeleteColumn_Description);
            setImageDescriptor(ImageConstants.DELETE_DESC);
            setDisabledImageDescriptor(ImageConstants.DELETE_DISABLED_DESC);
        }

        public boolean isEnabled() {
            return selectedColumn != null;
        }

        public void run() {
            DeleteColumnOperation operation = new DeleteColumnOperation(table, selectedColumn);
            operation.execute(ColumnsTab.this);
        }
    }


    /**
     * The Celleditor for the extandable datatype widgets
     * @author Hannes Niederhausen
     *
     */
    private class ExtendableDatatypeDialogCellEditor extends CellEditor {
    	private Column value;
    	private int columnNumber;
    	
		/**
		 * @param parent
		 * @param style
		 * @param columnNumber the number of the column, which uses the editor
		 */
		public ExtendableDatatypeDialogCellEditor(Composite parent, int style, int columnNumber) {
			super(parent, style);
			this.columnNumber = columnNumber;
		}


		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.CellEditor#createControl(org.eclipse.swt.widgets.Composite)
		 */
		protected Control createControl(Composite parent) {
			Composite comp=new Composite(parent, SWT.NONE);
			comp.setBackground(comp.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
			comp.addMouseListener(new MouseAdapter() {
				public void mouseUp(MouseEvent e) {
					openDatatypeExtraDialog();
				}
			});
			
			comp.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					if (e.character==' ') {
						openDatatypeExtraDialog();
					}
				} 
			});
			/*
			 * The pain listener positions the editor to the fronsite of the table cell.
			 * If he wouldn't the icon of the tablelabelprovider would still be there.
			 */
			comp.addPaintListener(new PaintListener() {

				public void paintControl(PaintEvent e) {
					Composite comp = (Composite) e.widget;
					Image image = RMBenchPlugin.getImage(ImageConstants.COLUMNTTAB_BUTTON);
					int y = (comp.getBounds().height-image.getBounds().height)/2;
					e.gc.drawImage(image, 1, y);
					
					org.eclipse.swt.widgets.Table table = (org.eclipse.swt.widgets.Table) comp.getParent(); 
                    int x = 0;
                    x = table.getSelection()[0].getBounds(columnNumber).x;
                    y = table.getSelection()[0].getBounds(columnNumber).y;
                    int height = table.getSelection()[0].getBounds(columnNumber).height;
                    //set size and relocate
                    comp.setBounds(x, y, image.getBounds().width+2, height);
				}
			});
			
			comp.addFocusListener(new FocusListener() {

				public void focusGained(FocusEvent e) {
				}

				public void focusLost(FocusEvent e) {
					ExtendableDatatypeDialogCellEditor.this.focusLost();					
				}
				
			});
			
			return comp;
		}
		
		/**
		 * Returns the IDatatype altered by the dialog
		 */
		protected Object doGetValue() {
			return value;
		}

		/**
		 * Sets the focus to the editor control
		 */
		protected void doSetFocus() {
			getControl().setFocus();
		}

		/**
		 * 
		 * @param value the IDatatype which should be altered by the dialog opened with this editor
		 */
		protected void doSetValue(Object value) {
			this.value = (Column) value;
		}
    }
}
