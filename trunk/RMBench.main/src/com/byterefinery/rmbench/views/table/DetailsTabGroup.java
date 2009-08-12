/*
 * created 05.04.2005
 * 
 * $Id:DetailsTabGroup.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.views.table;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;

import com.byterefinery.rmbench.model.schema.Table;


/**
 * a class that groups together the individual tab pages that show the 
 * details of a given database table
 * 
 * @author cse
 */
public class DetailsTabGroup {

    abstract static class DetailTab {
        
        private DetailsTabGroup group;
        protected Table table;
        
        abstract void createControl(Composite parent, IActionBars actionBars);
        abstract Image getImage();
        abstract String getTitle();
        abstract String getDescription();
        
        protected final int convertWidthInCharsToPixels(int chars) {
            return group.convertWidthInCharsToPixels(chars);
        }
        protected final Shell getShell() {
            return group.tabFolder.getShell();
        }
        void setTable(Table table) {
            this.table = table;
        }
        Action[] getActions() {
            return null;
        }
        void dispose() {
        }
        void activate() {
            int selection = group.tabFolder.getSelectionIndex();
            for (int i = 0; i < group.pages.length; i++) {
                if(this == group.pages[i]) {
                    if(i != selection) {
                        group.tabFolder.setSelection(i); //does not send event!
                        group.notifyTabListeners(group.tabFolder.getSelectionIndex());
                    }
                    break;
                }
            }
        }
    }
    
    public interface TabListener {
        void tabActivated(DetailTab tab);
    }
    
    private Table table;
    private CTabFolder tabFolder;
    
    private final IActionBars actionBars;
    private final DetailTab[] pages = new DetailTab[6];
    
    private Collection<TabListener> tabListeners = new HashSet<TabListener>(2);
    private FontMetrics fontMetrics;

    public DetailsTabGroup(IActionBars actionBars) {
        this.actionBars = actionBars;
        
        pages[0] = new GeneralTab();
        pages[1] = new ColumnsTab();
        pages[2] = new ConstraintsTab();
        pages[3] = new ForeignKeysTab();
        pages[4] = new ReferencesTab();
        pages[5] = new IndexesTab();
        
        for (int i = 0; i < pages.length; i++) {
            pages[i].group = this;
        }
    }
    
    /**
     * create the visible representation for this object
     *  
     * @param parent the parent composite
     */
    public void createControl(Composite parent, Table table) {
        
        storeFontMetrics(parent);
        
        tabFolder = new CTabFolder(parent, SWT.NONE);
        tabFolder.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent e) {
                notifyTabListeners(tabFolder.getSelectionIndex());
            }
            public void widgetDefaultSelected(SelectionEvent e) {
                notifyTabListeners(tabFolder.getSelectionIndex());
            }
        });

        for (int i = 0; i < pages.length; i++) {
            
            final CTabItem item = new CTabItem(tabFolder, SWT.NONE);
            final Composite composite = new Composite(tabFolder, SWT.NONE);
            
            GridLayout layout = new GridLayout();
            layout.marginWidth = 0;
            layout.marginHeight = 0;
            composite.setLayout(layout);
            composite.setLayoutData(new GridData(GridData.FILL_BOTH));
            item.setControl(composite);
            
            initializeTab(pages[i], item, composite, table);
        }
    }

    /**
     * @param table the table whose details are to be displayed/edited
     */
    public void setTable(Table table) {
        this.table = table;
        for (int i = 0; i < pages.length; i++) {
            pages[i].setTable(table);
        }
    }

    /**
     * @return the currently displayed table, or <code>null</code>
     */
    public Table getTable() {
        return table;
    }
    
    /**
     * add a listener to be notified when a tab is activated
     * @param listener
     */
    public void addTabListener(TabListener listener) {
        tabListeners.add(listener);
    }
    
    /**
     * Activate this object. This will also cause the registered tab listeners to
     * be notified of the initial tab selection.
     */
    public void activate() {
        notifyTabListeners(tabFolder.getSelectionIndex());
    }

    public void showFirstPage() {
        tabFolder.setSelection(0);
        activate();
    }
    
    /**
     * release all resources
     */
    public void dispose() {
        for (int i = 0; i < pages.length; i++) {
            pages[i].dispose();
        }
    }
    
    protected int convertWidthInCharsToPixels(int chars) {
        return Dialog.convertWidthInCharsToPixels(fontMetrics, chars);
    }
    
    private void storeFontMetrics(Composite control) {
        GC gc = new GC(control);
        gc.setFont(control.getFont());
        fontMetrics = gc.getFontMetrics();
        gc.dispose();
    }

    private void initializeTab(DetailTab tab, CTabItem item, Composite parent, Table table) {
        tab.createControl(parent, actionBars);
        tab.setTable(table);
        
        item.setImage(tab.getImage());
        item.setText(tab.getTitle());
        item.setToolTipText(tab.getDescription());
    }

    private void notifyTabListeners(int selectionIndex) {
        if (selectionIndex==-1)
            return;
        for (TabListener listener : tabListeners) {
            listener.tabActivated(pages[selectionIndex]);
        }
    }
}
