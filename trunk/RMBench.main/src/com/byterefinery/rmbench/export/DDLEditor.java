/*
 * created 31.12.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id$
 */
package com.byterefinery.rmbench.export;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;

import com.byterefinery.rmbench.EventManager;
import com.byterefinery.rmbench.exceptions.SystemException;
import com.byterefinery.rmbench.export.text.DDLSourceViewerConfiguration;
import com.byterefinery.rmbench.external.IDDLScript;
import com.byterefinery.rmbench.external.IDatabaseInfo;
import com.byterefinery.rmbench.model.dbimport.DBModel;
import com.byterefinery.rmbench.preferences.PreferenceHandler;

/**
 * an editor used to output generated DDL and to execute the DDL against a database
 * connection
 * 
 * @author cse
 */
public class DDLEditor extends TextEditor implements IDDLScriptContext {

    public static final String ID = "com.byterefinery.rmbench.editors.ddlEditor";

    private IPropertyChangeListener preferenceListener = new IPropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent event) {
            DDLSourceViewerConfiguration sourceViewerConfig = 
                (DDLSourceViewerConfiguration)getSourceViewerConfiguration();
            if(sourceViewerConfig.handlePreferenceChange(event))
                getSourceViewer().invalidateTextPresentation();
        }
    };
    
    private ScriptUtil scriptUtil;
    
    private ToolBarManager toolBarMgr;
    private SourceViewer sourceViewer;
    private DDLExecutionRulerColumn executionRuler;

    private ChooseDBModelContribution dbmodelContribution;
    private EventManager.Listener connectionListener;

    private IDatabaseInfo databaseInfo;
    
    public DDLEditor() {
        setSourceViewerConfiguration(new DDLSourceViewerConfiguration());
    }

    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        super.init(site, input);
        
        String terminator =  input instanceof DDLEditorInput ? 
                ((DDLEditorInput)input).getStatementTerminator() : IDDLScript.DEFAULT_TERMINATOR;
                
        if (input instanceof DDLEditorInput)
            databaseInfo = ((DDLEditorInput)input).getDatabaseInfo();
                
        scriptUtil = new ScriptUtil(terminator);
    }


    protected ISourceViewer createSourceViewer(Composite parent,  IVerticalRuler ruler, int style) {
        
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        toolBarMgr = new ToolBarManager(SWT.FLAT);
        ToolBar toolBar = toolBarMgr.createControl(composite);
        GridData gd = new GridData(SWT.FILL, SWT.TOP, false, false);
        gd.heightHint = 25;
        toolBar.setLayoutData(gd);
        
        final Action executeScriptAction = new ExecuteScriptAction(this);
        final Action executeStmtAction = new ExecuteStatementAction(this);
                
        dbmodelContribution = new ChooseDBModelContribution(databaseInfo);
        dbmodelContribution.addConnectionListener(new ChooseDBModelContribution.Listener() {
            public void dbModelSelected(DBModel dbmodel) {
                executeScriptAction.setEnabled(dbmodel != null);
                executeStmtAction.setEnabled(dbmodel != null);
            }
        });
        toolBarMgr.add(dbmodelContribution);
        toolBarMgr.add(executeScriptAction);
        toolBarMgr.add(executeStmtAction);
        toolBarMgr.update(true);
        
        connectionListener = new ChooseDBModelContribution.EventManagerListener(toolBarMgr);
        connectionListener.register();
        
        sourceViewer = new SourceViewer(composite, ruler, SWT.H_SCROLL | SWT.V_SCROLL);
        sourceViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        PreferenceHandler.addPreferenceChangeListener(preferenceListener);
        return sourceViewer;
    }

    protected CompositeRuler createCompositeRuler() {
        CompositeRuler compositeRuler = new CompositeRuler();
        executionRuler = new DDLExecutionRulerColumn();
        compositeRuler.addDecorator(0, executionRuler);
        
        return compositeRuler;
    }
    
   
    public void dispose() {
        super.dispose();
        PreferenceHandler.removePreferenceChangeListener(preferenceListener);
        connectionListener.unregister();
        dbmodelContribution.closeExecutors();
    }
    
    public DBModel getSelectedDBModel() {
        return dbmodelContribution.getSelectedDBModel();
    }

    public Shell getShell() {
        return getSite().getShell();
    }
    
    public IProgressMonitor getProgressMonitor() {
        return super.getProgressMonitor();
    }
    
    public IDDLScriptContext.Statement getSelectedStatement() {
        int caret = getSourceViewer().getTextWidget().getCaretOffset();
        return scriptUtil.parseStatement(getSourceViewer().getDocument(), caret);
    }

    public IDDLScriptContext.Statement[] getAllStatements() {
        return scriptUtil.parseStatements(getSourceViewer().getDocument());
    }

    public void aboutToExecute(final Statement statement) {
        getSourceViewer().getTextWidget().getDisplay().syncExec(new Runnable() {
            public void run() {
                getSourceViewer().revealRange(statement.offset, statement.length);
            }
        });
        executionRuler.setStatement(statement);
    }

    public void executed(Statement statement, SystemException error) {
        try {
        	//set the cursor to the line after the current statement
            int lastLine = getSourceViewer().getDocument().getLineOfOffset(statement.offset+statement.length);
            int nextOffset = getSourceViewer().getDocument().getLineOffset(lastLine+1);
            getSourceViewer().getTextWidget().setCaretOffset(nextOffset);
        }
        catch (BadLocationException e) {
        }
    }
}
