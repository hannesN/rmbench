/*
 * created 14.05.2005
 * 
 * $Id: SimpleURLSetupGroup.java 593 2006-11-24 21:43:10Z hannesn $
 */
package com.byterefinery.rmbench.util.database;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.byterefinery.rmbench.RMBenchMessages;
import com.byterefinery.rmbench.external.IURLSetupGroup;
import com.byterefinery.rmbench.external.database.ui.URLSetupGroup;

/**
 * an URL setup control that will allow unstructured editing of the connection URL by providing just
 * one text field. The URL is validated against the pattern
 * 
 * <pre>
 *  jdbc:.*
 * </pre>
 */
public class SimpleURLSetupGroup extends URLSetupGroup {

    private static final Pattern URL_PATTERN = Pattern.compile("jdbc:.*");

    public static IURLSetupGroup.Factory getFactory(final String urlTemplate) {

        return new IURLSetupGroup.Factory() {
            public IURLSetupGroup createSetupGroup(String connectionUrl, IURLSetupGroup.Context context) {
                String url = connectionUrl != null && connectionUrl.length() > 0 ? connectionUrl
                        : urlTemplate;
                return new SimpleURLSetupGroup(url, context);
            }
        };
    }

    private String connectionURL;

  //  private Label errorLabel;

    public SimpleURLSetupGroup(String url, IURLSetupGroup.Context context) {
        super(context);
        validateURL(url, false);
    }

    public void createEditArea(Composite parent) {

        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        parent.setLayout(layout);

        Label label = new Label(parent, SWT.NONE);
        label.setText(RMBenchMessages.SimpleURLSetupControl_label);
        final Text urlText = new Text(parent, SWT.BORDER | SWT.SINGLE);
        urlText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        if (connectionURL != null)
            urlText.setText(connectionURL);

        urlText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                validateURL(urlText.getText(), true);
                fireCompleteEvent(isComplete());
            }
        });

    }

    private void validateURL(String url, boolean errMsg) {
        if (url != null) {
            Matcher matcher = URL_PATTERN.matcher(url);
            if (matcher.matches()) {
                connectionURL = url;
                fireErrorEvent(null);
                return;
            }
            else if (errMsg) {
            	fireErrorEvent(RMBenchMessages.SimpleURLSetupControl_invalidURL);
            }
        }
        connectionURL = null;
    }

    public String getConnectionURL() {
        return connectionURL != null ? connectionURL : null;
    }

    public boolean isComplete() {
        return connectionURL != null;
    }
}
