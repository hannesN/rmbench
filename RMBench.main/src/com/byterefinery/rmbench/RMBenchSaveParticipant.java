/*
 * created 19.05.2005
 * 
 * $Id: RMBenchSaveParticipant.java 678 2008-02-17 22:52:09Z cse $
 */
package com.byterefinery.rmbench;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

import com.byterefinery.rmbench.exceptions.ExceptionMessages;
import com.byterefinery.rmbench.exceptions.SystemException;
import com.byterefinery.rmbench.extension.DatabaseExtension;
import com.byterefinery.rmbench.extension.JdbcDriverExtension;
import com.byterefinery.rmbench.jdbc.IDriverInfo;
import com.byterefinery.rmbench.jdbc.JdbcAccess;
import com.byterefinery.rmbench.jdbc.MutableDriverInfo;
import com.byterefinery.rmbench.model.dbimport.DBModel;
import com.byterefinery.rmbench.util.StringEncrypter;
import com.byterefinery.rmbench.util.StringEncrypter.EncryptionException;
import com.byterefinery.rmbench.util.xml.XMLAttributes;
import com.byterefinery.rmbench.util.xml.XMLReader;
import com.byterefinery.rmbench.util.xml.XMLWriter;

/**
 * responsible for saving and restoring plugin state
 * 
 * @author cse
 */
public class RMBenchSaveParticipant implements ISaveParticipant {

    private interface ATT {
        String FILE = "file";
        String DRIVER = "driver";
        String CLASS_NAME = "className";
        String DATABASE = "database";
        String URL = "url";
        String PASSWORD = "password";
        String USER = "user";
        String TYPE = "type";
        String DO_PROMPT = "doPrompt";
        String NAME = "name";
        String VERSION = "version";
        String LOAD_INDEXES = "loadIndexes";
        String LOAD_KEYINDEXES = "loadKeyIndexes";
        String LOAD_COMMENTS = "loadComments";
    }

    private interface ELEM {
        String JAR = "jar";
        String DRIVER = "driver";
        String JDBC_ACCESS = "jdbcAccess";
        String SCHEMA = "schema";
        String SCHEMA_RULE = "schemaRule";
        String CONNECTION = "dbmodel";
        String CONNECTIONS = "dbmodels";
        String RMBENCH = "rmbench";
    }

    public static final int CHANGED_CONNECTIONS = 1;
    public static final int CHANGED_ALL = Integer.MAX_VALUE;
    
    public static final String RMBENCHSTATE_XML = "rmbenchstate.xml";

    private int changed_flags;
    
    private StringEncrypter encrypter;
    
    public RMBenchSaveParticipant(){
        try{    
            encrypter = new StringEncrypter(StringEncrypter.DESEDE_ENCRYPTION_SCHEME);          
        }catch(EncryptionException e){
            RMBenchPlugin.logError("error initializing string encrypter",e);
        }
    }
    
    /**
     * set a changed marker
     * @param the aspect that was changed, one of the CHANGED_xx constants
     */
    public void setChanged(int what) {
        changed_flags |= what;
    }
    
    /**
     * set a changed marker for all aspects
     */
    public void setChangedAll() {
        changed_flags = CHANGED_ALL;
    }
    
    public void prepareToSave(ISaveContext context) throws CoreException {
    }

    public void saving(ISaveContext context) throws CoreException {
        if(changed_flags == 0)
            return;
        
        switch (context.getKind()) {
            case ISaveContext.FULL_SAVE: {
                RMBenchPlugin plugin = RMBenchPlugin.getDefault();
                int saveNumber = context.getSaveNumber();
                String saveFileName = RMBENCHSTATE_XML+"."+saveNumber;
                
                File stateFile = plugin.getStateLocation().append(saveFileName).toFile();
                writePluginState(stateFile, plugin);
                
                context.map(new Path(RMBENCHSTATE_XML), new Path(saveFileName));
                context.needSaveNumber();
                break;
            }
            case ISaveContext.PROJECT_SAVE:
                break;
            case ISaveContext.SNAPSHOT:
                break;
        }
    }
    
    public void doneSaving(ISaveContext context) {
        if(changed_flags == 0)
            return;
        
        RMBenchPlugin plugin = RMBenchPlugin.getDefault();

        int previousSaveNumber = context.getPreviousSaveNumber();
        String oldFileName = RMBENCHSTATE_XML + "." + previousSaveNumber;
        File oldFile = plugin.getStateLocation().append(oldFileName).toFile();
        
        oldFile.delete();
    }

    public void rollback(ISaveContext context) {
        if(changed_flags == 0)
            return;
        
        RMBenchPlugin plugin = RMBenchPlugin.getDefault();

        int saveNumber = context.getSaveNumber();
        String saveFileName = RMBENCHSTATE_XML+"."+saveNumber;
        File saveFile = plugin.getStateLocation().append(saveFileName).toFile();
        
        saveFile.delete();
    }

    public void readPluginState(File stateFile, RMBenchPlugin plugin) {
        
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(stateFile);
            XMLReader reader = new XMLReader(fileReader);
            reader.nextElement(ELEM.RMBENCH);
            reader.nextElement(ELEM.CONNECTIONS);
            readDBModels(reader, plugin.dbmodels);
        }
        catch(SystemException e) {
            RMBenchPlugin.logError("error parsing state file", e);
            setChangedAll();
        }
        catch (IOException e) {
            RMBenchPlugin.logError("error reading state file", e);
            setChangedAll();
        }
        finally {
            if(fileReader != null) try {
                fileReader.close();
                } catch(IOException x) {};
        }
    }
    
    private void writePluginState(File stateFile, RMBenchPlugin plugin) throws CoreException {
        XMLWriter writer;
        try {
            writer = new XMLWriter(new FileWriter(stateFile));
            XMLAttributes atts = new XMLAttributes();
            
            atts.addAttribute(ATT.VERSION, RMBenchPlugin.VERSION);
            writer.startElement(ELEM.RMBENCH, atts);
            
            writer.startElement(ELEM.CONNECTIONS);
            for (Iterator<DBModel> it = plugin.dbmodels.iterator(); it.hasNext();) {
                DBModel connection = it.next();
                if(connection.getDBAccess().isConfigurable())
                    writeDBModel(connection, writer);
            }
            writer.endElement(ELEM.CONNECTIONS);
            writer.endElement(ELEM.RMBENCH);
            writer.close();
        }
        catch (IOException e) {
            throw new CoreException(
                    new Status(
                            Status.ERROR, 
                            RMBenchConstants.PLUGIN_ID, 0, 
                            ExceptionMessages.errorWritingStateFile, e));
        }
    }

    private void readDBModels(XMLReader reader, List<DBModel> connections) 
    throws IOException {
        
        XMLAttributes atts = reader.nextElement(ELEM.CONNECTION);
        while(atts != null) {
            String name = atts.getString(ATT.NAME);
            boolean doPrompt = atts.getBoolean(ATT.DO_PROMPT);
            
            DBModel.SchemaRule schemaRule;
            atts = reader.nextElement(ELEM.SCHEMA_RULE);
            String type = atts.getString(ATT.TYPE);
            if("all".equals(type)) {
                schemaRule = DBModel.ALL_SCHEMAS;
            }
            else {
                DBModel.SelectedSchemasRule rule = new DBModel.SelectedSchemasRule();
                atts = reader.nextElement(ELEM.SCHEMA);
                while(atts != null) {
                    rule.addSchema(atts.getString(ATT.NAME));
                    atts = reader.nextElement(ELEM.SCHEMA);
                }
                schemaRule = rule;
            }
            JdbcAccess builder = readJdbcAccess(reader);
            if(builder != null) {
                connections.add(new DBModel(name, schemaRule, doPrompt, builder));
            }
            atts = reader.nextElement(ELEM.CONNECTION);
        }
    }

    private JdbcAccess readJdbcAccess(XMLReader reader) throws IOException {
        
        XMLAttributes atts = reader.nextElement(ELEM.JDBC_ACCESS);
        String driverId = atts.getString(ELEM.DRIVER);
        String user = atts.getString(ATT.USER);
        String password = "";
        try {
            password = atts.getString(ATT.PASSWORD);
            if (password!=null)
                password = encrypter.decrypt(password);
        }
        catch(EncryptionException e){
            RMBenchPlugin.logError("error decrypting password", e);
        }
        String url = atts.getString(ATT.URL);
        
        boolean loadIndexes = atts.getBoolean(ATT.LOAD_INDEXES);
        boolean loadKeyIndexes = atts.getBoolean(ATT.LOAD_KEYINDEXES);
        boolean loadComments = atts.getBoolean(ATT.LOAD_COMMENTS);
        
        IDriverInfo driverInfo;
        if(driverId != null) {
            driverInfo = RMBenchPlugin.getExtensionManager().getJdbcDriverExtension(driverId);
            if(driverInfo == null) {
                RMBenchPlugin.logError("driver info extension not found: "+driverId);
                return null;
            }
        }
        else {
            atts = reader.nextElement(ELEM.DRIVER);
            String dbId = atts.getString(ATT.DATABASE);
            String className = atts.getString(ATT.CLASS_NAME);
            
            DatabaseExtension dbext = RMBenchPlugin.getExtensionManager().getDatabaseExtension(dbId);
            if(dbext == null) {
                RMBenchPlugin.logError("database info extension not found: "+dbId);
                return null;
            }
            driverInfo = new MutableDriverInfo(className, dbext);
        }
        atts = reader.nextElement(ELEM.JAR);
        List<String> files = new ArrayList<String>();
        while(atts != null) {
            files.add(atts.getString(ATT.FILE));
            atts = reader.nextElement(ELEM.JAR);
        }
        String[] fileNames = (String[])files.toArray(new String[files.size()]);
        return new JdbcAccess(
                user, 
                password, 
                url, 
                driverInfo,
                fileNames, 
                loadIndexes, 
                loadKeyIndexes, 
                loadComments);
    }

    private void writeDBModel(DBModel dbModel, XMLWriter writer) throws IOException {
        
        XMLAttributes atts = new XMLAttributes();
        atts.addAttribute(ATT.NAME, dbModel.getName());
        atts.addAttribute(ATT.DO_PROMPT, dbModel.isDoPrompt());
        writer.startElement(ELEM.CONNECTION, atts);
        
        DBModel.SchemaRule schemaRule = dbModel.getSchemaRule();
        atts.reset();
        if(schemaRule == DBModel.ALL_SCHEMAS) {
            atts.addAttribute(ATT.TYPE, "all");
            writer.emptyElement(ELEM.SCHEMA_RULE, atts);
        }
        else {
            atts.addAttribute(ATT.TYPE, "selected");
            DBModel.SelectedSchemasRule selectedRule = (DBModel.SelectedSchemasRule)schemaRule;

            writer.startElement(ELEM.SCHEMA_RULE, atts);
            for (Iterator<String> it = selectedRule.iterator(); it.hasNext();) {
                String name = it.next();
                atts.reset();
                atts.addAttribute(ATT.NAME, name);
                writer.emptyElement(ELEM.SCHEMA, atts);
            }
            writer.endElement(ELEM.SCHEMA_RULE);
        }
        writeJdbcAccess((JdbcAccess)dbModel.getDBAccess(), writer);
        writer.endElement(ELEM.CONNECTION);
    }

    private void writeJdbcAccess(JdbcAccess jdbc, XMLWriter writer) throws IOException {

        XMLAttributes atts = new XMLAttributes();
        IDriverInfo driverInfo = jdbc.getDriverInfo();
        boolean constDriver = (driverInfo instanceof JdbcDriverExtension);
        if(constDriver){
            atts.addAttribute(ATT.DRIVER, ((JdbcDriverExtension)driverInfo).getId());
        }
        
        atts.addAttribute(ATT.LOAD_INDEXES, jdbc.getLoadIndexes());
        atts.addAttribute(ATT.LOAD_KEYINDEXES, jdbc.getLoadKeyIndexes());
        atts.addAttribute(ATT.LOAD_COMMENTS, jdbc.getLoadComments());
        
        if (jdbc.getUser()!=null)
            atts.addAttribute(ATT.USER, jdbc.getUser());
        try {
            if (jdbc.getPassword()!=null)
                atts.addAttribute(ATT.PASSWORD, encrypter.encrypt(jdbc.getPassword()));
        }
        catch (EncryptionException e) {
            RMBenchPlugin.logError("error encrypting password", e);
        }
        atts.addAttribute(ATT.URL, jdbc.getUrl());
        writer.startElement(ELEM.JDBC_ACCESS, atts);
        if(!constDriver) {
            atts.reset();
            atts.addAttribute(
                    ATT.DATABASE, 
                    RMBenchPlugin.getExtensionManager().getDatabaseExtension(driverInfo.getDatabaseInfo()).getId());
            atts.addAttribute(ATT.CLASS_NAME, driverInfo.getClassName());
            writer.emptyElement(ELEM.DRIVER, atts);
        }
        String[] jarFiles = jdbc.getJarFileNames();
        for (int i = 0; i < jarFiles.length; i++) {
            atts.reset();
            atts.addAttribute(ATT.FILE, jarFiles[i]);
            writer.emptyElement(ELEM.JAR, atts);
        }
        writer.endElement(ELEM.JDBC_ACCESS);
    }
}
