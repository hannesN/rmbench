/*
 * created 29.08.2005 by sell
 *
 * $Id: DatabaseExtension.java 678 2008-02-17 22:52:09Z cse $
 */
package com.byterefinery.rmbench.extension;

import java.util.Map;

import org.eclipse.swt.widgets.Shell;

import com.byterefinery.rmbench.external.IDataTypeEditorFactory;
import com.byterefinery.rmbench.external.IDatabaseInfo;
import com.byterefinery.rmbench.external.IMessageProvider;
import com.byterefinery.rmbench.external.model.IDataType;


/**
 * representation of a databaseInfo extension 
 * 
 * @author sell
 */
public class DatabaseExtension extends NamedExtension {

    private final IDatabaseInfo databaseInfo;
    private final Map<String, IDataTypeEditorFactory> typeEditorFactories;
    private final IMessageProvider messageFormatter;
    
    protected DatabaseExtension(
            String namespace,
            String id, 
            String name, 
            IDatabaseInfo databaseInfo,
            Map<String, IDataTypeEditorFactory> typeEditorFactories,
            IMessageProvider messageFormatter) {
        super(namespace, id, name);
        this.databaseInfo = databaseInfo;
        this.typeEditorFactories = typeEditorFactories;
        this.messageFormatter = messageFormatter;
    }

    public IDatabaseInfo getDatabaseInfo() {
        return databaseInfo;
    }
    
    public IMessageProvider getMessageFormatter() {
        return messageFormatter;
    }
    
    /**
     * open a type editor on the given data type for editing extra type information
     * 
     * @param shell the shell
     * @param dataType the data type
     * @return true if editing was successful, false if editing was aborted or no editor available
     */
    public boolean openTypeEditor(Shell shell, IDataType dataType) {
        
        IDataTypeEditorFactory editorFactory = (IDataTypeEditorFactory)typeEditorFactories.get(dataType.getPrimaryName());
        if(editorFactory != null) {
            return editorFactory.openEditor(shell, dataType);
        }
        return false;        
    }
}
