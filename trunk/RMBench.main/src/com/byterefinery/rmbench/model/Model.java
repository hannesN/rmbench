/*
 * created 29.05.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: Model.java 668 2007-10-04 18:48:16Z cse $
 */
package com.byterefinery.rmbench.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.byterefinery.rmbench.external.IDatabaseInfo;
import com.byterefinery.rmbench.external.INameGenerator;
import com.byterefinery.rmbench.external.model.IDiagram;
import com.byterefinery.rmbench.external.model.IModel;
import com.byterefinery.rmbench.external.model.ISchema;
import com.byterefinery.rmbench.model.diagram.Diagram;
import com.byterefinery.rmbench.model.schema.Column;
import com.byterefinery.rmbench.model.schema.Schema;
import com.byterefinery.rmbench.model.schema.Table;

/**
 * the model groups together any number of database schemas and diagramsshowing 
 * tables and relationships from the schemas. The model is the fundamental work unit 
 * of RMBench.
 * 
 * @author cse
 */
public class Model {

    private final IModel imodel = new IModel() {

        public IDatabaseInfo getDatabaseInfo() {
            return Model.this.getDatabaseInfo();
        }

        public ISchema[] getSchemas() {
            ISchema[] ischemas = new ISchema[Model.this.schemas.size()];
            for (int i = 0; i < ischemas.length; i++) {
                ischemas[i] = Model.this.schemas.get(i).getISchema();
            }
            return ischemas;
        }

		public String getName() {
			return name;
		}

		public IDiagram[] getDiagrams() {
            IDiagram[] idiagrams = new IDiagram[Model.this.diagrams.size()];
            for (int i = 0; i < idiagrams.length; i++) {
                idiagrams[i] = Model.this.diagrams.get(i).getIDiagram();
            }
            return idiagrams;
		}
    };
    
    private String name;
    private IDatabaseInfo databaseInfo;
    private INameGenerator nameGenerator;
    
    private static List<Diagram> EMPYT_DIAGRAMS = Collections.emptyList();
    
    private List<Schema> schemas = new ArrayList<Schema>();
    private List<Diagram> diagrams = EMPYT_DIAGRAMS;
    
    
    /**
     * @param name the model name, usually for display purposes
     * @param databaseInfo the database
     * @param nameGenerator the name generator
     */
    public Model(
            String name, 
            IDatabaseInfo databaseInfo, 
            INameGenerator nameGenerator) {
        
        this.name = name;
        this.databaseInfo = databaseInfo;
        this.nameGenerator = nameGenerator;
    }

    public IModel getIModel() {
        return imodel;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Collection<Diagram> getDiagrams() {
        return diagrams;
    }
    
    /**
     * @param name the diagram name
     * @return the diagram with the given name, or <code>null</code>
     */
    public Diagram getDiagram(String name) {
        for (Iterator<Diagram> it = getDiagrams().iterator(); it.hasNext();) {
            Diagram diagram = it.next();
            if(diagram.getName().equals(name))
                return diagram;
        }
        return null;
    }
    
    public Collection<Schema> getSchemas() {
        return schemas;
    }

    public Schema[] getSchemasArray() {
        return schemas.toArray(new Schema[schemas.size()]);
    }

    public IDatabaseInfo getDatabaseInfo() {
        return databaseInfo;
    }
    
    /**
     * set the database info for this model. the change will also be applied to
     * all enclosed schemas and subordinate entities, recursively 
     * 
     * @param databaseInfo
     */
    public void setDatabaseInfo(IDatabaseInfo databaseInfo) {
        this.databaseInfo = databaseInfo;
        for (Iterator<Schema> it = schemas.iterator(); it.hasNext();) {
            Schema schema = it.next();
            schema.setDatabaseInfo(databaseInfo);
        }
    }
    
    public INameGenerator getNameGenerator() {
        return nameGenerator;
    }
    
    public void setNameGenerator(INameGenerator nameGenerator) {
        this.nameGenerator = nameGenerator;
    }
    
    public void addSchema(Schema schema) {
        schemas.add(schema);
    }

    public void removeSchema(Schema schema) {
        schemas.remove(schema);
    }

    public Schema getSchema(String schemaName) {
        Schema schema;
        for (Iterator<Schema> it = schemas.iterator(); it.hasNext();) {
            schema = it.next();
            if (schema.getName().equals(schemaName))
                return schema;
        }
           
        return null;
            
    }
    
	public Table findTable(String schemaName, String tableName) {
		Schema schema = getSchema(schemaName);
		return schema != null ? schema.getTable(tableName) : null;
	}
	
    public void addDiagram(Diagram diagram) {
        getMutableDiagrams().add(diagram);
    }

    public void removeDiagram(Diagram diagram) {
        getMutableDiagrams().remove(diagram);
    }
    
    private List<Diagram> getMutableDiagrams() {
        if(diagrams == EMPYT_DIAGRAMS)
            diagrams = new ArrayList<Diagram>();
        return diagrams;
    }

    
    /**
     * @return true if this model does not contain any schemas yet
     */
    public boolean isEmpty() {
        return schemas.isEmpty();
    }
    
    /**
     * @param columns columns belonging to a foreign key. It is thus assumed that all
     * columns have the same owning table
     * @return a list of tables from this model that have primary keys that are foreign-key
     * compatible with the given columns
     */
    public List<Table> findMatchingTables(Column[] columns) {
        List<Table> matchingTables = new ArrayList<Table>();
        
        for (Iterator<Schema> schemaIterator = getSchemas().iterator(); schemaIterator.hasNext();) {
            Schema schema = schemaIterator.next();
            for (Iterator<Table> tableIterator = schema.getTables().iterator(); tableIterator.hasNext();) {
                Table table = tableIterator.next();
                if(table.getPrimaryKey() != null && table.getPrimaryKey().matches(columns))
                    matchingTables.add(table);
            }
        }
        return matchingTables;
    }
    
    /**
     * @param constraintName a constraint name
     * @return true if the model contains a constraint with the given name
     */
    public boolean containsConstraint(String constraintName){
        for (Iterator<Schema> i1 = schemas.iterator(); i1.hasNext();) {
            Schema schema = i1.next();
            
            for (Iterator<Table> i2 = schema.getTables().iterator(); i2.hasNext();) {
                Table table = i2.next();
                
                if(table.hasConstraintName(constraintName))
                    return true;
            }
        }
        return false;  
    }

    /**
     * @return the number of tables in all schemas
     */
    public int getTableCount() {
        int count = 0;
        for (Iterator<Schema> i1 = schemas.iterator(); i1.hasNext();) {
            Schema schema = i1.next();
            count += schema.getTables().size();
        }
        return count;
    }
    
    public boolean equals(Object obj) {
        return obj instanceof Model && name.equals(((Model)obj).name);
    }

    public int hashCode() {
        return name.hashCode();
    }
}
