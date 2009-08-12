/*
 * created 31.12.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id$
 */
package com.byterefinery.rmbench.export;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;

import com.byterefinery.rmbench.external.IDDLFormatter;
import com.byterefinery.rmbench.external.IDDLGenerator;
import com.byterefinery.rmbench.external.IDDLScript;
import com.byterefinery.rmbench.external.IDatabaseInfo;
import com.byterefinery.rmbench.model.Model;
import com.byterefinery.rmbench.model.schema.CheckConstraint;
import com.byterefinery.rmbench.model.schema.ForeignKey;
import com.byterefinery.rmbench.model.schema.Index;
import com.byterefinery.rmbench.model.schema.Schema;
import com.byterefinery.rmbench.model.schema.Table;
import com.byterefinery.rmbench.model.schema.UniqueConstraint;

/**
 * an editor input for use with an {@link com.byterefinery.rmbench.export.DDLEditor}
 * 
 * @author cse
 */
public class DDLEditorInput implements IStorageEditorInput, IPersistableElement {

    private final Model model;
    private final IStorage storage;
    private File file;
    private byte[] ddl;
    
    private final String terminator;
    
    public DDLEditorInput(
            Model model, 
            IDDLGenerator generator, 
            IDDLFormatter formatter, 
            IDDLScript script, 
            boolean generateDrop) {
    	
        this.model = model;
        this.terminator = script.getStatementTerminator();
        generateDDL(script, generator, generateDrop);
        this.ddl = script.generate(formatter).getBytes();
        this.storage = new Storage();
    }

    public DDLEditorInput(File file) {
        this.model = null;
        this.terminator = IDDLScript.DEFAULT_TERMINATOR;
        this.file = file;
        this.storage = new Storage();
    }


    /**
     * @return the statement terminator used by the undelying script
     */
    public String getStatementTerminator() {
        return terminator;
    }
    
    public boolean exists() {
        return file != null ? file.exists() : false;
    }

    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    public String getName() {
        return file != null ? file.getName() : model.getName()+" "+Messages.ExportInput_NameExt;
    }

    public IPersistableElement getPersistable() {
        return file != null ? this : null;
    }

    public String getToolTipText() {
        return file != null ? file.getAbsolutePath() : Messages.ExportInput_ToolTip;
    }

    @SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
        return null;
    }

    public IStorage getStorage() throws CoreException {
        return storage;
    }

    public String getFactoryId() {
        return "not.yet.implemented";
    }

    public void saveState(IMemento memento) {
    }
    
    public boolean equals(Object other) {
        
        if (this == other)
            return true;
        if (!(other instanceof DDLEditorInput))
            return false;
        DDLEditorInput input = (DDLEditorInput)other;
        return file == null ? 
                model.equals(input.model) : file.equals(input.file);
    }
    
    /**
     * @return the database info of the model
     */
    public IDatabaseInfo getDatabaseInfo() {
        return model.getDatabaseInfo();
    }
    
    /*
     * run through the model and invoke the generator for each element
     */
    private void generateDDL(
            IDDLScript script, IDDLGenerator generator, boolean generateDrop) {

        if(generateDrop)
            generateDropDDL(script, generator);
        
        List<ForeignKey> foreignKeys = new ArrayList<ForeignKey>();
        for(Iterator<Schema> schemaIter=model.getSchemas().iterator(); schemaIter.hasNext(); ) {
            Schema schema = schemaIter.next();
            generator.createSchema(schema.getISchema(), script);
            
            for (Iterator<Table> tableIter = schema.getTables().iterator(); tableIter.hasNext();) {
                Table table = tableIter.next();
                generator.createTable(table.getITable(), script);
                foreignKeys.addAll(table.getForeignKeys());
                
                for (Iterator<Index> indexIter = table.getIndexes().iterator(); indexIter.hasNext();) {
                    Index index = indexIter.next();
                    generator.createIndex(index.getIIndex(), script);
                }
                for (Iterator<UniqueConstraint> it=table.getUniqueConstraints().iterator(); it.hasNext();) {
                	UniqueConstraint constraint = it.next();
                    generator.createUniqueConstraint(constraint.getIConstraint(), script);
                }
                for (Iterator<CheckConstraint> it=table.getCheckConstraints().iterator(); it.hasNext();) {
                	CheckConstraint constraint = it.next();
                    generator.createCheckConstraint(constraint.getIConstraint(), script);
                }
            }
        }
        for (Iterator<ForeignKey> fkIter = foreignKeys.iterator(); fkIter.hasNext();) {
            ForeignKey foreignKey = fkIter.next();
            generator.createForeignKey(foreignKey.getIForeignKey(), script);
        }
    }

    private void generateDropDDL(IDDLScript script, IDDLGenerator generator) {
        
        for(Iterator<Schema> schemaIter=model.getSchemas().iterator(); schemaIter.hasNext(); ) {
            Schema schema = schemaIter.next();
            
            for (Iterator<Table> tableIter = schema.getTables().iterator(); tableIter.hasNext();) {
                Table table = tableIter.next();
                for (Iterator<ForeignKey> fkIter = table.getForeignKeys().iterator(); fkIter.hasNext();) {
                    ForeignKey foreignKey = fkIter.next();
                    generator.dropForeignKey(foreignKey.getIForeignKey(), script);
                }
                for (Iterator<Index> indexIter = table.getIndexes().iterator(); indexIter.hasNext();) {
                    Index index = indexIter.next();
                    generator.dropIndex(index.getIIndex(), script);
                }
                generator.dropTable(table.getITable(), script);
            }
            generator.dropSchema(schema.getISchema(), script);
        }
    }
    
    private class Storage implements IStorage {

        public InputStream getContents() throws CoreException {
            if(file == null) {
                return new ByteArrayInputStream(ddl);
            }
            else {
                try {
                    return new FileInputStream(file);
                }
                catch (IOException e) {
                    return new ByteArrayInputStream(new byte[0]);
                }
            }
        }

        public IPath getFullPath() {
            return null;
        }

        public String getName() {
            return DDLEditorInput.this.getName();
        }

        public boolean isReadOnly() {
            return false;
        }

        @SuppressWarnings("unchecked")
		public Object getAdapter(Class adapter) {
            return null;
        }
    }
}
