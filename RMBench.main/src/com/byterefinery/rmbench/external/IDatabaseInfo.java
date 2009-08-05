/*
 * created 20.05.2005
 * 
 * $Id: IDatabaseInfo.java 657 2007-08-31 23:20:24Z cse $
 */
package com.byterefinery.rmbench.external;

import java.util.Set;

import com.byterefinery.rmbench.external.model.IDataType;
import com.byterefinery.rmbench.external.model.IForeignKey;

/**
 * Specification of a database product. This interface may be implemented
 * by external parties that use the databaseInfo extension point
 * 
 * @author cse
 */
public interface IDatabaseInfo {
    
    /**
     * interface that predefines some commonly used message keys 
     */
    public interface MessageKeys {

        String INVALID_IDENTIFIER = "invalid_identifier";
        String RESERVED_NAME = "reserved_name";
    }
    
    /**
     * type converter used during schema conversion between different databases
     */
    public interface TypeConverter {
        /**
         * convert a data type that originates from another database to a type native to the 
         * owning database
         * 
         * @param dataType the datatype to convert
         * @param originInfo the originating database
         * @return the converted data type, or <code>null</code> if the conversion is impossible 
         */
        public IDataType convert(IDataType dataType, IDatabaseInfo originInfo);
    }
    
    /**
     * @return an array containing the primary type names registered with 
     * this object
     */
    String[] getPrimaryTypeNames();

    /**
     * @param dataType a registered data type
     * @return the 0-based index at which the primary name of the given type 
     * appears in the array returnd by {@link #getPrimaryTypeNames()}
     * @throws IllegalArgumentException if dataType is not registered
     */
    int getPrimaryNameIndex(IDataType dataType);

    /**
     * @param primaryIndex an index returned by {@link #getPrimaryNameIndex(IDataType)}
     * @return the data type whose name appears at the given index, in writable state
     */
    IDataType getDataType(int primaryIndex);

    /**
     * @param name a type name (primary or secondary)
     * @return the data type registered under the given name, in writable state, or <code>null</code>
     */
    IDataType getDataType(String name);

    /**
     * JDBC column metadata import. The returned data type object is readily initialized from 
     * the given parameter values
     * 
     * @param typeID the JDBC type id as defined in {@link java.sql.Types}
     * @param typeName the native type name, as returned by the JDBC driver
     * @param size the size as returned from the JDBC driver
     * @param scale the scale as returned from the JDBC driver
     * @return the data type object, or <code>null</code> if the datatype is unknown
     */
    IDataType getDataType(int typeID, String typeName, long size, int scale);
    
    /**
     * @return the default type for new columns, in writable state if applicable
     */
    IDataType getDefaultDataType();
    
    /**
     * check whether <code>identifier</code> complies with the rules for identifiers. 
     *  
     * @param identifier
     * @return an error key (usually {@link MessageKeys#INVALID_NAME), or <code>null</code>
     * @see #getReservedWords()
     */
    String validateIdentifier(String identifier);

    /**
     * @param name a name for a new schema
     * @return an error key that can be resolved by the message provider associated with this
     * database info, or <code>null</code> if the name is valid
     */
    String validateSchemaName(String name);

    /**
     * @param name a proposed table name
     * @return an error key that can be resolved by the message provider associated with this
     * database info, or <code>null</code> if the name is valid
     */
    String validateTableName(String name);
    

    /**
     * @param name a proposed column name
     * @return an error key that can be resolved by the message provider associated with this
     * database info, or <code>null</code> if the name is valid
     */
    String validateColumnName(String name);
    
    /**
     * @return the reserved keywords defined by this database.
     */
    public Set<String> getReservedWords();
    
    /**
     * get a type converter that can convert a type from another database to a type of this 
     * database
     * 
     * @param dataType the data type to convert
     * @param originInfo the database from which the type originates
     * @return a type converter capable of converting the given datatype to a native type 
     * of this database, or <code>null</code>
     */
    TypeConverter getTypeConverter(IDataType dataType, IDatabaseInfo originInfo);

    /**
     * get a type converter that can convert a type from this database to a type of the 
     * SQL99 standard database
     * 
     * @param dataType a native datatype from this database
     * @return a type converter capable of converting the given native datatype to a 
     * type from the SQL99 standard database, or <code>null</code>
     */
    TypeConverter getSQL99Converter(IDataType dataType);
    
    /**
     * @return the default action for the ON DELETE clause of a foreign key
     */
    IForeignKey.Action getDefaultDeleteAction();

    /**
     * @return the default action for the ON DELETE clause of a foreign key
     */
    IForeignKey.Action getDefaultUpdateAction();
    
    /**
     * @return the available actions for foreign key ON DELETE clauses
     */
    IForeignKey.Action[] getDeleteActions();

    /**
     * @return the available actions for foreign key ON UPDATE clauses
     */
    IForeignKey.Action[] getUpdateActions();

    /**
     * @param name
     * @return the corresponding action
     */
    IForeignKey.Action getForeignKeyAction(String deleteRule);

    /**
     * @param name a catalog name, possibly <code>null</code>
     * @param name a schema name
     * @return <code>true</code> if this the name of a schema that is always available and cannot 
     * be created explicitly
     */
    boolean isPublicSchema(String catalogname, String name);

    /**
     * used during schema import
     * 
     * @param name an object name (typically table, view, or sequence)
     * @param tableType a table type descriptor, as provided by the metadata access API (e.g., jdbc)
     * @return whether the given object should be loaded from the database
     */
    boolean isLoadableObject(String name, String tableType);

    /**
     * @param dataType a datatype from this database 
     * @return <code>true</code> if this type implies a default value, which is ususally the case for
     * autoincrement types
     */
	boolean impliesDefault(IDataType dataType);

    /**
     * @return the maximum length for a constraint name
     */
    int getMaxConstraintNameLength();

    /**
     * @return the maximum length for a column name
     */
    int getMaxColumnNameLength();

    /**
     * @return the maximum length for a table name
     */
    int getMaxTableNameLength();
}