/*
  * created 21-Feb-2006
  *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
  *
  * $Id: ColumnDependencyDialog.java 656 2007-08-31 00:11:31Z cse $
  */
package com.byterefinery.rmbench.dialogs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

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
import com.byterefinery.rmbench.model.schema.Column;
import com.byterefinery.rmbench.model.schema.ForeignKey;
import com.byterefinery.rmbench.model.schema.Index;
import com.byterefinery.rmbench.model.schema.UniqueConstraint;
import com.byterefinery.rmbench.util.ScriptWriter;

public class ColumnDependencyDialog extends AbstractDependencyDialog {

    private Column column;

    private Button deleteButton;

    private Button removeFKButton;

    private Button deleteConstraintButton;
    
    private Button deleteIndexButton;

    private Button removeColumnButton;

    private boolean deleteFK;

    private boolean deleteUnique;
    
    private boolean deleteIndex;

    private int primaryKeyIndex;

    private List<ForeignKey> foreignKeysDeletable;
    
    private List<ForeignKey> foreignKeysUnDeletable;
    
    private List<Index> indexList;

    public ColumnDependencyDialog(Shell parentShell, Column column) {
        super(parentShell);
        this.column = column;
        deleteFK = true;
        deleteUnique = true;
        deleteIndex = true;
    }

    public ColumnDependencyDialog(Column column) {
        this(RMBenchPlugin.getModelView().getViewSite().getShell(), column);

    }

    protected String getDetails() {
        ScriptWriter writer = new ScriptWriter();

        if (foreignKeysDeletable == null)
            calculateDependencies();

        //Action text
        writer.println("Action: delete Column"); //$NON-NLS-1$
        //implied actions:
        writer.println("Implied Actions:"); //$NON-NLS-1$

        ForeignKey fk;
        writer.indent(1);
        // print foreignkeys, which can only be removed (without deltion of column)
        for (Iterator<ForeignKey> it = foreignKeysUnDeletable.iterator(); it.hasNext();) {
            fk = it.next();
            writer.print("remove foreignkey constraint from  column "); //$NON-NLS-1$

            writer.print(fk.getColumn(primaryKeyIndex).getName());
            writer.print(" from table "); //$NON-NLS-1$
            writer.println(fk.getColumn(primaryKeyIndex).getTable().getName());
        }

        // print deletable foreignkeys
        for (Iterator<ForeignKey> it = foreignKeysDeletable.iterator(); it.hasNext();) {
            fk = it.next();
            if (deleteFK) {
                writer.print("delete column "); //$NON-NLS-1$
            }
            else {
                writer.print("remove foreignkey constraint from  column "); //$NON-NLS-1$
            }
            writer.print(fk.getColumn(primaryKeyIndex).getName());
            writer.print(" from table "); //$NON-NLS-1$
            writer.println(fk.getColumn(primaryKeyIndex).getTable().getName());
        }

        for (Iterator<UniqueConstraint> it = column.getUniqueConstraints().iterator(); it.hasNext();) {
            UniqueConstraint constraint = it.next();
            if (deleteUnique) {
                writer.print("delete constraint "); //$NON-NLS-1$
            }
            else {
                writer.print("remove column from  constraint "); //$NON-NLS-1$
            }
            writer.println(constraint.getName());
        }
        
        for (Iterator<Index> it = indexList.iterator(); it.hasNext();) {
            Index index = it.next();
            if (deleteIndex) {
                writer.print("delete index "); //$NON-NLS-1$
            }
            else {
                writer.print("remove column from  index "); //$NON-NLS-1$
            }
            writer.println(index.getName());
        }

        return writer.getString();
    }

    public boolean calculateDependencies() {
        foreignKeysDeletable = Collections.emptyList();
        foreignKeysUnDeletable = Collections.emptyList();
        indexList = Collections.emptyList();
        
        if (((column.getTable().getPrimaryKey() == null) || (!column.getTable().getPrimaryKey()
                .contains(column))|| (column.getTable().getReferences().size()==0))
                && (column.getUniqueConstraints().size() == 0)
                && (column.getTable().getIndexes().size()==0)) {
            return false;
        }
        if (column.belongsToPrimaryKey()) {
            primaryKeyIndex = column.getTable().getPrimaryKey().getIndex(column);

            // need to create new array list for concurency reasons
            foreignKeysDeletable = new ArrayList<ForeignKey>(column.getTable().getReferences().size());
            foreignKeysUnDeletable = new ArrayList<ForeignKey>();
            
            for (Iterator<ForeignKey> it = column.getTable().getReferences().iterator(); it.hasNext();) {
                ForeignKey foreignKey = (ForeignKey) it.next();
                Column fkColumn = foreignKey.getColumn(primaryKeyIndex);
                if ( (fkColumn.getForeignKeys().size()==1) && (!fkColumn.belongsToPrimaryKey()))
                    foreignKeysDeletable.add(foreignKey);
                else
                    foreignKeysUnDeletable.add(foreignKey);
            }
        }
        List<Index> indexes = column.getTable().getIndexes();
        for (Iterator<Index> it=indexes.iterator(); it.hasNext();) {
        	Index index = it.next();
        	if (index.contains(column)) {
        		if (indexList.isEmpty()) {
        			indexList = new ArrayList<Index>();        			
        		}
        		indexList.add(index);
        	}
        }

        return true;
    }

    protected void createOptionsArea(Composite dialogArea) {
        
        
        if (column.belongsToPrimaryKey()) {
            Group actionGroup = new Group(dialogArea, SWT.NONE);
            actionGroup.setText(Messages.ColumnDependencyDialog_GroupName);
    
            GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false);
            actionGroup.setLayoutData(gd);
            GridLayout layout = new GridLayout();
            layout.numColumns = 2;
            actionGroup.setLayout(layout);
    
            deleteButton = new Button(actionGroup, SWT.RADIO);
            deleteButton.setText(Messages.ColumnDependencyDialog_DeleteButton);
            gd = new GridData();
            gd.horizontalSpan = 2;
            gd.verticalAlignment = SWT.TOP;
            deleteButton.setLayoutData(gd);
            deleteButton.setSelection(true);
            deleteButton.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    deleteFK = true;
                    setDetailsText(getDetails());
                }
            });
    
            removeFKButton = new Button(actionGroup, SWT.RADIO);
            removeFKButton.setText(Messages.ColumnDependencyDialog_RemoveButton);
            removeFKButton.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    deleteFK = false;
                    setDetailsText(getDetails());
                }
            });
        }
        if (column.getUniqueConstraints().size() > 0) {
			Group actionGroup = new Group(dialogArea, SWT.NONE);
			actionGroup.setText("Unique constraint handling");

			GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false);
			actionGroup.setLayoutData(gd);

			GridLayout layout = new GridLayout();
			layout.numColumns = 2;
			actionGroup.setLayout(layout);

			deleteConstraintButton = new Button(actionGroup, SWT.RADIO);
			deleteConstraintButton.setText("delete constraints");
			gd = new GridData();
			gd.horizontalSpan = 2;
			gd.verticalAlignment = SWT.TOP;
			deleteConstraintButton.setLayoutData(gd);
			deleteConstraintButton.setSelection(true);
			deleteConstraintButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					deleteUnique = true;
					setDetailsText(getDetails());
				}
			});

			removeColumnButton = new Button(actionGroup, SWT.RADIO);
			removeColumnButton.setText("remove column from constraints");
			removeColumnButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					deleteUnique = false;
					setDetailsText(getDetails());
				}
			});
		}
        
        if (column.getTable().getIndexes().size() > 0) {
			Group actionGroup = new Group(dialogArea, SWT.NONE);
			actionGroup.setText("Indices handling");

			GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false);
			actionGroup.setLayoutData(gd);

			GridLayout layout = new GridLayout();
			layout.numColumns = 2;
			actionGroup.setLayout(layout);

			deleteIndexButton = new Button(actionGroup, SWT.RADIO);
			deleteIndexButton.setText("delete indices");
			gd = new GridData();
			gd.horizontalSpan = 2;
			gd.verticalAlignment = SWT.TOP;
			deleteIndexButton.setLayoutData(gd);
			deleteIndexButton.setSelection(true);
			deleteIndexButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					deleteIndex = true;
					setDetailsText(getDetails());
				}
			});

			removeColumnButton = new Button(actionGroup, SWT.RADIO);
			removeColumnButton.setText("remove column from index");
			removeColumnButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					deleteIndex = false;
					setDetailsText(getDetails());
				}
			});
		}
    }

    /**
	 * 
	 * @return the foreignkeys which column may be deleted
	 */
    public List<ForeignKey> getDeletableForeignKeys() {
        return foreignKeysDeletable;
    }
    
    /**
	 * 
	 * @return the indexlist which contains the column, which will bedeleted
	 */
    public List<Index> getIndexList() {
    	return indexList;
    }
    
    /**
	 * 
	 * @return the foreignkeys which columns must not be deleted, because there
	 *         are used by other foreignkeys
	 */
    public List<ForeignKey> getUnDeletableForeignKeys() {
        return foreignKeysUnDeletable;
    }
    
    public boolean getDeleteConstraints() {
        return deleteUnique;
    }
    
    public boolean getDeleteIndices() {
    	return deleteIndex;
    }
    
    protected void okPressed() {
        if (!deleteFK) {
            foreignKeysUnDeletable.addAll(foreignKeysDeletable);
            foreignKeysDeletable = Collections.emptyList();
        }
        super.okPressed();
    }

}
