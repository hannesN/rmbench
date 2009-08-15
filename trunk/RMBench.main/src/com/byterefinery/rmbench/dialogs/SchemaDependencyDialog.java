/*
 * created 20-Feb-2006
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Id: SchemaDependencyDialog.java 665 2007-09-29 15:31:59Z cse $
 */
package com.byterefinery.rmbench.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.model.Model;
import com.byterefinery.rmbench.model.diagram.DTable;
import com.byterefinery.rmbench.model.diagram.Diagram;
import com.byterefinery.rmbench.model.schema.ForeignKey;
import com.byterefinery.rmbench.model.schema.Schema;
import com.byterefinery.rmbench.model.schema.Table;

public class SchemaDependencyDialog extends AbstractDependencyDialog {

    private Model model;
    private Schema schema;
    private Schema newSchema;

    private Button deleteButton;
    private Button reassign;
    private Combo  newSchemaCombo;
    
    private List<DTable> dTables;
    private List<Diagram> diagrams;
    private List<ForeignKey> foreignKeys = new ArrayList<ForeignKey>();
    
    private int indexOfDiagramInfos;
    private StringBuffer detailMessageBuffer;
    
    public SchemaDependencyDialog(Shell parentShell, Model model, Schema schema) {
        super(parentShell);
        this.model = model;
        this.schema = schema;
    }
    public SchemaDependencyDialog(Schema schema) {
        super(RMBenchPlugin.getModelView().getViewSite().getShell());
        this.model = RMBenchPlugin.getActiveModel();
        this.schema = schema;
    }
    protected String getDetails() {
        
        if (detailMessageBuffer==null) {
            detailMessageBuffer=new StringBuffer();
            indexOfDiagramInfos = -1;
        }
        
        if (dTables==null)
            calculateDependencies();
        
        // creating static data, this means is not depending on radio buttons
        if (indexOfDiagramInfos==-1) {        
            //Action text
            detailMessageBuffer.append("Action: delete Schema (with ");
            detailMessageBuffer.append(schema.getTables().size());
            detailMessageBuffer.append(" tables)\r");
            //implied actions:
            detailMessageBuffer.append("Implied Actions:\r");
            for (DTable dTable : dTables) {
                detailMessageBuffer.append("\t");
                detailMessageBuffer.append("remove table ");
                detailMessageBuffer.append(dTable.getTable().getName());
                detailMessageBuffer.append(" from diagram ");
                detailMessageBuffer.append(dTable.getDiagram().getName());
                detailMessageBuffer.append("\r");
            }
            
            for (ForeignKey reference : foreignKeys) {
                detailMessageBuffer.append("\t");
                detailMessageBuffer.append("remove foreignkey from table ");
                detailMessageBuffer.append(reference.getTable().getFullName());
                detailMessageBuffer.append("\r");
            }
            indexOfDiagramInfos=detailMessageBuffer.length();
        }
        if (indexOfDiagramInfos<detailMessageBuffer.length()) {
            //cutting the old info
            detailMessageBuffer.setLength(indexOfDiagramInfos);
        }
        //now setting the diagram info depending on radio button/combo box choice
        
        for (Diagram diagram : diagrams) {
            detailMessageBuffer.append("\t");
            if (deleteButton.getSelection()) {
                detailMessageBuffer.append("delete diagram ");
                detailMessageBuffer.append(diagram.getName());
            } else {
                detailMessageBuffer.append("set default schema of diagram ");
                detailMessageBuffer.append(diagram.getName());
                detailMessageBuffer.append(" to ");
                detailMessageBuffer.append(newSchemaCombo.getItem(newSchemaCombo.getSelectionIndex()));
            }
            detailMessageBuffer.append("\r");
        }
        
        return detailMessageBuffer.toString();
    }
    
    /**
     * Calaculates the dependency of the schema
     * @return true, if any dependency exists;
     *         false, else
     */
    public boolean calculateDependencies() {
        dTables = new ArrayList<DTable>();
        diagrams = new ArrayList<Diagram>();
        foreignKeys = new ArrayList<ForeignKey>();

        // calculating dependencies
        for (Diagram diagram : model.getDiagrams()) {
            if (diagram.getDefaultSchema() == schema)
                diagrams.add(diagram);
            for (Table table : schema.getTables()) {
                DTable dTable = diagram.getDTable(table);
                if (dTable != null) {
                    dTables.add(dTable);
                }
            }
        }
        for (Table table : schema.getTables()) {
            for (ForeignKey reference : table.getReferences()) {
                if (reference.getTable().getSchema()!=schema) {
                    //we have a relation out of the schema
                    foreignKeys.add(reference);
                }
            }
        }
        
        return (! ((dTables.isEmpty()) && (diagrams.isEmpty()) && (foreignKeys.isEmpty())));
    }

    protected void createOptionsArea(Composite dialogArea) {
        
        if(diagrams.isEmpty())
            return;
        
        Group actionGroup = new Group(dialogArea, SWT.NONE);
        actionGroup.setText(Messages.DependencyDialog_optionalDialogActions);
        
        GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false);
        actionGroup.setLayoutData(gd);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        actionGroup.setLayout(layout);
        
        deleteButton = new Button(actionGroup, SWT.RADIO);
        deleteButton.setText(Messages.DependencyDialog_dialogActionDelete);
        gd = new GridData();
        gd.horizontalSpan = 2;
        gd.verticalAlignment = SWT.TOP;
        deleteButton.setLayoutData(gd);
        deleteButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                newSchemaCombo.setEnabled(!deleteButton.isEnabled());
                newSchema=null;
                setDetailsText(getDetails());
            }
        });
        
        reassign = new Button(actionGroup, SWT.RADIO);
        reassign.setText(Messages.DependencyDialog_dialogActionSetSchema);
        reassign.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                newSchemaCombo.setEnabled(reassign.isEnabled());
                newSchema=model.getSchema(newSchemaCombo.getItem(newSchemaCombo.getSelectionIndex()));
                setDetailsText(getDetails());
            }
        });
        
        newSchemaCombo = new Combo(actionGroup, SWT.SINGLE);
        
        if (model.getSchemas().size()==1) {
            reassign.setEnabled(false);
            newSchemaCombo.setEnabled(false);
            newSchemaCombo.setSize(100, 50);
            deleteButton.setSelection(true);
        } 
        else {
            reassign.setSelection(true);
            String items[] = new String[model.getSchemas().size()-1];
            Schema schemas[] = model.getSchemasArray();
            int j=0;
            for (int i=0; i<schemas.length; i++) {
                if (!schemas[i].equals(schema)) {
                    items[j]=schemas[i].getName();
                    j++;
                }
            }
            newSchemaCombo.setItems(items);
            newSchemaCombo.select(0);
            newSchemaCombo.addSelectionListener(new SelectionAdapter() {
               public void widgetSelected(SelectionEvent e) {
                   newSchema=model.getSchema(newSchemaCombo.getItem(newSchemaCombo.getSelectionIndex()));
                   setDetailsText(getDetails());
               } 
            });
        }
    }
    
    public List<Diagram> getDiagrams() {
        return diagrams;
    }
    
    public List<DTable> getDTables() {
        return dTables;
    }
    
    public List<ForeignKey> getForeignKeys() {
        return foreignKeys;
    }
    
    public Schema getNewSchema() {
        return newSchema;
    }
    
}
