/*
 * created 08.08.2005 by cse
 *
 * $Id: GeneralTab.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.views.dbtable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.byterefinery.rmbench.RMBenchMessages;
import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.model.dbimport.DBTable;
import com.byterefinery.rmbench.util.ImageConstants;

/**
 * Import table view tab for name, schema name, and comment
 * 
 * @author cse
 */
class GeneralTab implements DBTableView.DetailTab {

    private FormToolkit toolkit;
    private ScrolledForm form;
    
    private Text nameText;
    private Text schemaText;
    private Text commentText;
    
    public Image getImage() {
        return RMBenchPlugin.getImage(ImageConstants.INFO);
    }

    public String getTitle() {
        return RMBenchMessages.NameTab_Title;
    }

    public String getDescription() {
        return RMBenchMessages.NameTab_Description;
    }

    public void createControl(Composite parent) {
        parent.setLayout(new FillLayout());
        toolkit = new FormToolkit(parent.getDisplay());
        form = toolkit.createScrolledForm(parent);
        
        Composite body = form.getBody();
        
        GridData gridData = new GridData(SWT.LEFT, SWT.TOP, true, true);
        body.setLayoutData(gridData);
        body.setLayout(new GridLayout(2, false));

        toolkit.createLabel(body, RMBenchMessages.NameTab_FName);
        nameText = toolkit.createText(body, null, SWT.SINGLE | SWT.BORDER);
        nameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        nameText.setEditable(false);
        
        toolkit.createLabel(body, RMBenchMessages.NameTab_FSchema);
        schemaText = toolkit.createText(body, null, SWT.SINGLE | SWT.BORDER);
        schemaText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        schemaText.setEditable(false);
        
        toolkit.createLabel(body, RMBenchMessages.NameTab_FComment);
        commentText = toolkit.createText(body, null, SWT.SINGLE | SWT.BORDER);
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        commentText.setLayoutData(gridData);
        commentText.setEditable(false);
    }

    public void setTable(DBTable table) {
        if(table != null)
            nameText.setText(table.getName());
        else
            nameText.setText("");
        if(table != null && table.getSchema() != null)
            schemaText.setText(table.getSchemaName());
        else
            schemaText.setText("");
        if(table != null && table.getComment() != null)
            commentText.setText(table.getComment());
        else
            commentText.setText("");
    }
}
