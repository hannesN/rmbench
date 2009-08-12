/*
 * created 15.01.2006
 *
 * Copyright 2006, DynaBEAN Consulting
 * 
 * $Id$
 */
package com.byterefinery.rmbench.export;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.byterefinery.rmbench.EventManager;
import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.EventManager.Event;
import com.byterefinery.rmbench.exceptions.SystemException;
import com.byterefinery.rmbench.external.IDatabaseInfo;
import com.byterefinery.rmbench.model.dbimport.DBModel;

/**
 * a toolbar contribution item that allows the selection of a database connection from a drop down
 * list
 * @author cse
 */
class ChooseDBModelContribution extends ControlContribution {

    public static final String ID = "ChooseDBModelContribution";

    /**
     * a listener that is notified of connection selection changes
     */
    public interface Listener {
        void dbModelSelected(DBModel dbModel);
    }

    /**
     * a listener that is registered with the global event manager to update the toolbar when new
     * connections are added
     */
    public static final class EventManagerListener extends EventManager.Listener {

        private final ToolBarManager toolBarMgr;

        public EventManagerListener(ToolBarManager toolBarMgr) {
            this.toolBarMgr = toolBarMgr;
        }

        public void eventOccurred(int eventType, Event event) {
            ChooseDBModelContribution item = (ChooseDBModelContribution) toolBarMgr
                    .find(ChooseDBModelContribution.ID);
            item.refreshConnections();
            toolBarMgr.update(true);
        }

        public void register() {
            RMBenchPlugin.getEventManager().addListener(DBMODELS_CHANGED|DBMODELS_ADDED|DBMODELS_REMOVED, this);
        }
    }

    private DBModel[] dbModels;

    private DBModel selectedDBModel;

    private Combo modelsCombo;

    private IDatabaseInfo dbInfo;

    private final List<Listener> listeners = new ArrayList<Listener>(2);

    ChooseDBModelContribution() {
        super(ID);
        this.dbInfo = null;
    }

    ChooseDBModelContribution(IDatabaseInfo dbInfo) {
        super(ID);
        this.dbInfo = dbInfo;
    }

    /**
     * add a listener to be notified when the selected connection changes
     */
    public void addConnectionListener(Listener listener) {
        listeners.add(listener);
    }

    /**
     * We have dynamic element, which means it's size is variable (dependent on the longest connection name).
     * 
     * This has to be true, because the update method of the toolbar manager only recreates dynamic widgets.
     */
    public boolean isDynamic() {
    	return true;
    }
    
    /**
     * @return the currently selected connection
     */
    public DBModel getSelectedDBModel() {
        return selectedDBModel;
    }

    public void setSelectedDBModel(DBModel dbModel) {
        modelsCombo.select(modelsCombo.indexOf(dbModel.getName()));
    }

    protected Control createControl(Composite parent) {
        modelsCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
        computeItems();

        modelsCombo.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                selectedDBModel = dbModels[modelsCombo.getSelectionIndex()];
                fireSelectedEvents();
            }
        });
        return modelsCombo;
    }

    public void refreshConnections() {
        closeExecutors();
        modelsCombo.removeAll();
        computeItems();
    }
    
    public void closeExecutors() {
        for (int i = 0; i < dbModels.length; i++) {
            try {
                dbModels[i].getExecutor().close();
            }
            catch (SystemException e) {
                RMBenchPlugin.logError(e);
            }
        }
    }

    private void computeItems() {
        dbModels = RMBenchPlugin.getDefault().getDBModels();

        List<DBModel> usableDbModels = new ArrayList<DBModel>(dbModels.length);
        if (dbInfo != null) {
            for (int i = 0; i < dbModels.length; i++) {
                if (dbModels[i].getDatabaseInfo() == dbInfo)
                    usableDbModels.add(dbModels[i]);
            }
            dbModels = (DBModel[]) usableDbModels.toArray(new DBModel[usableDbModels.size()]);
        }

        int selectIndex = -1;
        String[] connectionNames = new String[dbModels.length];
        for (int i = 0; i < connectionNames.length; i++) {
            if (selectedDBModel == dbModels[i])
                selectIndex = i;
            connectionNames[i] = dbModels[i].getName();
        }
        modelsCombo.setItems(connectionNames);
        if (dbModels.length > 0) {
            if (selectIndex >= 0)
                modelsCombo.select(selectIndex);
            else {
                modelsCombo.select(0);
                selectedDBModel = dbModels[0];
            }
        }
        fireSelectedEvents();
    }

    private void fireSelectedEvents() {
        for (Listener listener : listeners) {
            listener.dbModelSelected(selectedDBModel);
        }
    }
}
