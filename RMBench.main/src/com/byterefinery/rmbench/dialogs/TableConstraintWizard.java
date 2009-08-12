/*
 * created 21.08.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: TableConstraintWizard.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.dialogs;

import org.eclipse.jface.wizard.Wizard;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.model.schema.Table;
import com.byterefinery.rmbench.operations.AddTableConstraintOperation;
import com.byterefinery.rmbench.util.ImageConstants;

/**
 * a wizard fore creating table constraints
 * 
 * @author cse
 */
public class TableConstraintWizard extends Wizard {

    interface ConstraintPage {
        Object getConstraintArg();
    }
    
    private final Table table;
    
    private ConstraintTypePage constraintTypePage;

    
    public TableConstraintWizard(Table table) {
        super();
        setDefaultPageImageDescriptor(
                RMBenchPlugin.getImageDescriptor(ImageConstants.CONSTRAINT_WIZARD));
        setNeedsProgressMonitor(true);
        
        this.table = table;
    }

    public void addPages() {
        constraintTypePage = new ConstraintTypePage();
        addPage(constraintTypePage);
        addPage(new CheckConstraintPage());
        addPage(new UniqueConstraintPage(table.getColumns()));
        addPage(new PrimaryConstraintPage(table.getColumns()));
    }
    
    public boolean canFinish() {
        if(getContainer().getCurrentPage() != constraintTypePage) {
            return getPage(constraintTypePage.getConstraintType()).isPageComplete();
        }
        return false;
    }

    public boolean performFinish() {
        AddTableConstraintOperation operation = new AddTableConstraintOperation(
                table, 
                constraintTypePage.getConstraintType(), 
                constraintTypePage.getConstraintName(), 
                getConstraintArg());
        operation.execute(this);
        
        return true;
    }

    private Object getConstraintArg() {
        ConstraintPage page = (ConstraintPage)getPage(constraintTypePage.getConstraintType());
        return page.getConstraintArg();
    }

    protected Table getTable() {
        return table;
    }
}
