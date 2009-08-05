/*
 * created 02.06.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: ModelReader.java 678 2008-02-17 22:52:09Z cse $
 */
package com.byterefinery.rmbench.util.xml;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.exceptions.SystemException;
import com.byterefinery.rmbench.extension.DatabaseExtension;
import com.byterefinery.rmbench.extension.NameGeneratorExtension;
import com.byterefinery.rmbench.external.IDatabaseInfo;
import com.byterefinery.rmbench.external.INameGenerator;
import com.byterefinery.rmbench.external.database.sql99.SQL99;
import com.byterefinery.rmbench.external.model.IDataType;
import com.byterefinery.rmbench.external.model.IForeignKey;
import com.byterefinery.rmbench.model.Model;
import com.byterefinery.rmbench.model.diagram.DForeignKey;
import com.byterefinery.rmbench.model.diagram.DTable;
import com.byterefinery.rmbench.model.diagram.Diagram;
import com.byterefinery.rmbench.model.schema.CheckConstraint;
import com.byterefinery.rmbench.model.schema.Column;
import com.byterefinery.rmbench.model.schema.ForeignKey;
import com.byterefinery.rmbench.model.schema.Index;
import com.byterefinery.rmbench.model.schema.PrimaryKey;
import com.byterefinery.rmbench.model.schema.Schema;
import com.byterefinery.rmbench.model.schema.Table;
import com.byterefinery.rmbench.model.schema.UniqueConstraint;
import com.byterefinery.rmbench.util.IModelStorage;

/**
 * reads a model file which was previously stored by {@link com.byterefinery.rmbench.util.xml.ModelWriter}.
 * <br/>Only superficial validation is done
 * 
 * @author cse
 */
public class ModelReader implements XMLConstants {

    private static class Adapter {

        void logError(String message) {
            RMBenchPlugin.logError(message);
        }

        void logWarning(String message) {
            RMBenchPlugin.logWarning(message);
        }

        void logWarning(String message, Object[] args) {
            RMBenchPlugin.logWarning(message, args);
        }

        IDatabaseInfo getDatabaseInfo(String infoName) {
            DatabaseExtension dbext = RMBenchPlugin.getExtensionManager().getDatabaseExtension(infoName);
            return dbext != null ? dbext.getDatabaseInfo() : null;
        }

        IDatabaseInfo getDefaultDatabaseInfo() {
            return RMBenchPlugin.getStandardDatabaseInfo();
        }

        public INameGenerator getNameGenerator(String genId) {
            NameGeneratorExtension genext = RMBenchPlugin.getExtensionManager().getNameGeneratorExtension(genId);
            return genext != null ? genext.getNameGenerator() : null;
        }

        public INameGenerator getDefaultNameGenerator() {
            return RMBenchPlugin.getDefaultNameGenerator();
        }
    }
    
    /**
     * @param inputStream the stream to read from
     * @param name name used for error reporting, normally the file name
     * @param listener a listener for model modifications during loading
     * 
     * @return a model parsed from the XML stream
     * 
     * @throws SystemException
     */
    public static Model read(InputStream inputStream, String name, IModelStorage.LoadListener listener) 
        throws SystemException {
        
        return read(inputStream, name, new Adapter(), listener);
    }
    
    private static Model read(InputStream inputStream, String name, Adapter adapter, IModelStorage.LoadListener listener) 
        throws SystemException {

        ModelReader reader = new ModelReader(name, adapter, listener);
        try {
            XmlPullParser xpp = setupParser(inputStream);
            return reader.readModel(xpp);
        }
        catch(Exception x) {
            throw new SystemException(x);
        }
        finally {
            try {
                inputStream.close();
            }
            catch (IOException e) {
                throw new SystemException(e);
            }
        }
    }

    private final String fileName;
    private final Adapter adapter;
    private final IModelStorage.LoadListener listener;
    private final List<ForeignKeyBuilder> foreignKeyBuilders = new ArrayList<ForeignKeyBuilder>();
    private final List<TmpDForeignKey> tmpDForeignKeyList = new ArrayList<TmpDForeignKey>();
    
    
    //hide the constructor
    private ModelReader(String fileName, Adapter adapter, IModelStorage.LoadListener listener) {
        this.fileName = fileName;
        this.adapter = adapter;
        this.listener = listener;
    }

    private static XmlPullParser setupParser(InputStream inputStream) throws SystemException {
        XmlPullParserFactory factory;
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
        }
        catch (XmlPullParserException e) {
            throw new SystemException(e);
        }
        factory.setNamespaceAware(true);
        try {
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new InputStreamReader(inputStream));
            return parser;
        }
        catch (XmlPullParserException e) {
            throw new SystemException(e);
        }
    }

    public Model readModel(XmlPullParser xpp) throws Exception  {
        
        assertStartTag(xpp, Elements.MODEL);
        
        String name = xpp.getAttributeValue(ATT_NAMESPACE, Attributes.NAME);
        String dbId = xpp.getAttributeValue(ATT_NAMESPACE, Attributes.DBINFO);
        String genId = xpp.getAttributeValue(ATT_NAMESPACE, Attributes.GENERATOR);
        
        IDatabaseInfo dbinfo = adapter.getDatabaseInfo(dbId);
        if(dbinfo == null) {
            adapter.logError(
                    ParserErrors.message(
                            fileName, 
                            xpp.getLineNumber(), 
                            ParserErrors.errorUndefinedDbInfo,
                            new Object[]{dbId}));
            dbinfo = adapter.getDefaultDatabaseInfo();
        }
        INameGenerator generator = adapter.getNameGenerator(genId);
        if(generator == null) {
            adapter.logError(
                    ParserErrors.message(
                            fileName, 
                            xpp.getLineNumber(), 
                            ParserErrors.errorUndefinedGenerator,
                            new Object[]{genId}));
            generator = adapter.getDefaultNameGenerator();
        }
        Model model = new Model(name, dbinfo, generator);
        
        assertStartTag(xpp, Elements.SCHEMAS);
        for(xpp.nextTag(); isStartTag(xpp, Elements.SCHEMA); xpp.nextTag()) {
            readSchema(xpp, model);
        }
        
        assertStartTag(xpp, Elements.DIAGRAMS);
        for(xpp.nextTag(); isStartTag(xpp, Elements.DIAGRAM); xpp.nextTag()) {
            readDiagram(xpp, model);
        }
        
        endTag(xpp);
        
        for (ForeignKeyBuilder builder : foreignKeyBuilders) {
            builder.buildForeignKey(model);
        }
        
        //building dforeignkeys
        for (TmpDForeignKey tmpDforeignKey : tmpDForeignKeyList) {
            ForeignKey foreignKey = null;
            
            //searching foreignKey in table
            for (ForeignKey fk : tmpDforeignKey.getDtable().getTable().getForeignKeys()) {
                if (fk.getName().equals(tmpDforeignKey.getForeignKeyName())) {
                	foreignKey = fk;
                    break;
                }
            }
            if (foreignKey != null) {
            	DForeignKey dForeignKey = new DForeignKey(foreignKey);
                dForeignKey.setSourceEdge(tmpDforeignKey.getSourceEdge());
                dForeignKey.setSourceSlot(tmpDforeignKey.getSourceSlot());
                dForeignKey.setSourceValid(true);
                dForeignKey.setTargetEdge(tmpDforeignKey.getTargetEdge());
                dForeignKey.setTargetSlot(tmpDforeignKey.getTargetSlot());
                dForeignKey.setTargetValid(true);
                tmpDforeignKey.getDtable().addDForeignKey(dForeignKey);
            }
        }
        
        return model;
    }

    private void readSchema(XmlPullParser xpp, Model model) throws Exception {
        
        String catalogName = xpp.getAttributeValue(ATT_NAMESPACE, Attributes.CATALOG);
        String name = xpp.getAttributeValue(ATT_NAMESPACE, Attributes.NAME);

        Schema schema = new Schema(catalogName, name, model.getDatabaseInfo());
        
        for(xpp.nextTag(); isStartTag(xpp, Elements.TABLE); xpp.nextTag()) {
            readTable(xpp, schema);
        }
        model.addSchema(schema);
    }

    private void readTable(XmlPullParser xpp, Schema schema) throws Exception {
        
        String tableName = xpp.getAttributeValue(ATT_NAMESPACE,  Attributes.NAME);
        Table table = new Table(schema, tableName);
        
        String tableType = xpp.getAttributeValue(ATT_NAMESPACE,  Attributes.TYPE);
        if(tableType != null)
            table.setType(tableType);
        
        xpp.nextTag();
        while(isStartTag(xpp, Elements.COLUMN)) {
            readColumn(xpp, table);
            xpp.nextTag();
        }
        if(isStartTag(xpp, Elements.PRIMARY_KEY)) {
            readPrimaryKey(xpp, table);
            xpp.nextTag();
        }
        while(isStartTag(xpp, Elements.FOREIGN_KEY)) {
            readForeignKey(xpp, table);
            xpp.nextTag();
        }
        while(isStartTag(xpp, Elements.INDEX)) {
            readIndex(xpp, table);
            xpp.nextTag();
        }
        while(isStartTag(xpp, Elements.UNIQUE)) {
            readUniqueConstraint(xpp, table);
            xpp.nextTag();
        }
        while(isStartTag(xpp, Elements.CHECK)) {
            readCheckConstraint(xpp, table);
            xpp.nextTag();
        }
        
        
        if (isStartTag(xpp, Elements.COMMENT)) {
        	xpp.next();
        	if (!isEndTag(xpp)) {
	        	table.setComment(xpp.getText());
	        	xpp.getName();
	        	endTag(xpp);
        	}
            xpp.nextTag();            
        }
        
        if (isStartTag(xpp, Elements.DESCRIPTION)) {
        	xpp.next();
        	if (!isEndTag(xpp)) {
	        	table.setDescription(xpp.getText());
	            endTag(xpp);
        	}
            xpp.nextTag();
        }
    }

    private void readCheckConstraint(XmlPullParser xpp, Table table) throws Exception {
        String name = xpp.getAttributeValue(ATT_NAMESPACE, Attributes.NAME);
        String expression = "";
        
        assertStartTag(xpp, Elements.EXPRESSION);
        xpp.next();
        if (!isEndTag(xpp)) {
            expression = xpp.getText();
            endTag(xpp);
        }
        xpp.nextTag();
        
        table.addCheckConstraint(new CheckConstraint(name, expression, table));
    }

    private void readUniqueConstraint(XmlPullParser xpp, Table table) throws Exception {
        String name = xpp.getAttributeValue(ATT_NAMESPACE, Attributes.NAME);
        
        List<Column> columns = new ArrayList<Column>();
        for(xpp.nextTag(); isStartTag(xpp, Elements.COLUMN_REF); xpp.nextTag()) {
            String colName = xpp.getAttributeValue(ATT_NAMESPACE,  Attributes.NAME);
            Column column = table.getColumn(colName);
            if(colName != null)
                columns.add(column);
            endTag(xpp);
        }
        Column[] cols = (Column[])columns.toArray(new Column[columns.size()]);
        table.addUniqueConstraint(new UniqueConstraint(name, cols, table));
    }
    
    private void readColumn(XmlPullParser xpp, Table table) throws Exception {
        
        String columnName = xpp.getAttributeValue(ATT_NAMESPACE,  Attributes.NAME);
        String typeName = xpp.getAttributeValue(ATT_NAMESPACE,  Attributes.TYPE);
        
        Boolean nullable = Boolean.valueOf(xpp.getAttributeValue(ATT_NAMESPACE, Attributes.NULLABLE));
        
        IDataType dataType = table.getSchema().getDatabaseInfo().getDataType(typeName);
        if(dataType == null) {
            adapter.logError(
                    ParserErrors.message(
                            fileName, 
                            xpp.getLineNumber(), 
                            ParserErrors.errorUndefinedType,
                            new Object[]{typeName}));
            dataType = table.getSchema().getDatabaseInfo().getDefaultDataType();
        }
        String sz = xpp.getAttributeValue(ATT_NAMESPACE,  Attributes.SIZE);
        String sc = xpp.getAttributeValue(ATT_NAMESPACE,  Attributes.SCALE);
        String cust = xpp.getAttributeValue(ATT_NAMESPACE,  Attributes.EXTRA);
        if(sz != null && dataType.acceptsSize()) {
            long size = Long.parseLong(sz);
            dataType.setSize(size);
        }
        if(sc != null && dataType.acceptsScale()) {
            int scale = Integer.parseInt(sc);
            dataType.setScale(scale);
        }
        if(cust != null&& dataType.hasExtra()) {
            dataType.setExtra(cust);
        }
        String defaultValue = null, comment = null;
        xpp.nextTag();
        if(isStartTag(xpp, Elements.DEFAULT)) {
            xpp.next();
            defaultValue = xpp.getText();
            endTag(xpp);
            xpp.nextTag();
        }
        if(isStartTag(xpp, Elements.COMMENT)) {
            xpp.next();
            comment = xpp.getText();
            endTag(xpp);
            xpp.nextTag();
        }
        new Column(
                table, 
                columnName, 
                dataType, 
                nullable.booleanValue(), 
                defaultValue, 
                comment);
    }

    private void readPrimaryKey(XmlPullParser xpp, Table table) throws Exception {
        String pkName = xpp.getAttributeValue(ATT_NAMESPACE,  Attributes.NAME);
        List<String> colRefs = new ArrayList<String>();
        for(xpp.nextTag(); isStartTag(xpp, Elements.COLUMN_REF); xpp.nextTag()) {
            colRefs.add(xpp.getAttributeValue(ATT_NAMESPACE,  Attributes.NAME));
            endTag(xpp);
        }
        new PrimaryKey(
                pkName, 
                colRefs.toArray(new String[colRefs.size()]), 
                table);
    }

    private void readForeignKey(XmlPullParser xpp, Table table) throws Exception {
        String fkName = xpp.getAttributeValue(ATT_NAMESPACE,  Attributes.NAME);
        String deleteRule = xpp.getAttributeValue(ATT_NAMESPACE,  Attributes.DELETE_RULE);
        String updateRule = xpp.getAttributeValue(ATT_NAMESPACE,  Attributes.UPDATE_RULE);
        
        assertStartTag(xpp, Elements.TARGET);
        String schemaName = xpp.getAttributeValue(ATT_NAMESPACE, Attributes.SCHEMA);
        String tableName = xpp.getAttributeValue(ATT_NAMESPACE, Attributes.TABLE);
        endTag(xpp);
        
        List<String> colRefs = new ArrayList<String>();
        for(xpp.nextTag(); isStartTag(xpp, Elements.COLUMN_REF); xpp.nextTag()) {
            colRefs.add(xpp.getAttributeValue(ATT_NAMESPACE,  Attributes.NAME));
            endTag(xpp);
        }
        foreignKeyBuilders.add(new ForeignKeyBuilder(
                fkName, 
                colRefs.toArray(new String[colRefs.size()]),
                table,
                schemaName,
                tableName,
                deleteRule,
                updateRule));
    }

    private void readIndex(XmlPullParser xpp, Table table) throws Exception {
        String indexName = xpp.getAttributeValue(ATT_NAMESPACE,  Attributes.NAME);
        Boolean unique = Boolean.valueOf(xpp.getAttributeValue(ATT_NAMESPACE,  Attributes.UNIQUE));
        List<String> colRefs = new ArrayList<String>();
        for(xpp.nextTag(); isStartTag(xpp, Elements.COLUMN_REF); xpp.nextTag()) {
            colRefs.add(xpp.getAttributeValue(ATT_NAMESPACE,  Attributes.NAME));
            endTag(xpp);
        }
        new Index(
                indexName, 
                colRefs.toArray(new String[colRefs.size()]), 
                table,
                unique.booleanValue(),
                null); //TODO V1: read ascdesc values
    }

    private void readDiagram(XmlPullParser xpp, Model model) throws Exception {
        String diagramName = xpp.getAttributeValue(ATT_NAMESPACE, Attributes.NAME);
        String schemaName = xpp.getAttributeValue(ATT_NAMESPACE, Attributes.SCHEMA);
        
        Schema defaultSchema = model.getSchema(schemaName);
        if(defaultSchema == null) {
            adapter.logWarning(
                ParserErrors.message(
                        fileName, 
                        xpp.getLineNumber(), 
                        ParserErrors.warnUndefinedSchemaInDiagram,
                        new Object[]{schemaName}));
            defaultSchema = new Schema(schemaName, model.getDatabaseInfo());
            model.addSchema(defaultSchema);
            if(listener != null)
                listener.elementAdded(defaultSchema);
        }
        Diagram diagram = new Diagram(model, diagramName, defaultSchema);
        
        for(xpp.nextTag(); isStartTag(xpp, Elements.TABLE_REF); xpp.nextTag()) {
            readDTable(xpp, diagram);
        }
    }

    private void readDTable(XmlPullParser xpp, Diagram diagram) throws Exception  {
        
        String schemaName = xpp.getAttributeValue(ATT_NAMESPACE,  Attributes.SCHEMA);
        String tableName =xpp.getAttributeValue(ATT_NAMESPACE,  Attributes.NAME);
        Boolean collapsed = Boolean.valueOf(
                xpp.getAttributeValue(ATT_NAMESPACE,  Attributes.COLLAPSED));

        xpp.nextTag();
        Point location = null;
        if(isStartTag(xpp, Elements.LOCATION)) {
            int x = Integer.parseInt(xpp.getAttributeValue(ATT_NAMESPACE,  Attributes.X));
            int y = Integer.parseInt(xpp.getAttributeValue(ATT_NAMESPACE,  Attributes.Y));
            location = new Point(x, y);
            xpp.nextTag();
            xpp.nextTag();
        }
        Table table = null;
        Schema schema = diagram.getModel().getSchema(schemaName);
        if(schema != null) {
            table = schema.getTable(tableName);
            if(table == null) {
                adapter.logError(
                        ParserErrors.message(
                                fileName, 
                                xpp.getLineNumber(), 
                                ParserErrors.errorUndefinedTable,
                                new Object[]{tableName}));
            }
        }
        else {
            adapter.logError(
                    ParserErrors.message(
                            fileName, 
                            xpp.getLineNumber(), 
                            ParserErrors.errorUndefinedSchema,
                            new Object[]{schemaName}));
        }
        if(table != null) {
            DTable dtable = new DTable(table, location);
            dtable.setCollapsed(collapsed.booleanValue());
            diagram.addTable(dtable);
            readDForeignKeys(xpp, dtable);
        }
    }

    
    private void readDForeignKeys (XmlPullParser xpp, DTable dtable) throws Exception{
        int sourceEdge=0, sourceSlot=0;
        int targetEdge=0, targetSlot=0;
        String foreignKeyName;
        while (isStartTag(xpp, Elements.FOREIGN_KEY_REF)) {
            foreignKeyName = xpp.getAttributeValue(ATT_NAMESPACE, Attributes.NAME);
            
            xpp.nextTag();
            if (isStartTag(xpp, Elements.SOURCE)) {
                sourceEdge = Integer.parseInt(xpp.getAttributeValue(ATT_NAMESPACE, Attributes.EDGE));                
                sourceSlot = Integer.parseInt(xpp.getAttributeValue(ATT_NAMESPACE, Attributes.SLOTNUMBER));
                xpp.nextTag(); //endTag
                xpp.nextTag(); //startTag
            }
            
            if (isStartTag(xpp, Elements.TARGET)) {
                targetEdge = Integer.parseInt(xpp.getAttributeValue(ATT_NAMESPACE, Attributes.EDGE));
                targetSlot = Integer.parseInt(xpp.getAttributeValue(ATT_NAMESPACE, Attributes.SLOTNUMBER));
                xpp.nextTag();
                xpp.nextTag();
            }
            tmpDForeignKeyList.add(new TmpDForeignKey(dtable, foreignKeyName, sourceSlot, sourceEdge,
                    targetSlot, targetEdge));
            //read close tag of foreignkey_ref
            xpp.nextTag();            
        }
    }
    
    private boolean isStartTag(XmlPullParser xpp, String name) throws Exception {
    	return 
            xpp.getEventType() == XmlPullParser.START_TAG && 
            isNamespace(xpp.getNamespace()) && 
            name.equals(xpp.getName());
    }
    
    private boolean isNamespace(String namespace) {
		return NAMESPACE.equals(namespace) || NAMESPACE_OLD.equals(namespace);
	}

	private boolean isEndTag(XmlPullParser xpp) throws Exception {
    	return xpp.getEventType() == XmlPullParser.END_TAG;    	
    }

    private void endTag(XmlPullParser xpp) throws Exception {
        if(xpp.nextTag() != XmlPullParser.END_TAG){
            throw new SystemException(
                    ParserErrors.message(
                            fileName, xpp.getLineNumber(), ParserErrors.endTagExpected, null));
        }
    }
    
    private void assertStartTag(XmlPullParser xpp, String tag) throws Exception {
        
        if(xpp.nextTag() != XmlPullParser.START_TAG)
            throw new SystemException(
                    ParserErrors.message(
                            fileName, xpp.getLineNumber(), ParserErrors.startTagExpected, null));
        
        if(!(isNamespace(xpp.getNamespace()) && tag.equals(xpp.getName())))
            throw new SystemException(
                    ParserErrors.message(
                            fileName,
                            xpp.getLineNumber(),
                            ParserErrors.tagExpected, 
                            new Object[]{tag, xpp.getName()}));
    }
    
    private class ForeignKeyBuilder {

        private final String keyName;
        private final String[] columnNames;
        private final Table owningTable;
        private final String targetSchema;
        private final String targetTable;
        private final String deleteRule;
        private final String updateRule;
        
        public ForeignKeyBuilder(
                String name, 
                String[] columns, 
                Table table, 
                String targetSchema, 
                String targetTable,
                String deleteRule,
                String updateRule) {
            this.keyName = name;
            this.columnNames = columns;
            this.owningTable = table;
            this.targetSchema = targetSchema;
            this.targetTable = targetTable;
            this.deleteRule = deleteRule;
            this.updateRule = updateRule;
        }
        
        void buildForeignKey(Model model) {
            Schema schema = model.getSchema(targetSchema);
            if(schema == null) {
                RMBenchPlugin.logError("Target Schema ("+targetSchema+") does not exist");
                return;
            }
            Table table = schema.getTable(targetTable);
            if(table ==  null) {
                RMBenchPlugin.logError("Target table ("+targetTable+") does not exist");
                return;
            }
            IForeignKey.Action deleteAction = null;
            if(deleteRule != null) {
                deleteAction = model.getDatabaseInfo().getForeignKeyAction(deleteRule);
                if(deleteAction ==  null) {
                    RMBenchPlugin.logError("delete action ("+deleteRule+"(not supported");
                    return;
                }
            }
            IForeignKey.Action updateAction = null;
            if(updateRule != null) {
                updateAction = model.getDatabaseInfo().getForeignKeyAction(updateRule);
                if(updateAction ==  null) {
                    RMBenchPlugin.logError("update action ("+updateRule+"(not supported");
                    return;
                }
            }
            new ForeignKey(keyName, columnNames, owningTable, table, deleteAction, updateAction);
        }
    }
    
    private class TmpDForeignKey {
        private DTable dtable;
        private String foreignKeyName;
        private int sourceSlot;
        private int sourceEdge;
        private int targetSlot;
        private int targetEdge;
        /**
         * @param dtable
         * @param foreignKeyName
         * @param sourceSlot
         * @param sourceEdge
         * @param targetSlot
         * @param targetEdge
         */
        public TmpDForeignKey(DTable dtable, String foreignKeyName, int sourceSlot, int sourceEdge, int targetSlot, int targetEdge) {
            this.dtable = dtable;
            this.foreignKeyName = foreignKeyName;
            this.sourceSlot = sourceSlot;
            this.sourceEdge = sourceEdge;
            this.targetSlot = targetSlot;
            this.targetEdge = targetEdge;
        }
        
        public DTable getDtable() {
            return dtable;
        }
        
        public String getForeignKeyName() {
            return foreignKeyName;
        }
        public int getSourceEdge() {
            return sourceEdge;
        }
        
        public int getSourceSlot() {
            return sourceSlot;
        }
        
        public int getTargetEdge() {
            return targetEdge;
        }
        
        public int getTargetSlot() {
            return targetSlot;
        }
    }
    
    public static void main(String[] args) throws Exception {
        
        InputStream inStream = new FileInputStream("test1.rbm");
        Model model = ModelReader.read(inStream, "test1.rbm", new Adapter() {

            public IDatabaseInfo getDatabaseInfo(String infoName) {
                return new SQL99();
            }
            public void logError(String message) {
                System.out.println(message);
            }
            public IDatabaseInfo getDefaultDatabaseInfo() {
                return new SQL99();
            }
            
        }, null);
        System.out.println("done reading model "+model.getName());
    }
}
