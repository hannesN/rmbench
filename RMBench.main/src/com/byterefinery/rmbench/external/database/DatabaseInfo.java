/*
 * created 07.04.2005
 * 
 * $Id: DatabaseInfo.java 666 2007-10-01 19:32:51Z cse $
 */
package com.byterefinery.rmbench.external.database;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.external.IDatabaseInfo;
import com.byterefinery.rmbench.external.database.sql99.SQL99;
import com.byterefinery.rmbench.external.model.IDataType;
import com.byterefinery.rmbench.external.model.IForeignKey;
import com.byterefinery.rmbench.external.model.IForeignKey.Action;
import com.byterefinery.rmbench.external.model.type.IntegralDataType;
import com.byterefinery.rmbench.external.model.type.SizeDataType;
import com.byterefinery.rmbench.external.model.type.SizeScaleDataType;

/**
 * Subclasses of this class describe concrete database products and their specific properties.
 * <p>
 * <em>Note: data type names are looked up internally as lowercase</em>
 * @author cse
 */
public abstract class DatabaseInfo implements IDatabaseInfo {

    /**
     * Type converter implementation that works by first converting the type to a standard SQL type,
     * using the converter supplied by the originating database, and then converting the obtained
     * standard SQL type to a native type locally.
     */
    private class TwoStepTypeConverter implements TypeConverter {

        final TypeConverter standardConverter;

        public TwoStepTypeConverter(TypeConverter standardConverter) {
            this.standardConverter = standardConverter;
        }

        public IDataType convert(IDataType dataType, IDatabaseInfo originInfo) {

            IDataType sql99Type = standardConverter.convert(dataType, originInfo);
            return sql99Type != null ? getNativeDataType(sql99Type) : null;
        }
    }

    /**
     * determines the standard datatype by looking up the mapping provided during type registration
     */
    private class StandardTypeConverter implements TypeConverter {

        private final String standardTypeName;

        private final int priority;

        public StandardTypeConverter(String standardTypeName, int priority) {
            this.standardTypeName = standardTypeName;
            this.priority = priority;
        }

        public IDataType convert(IDataType dataType, IDatabaseInfo originInfo) {

            IDataType standardType = RMBenchPlugin.getStandardDatabaseInfo().getDataType(
                    standardTypeName);
            if (standardType != null) {
                IDataType concreteInstance = standardType.concreteInstance();
                setTypeValues(concreteInstance, dataType);
                return concreteInstance;
            }
            return null;
        }

        /**
         * @return Returns the priority.
         */
        public int getPriority() {
            return priority;
        }

    }

    private final Map<String, IDataType> registeredTypes = new HashMap<String, IDataType>();

    private final Map<String, StandardTypeConverter> standardMappings = new HashMap<String, StandardTypeConverter>();

    private transient String[] primaryTypeNames; // cached, sorted names

    protected DatabaseInfo() {
    }

    /**
     * register a datatype with a given standard SQL name
     * @param type
     *            the data type
     * @param standardName
     *            a name from the SQL99 database which is used for type conversion if no specific
     *            conversion is available
     * @param the
     *            priority of the database, it's used for conversion from the SQLStandard to the
     *            Database The smaller the priority the higher the priority.
     * @return the registered type, which is the same as the <code>type</code> parameter
     */
    protected IDataType registerDataType(IDataType type, String standardName, int priority) {
        primaryTypeNames = null; // invalidate cache
        registeredTypes.put(type.getPrimaryName().toUpperCase(), type);
        if (standardName != null) {
            standardMappings.put(
            		type.getPrimaryName().toUpperCase(), new StandardTypeConverter(standardName, priority));
        }
        return type;
    }

    /**
     * register a non-parameterizable datatype (i.e. one that does not take parameters like size or
     * precision parameters)
     * @param name
     *            the datatype name
     * @param standardName
     *            a name from the SQL99 database which is used for type conversion if no specific
     *            conversion is available
     * @param the
     *            priority of the database, it's used for conversion from the SQLStandard to the
     *            Database The smaller the priority the higher the priority.
     * @return the registered data type
     */
    protected IDataType registerDataType(String name, String standardName, int priority) {
        return registerDataType(new String[] { name }, standardName, priority);
    }

    /**
     * register a parameterizable datatype
     * @param name
     *            the type name
     * @param maxSize
     *            the maximum size value, which may also be {@link IDataType#UNLIMITED_SIZE}
     * @param sizeRequired
     *            whether a size parameter is required for this type
     * @param initSize
     *            if <code>sizeRequired</code> is true, the intial (default) size, or
     *            {@link IDataType#UNSPECIFIED_SIZE} if a default value does not apply
     * @param standardName
     *            a name from the SQL99 database which is used for type conversion if no specific
     *            conversion is available
     * @param the
     *            priority of the database, it's used for conversion from the SQLStandard to the
     *            Database The smaller the priority the higher the priority.
     * @return the registered data type
     */
    protected IDataType registerDataType(String name, long maxSize, boolean sizeRequired,
            long initSize, String standardName, int priority) {
        return registerDataType(new String[] { name }, maxSize, sizeRequired, initSize,
                standardName, priority);
    }

    /**
     * register a non-parameterizable data type
     * @param names
     *            the alternative names for the type, with the first one considered as primary name
     * @param standardName
     *            a name from the SQL99 database which is used for type conversion if no specific
     *            conversion is available
     * @param the
     *            priority of the database, it's used for conversion from the SQLStandard to the
     *            Database The smaller the priority the higher the priority.
     * @return the registered data type
     */
    protected IDataType registerDataType(String[] names, String standardName, int priority) {
        return registerDataType(new IntegralDataType(names), standardName, priority);
    }

    /**
     * register a parameterizable datatype
     * @param names
     *            the alternative names for the type, with the first one considered as primary name
     * @param maxSize
     *            the maximum size value, which may also be {@link IDataType#UNLIMITED_SIZE}
     * @param sizeRequired
     *            whether a size parameter is required for this type
     * @param initSize
     *            if <code>sizeRequired</code> is true, the intial (default) size, or
     *            {@link IDataType#UNSPECIFIED_SIZE} if a default value does not apply
     * @param standardName
     *            a name from the SQL99 database which is used for type conversion if no specific
     *            conversion is available
     * @param the
     *            priority of the database, it's used for conversion from the SQLStandard to the
     *            Database The smaller the priority the higher the priority.
     * @return the registered data type
     */
    protected IDataType registerDataType(String[] names, long maxSize, boolean sizeRequired,
            long initSize, String standardName, int priority) {

        return registerDataType(new SizeDataType(names, maxSize, sizeRequired, initSize),
                standardName, priority);
    }

    /**
     * register a parameterizable datatype
     * @param names
     *            the alternative names for the type, with the first one considered as primary name
     * @param maxSize
     *            the maximum size value, which may also be {@link IDataType#UNLIMITED_SIZE}
     * @param sizeRequired
     *            whether a size parameter is required for this type
     * @param initSize
     *            if <code>sizeRequired</code> is true, the intial (default) size, or
     *            {@link IDataType#UNSPECIFIED_SIZE} if a default value does not apply
     * @param maxScale
     *            the maximum scale value, which may also be {@link IDataType#UNLIMITED_SCALE}
     * @param scaleRequired
     *            whether a size parameter is required for this type
     * @param initScale
     *            if <code>scaleRequired</code> is true, the intial (default) scale, or
     *            {@link IDataType#UNSPECIFIED_SCALE} if a default value does not apply
     * @param standardName
     *            a name from the SQL99 database which is used for type conversion if no specific
     *            conversion is available
     * @param the
     *            priority of the database, it's used for conversion from the SQLStandard to the
     *            Database The smaller the priority the higher the priority.
     * @return the registered data type
     */
    protected IDataType registerDataType(String[] names, long maxSize, boolean sizeRequired,
            long initSize, int maxScale, boolean scaleRequired, int initScale, String standardName,
            int priority) {

        return registerDataType(new SizeScaleDataType(names, maxSize, sizeRequired, initSize,
                maxScale, scaleRequired, initScale), standardName, priority);
    }

    public String[] getPrimaryTypeNames() {
        if (primaryTypeNames == null) {
            primaryTypeNames = (String[]) registeredTypes.keySet().toArray(
                    new String[registeredTypes.size()]);
            Arrays.sort(primaryTypeNames);
        }
        return primaryTypeNames;
    }

    public int getPrimaryNameIndex(IDataType dataType) {
        String[] names = getPrimaryTypeNames();
        for (int i = 0; i < names.length; i++) {
            if (names[i].equalsIgnoreCase(dataType.getPrimaryName())) {
                return i;
            }
        }
        throw new IllegalArgumentException();
    }

    public IDataType getDataType(int primaryIndex) {
        IDataType type = (IDataType) registeredTypes.get(getPrimaryTypeNames()[primaryIndex]);
        return type.concreteInstance();
    }

    public IDataType getDataType(String name) {
        name = name.toUpperCase();
        IDataType type = (IDataType) registeredTypes.get(name.toUpperCase());
        if (type == null) {
            for (IDataType dt : registeredTypes.values()) {
                if (dt.hasName(name))
                    return dt.concreteInstance();
            }
            return null;
        }
        else
            return type.concreteInstance();
    }

    public IDataType getDefaultDataType() {
        return getDataType("varchar");
    }

    /**
     * @return the reserved keywords defined by this database. By default, this method delegates to
     *         the {@link SQL99} implementation.
     */
    public Set<String> getReservedWords() {
        return SQL99.instance.getReservedWords();
    }

    public TypeConverter getTypeConverter(IDataType dataType, IDatabaseInfo originInfo) {

        TypeConverter standardConverter = originInfo.getSQL99Converter(dataType);
        if (standardConverter != null) {
            return new TwoStepTypeConverter(standardConverter);
        }
        return null;
    }

    public TypeConverter getSQL99Converter(IDataType dataType) {
        return (TypeConverter) standardMappings.get(dataType.getPrimaryName().toUpperCase());
    }

    /**
     * @param standardType
     *            a type from the standard database
     * @return the native datatype as determined by looking up the mapping provided during type
     *         registration
     */
    public IDataType getNativeDataType(IDataType standardType) {
        IDataType nativeType = null;
        int priority = 10;
        for (Map.Entry<String, StandardTypeConverter> entry : standardMappings.entrySet()) {
            String standardName = entry.getValue().standardTypeName;
            
            if (standardName.equalsIgnoreCase(standardType.getPrimaryName())) {
                
                if (priority > ((StandardTypeConverter) entry.getValue()).getPriority()) {
                    priority = ((StandardTypeConverter) entry.getValue()).getPriority();
                    nativeType = getDataType((String) entry.getKey());
                    if (nativeType != null) {
                        nativeType = nativeType.concreteInstance();
                        setTypeValues(nativeType, standardType);
                    }
                    if (priority==1)
                        return nativeType;
                }
            }
        }
        return nativeType;
    }

    private void setTypeValues(IDataType targetType, IDataType sourceType) {
        if (sourceType.acceptsSize() && targetType.acceptsSize()) {
            long size = sourceType.getSize();
            long maxSize = targetType.getMaxSize();
            targetType.setSize(Math.min(size, maxSize));
        }
        if (sourceType.acceptsScale() && targetType.acceptsScale()) {
            int scale = sourceType.getScale();
            int maxScale = targetType.getMaxScale();
            targetType.setScale(Math.min(scale, maxScale));
        }
    }

    public IForeignKey.Action getDefaultDeleteAction() {
        return IForeignKey.NO_ACTION;
    }

    public IForeignKey.Action getDefaultUpdateAction() {
        return IForeignKey.NO_ACTION;
    }

    public IForeignKey.Action[] getDeleteActions() {
        return IForeignKey.ALL_ACTIONS;
    }

    public IForeignKey.Action[] getUpdateActions() {
        return IForeignKey.ALL_ACTIONS;
    }

    public Action getForeignKeyAction(String deleteRule) {
        for (int i = 0; i < IForeignKey.ALL_ACTIONS.length; i++) {
            if (IForeignKey.ALL_ACTIONS[i].getName().equals(deleteRule))
                return IForeignKey.ALL_ACTIONS[i];
        }
        return null;
    }

    /**
     * by default, always answer <code>true</code>
     */
    public boolean isLoadableObject(String name, String tableType) {
        return true;
    }

    /**
     * by default, always answer <code>false</code>
     */
    public boolean isPublicSchema(String catalogname, String name) {
        return false;
    }

    /**
     * by default, return <code>false</code>
     */
    public boolean impliesDefault(IDataType dataType) {
        return false;
    }

    /**
     * this default implementation checks against the reserved words and then delegates to the SQL99
     * database
     * @see SQL99
     */
    public String validateIdentifier(String identifier) {
        if (getReservedWords().contains(identifier)
                || getReservedWords().contains(identifier.toUpperCase()))
            return MessageKeys.RESERVED_NAME;
        return SQL99.instance.validateIdentifier(identifier);
    }

    /**
     * default implementation, delegates to {@link #validateName(String)}
     */
    public String validateSchemaName(String name) {
        return validateIdentifier(name);
    }

    /**
     * default implementation, delegates to {@link #validateName(String)}
     */
    public String validateTableName(String name) {
        return validateIdentifier(name);
    }

    /**
     * default implementation, delegates to {@link #validateName(String)}
     */
    public String validateColumnName(String name) {
        return validateIdentifier(name);
    }

    /**
     * @return 999
     */
    public int getMaxConstraintNameLength() {
        return 999;
    }

    /**
     * @return 999
     */
    public int getMaxColumnNameLength() {
        return 999;
    }

    /**
     * @return 999
     */
    public int getMaxTableNameLength() {
        return 999;
    }
}