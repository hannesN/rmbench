/*
 * created 08.08.2005 by sell
 *
 * $Id: DBTableView.java 416 2006-07-13 20:27:18Z hannesn $
 */
package com.byterefinery.rmbench.views.dbtable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.byterefinery.rmbench.model.dbimport.DBTable;

/**
 * a view that shows details of the database metadata for an imported table
 * 
 * @author sell
 */
public class DBTableView extends ViewPart {

    public static final String VIEW_ID = "com.byterefinery.rmbench.dbtableview";
    
    interface DetailTab {
        Image getImage();
        String getTitle();
        String getDescription();
        void createControl(Composite parent);
        void setTable(DBTable table);
    }
    
    private CTabFolder tabFolder;
    private DetailTab[] pages = new DetailTab[] {
            new GeneralTab(),
            new ColumnsTab(),
            new ForeignKeysTab(),
            new IndexesTab()
    };
    
    //@see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
    public void createPartControl(Composite parent) {

        tabFolder = new CTabFolder(parent, SWT.NONE);
        for (int i = 0; i < pages.length; i++) {
            
            final CTabItem item = new CTabItem(tabFolder, SWT.NONE);
            final Composite composite = new Composite(tabFolder, SWT.NONE);
            
            GridLayout layout = new GridLayout();
            layout.marginWidth = 0;
            layout.marginHeight = 0;
            composite.setLayout(layout);
            composite.setLayoutData(new GridData(GridData.FILL_BOTH));
            item.setControl(composite);
            
            pages[i].createControl(composite);
            item.setImage(pages[i].getImage());
            item.setText(pages[i].getTitle());
            item.setToolTipText(pages[i].getDescription());
        }
        tabFolder.setSelection(0);
    }

    //@see org.eclipse.ui.part.WorkbenchPart#setFocus()
    public void setFocus() {
    }
    
    public void setDBTable(DBTable table) {
        for (int i = 0; i < pages.length; i++) {
            pages[i].setTable(table);
        }
    }
}
