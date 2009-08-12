/*created 26.05.2005
 *
 * &copy; 2005, DynaBEAN Consulting
 * 
 * $Id: SchemaPropertySource.java 179 2006-02-14 07:42:59Z hannesn $
 */
package com.byterefinery.rmbench.views.property;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.byterefinery.rmbench.EventManager;
import com.byterefinery.rmbench.RMBenchMessages;
import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.EventManager.Event;
import com.byterefinery.rmbench.model.schema.Schema;
import com.byterefinery.rmbench.operations.SchemaCatalogOperation;
import com.byterefinery.rmbench.operations.SchemaNameOperation;
import com.byterefinery.rmbench.views.model.SchemaNameValidator;

/**
 * property source for schemas
 * 
 * @author hannesn
 */
public class SchemaPropertySource extends RMBenchPropertySource {

    public final static String  P_CATALOGNAME="catalogname_property";
    
    private Schema schema;
    
    private SchemaNameValidator nameValidator = new  SchemaNameValidator();
    
    private final IPropertyDescriptor propertiesDescriptors[] = {
            new TextPropertyDescriptor(P_NAME, RMBenchMessages.SchemaPropertySource_name),
            new TextPropertyDescriptor(P_CATALOGNAME, RMBenchMessages.SchemaPropertySource_catalog),
    };
    
    private EventManager.Listener listener = new EventManager.Listener() {

        public void eventOccurred(int eventType, Event event) {
            if (event.origin==SchemaPropertySource.this)
                return;
            
            switch (eventType) {
                case SCHEMA_DELETED: {
                    RMBenchPlugin.getEventManager().removeListener(this);
                    break;
                }
                case SCHEMA_MODIFIED: {
                    refresh();
                    break; 
                }
            }
        }
        
        public void register() {
            RMBenchPlugin.getEventManager().addListener(SCHEMA_MODIFIED | SCHEMA_DELETED, this);
        }
        
    };
    
    
    public SchemaPropertySource(Schema schema) {
        this.schema=schema;
        listener.register();
    }

    public Object getPropertyValue(Object id) {
        if (id.equals(P_NAME))
            return schema.getName();
        if (id.equals(P_CATALOGNAME))
            return (schema.getCatalogName()!=null) ? schema.getCatalogName() : "";
        
        return null;
    }

    public void setPropertyValue(Object id, Object value) {
        if (id.equals(P_NAME)) {
            if (((String)value).length()==0)
                return;
            if (nameValidator.isValid((String) value)==null) {
                SchemaNameOperation op=new SchemaNameOperation(schema, (String) value);
                op.execute(this);
            }
            return;
        }
        if (id.equals(P_CATALOGNAME)) {
            if (((String)value).length()==0)
                return;
            SchemaCatalogOperation op=new SchemaCatalogOperation(schema, (String) value);
            op.execute(this);
            return;
        }
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {
        return propertiesDescriptors;
    }
}
