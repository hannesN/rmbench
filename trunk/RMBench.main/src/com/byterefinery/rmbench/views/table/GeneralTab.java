/*
 * created 10.06.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: GeneralTab.java 666 2007-10-01 19:32:51Z cse $
 */
package com.byterefinery.rmbench.views.table;

import java.util.Collection;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.byterefinery.rmbench.EventManager;
import com.byterefinery.rmbench.RMBenchMessages;
import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.EventManager.Event;
import com.byterefinery.rmbench.model.Model;
import com.byterefinery.rmbench.model.schema.Schema;
import com.byterefinery.rmbench.model.schema.Table;
import com.byterefinery.rmbench.operations.TableCommentOperation;
import com.byterefinery.rmbench.operations.TableDescriptionOperation;
import com.byterefinery.rmbench.operations.TableNameOperation;
import com.byterefinery.rmbench.operations.TableSchemaOperation;
import com.byterefinery.rmbench.util.DisplayUtils;
import com.byterefinery.rmbench.util.ImageConstants;
import com.byterefinery.rmbench.views.table.DetailsTabGroup.DetailTab;

/**
 * tab for displaying and editing general table info like name, comment, description
 * 
 * @author cse
 */
public class GeneralTab extends DetailTab {

    private EventManager.Listener eventListener = new EventManager.Listener() {
        public void eventOccurred(int eventType, Event event) {
            blockLocalEvents = true;
            switch(eventType) {
                case SCHEMA_ADDED:
                case SCHEMA_MODIFIED:
                case SCHEMA_DELETED: {
                    Model schemaModel = RMBenchPlugin.getModelManager().getModel();
                    if(schemaModel == model)
                        schemaCombo.setInput(model.getSchemas());
                    if(table != null)
                        schemaCombo.setSelection(new StructuredSelection(table.getSchema()));
                    break;
                }
                case TABLE_MODIFIED: {
                    if((Table)event.element == table && event.origin != GeneralTab.this) {
                        if(event.info == NAME) {
                        	// check if new description is different from the text field text
                            // if so the changes come from anothre source than the text, and we change it
                            // if not the text field is the change source and we don't need to update it
                            // if we would do this anyway the caret of the text would jump to the start
                            // and after writing some letters you wrote a word in mirror writing
                            if (!table.getName().equals(nameText.getText()))
                            	nameText.setText(table.getName());
                        }
                        else if(event.info == COMMENT) {
                            String cmt = table.getComment();
                            // check if new description is different from the text field text
                            // if so the changes come from anothre source than the text, and we change it
                            // if not the text field is the change source and we don't need to update it
                            // if we would do this anyway the caret of the text would jump to the start
                            // and after writing some letters you wrote a word in mirror writing
                            if (!cmt.equals(commentText.getText()))
                            commentText.setText(cmt != null ? cmt : "");
                        }
                        else if(event.info == DESCRITPION) {
                            String dsc = table.getDescription();
                            //check if new description is different from the text field text
                            // if so the changes come from anothre source than the text, and we change it
                            // if not the text field is the change source and we don't need to update it
                            // if we would do this anyway the caret of the text would jump to the start
                            // and after writing some letters you wrote a word in mirror writing
                            if (!dsc.equals(descriptionText.getText()))
                            	descriptionText.setText(dsc != null ? dsc : "");
                           
                        }
                        else if(event.info == SCHEMA) {
                            schemaCombo.setSelection(new StructuredSelection(table.getSchema()));
                        }
                    }
                    break;
                }
            }
            blockLocalEvents = false;
        }

        public void register() {
            RMBenchPlugin.getEventManager().addListener(
                    SCHEMA_ADDED | SCHEMA_MODIFIED | SCHEMA_DELETED | TABLE_MODIFIED, this);
        }
    };

    private Model model;
    
    private FormToolkit toolkit;
    private ScrolledForm form;
    
    private Text nameText; 
    private Text commentText;
    private Text descriptionText;
    private ComboViewer schemaCombo;
    
    private transient boolean blockLocalEvents;
    
    private TableNameOperation tableNameOperation;
    private TableCommentOperation tableCommentOperation;
    private TableDescriptionOperation tableDescriptionOperation;
    
    public void createControl(Composite parent, IActionBars actionBars) {
        
        parent.setLayout(new FillLayout());
        toolkit = new FormToolkit(parent.getDisplay());
        form = toolkit.createScrolledForm(parent);
        
        Composite body = form.getBody();
        
        GridData gridData = new GridData(SWT.LEFT, SWT.TOP, true, true);
        body.setLayoutData(gridData);
        body.setLayout(new GridLayout(2, false));

        toolkit.createLabel(body, RMBenchMessages.NameTab_FName);
        nameText = toolkit.createText(body, null, SWT.SINGLE | SWT.BORDER);
        nameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        nameText.addFocusListener(new FocusListener(){

            public void focusGained(FocusEvent e) {
                tableNameOperation = new TableNameOperation(table);
            }
            public void focusLost(FocusEvent e) {
            }
        });
        nameText.addModifyListener(new ModifyListener() {
            
            public void modifyText(ModifyEvent e) {
                if(!blockLocalEvents) {
                    tableNameOperation.setNewName(nameText.getText());
                    tableNameOperation.execute(GeneralTab.this);
                }
            }
        });

        toolkit.createLabel(body, RMBenchMessages.NameTab_FSchema);
        schemaCombo = new ComboViewer(body, SWT.DROP_DOWN | SWT.READ_ONLY);
        schemaCombo.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
        schemaCombo.setLabelProvider(new SchemaLabelProvider());
        schemaCombo.setContentProvider(new SchemaContentProvider());
        gridData = new GridData();
        gridData.minimumWidth = DisplayUtils.computeWidth(schemaCombo.getControl(), 30);
        gridData.widthHint = gridData.minimumWidth; 
        schemaCombo.getControl().setLayoutData(gridData);
        schemaCombo.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {
                if(table != null && !blockLocalEvents) {
                    IStructuredSelection selection = (IStructuredSelection)event.getSelection();
                    TableSchemaOperation op = 
                        new TableSchemaOperation(table);
                    op.execute(GeneralTab.this, (Schema)selection.getFirstElement());
                }
            }
        });
        
        toolkit.createLabel(body, RMBenchMessages.NameTab_FComment);
        commentText = toolkit.createText(body, null, SWT.SINGLE | SWT.BORDER);
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        commentText.setLayoutData(gridData);
        commentText.addFocusListener(new FocusListener() {

            public void focusGained(FocusEvent e) {
                tableCommentOperation = new TableCommentOperation(table);
            }
            public void focusLost(FocusEvent e) {
            }
        });
        commentText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                if(!blockLocalEvents) {
                    tableCommentOperation.setNewComment(commentText.getText());
                    tableCommentOperation.execute(GeneralTab.this);
                }
            }
        });

        toolkit.createLabel(body, RMBenchMessages.NameTab_FDescription);
        descriptionText = toolkit.createText(body, null, SWT.MULTI | SWT.BORDER | SWT.WRAP);
        gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
        gridData.minimumHeight = descriptionText.getLineHeight();
        gridData.minimumWidth = DisplayUtils.computeWidth(descriptionText, 40);
        descriptionText.setLayoutData(gridData);
        descriptionText.addFocusListener(new FocusListener() {

            public void focusGained(FocusEvent e) {
                tableDescriptionOperation = new TableDescriptionOperation(table);
            }
            public void focusLost(FocusEvent e) {
            }
        });
        descriptionText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
            //	blockLocalEvents=true;
                if(!blockLocalEvents) {
                    tableDescriptionOperation.setNewDescription(descriptionText.getText());
                    tableDescriptionOperation.execute(GeneralTab.this);
                }
            }
        });

        eventListener.register();
    }

    public void setFocus() {
        form.setFocus();
    }

    public void dispose() {
        schemaCombo.getCombo().dispose();
        toolkit.dispose();
        eventListener.unregister();
    }
    
    public void setTable(Table table) {
        super.setTable(table);
        blockLocalEvents = true;
        
        if(table != null) {
            nameText.setText(table.getName());
            commentText.setText(table.getComment() != null ? table.getComment() : "");
            descriptionText.setText(table.getDescription() != null ? table.getDescription() : "");

            Model activeModel = RMBenchPlugin.getActiveModel();
            if(model != activeModel) {
                model = activeModel;
                schemaCombo.setInput(model.getSchemas());
            }
            schemaCombo.setSelection(new StructuredSelection(table.getSchema()));
        }
        else {
            nameText.setText("");
            commentText.setText("");
            descriptionText.setText("");
            schemaCombo.setSelection(null);
        }
        form.setEnabled(table != null);
        blockLocalEvents = false;
    }

    public Image getImage() {
        return RMBenchPlugin.getImage(ImageConstants.INFO);
    }

    public String getTitle() {
        return RMBenchMessages.NameTab_Title;
    }

    public String getDescription() {
        return RMBenchMessages.NameTab_Description;
    }
    
    private static class SchemaLabelProvider extends LabelProvider {

        public String getText(Object element) {
            return ((Schema)element).getName();
        }
    }
    
    private static class SchemaContentProvider implements IStructuredContentProvider {

        Schema[] schemas;
        
        public Object[] getElements(Object inputElement) {
            return schemas;
        }
        
        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            if(newInput != null) {
                Collection<?> collection = (Collection<?>)newInput;
                schemas = (Schema[])collection.toArray(new Schema[collection.size()]);
            }
            else {
                schemas = null;
            }
        }
    }
}
