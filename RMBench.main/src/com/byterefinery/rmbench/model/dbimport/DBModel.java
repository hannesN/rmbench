/*
 * created 29.04.2005
 * 
 * $Id: DBModel.java 683 2008-03-06 22:38:03Z cse $
 */
package com.byterefinery.rmbench.model.dbimport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.byterefinery.rmbench.RMBenchMessages;
import com.byterefinery.rmbench.dialogs.PasswordInputDialog;
import com.byterefinery.rmbench.exceptions.SystemException;
import com.byterefinery.rmbench.external.IDBAccess;
import com.byterefinery.rmbench.external.IDatabaseInfo;

/**
 * DBModel represents a physical database and is used to import model data during database 
 * reverse engineering. It also maintains an {@link IDBAccess} instance which can be used 
 * to access the underlying database, e.g. for executing SQL statements during schema export  
 *  
 * @author cse
 */
public class DBModel {

    private static final List<Listener> EMPTY_LISTENERS = Collections.emptyList();
    private static final List<DBSchema> EMPTY_SCHEMAS = Collections.emptyList();
    private static final List<String> EMPTY_NAMES = Collections.emptyList();
    
    /**
     * interface for listeners interested in a model state change
     */
    public interface Listener {
        /**
         * notification about a change to the metadata load state
         * 
         * @param dbmodel the db model
         */
        void loadedChanged(DBModel dbmodel);
        
        /**
         * notification about a change to the database connection state. Note that this 
         * notification only applies to connections made through an executor, not those 
         * made during metadata loading
         * 
         * @param executor the executor
         */
        void connectChanged(IDBAccess.Executor executor);
    }
    
    /**
     * a schema rule determines which schemas are imported from the database
     */
    public interface SchemaRule {
        /**
         * @param catalogName the catalog name
         * @param schemaName the schema name
         * @return whether the schema should be imported according to this rule
         */
        boolean accepts(String catalogName, String schemaName);
    }

    /**
     * rule for importing all schemas
     */
    public static final SchemaRule ALL_SCHEMAS = new SchemaRule() {

        public boolean accepts(String catalogName, String schemaName) {
            return true;
        }
    };
    
    /**
     * schema rule for importing user-selected schemas
     */
    public static class SelectedSchemasRule implements SchemaRule {

        private List<String> names = EMPTY_NAMES;
        
        public String[] getSchemaNames() {
            return names.toArray(new String[names.size()]);
        }
        /**
         * @param name a schema name
         */
        public void addSchema(String name) {
            if(names == EMPTY_NAMES)
                names = new ArrayList<String>();
            names.add(name);
        }
        /**
         * @param name a schema name
         */
        public void removeSchema(String name) {
            if(names != EMPTY_NAMES)
                names.remove(name);
        }
        /**
         * @param schemas schema names, or null
         */
        public void setSelectedSchemas(String[] schemas) {
        	if(schemas != null)
        		names = new ArrayList<String>(Arrays.asList(schemas));
        	else
                names = EMPTY_NAMES;
        }
        
        public Iterator<String> iterator() {
            return names.iterator();
        }
        
        public boolean accepts(String catalogName, String schemaName) {
        	if(names == null) {
        		return false;
        	}
        	String name = (catalogName != null && catalogName.length() > 0) ? catalogName + "." + schemaName : schemaName;
            return names.contains(name);
        }
    }
    
    private final String name;
    private final IDBAccess dbAccess;
    private final IDBAccess.Executor dbExecutor;
    private final SchemaRule schemaRule;
    private final boolean doPrompt;
    
    private boolean isLoaded;
    private List<Listener> listeners = EMPTY_LISTENERS;
    
    private transient List<DBSchema> schemas = EMPTY_SCHEMAS;
    
    public DBModel(String name, IDBAccess dbAccess) {
        this(name, DBModel.ALL_SCHEMAS, false, dbAccess);
    }
    
    public DBModel(String name, SchemaRule schemaRule, boolean doPrompt, IDBAccess dbAccess) {
        
        this.name = name;
        this.dbAccess = dbAccess;
        this.dbExecutor = dbAccess.getExecutor();
        
        dbExecutor.addListener(new IDBAccess.Listener() {
            public void connected(boolean state) {
                for (Listener listener : listeners) {
                    listener.connectChanged(dbExecutor);
                }
            }
        });
        this.doPrompt = doPrompt;
        this.schemaRule = schemaRule;
    }
    
    /**
     * @param name the name for the new dbmodel
     * @param schemaRule a filter rule to apply to the new connection object
     * @param doPrompt whether the new connection should prompt for connection data
     * @param builder the connection builder
     * @return a dbmodel with the same property values as this object
     */
    public DBModel clone(String name, SchemaRule schemaRule, boolean doPrompt, IDBAccess builder) {
        
        DBModel newModel = new DBModel(name, schemaRule, doPrompt, builder);
        List<DBSchema> newSchemas = new ArrayList<DBSchema>(schemas.size());
        
        for (DBSchema schema : schemas) {
            if(schemaRule.accepts(null, schema.getName()))
                newSchemas.add(schema);
		}
        newModel.schemas = newSchemas;
        newModel.listeners = listeners;
        return newModel;
    }
    
    public void addListener(Listener listener) {
        if(listeners == EMPTY_LISTENERS)
            listeners = new ArrayList<Listener>();
        listeners.add(listener);
    }     
    
    public void removeListener(Listener listener) {
        if(listeners == EMPTY_LISTENERS)
            return;
        listeners.remove(listener);
    }     
    
    public String getName() {
        return name;
    }
    
    public boolean isDoPrompt() {
        return doPrompt;
    }
    
    public boolean isLoaded() {
        return isLoaded;
    }
    
    public SchemaRule getSchemaRule() {
        return schemaRule;
    }
    
    public IDBAccess getDBAccess() {
        return dbAccess;
    }

    /**
     * @param shell parent shell in case a dialog needs to be displayed 
     * @return an executor ready to execute DDL statements
     * @throws SystemException
     */
    public IDBAccess.Executor getExecutor(Shell shell) throws SystemException {
        IDBAccess.Executor executor = dbAccess.getExecutor();
        if(!executor.isConnected()) {
            String password = null;
            if(doPrompt) {
            	PasswordInputDialog inputDialog = new PasswordInputDialog(
                        shell, 
                        RMBenchMessages.DBModel_connectToDBMS, 
                        RMBenchMessages.DBModel_enterPassword,
                        null,
                        null);
                if (inputDialog.open() != Window.OK)
                    return null;
                
                password = inputDialog.getValue();
            }
            executor.connect(password);
        }
        return executor;
    }

    public IDBAccess.Executor getExecutor() {
        return dbExecutor;
    }
    
    public IDatabaseInfo getDatabaseInfo() {
        return dbAccess.getDatabaseInfo();
    }
    
    public List<DBSchema> getSchemaList() {
        return schemas;
    }

    public DBSchema[] getSchemas() {
        return (DBSchema[])schemas.toArray(new DBSchema[schemas.size()]);
    }

	public void addSchema(DBSchema schema) {
		if(schemas == EMPTY_SCHEMAS)
			schemas = new ArrayList<DBSchema>(1);
		schemas.add(schema);
	}
	
    /**
     * connect to the underlying database and load the metadata
     * 
     * @param shell parent shell for input or message dialogs, if required 
     * @return false if an input dialog was cancelled
     * @throws SystemException for technical errors from lower layers
     */
	public boolean load(final Shell shell) throws SystemException {
        
        String password = null;
        
        if (doPrompt) {
            PromptRunnable runnable = new PromptRunnable(shell);
            shell.getDisplay().syncExec(runnable);
            
            if (runnable.isCanceled())
                return false;
            password = runnable.getPassword();
            dbAccess.setPassword(password);
        }
        schemas = EMPTY_SCHEMAS;
		boolean success = dbAccess.loadModel(this);
        if(!success) {
            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    MessageDialog.openWarning(shell, null, RMBenchMessages.DBModel_errorsLoading);                }
            });
        }

        setLoaded(true);
        
        return isLoaded;
	}
    
    protected void setLoaded(boolean loaded) {
        isLoaded = loaded;
        for (Listener listener : listeners) {
        	listener.loadedChanged(this);
		}
    }

    /**
     * @return the names of the schemas held by this model that cannot be created
     * explicitly, i.e. they are always available
     */
    public String[] getPublicSchemaNames() {
        List<String> result = new ArrayList<String>(5);
        for (DBSchema schema : schemas) {
            if(dbAccess.getDatabaseInfo().isPublicSchema(schema.getCatalogName(), schema.getName()))
                result.add(schema.getName());
		}
        return (String[])result.toArray(new String[result.size()]);
    }

    /**
     * @param newName the name for the copy
     * @return a copy of this object, whith the new name. Listeners are not copied
     */
	public DBModel copy(String newName) {
		return new DBModel(newName, schemaRule, doPrompt, dbAccess);
	}
    
    /**
     * Runnable to show the prompt dialog in a UI-Thread
     * @author Hannes Niederhausen
     *
     */
    private class PromptRunnable implements Runnable {

        private boolean canceled = false;
        private Shell shell;
        private String password;
        
        public PromptRunnable(Shell shell) {
            this.shell = shell;
        }
        
        public void run() {
        	PasswordInputDialog inputDialog = new PasswordInputDialog(shell, RMBenchMessages.DBModel_connectToDBMS,
                    RMBenchMessages.DBModel_enterPassword, null, null);
            if (inputDialog.open() != Window.OK) {
                canceled = true;
                return;
            }

            password = inputDialog.getValue();
            
        }

        public boolean isCanceled() {
            return canceled;
        }

        public String getPassword() {
            return password;
        }
    }
}
