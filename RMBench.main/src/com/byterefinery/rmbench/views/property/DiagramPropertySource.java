/*
 * created 26.05.2005
 *
 * &copy; 2005, DynaBEAN Consulting
 * 
 * $Id: DiagramPropertySource.java 666 2007-10-01 19:32:51Z cse $ 
 */
package com.byterefinery.rmbench.views.property;

import java.util.Iterator;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.byterefinery.rmbench.EventManager;
import com.byterefinery.rmbench.RMBenchMessages;
import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.EventManager.Event;
import com.byterefinery.rmbench.model.Model;
import com.byterefinery.rmbench.model.diagram.Diagram;
import com.byterefinery.rmbench.model.schema.Schema;
import com.byterefinery.rmbench.operations.DiagramDefaultSchemaOperation;
import com.byterefinery.rmbench.operations.DiagramNameOperation;

/**
 * property source for diagrams
 * 
 * @author hannesn
 */
public class DiagramPropertySource extends RMBenchPropertySource {

    public final static String  P_DEFAULTSCHEMA = "defaultschema_property";
    
    private Diagram diagram;
    private Integer defaultNameIndex;
    private String[] nameList;
    
    private IPropertyDescriptor propertiesDescriptors[] = {
            new TextPropertyDescriptor(P_NAME, RMBenchMessages.DiagramPropertySource_Name),
            null
    };
    
    private EventManager.Listener listener = new EventManager.Listener() {

        public void eventOccurred(int eventType, Event event) {
            if (event.origin==DiagramPropertySource.this)
                return;
            
            switch (eventType) {
                case SCHEMA_ADDED:
                case SCHEMA_DELETED:
                case SCHEMA_MODIFIED: {
                    propertiesDescriptors[1] = getSchemaPropertyDescriptor();
                    refresh();
                    break;
                }
                case DIAGRAM_MODIFIED: {
                    if(event.element == diagram) {
                        if(event.info == EventManager.Properties.SCHEMA) {
                            computeSelectedName();
                        }
                        refresh();
                    }
                    break;
                }
                case DIAGRAM_DELETED: { 
                    if (event.element.equals(diagram))
                        RMBenchPlugin.getEventManager().removeListener(this);
                    break;
                }
            }
        }
        
        public void register() {
            RMBenchPlugin.getEventManager().addListener(
                    SCHEMA_MODIFIED | 
                    SCHEMA_DELETED | 
                    SCHEMA_ADDED | 
                    DIAGRAM_MODIFIED |
                    DIAGRAM_DELETED, this);
        }
    };

    public DiagramPropertySource(Diagram diagram) {
        this.diagram=diagram;
        propertiesDescriptors[1] = getSchemaPropertyDescriptor();
        listener.register();
    }

    public Object getPropertyValue(Object id) {
        if (id.equals(P_NAME))
            return diagram.getName();
        if (id.equals(P_DEFAULTSCHEMA))
            return defaultNameIndex;
        return null;
    }

    public void setPropertyValue(Object id, Object value) {
       if (id.equals(P_NAME)) {
            if (((String)value).length()==0)
                return;
            //check if another diagram has the wanted name
            for (Diagram tmpDiagram : diagram.getModel().getDiagrams()) {
                if ((tmpDiagram!=diagram) && (tmpDiagram.getName().equals(value)) )
                    return;
            }
            //name is ok
            DiagramNameOperation op = new DiagramNameOperation(diagram, (String) value);
            op.execute(this);
            return;
       }
       else if (id.equals(P_DEFAULTSCHEMA)) {
           Schema newSchema=diagram.getModel().getSchema(nameList[((Integer)value).shortValue()]);
           DiagramDefaultSchemaOperation op=new DiagramDefaultSchemaOperation(diagram, newSchema);
           op.execute(this);
           defaultNameIndex = (Integer) value;
       }
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {
        return propertiesDescriptors;
    }

    private IPropertyDescriptor getSchemaPropertyDescriptor() {
        ComboBoxPropertyDescriptor propertyDescriptor = null;
        Model model = diagram.getModel();
        if (model != null) {
            nameList = new String[model.getSchemas().size()];
            int i=0;
            for (Iterator<Schema> iter=diagram.getModel().getSchemas().iterator(); iter.hasNext(); i++) {
                Schema tmpSchema = iter.next();
                nameList[i] = tmpSchema.getName();
                if (nameList[i].equals(diagram.getDefaultSchema().getName())) {
                    defaultNameIndex = new Integer(i);
                }
            }
        }
        propertyDescriptor = new ComboBoxPropertyDescriptor(
                P_DEFAULTSCHEMA, RMBenchMessages.DiagramPropertySource_DefaultSchema, nameList);
        
        return propertyDescriptor;
    }

    private void computeSelectedName() {
        for (int i = 0; i < nameList.length; i++) {
            if(nameList[i].equals(diagram.getDefaultSchema().getName())) {
                defaultNameIndex = new Integer(i);
                break;
            }
        }
    }
}
