/*
 * created 16.04.2005
 * 
 * $Id: IndexEditorDialog.java 669 2007-10-27 08:23:31Z cse $
 */
package com.byterefinery.rmbench.dialogs;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.JFaceColors;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.model.schema.Column;
import com.byterefinery.rmbench.model.schema.Index;
import com.byterefinery.rmbench.model.schema.Table;
import com.byterefinery.rmbench.operations.AddIndexOperation;
import com.byterefinery.rmbench.operations.ModifyIndexOperation;
import com.byterefinery.rmbench.util.ImageConstants;


/**
 * Index definition editor
 * 
 * @author cse
 */
public class IndexEditorDialog extends Dialog {

    private static final int INDEX_NAME_MAXLEN = 10;
    private static final int INDEX_NAME_CHARS = 30;
    private static final Pattern INDEX_NAME_Pattern = 
        Pattern.compile("[A-Za-z][A-Za-z0-9_]*");
    
    private static final String PROPERTY_ASCDESC = "ascdesc";
    private static final String ASC = "ASC";
    private static final String DESC = "DESC";
    
    private final Table table;
    private Index editIndex;
    private java.util.List<String> availableColumns;
    
    private String name = "";
    private boolean unique;
    private final java.util.List<Boolean> indexOrders = new ArrayList<Boolean>();
    private final java.util.List<String> indexColumns = new ArrayList<String>();
    
    private Label messageLabel;
    private TableViewer columnsViewer;
    private org.eclipse.swt.widgets.Table columnsTable;
    
    private Combo columnsComboWidget;
    private Button addColumnButton;
    
    private ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
    
    private Button columnDeleteButton;
    private Button columnUpButton;
    private Button columnDownButton;

    
    public IndexEditorDialog(Shell parentShell, Table table) {
        super(parentShell);
        this.table = table;
    }

    /**
     * open the editor dialog on an existing index
     * @param index the index to be changed
     */
    public void open(Index index) {
        
        this.name = index.getName();
        this.unique = index.isUnique();
        Column[] columns = index.getColumns();
        for (int i = 0; i < columns.length; i++) {
            addIndexColum(columns[i].getName(), index.isAscending(columns[i]));
        }
        this.editIndex = index;
        super.open();
    }
    
    protected Control createDialogArea(Composite parent) {
        Composite mainComposite = (Composite) super.createDialogArea(parent);
        
        GridData gd;
        
        final Composite nameLabelComp = new Composite(mainComposite, SWT.NONE);
        nameLabelComp.setLayout(new GridLayout(2, false));
        
        final Label nameLabel = new Label(nameLabelComp, SWT.NONE);
        nameLabel.setText(Messages.IndexEditorDialog_Name);
        gd = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
        nameLabel.setLayoutData(gd);

        final Text nameText = new Text(nameLabelComp, SWT.BORDER | SWT.SINGLE);     
        gd = new GridData(SWT.LEFT, SWT.FILL, false, true);
        gd.widthHint = convertWidthInCharsToPixels(INDEX_NAME_CHARS);
        nameText.setLayoutData(gd);
        nameText.setTextLimit(INDEX_NAME_CHARS);
        nameText.setText(name);
        
        nameText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                validateName(nameText);
                updateOKButton();
            }
        });

        messageLabel = new Label(mainComposite, SWT.NONE);
        messageLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        messageLabel.setForeground(JFaceColors.getErrorText(messageLabel.getDisplay()));
        
        final Button uniqueCheck = new Button(mainComposite, SWT.CHECK);
        uniqueCheck.setText(Messages.IndexEditorDialog_Unique);
        uniqueCheck.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
        uniqueCheck.setSelection(unique);
        
        uniqueCheck.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                unique = uniqueCheck.getSelection();
            }
        });
        
        final Group columnsGroup = new Group(mainComposite, SWT.NONE);
        columnsGroup.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, true));
        columnsGroup.setLayout(new GridLayout(2, false));
        
        columnsComboWidget = new Combo(columnsGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
        columnsComboWidget.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, true));
        computeAvailableColumns();
        columnsComboWidget.addSelectionListener(new SelectionAdapter() {
            
            public void widgetSelected(SelectionEvent e) {
                updateColumnButtons();
            }
        });
        
        addColumnButton = new Button(columnsGroup, SWT.PUSH);
        addColumnButton.setImage(RMBenchPlugin.getImage(ImageConstants.ADD));
        addColumnButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                int index = columnsComboWidget.getSelectionIndex();
                String col = (String)availableColumns.get(index);
                addIndexColum(col);
                columnsViewer.add(col);
                computeAvailableColumns();
                
                updateColumnButtons();
                updateOKButton();
            }
        });
        
        createColumnsTableViewer(columnsGroup);
        
        columnDeleteButton = new Button(columnsGroup, SWT.PUSH);
        columnDeleteButton.setImage(sharedImages.getImage(ISharedImages.IMG_TOOL_DELETE));
        columnDeleteButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                int index = columnsTable.getSelectionIndex();
                String col = removeIndexColumn(index);
                columnsViewer.remove(col);
                computeAvailableColumns();
                updateColumnButtons();
            }
        });
        columnUpButton = new Button(columnsGroup, SWT.PUSH);
        columnUpButton.setImage(RMBenchPlugin.getImage(ImageConstants.UP));
        columnUpButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                int index = columnsTable.getSelectionIndex();
                moveIndexColumn(index, -1);
                columnsViewer.refresh();
                updateColumnButtons();
            }
        });
        columnDownButton = new Button(columnsGroup, SWT.PUSH);
        columnDownButton.setImage(RMBenchPlugin.getImage(ImageConstants.DOWN));
        columnDownButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                int index = columnsTable.getSelectionIndex();
                moveIndexColumn(index, 1);
                columnsViewer.refresh();
                updateColumnButtons();
            }
        });
        return mainComposite;
    }

    protected Control createButtonBar(Composite parent) {
        Control bar = super.createButtonBar(parent);
        updateColumnButtons();
        updateOKButton();
        return bar;
    }

    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(Messages.IndexEditorDialog_Title);
    }
    
    protected void okPressed() {
        Column[] cols = new Column[indexColumns.size()];
        for (Column column : table.getColumns()) {
            int index = indexColumns.indexOf(column.getName()); 
            if(index >= 0) {
                cols[index] = column;
            }
        }
        boolean[] bools = new boolean[indexOrders.size()];
        int i = 0;
        for (Boolean b : indexOrders) {
            bools[i] = b.booleanValue();
            i++;
        }
        if(editIndex != null) {
            ModifyIndexOperation operation = new ModifyIndexOperation(editIndex, name, cols, unique, bools);
            operation.execute(null);
        }
        else {
            AddIndexOperation operation = new AddIndexOperation(name, cols, table, unique, bools);
            operation.execute(null);
        }
        super.okPressed();
    }

    private void moveIndexColumn(int index, int offset) {
        String col = (String)indexColumns.remove(index + offset);
        Boolean b = (Boolean)indexOrders.remove(index + offset);
        
        indexColumns.add(index, col);
        indexOrders.add(index, b);
    }
    
    private String removeIndexColumn(int index) {
        indexOrders.remove(index);
        return (String)indexColumns.remove(index);
    }
    
    private void addIndexColum(String col) {
        indexColumns.add(col);
        indexOrders.add(new Boolean(true));
    }
    
    private void addIndexColum(String col, boolean ascending) {
        indexColumns.add(col);
        indexOrders.add(new Boolean(ascending));
    }
    
    /*
     * create the table viewer that shows the currently selected index columns
     */
    private void createColumnsTableViewer(Composite parent) {
        
        columnsTable = new org.eclipse.swt.widgets.Table(
                parent, SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.SINGLE | SWT.BORDER);

        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.heightHint = columnsTable.getItemHeight() * 4;
        gd.verticalSpan = 3;
        columnsTable.setLayoutData(gd);
        
        TableLayout layout = new TableLayout();
        layout.addColumnData(new ColumnWeightData(80));
        layout.addColumnData(new ColumnWeightData(30));
        columnsTable.setLayout(layout);
        columnsTable.setFont(parent.getFont());

        columnsTable.setHeaderVisible(false);
        columnsTable.setLinesVisible(true);
        
        TableColumn column;
        column = new TableColumn(columnsTable, SWT.LEFT);
        column.setText(Messages.IndexEditorDialog_Column_Name);
        column = new TableColumn(columnsTable, SWT.LEFT);
        column.setText(Messages.IndexEditorDialog_Column_Order);

        CellEditor[] editors = new CellEditor[2];
        editors[0] = new TextCellEditor(columnsTable);
        editors[1] = new CheckboxCellEditor(columnsTable);

        columnsViewer = new TableViewer(columnsTable);
        columnsViewer.setCellEditors(editors);
        columnsViewer.setColumnProperties(new String[]{".", PROPERTY_ASCDESC});
        columnsViewer.setLabelProvider(new ColumnsLabelProvider());
        columnsViewer.setContentProvider(new ColumnsContentProvider());
        columnsViewer.setCellModifier(new ColumnsCellModifier());

        columnsViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            
            public void selectionChanged(SelectionChangedEvent event) {
                updateColumnButtons();
            }
        });
        
        columnsViewer.setInput(indexColumns);
    }
    
    private void validateName(Text nameText) {
        String value = nameText.getText();
        Matcher matcher = INDEX_NAME_Pattern.matcher(value);
        
        if(!matcher.matches() || value.length() > INDEX_NAME_MAXLEN) {
            messageLabel.setText(Messages.IndexEditorDialog_msg_invalidname);
            name = null;
        }
        else {
            boolean valid = true;
            for (Index index : table.getIndexes()) {
                if(index != editIndex && index.getName().equals(value)) {
                    messageLabel.setText(Messages.IndexEditorDialog_msg_duplicatename);
                    name = null;
                    valid = false;
                }
            }
            if(valid) {
                messageLabel.setText("");
                name = value;
            }
        }
    }
    
    private void updateColumnButtons() {
        addColumnButton.setEnabled(columnsComboWidget.getSelectionIndex() >= 0);

        int index = columnsTable.getSelectionIndex();
        
        columnDeleteButton.setEnabled(index >= 0);
        columnUpButton.setEnabled(index > 0);
        columnDownButton.setEnabled(index >= 0 && index < (indexColumns.size() - 1));
    }
    
    private void updateOKButton() {
        boolean isOk = name != null && name.length() > 0 && indexColumns.size() > 0;
        getButton(IDialogConstants.OK_ID).setEnabled(isOk);
    }

    private int computeAvailableColumns() {
        java.util.List<Column> columns = table.getColumns();
        availableColumns = new ArrayList<String>(columns.size());
        int maxChars = 0;
        for (Column col : columns) {
            if(!indexColumns.contains(col.getName())) {
                availableColumns.add(col.getName());
                maxChars = Math.max(maxChars, col.getName().length());
            }
        }
        String[] cols = (String[])availableColumns.toArray(new String[availableColumns.size()]);
        columnsComboWidget.setItems(cols);
        
        return maxChars;
    }
    
    private class ColumnsLabelProvider implements ITableLabelProvider {

        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        public String getColumnText(Object element, int columnIndex) {
            switch(columnIndex) {
                case 0:
                    return (String)element;
                case 1: {
                    int index = indexColumns.indexOf(element);
                    Boolean b = (Boolean)indexOrders.get(index);
                    return b.booleanValue() ? ASC : DESC;
                }
            }
            return null;
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
    
    private class ColumnsContentProvider implements IStructuredContentProvider {

        public Object[] getElements(Object inputElement) {
            return indexColumns.toArray();
        }
        public void dispose() {
        }
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }
    
    private class ColumnsCellModifier implements ICellModifier {

        public boolean canModify(Object element, String property) {
            return property == PROPERTY_ASCDESC;
        }

        public Object getValue(Object element, String property) {
            if(property == PROPERTY_ASCDESC) {
                int index = indexColumns.indexOf(element);
                return indexOrders.get(index);
            }
            return null;
        }

        public void modify(Object element, String property, Object value) {
            if(property == PROPERTY_ASCDESC) {
                if(element instanceof Item)
                    element = ((Item)element).getData();
                int index = indexColumns.indexOf(element);
                indexOrders.set(index, (Boolean)value);
                
                columnsViewer.refresh(element);
            }
        }
    }
}
