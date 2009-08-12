/*
 * created 13.05.2005
 * 
 * $Id:DynamicURLSetupGroup.java 2 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.util.database;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.byterefinery.rmbench.RMBenchMessages;
import com.byterefinery.rmbench.dialogs.JdbcParametersDialog;
import com.byterefinery.rmbench.extension.VariableDescriptor;
import com.byterefinery.rmbench.external.IURLSetupGroup;
import com.byterefinery.rmbench.external.database.ui.URLSetupGroup;
import com.byterefinery.rmbench.util.IURLValueParser;

/**
 * a generic URL setup group that will dynamically configure the user interface according to the URL
 * pattern and variable definitions made in the driverInfo plugin extension.
 * @author cse
 */
public class DynamicURLSetupGroup extends URLSetupGroup {

    public static IURLSetupGroup.Factory getFactory(final IURLValueParser parser,
            final VariableDescriptor[] variables) {

        return new IURLSetupGroup.Factory() {

            public IURLSetupGroup createSetupGroup(String connectionUrl, IURLSetupGroup.Context context) {
                parser.reset();
                if (connectionUrl != null) {
                    parser.parseUrl(connectionUrl);
                }
                return new DynamicURLSetupGroup(parser, variables, context);
            }
        };
    }

    // private final VariableReplacer replacer;
    private final IURLValueParser urlParser;

    private final VariableDescriptor[] variables;

    private final boolean[] completeFlags;

    private Map<VariableDescriptor, Text> variableTextLookup;

    private final PropertyChangeListener listener = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
            String url = urlParser.getValidURL();

            setUrlText(url);
        }
    };

    private Label urlText;

    public DynamicURLSetupGroup(
            IURLValueParser parser, VariableDescriptor[] variables, IURLSetupGroup.Context context) {

        super(context);
        this.variables = variables;
        // this.replacer = replacer;
        this.completeFlags = new boolean[variables.length];
        this.urlParser = parser;

        for (int i = 0; i < variables.length; i++) {
            String value = urlParser.getValue(variables[i].getName());
            if (value == null) {
                value = variables[i].getDefaultValue();
                if (value != null)
                    parser.setValue(variables[i].getName(), value);
            }
            completeFlags[i] = (!variables[i].isRequired() || value != null);
        }
    }

    public void createEditArea(Composite parent) {

        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        parent.setLayout(layout);

        variableTextLookup = new HashMap<VariableDescriptor, Text>(variables.length);

        for (int i = 0; i < variables.length; i++) {
            if (variables[i].isTextType()) {
                Label label = new Label(parent, SWT.NONE);
                label.setText("&"+variables[i].getLabel());
                Text text = new Text(parent, SWT.BORDER | SWT.SINGLE);
                text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
                String val = urlParser.getValue(variables[i].getName());
                if (val != null)
                    text.setText(val);

                variableTextLookup.put(variables[i], text);

                StringListener listener;
                if (variables[i].isIntType())
                    listener = new IntListener(variables[i]);
                else if (variables[i].isJavaNameType())
                    listener = new JavaNameListener(variables[i]);
                else
                    listener = new StringListener(variables[i]);
                text.addVerifyListener(listener);
                text.addFocusListener(new TextFocusListener(text, listener));
            }
            else if (variables[i].isBooleanType()) {
                Button check = new Button(parent, SWT.CHECK);
                check.setText(variables[i].getLabel());
                check.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
                check.addSelectionListener(new BooleanListener(variables[i]));
            }
        }

        Button optionButton = new Button(parent, SWT.NONE);
        optionButton.setText(RMBenchMessages.DynamicURLSetupGroup_addParams);
        GridData gridData = new GridData();
        gridData.horizontalAlignment = SWT.END;
        gridData.horizontalSpan = 2;
        optionButton.setLayoutData(gridData);
        optionButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                JdbcParametersDialog dlg = new JdbcParametersDialog(Display.getCurrent()
                        .getActiveShell(), urlParser.getURLOptions());

                if (dlg.open() == Dialog.OK) {
                    urlParser.setUrlOptions(dlg.getOptionString());
                    setUrlText(urlParser.getValidURL());
                    // need to trigger event, in case, that fields are complete
                    // otherwise the url options don't make it to the wizards url copy
                    fireCompleteEvent(isComplete());
                }

            }
        });

        createUrlLabel(parent);

        calculateErrorMessage();
    }

    /*
     * create the fields that show the resulting url
     */
    private void createUrlLabel(Composite parent) {

        // fill 1st column
        new Label(parent, SWT.NONE);

        urlText = new Label(parent, SWT.NONE);

        GridData gridData = new GridData(SWT.FILL, SWT.NONE, true, false);
        urlText.setLayoutData(gridData);

        setUrlText(urlParser.getValidURL());

        urlText.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                urlParser.removePropertyChangeListener(listener);
            }
        });

        urlParser.addPropertyListener(listener);
    }

    public String getConnectionURL() {
        return urlParser.getValidURL();
    }

    public boolean isComplete() {
        for (int i = 0; i < completeFlags.length; i++) {
            if (!completeFlags[i])
                return false;
        }
        return true;
    }

    private void setUrlText(String text) {
        StringBuffer buffer = new StringBuffer(text.length());

        int index = 0;
        int lastIndex = 0;
        while ((index = text.indexOf('&', lastIndex)) != -1) {
            buffer.append(text.substring(lastIndex, index));
            buffer.append("&&");
            lastIndex = index + 1;
        }
        buffer.append(text.substring(lastIndex, text.length()));

        String result = buffer.toString();
        if (result.equals(urlText.getText()))
            return;

        urlText.setText(result);
    }

    private void verifyComplete(VariableDescriptor desc, String value) {
        if (value == null && desc.isRequired()) {
            for (int i = 0; i < variables.length; i++) {
                if (desc == variables[i]) {
                    completeFlags[i] = false;
                    break;
                }
            }
            fireCompleteEvent(false);
        }
        else {
            boolean hasIncomplete = false;
            for (int i = 0; i < variables.length; i++) {
                if (desc == variables[i]) {
                    completeFlags[i] = true;
                }
                else if (!completeFlags[i])
                    hasIncomplete = true;
            }
            if (!hasIncomplete)
                fireErrorEvent(null);
            fireCompleteEvent(!hasIncomplete);
        }
        calculateErrorMessage();
    }

    private void calculateErrorMessage() {
        int count = 0;
        String missingValueName = "";
        for (int i = 0; i < variables.length; i++) {
            if ((variables[i].isRequired()) && (!completeFlags[i])) {
                count++;
                missingValueName = variables[i].getName();
            }
        }

        if (count == 0) {
            urlText.setForeground(ColorConstants.menuForeground);
            fireErrorEvent(null);
        }
        else if (count == 1) {
            fireErrorEvent(missingValueName + " is required");
            urlText.setForeground(ColorConstants.red);
        }
        else {
            fireErrorEvent("the URL is not complete");
            urlText.setForeground(ColorConstants.red);
        }

    }

    /*
     * verifies and reads string fields
     */
    private class StringListener implements ModifyListener, VerifyListener {

        private final VariableDescriptor desc;

        StringListener(VariableDescriptor desc) {
            this.desc = desc;
        }

        public void modifyText(ModifyEvent e) {
            String value = ((Text) e.getSource()).getText();
            if (urlParser.isValidUrl(value)) {
                urlParser.parseUrl(value);
                for (int i = 0; i < variables.length; i++) {
                    String val = urlParser.getValue(variables[i].getName());
                    if (val == null)
                        val = "";
                    ((Text) variableTextLookup.get(variables[i])).setText(val);
                }

                return;
            }

            if (value.length() == 0) {
                value = null;
            }
            urlParser.setValue(desc.getName(), value);
            verifyComplete(desc, value);
        }

        public void verifyText(VerifyEvent e) {
            String url = ((Text) e.getSource()).getText();
            if (urlParser.isValidUrl(url))
                e.doit = true;
        }
    }

    /*
     * verifies and reads int fields
     */
    private class IntListener extends StringListener {

        IntListener(VariableDescriptor desc) {
            super(desc);
        }

        public void verifyText(VerifyEvent e) {
            String text = e.text;
            for (int i = 0; i < text.length(); i++) {
                if (!Character.isDigit(text.charAt(i))) {
                    e.doit = false;
                }
            }
            super.verifyText(e);
        }
    }

    /*
     * verifies and reads java name fields
     */
    private class JavaNameListener extends StringListener {

        JavaNameListener(VariableDescriptor desc) {
            super(desc);
        }

        public void verifyText(VerifyEvent e) {
            String text = e.text;
            for (int i = 0; i < text.length(); i++) {
                if (!Character.isJavaIdentifierPart(text.charAt(i))) {
                    e.doit = false;
                }
            }
            super.verifyText(e);
        }
    }

    /*
     * verifies and reads boolean fields
     */
    private class BooleanListener implements SelectionListener {

        private final VariableDescriptor desc;

        BooleanListener(VariableDescriptor desc) {
            this.desc = desc;
        }

        public void widgetSelected(SelectionEvent e) {
            Button check = (Button) e.getSource();
            String val = Boolean.toString(check.getSelection());
            urlParser.setValue(desc.getName(), val);
            verifyComplete(desc, val);
        }

        public void widgetDefaultSelected(SelectionEvent e) {
        }
    }

    private class TextFocusListener implements FocusListener {
        private Text textField;

        private ModifyListener listener;

        public TextFocusListener(Text textField, ModifyListener listener) {
            super();
            this.textField = textField;
            this.listener = listener;
        }

        public void focusGained(FocusEvent e) {
            textField.addModifyListener(listener);
        }

        public void focusLost(FocusEvent e) {
            textField.removeModifyListener(listener);
        }
    }

    protected String getGroupTitle() {
        return RMBenchMessages.DynamicURLSetupGroup_title;
    }
}
