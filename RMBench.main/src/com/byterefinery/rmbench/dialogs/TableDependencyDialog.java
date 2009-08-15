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
 * $Id: TableDependencyDialog.java 664 2007-09-28 17:28:39Z cse $
 */
package com.byterefinery.rmbench.dialogs;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.widgets.Shell;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.model.Model;
import com.byterefinery.rmbench.model.diagram.DTable;
import com.byterefinery.rmbench.model.diagram.Diagram;
import com.byterefinery.rmbench.model.schema.ForeignKey;
import com.byterefinery.rmbench.model.schema.Table;
import com.byterefinery.rmbench.util.ScriptWriter;

/**
 * a dialog that will show implications of a requested operation and allow the user to
 * review and confirm them
 * 
 * @author cse
 */
public class TableDependencyDialog extends AbstractDependencyDialog {

    private     Model model;
    private     Table table;
    ArrayList<DTable>   dTables;
    
    public TableDependencyDialog(Shell parentShell, Model model, Table table) {
        super(parentShell);
        this.model = model;
        this.table = table;
    }
    
    public TableDependencyDialog(Table table) {
        super(RMBenchPlugin.getModelView().getViewSite().getShell());
        this.model = RMBenchPlugin.getActiveModel();
        this.table = table;
    }

    public boolean calculateDependencies() {
        DTable dTable;
        dTables = new ArrayList<DTable>();

        for (Iterator<Diagram> it=model.getDiagrams().iterator(); it.hasNext();) {
            dTable = ((Diagram) it.next()).getDTable(table);
            if (dTable!=null)
                dTables.add(dTable);
        }
        
        return (!((dTables.isEmpty()) && (table.getReferences().isEmpty())));
    }
    
    protected String getDetails() {
        ScriptWriter writer = new ScriptWriter();

        DTable dTable;
        ForeignKey reference;
        
        // Action text
        String msg = MessageFormat.format(
                Messages.TableDependencyDialog_actionDeleteTable, new Object[]{table.getName()});
        writer.println(msg);
        
        // implied actions:
        writer.println(Messages.TableDependencyDialog_impliedActions);
        writer.indent(1);
        for (Iterator<DTable> it=dTables.iterator(); it.hasNext();) {
            dTable = (DTable) it.next();
            msg = MessageFormat.format(
                    Messages.TableDependencyDialog_actionRemoveTable, 
                    new Object[]{dTable.getTable().getName(), dTable.getDiagram().getName()});
            writer.println(msg);
        }
        for (Iterator<ForeignKey> it = table.getReferences().iterator(); it.hasNext();) {
            reference = (ForeignKey) it.next();
            msg = MessageFormat.format(
                    Messages.TableDependencyDialog_actionRemoveForeignKey, 
                    new Object[]{reference.getTable().getFullName()});
            writer.println(msg);
        }
        return writer.getString();
    }

    protected String getMessage() {
        return Messages.TableDependencyDialog_dependenciesOccurred;
    }

    public int open() {
        int result = super.open();
        
        return result;
    }
    
    public List<DTable> getDTables() {
        return dTables; 
    }
}
