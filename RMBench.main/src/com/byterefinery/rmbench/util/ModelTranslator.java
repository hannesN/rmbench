/*
 * created 09.09.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: ModelTranslator.java 678 2008-02-17 22:52:09Z cse $
 */
package com.byterefinery.rmbench.util;

import java.io.IOException;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.ui.console.MessageConsoleStream;

import com.byterefinery.rmbench.EventManager;
import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.extension.DatabaseExtension;
import com.byterefinery.rmbench.external.IDatabaseInfo;
import com.byterefinery.rmbench.external.IDatabaseInfo.TypeConverter;
import com.byterefinery.rmbench.external.model.IDataType;
import com.byterefinery.rmbench.model.Model;
import com.byterefinery.rmbench.model.schema.Column;
import com.byterefinery.rmbench.model.schema.Schema;
import com.byterefinery.rmbench.model.schema.Table;

/**
 * translator that translates a model from one database to another, converting data types, 
 * and calling the validate methods for object names and generating new object names where 
 * needed.
 * 
 * @author cse
 */
public class ModelTranslator {

    MessageConsoleStream consoleStream;
    
    /**
     * translate a model to a new target database
     * @param model the model
     * @param databaseInfo the target database
     */
    public void translate(Model model, IDatabaseInfo databaseInfo) {
        consoleStream = 
            new MessageConsoleStream(RMBenchPlugin.getDefault().getMessageConsole());
        consoleStream.setActivateOnWrite(true);
        consoleStream.setColor(ColorConstants.darkBlue);
        consoleStream.println("** begin model conversion **");
        
        for (Schema schema : model.getSchemas()) {
            translateSchema(model, databaseInfo, schema);
        }
        
        model.setDatabaseInfo(databaseInfo);
        consoleStream.println("** finished model conversion **");
        try {
            consoleStream.close();
        }
        catch (IOException e) {
            RMBenchPlugin.logError(e);
        }
    }

    /**
     * translate a schema to a new target database
     * @param model the owning model
     * @param schema the schema
     */
    protected void translateSchema(Model model, IDatabaseInfo databaseInfo, Schema schema) {
        
        DatabaseExtension extension = RMBenchPlugin.getExtensionManager().getDatabaseExtension(databaseInfo);
        
        String validateKey = databaseInfo.validateSchemaName(schema.getName());
        if(validateKey != null) {
            consoleStream.println(extension.getMessageFormatter().getMessage(validateKey, schema.getName()));
            String newName = model.getNameGenerator().generateNewSchemaName(
                    model.getIModel(), schema.getName());
            
            consoleStream.println("changing schema name from "+schema.getName()+" to "+newName);
            schema.setName(newName);
            // report schema name change to event manager
            RMBenchPlugin.getEventManager().fireSchemaModified(this, EventManager.Properties.NAME, schema);
        }
        schema.setDatabaseInfo(databaseInfo);
        
        for (Table table : schema.getTables()) {
            translateTable(model, databaseInfo, table);
        }
    }

    /**
     * translate a table to a new target database
     * @param model the owning model
     * @param table the table
     */
    protected void translateTable(Model model, IDatabaseInfo databaseInfo, Table table) {
        
        DatabaseExtension extension = RMBenchPlugin.getExtensionManager().getDatabaseExtension(databaseInfo);
        
        String validateKey = databaseInfo.validateTableName(table.getName());
        
        if(validateKey != null) {
            consoleStream.println(extension.getMessageFormatter().getMessage(validateKey, table.getName()));
            String newName = model.getNameGenerator().generateNewTableName(
                    model.getIModel(), table.getSchema().getISchema(), table.getName());
            
            consoleStream.println("changing table name from "+table.getName()+" to "+newName);
            table.setName(newName);
        }
        for (Column column : table.getColumns()) {
            translateColumn(model, databaseInfo, column);
        }
    }

    /**
     * translate a column to a new target database
     * @param model the owning model
     * @param column the column
     */
    protected void translateColumn(Model model, IDatabaseInfo databaseInfo, Column column) {
        DatabaseExtension extension = RMBenchPlugin.getExtensionManager().getDatabaseExtension(databaseInfo);
        
        String validateKey = databaseInfo.validateColumnName(column.getName());
        
        if(validateKey != null) {
            consoleStream.println(extension.getMessageFormatter().getMessage(validateKey, column.getName()));
            String newName = model.getNameGenerator().generateNewColumnName(
                    model.getIModel(), column.getTable().getITable(), column.getName());
            
            consoleStream.println("changing column name from "+column.getName()+" to "+newName);
            column.setName(newName);
        }
        IDataType type = null;
        TypeConverter typeConverter = 
            databaseInfo.getTypeConverter(column.getDataType(), model.getDatabaseInfo());
        if(typeConverter != null) {
            type = typeConverter.convert(column.getDataType(), model.getDatabaseInfo());
        }
        if(type == null) {
            consoleStream.println(
                    "unable to translate data type of column "+
                    column.getTable().getFullName()+"."+column.getName());
            column.setDataType(databaseInfo.getDefaultDataType());
        }
        else if(!type.equals(column.getDataType())){
            consoleStream.println(
            		"changing type for column "+
            		column.getName()+
            		" from "+column.getDataType().getDDLName()+
            		" to "+type.getDDLName());
        	column.setDataType(type);
        }
    }
}
