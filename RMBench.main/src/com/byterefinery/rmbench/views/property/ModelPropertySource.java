/*
 * created 21-Nov-2005
 *
 * &copy; 2005, DynaBEAN Consulting
 * 
 */
package com.byterefinery.rmbench.views.property;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.byterefinery.rmbench.RMBenchMessages;
import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.extension.DatabaseExtension;
import com.byterefinery.rmbench.extension.NameGeneratorExtension;
import com.byterefinery.rmbench.model.Model;
import com.byterefinery.rmbench.operations.ModifyModelOperation;

public class ModelPropertySource extends RMBenchPropertySource {

    public final static String P_NAME_GENERATOR = "name_generator_property";
    public final static String P_DATABASE = "database_property";

    private Model model;
    private Integer databaseSelection;
    private Integer nameGeneratorSelection;

    private IPropertyDescriptor propertiesDescriptors[] = {
            new TextPropertyDescriptor(P_NAME, RMBenchMessages.DiagramPropertySource_Name), null };

    ModelPropertySource(Model model) {
        super();
        this.model = model;
        createPropertyDescriptors();
    }

    private void createPropertyDescriptors() {
        int i;
        String[] tmpString;
        DatabaseExtension[] databaseExtensions = RMBenchPlugin.getExtensionManager().getDatabaseExtensions();
        NameGeneratorExtension[] nameGenerators = RMBenchPlugin.getExtensionManager().getNameGeneratorExtensions();

        tmpString = new String[databaseExtensions.length];
        for (i = 0; i < databaseExtensions.length; i++) {
            tmpString[i] = databaseExtensions[i].getName();
            if (model.getDatabaseInfo().equals(databaseExtensions[i].getDatabaseInfo())) {
                databaseSelection = new Integer(i);
            }
        }
        propertiesDescriptors[1] = new ComboBoxPropertyDescriptor(P_DATABASE,
                RMBenchMessages.ModelPropertySource_Database, tmpString);

        tmpString = new String[nameGenerators.length];
        for (i = 0; i < nameGenerators.length; i++) {
            tmpString[i] = nameGenerators[i].getName();
            if (model.getNameGenerator().equals(nameGenerators[i])) {
                nameGeneratorSelection = new Integer(i);
            }
        }
        propertiesDescriptors[2] = new ComboBoxPropertyDescriptor(P_NAME_GENERATOR,
                RMBenchMessages.ModelPropertySource_NameGenerator, tmpString);
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {
        return propertiesDescriptors;
    }

    public Object getPropertyValue(Object id) {
        if (id.equals(P_NAME)) {
            return model.getName();
        }
        else if (id.equals(P_DATABASE)) {
            return databaseSelection;
        }
        else if (id.equals(P_NAME_GENERATOR)) {
            return nameGeneratorSelection;
        }
        return null;
    }

    public void setPropertyValue(Object id, Object value) {
        ModifyModelOperation op=null;
        if (id.equals(P_NAME)) {
            op = new ModifyModelOperation(model, ModifyModelOperation.PROP_NAME, value);
        }
        else if (id.equals(P_DATABASE)) {
            DatabaseExtension[] databaseExtensions = RMBenchPlugin.getExtensionManager().getDatabaseExtensions();
            op = new ModifyModelOperation(model, ModifyModelOperation.PROP_DATABASE,
                    databaseExtensions[databaseSelection.intValue()].getDatabaseInfo());
        }
        else if (id.equals(P_NAME_GENERATOR)) {
            NameGeneratorExtension[] nameGenerators = RMBenchPlugin.getExtensionManager().getNameGeneratorExtensions();
            model.setNameGenerator(nameGenerators[nameGeneratorSelection.intValue()]
                    .getNameGenerator());
            op = new ModifyModelOperation(model, ModifyModelOperation.PROP_NAME_GENERATOR,
                    nameGenerators[nameGeneratorSelection.intValue()].getNameGenerator());
        }
        if (op!=null) //just to be sure
            op.execute(this);
    }
}
