/*
 * created 22-Feb-2006
 *
 * Copyright 2006, DynaBEAN Consulting
 *
 * $Id: PrimaryKeyDependencyDialog.java 665 2007-09-29 15:31:59Z cse $
 */
package com.byterefinery.rmbench.dialogs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.external.model.IDataType;
import com.byterefinery.rmbench.model.Model;
import com.byterefinery.rmbench.model.schema.Column;
import com.byterefinery.rmbench.model.schema.ForeignKey;
import com.byterefinery.rmbench.model.schema.PrimaryKey;
import com.byterefinery.rmbench.model.schema.Table;
import com.byterefinery.rmbench.util.ScriptWriter;
import com.byterefinery.rmbench.util.Utils;

/**
 * Dialog to confirm primary key dependencies 
 * @author Hannesn
 *
 */
public class PrimaryKeyDependencyDialog extends AbstractDependencyDialog {

    private PrimaryKey primaryKey;
    
    private Button deleteButton;
    private Button removeFKButton;
    
    private boolean deleteFK;
    
    /** Map which contains for every foreignkey an array of newColumns...*/
    private Map<ForeignKey, ColumnInfo[]> foreignKeyColumns;
    
    private Column newPKColumns[];
    
    /**Flag if the dependency check is for a pk modification or deletion*/
    private boolean deletePK;
    
    private List<ForeignKey> foreignKeys;
    
    private List<Column> removeableFKColumns;
    
    public PrimaryKeyDependencyDialog(Shell parentShell, PrimaryKey primaryKey) {
        super(parentShell);
        removeableFKColumns = Utils.emptyList();
        this.primaryKey = primaryKey;
        deleteFK=true;
    }

    public PrimaryKeyDependencyDialog(PrimaryKey primaryKey) {
        this(RMBenchPlugin.getModelView().getViewSite().getShell(), primaryKey);
        deletePK = true;
    }
    
    public PrimaryKeyDependencyDialog(PrimaryKey primaryKey, Column columns[], boolean delete) {
        this(RMBenchPlugin.getModelView().getViewSite().getShell(), primaryKey);
        deletePK = delete;
        newPKColumns = columns;
    }
    
    protected String getDetails() {
        ScriptWriter writer = new ScriptWriter();
                
        if (foreignKeys==null)
            calculateDependencies();
        
        //Action text
        if (deletePK)
            writer.println("Action: delete primary key"); //$NON-NLS-1$
        else
            writer.println("Action: modify primary key"); //$NON-NLS-1$
        
        //no implied actions...
        if(foreignKeys==Collections.EMPTY_LIST)
            return writer.getString();
        
        //implied actions:
        writer.println("Implied Actions:"); //$NON-NLS-1$
        
        
        writer.indent(1);
        if (deletePK) {
            for (ForeignKey fk : foreignKeys) {
                if (deleteFK) {
                    writer.print("delete foreignkey"); //$NON-NLS-1$
                }
                else {
                    writer.print("remove foreignkey constraint from column "); //$NON-NLS-1$
                }
                writer.print(" from table "); //$NON-NLS-1$
                writer.println(fk.getTable().getName());
            }
        }
        else {
            if (deleteFK)
                writer.println("delete foreignkey columns"); //$NON-NLS-1$
            else
                writer.println("remove foreignkey constraint from columns "); //$NON-NLS-1$
            writer.indent();
            for (Column col : removeableFKColumns) {
                writer.print(col.getName());
                writer.print(" from table ");
                writer.println(col.getTable().getName());
            }
            writer.dedent();
            writer.println("adding new columns"); //$NON-NLS-1$
            writer.indent();
            for (ForeignKey fk : foreignKeyColumns.keySet()) {
                ColumnInfo infos[] = (ColumnInfo[]) foreignKeyColumns.get(fk);
                for (int i=0; i<infos.length; i++) {
                    if (!infos[i].isExist()) {
                        writer.print(infos[i].getName()); //$NON-NLS-1$
                        writer.print(" to table "); //$NON-NLS-1$
                        writer.println(infos[i].getTable().getName()); //$NON-NLS-1$
                    }
                }
            }
        }
        return writer.getString();
    }

    public boolean calculateDependencies() {
        if (primaryKey.getTable().getReferences().size()==0) {
            foreignKeys = Utils.emptyList();
            return false;
        }
        
        //need to create new array list for concurency reasons 
        foreignKeys = new ArrayList<ForeignKey>(primaryKey.getTable().getReferences().size());
        for (ForeignKey fk : primaryKey.getTable().getReferences()) {
            foreignKeys.add(fk);
        }
        
        if (deletePK)
            return true;
        
        foreignKeyColumns = new HashMap<ForeignKey, ColumnInfo[]>(foreignKeys.size());
        for (ForeignKey fk : foreignKeys) {
            foreignKeyColumns.put(fk, getNewColumns(fk));
        }
        
        return true;
    }

    private ColumnInfo[] getNewColumns(ForeignKey fk) {
        Column pkOldColumns[]=primaryKey.getColumns();
        Column fkOldColumns[]=fk.getColumns();
        
        ColumnInfo fkNewColumnInfo[]=new ColumnInfo[newPKColumns.length];
        
        boolean found=false;
        
        for (int i=0; i<pkOldColumns.length; i++) {
            found=false;
            for (int j=0; j<newPKColumns.length; j++) {
                if ( pkOldColumns[i]==newPKColumns[j] ) {
                    fkNewColumnInfo[j]= new ColumnInfo(fkOldColumns[i]);
                    found = true;
                }
            }
            if (!found) {
                if (removeableFKColumns==Collections.EMPTY_LIST)
                    removeableFKColumns= new ArrayList<Column>();
                removeableFKColumns.add(fkOldColumns[i]);
            }
        }
        // now we have all columns, which are already in the fk
        for (int i=0; i<fkNewColumnInfo.length; i++) {
            if (fkNewColumnInfo[i]==null) {
                //create new column
                fkNewColumnInfo[i] = createForeignKeyColumnInfo(newPKColumns[i], fk.getTable());
            }
        }
        
        return fkNewColumnInfo;
    }

    protected void createOptionsArea(Composite dialogArea) {
        Group actionGroup = new Group(dialogArea, SWT.NONE);
        actionGroup.setText(Messages.PrimaryKeyDependencyDialog_GroupTitle);
        
        GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false);
        actionGroup.setLayoutData(gd);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        actionGroup.setLayout(layout);
        
        deleteButton = new Button(actionGroup, SWT.RADIO);
        deleteButton.setText(Messages.PrimaryKeyDependencyDialog_DeleteButton);
        gd = new GridData();
        gd.horizontalSpan = 2;
        gd.verticalAlignment = SWT.TOP;
        deleteButton.setLayoutData(gd);
        deleteButton.setSelection(true);
        deleteButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                deleteFK=true;
                setDetailsText(getDetails());
            }
        });
        
        removeFKButton = new Button(actionGroup, SWT.RADIO);
        removeFKButton.setText(Messages.PrimaryKeyDependencyDialog_RemoveButton);
        removeFKButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                deleteFK=false;
                setDetailsText(getDetails());
            }
        });
    }

    public List<ForeignKey> getForeignKeys() {
        return foreignKeys;
    }

    public boolean getDeleteFK() {
        return deleteFK;
    }

    
    private ColumnInfo createForeignKeyColumnInfo(Column targetColumn, Table sourceTable) {
        Model model = RMBenchPlugin.getActiveModel();
        ColumnInfo result;
        
        
        String columnName = model.getNameGenerator().generateForeignKeyColumnName(
                model.getIModel(),
                sourceTable.getITable(), 
                primaryKey.getIPrimaryKey(), 
                targetColumn.getIColumn());
        result = new ColumnInfo(columnName, sourceTable, targetColumn.getDataType(), false);
        
        return result;
    }

    public Map<ForeignKey, ColumnInfo[]> getForeignKeyColumns() {
        return foreignKeyColumns;
    }
    public List<Column> getRemoveableFKColumns() {
        return removeableFKColumns;
    }
    
    /**
     * This class contains information about a class. 
     * @author Hannes Niederhausen
     *
     */
    public class ColumnInfo {
        private String name;
        private Table table;
        private IDataType dataType;
        private boolean exist;      //Flag if the column is already in the table
        
        /**
         * 
         * @param name      name of column
         * @param table     table which contains (will contain) column
         * @param dataType  datatype of column
         * @param exist     flag if column is already in table  
         */
        public ColumnInfo(String name, Table table, IDataType dataType, boolean exist) {
            super();
            this.name = name;
            this.table = table;
            this.dataType = dataType;
            this.exist = exist;
        }
        
        /**
         * 
         * @param name      name of column
         * @param table     table which contains (will contain) column
         * @param dataType  datatype of column
         * @param exist     flag if column is already in table  
         */
        public ColumnInfo(String name, Table table, IDataType dataType) {
            super();
            this.name = name;
            this.table = table;
            this.dataType = dataType;
            this.exist = false;
        }
        
        /**
         * 
         * @param column column, which info should be stored
         */
        public ColumnInfo (Column column) {
            name = column.getName();
            table = column.getTable();
            dataType = column.getDataType();
            exist = true;
        }
        
        public IDataType getDataType() {
            return dataType;
        }
        public void setDataType(IDataType dataType) {
            this.dataType = dataType;
        }
        public boolean isExist() {
            return exist;
        }
        public void setExist(boolean exist) {
            this.exist = exist;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public Table getTable() {
            return table;
        }
        public void setTable(Table table) {
            this.table = table;
        }
        
        public Column getColumn() {
            if (exist)
                return table.getColumn(name);
            else {
                exist=true;
                return new Column(table, name, dataType);
            }
        }
    }



 
}
