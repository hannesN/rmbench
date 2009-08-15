/*
 * created 18.10.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: ModelCompareEditor.java 671 2007-11-01 23:29:48Z cse $
 */
package com.byterefinery.rmbench.export;

import java.text.MessageFormat;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareViewerPane;
import org.eclipse.compare.Splitter;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewerExtension;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.AnnotationPainter;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.IAnnotationAccess;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.INavigationLocation;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IReusableEditor;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.IElementStateListener;
import org.eclipse.ui.texteditor.TextSelectionNavigationLocation;

import com.byterefinery.rmbench.EventManager;
import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.exceptions.SystemException;
import com.byterefinery.rmbench.export.diff.IComparisonNode;
import com.byterefinery.rmbench.export.diff.IDBComparisonNode;
import com.byterefinery.rmbench.export.diff.IModelComparisonNode;
import com.byterefinery.rmbench.export.text.DDLDocumentProvider;
import com.byterefinery.rmbench.export.text.DDLSourceViewerConfiguration;
import com.byterefinery.rmbench.export.text.GeneratedDDLDocumentProvider;
import com.byterefinery.rmbench.external.IDDLScript;
import com.byterefinery.rmbench.external.IDatabaseInfo;
import com.byterefinery.rmbench.model.dbimport.DBModel;
import com.byterefinery.rmbench.operations.RMBenchOperation;
import com.byterefinery.rmbench.operations.UndoRedoActionGroup;
import com.byterefinery.rmbench.preferences.PreferenceHandler;
import com.byterefinery.rmbench.util.ImageConstants;

/**
 * an editor that shows the difference between two models (i.e., a live database and a RMBench model). 
 * The editor area is separated into two panes. the upper one shows a structural view, where model change 
 * elements are arranged in a tree. The lower pane shows an SQL script that was generated from the 
 * difference, and which can be executed to move the live database into the state shown by the RMBench model.
 * <p/>
 * <em>This class was modeled after the eclipse CompareEditor and makes use of several artefacts 
 * from the compare framework. It also uses code from the stock TextEditor, which was quite a pain
 * to figure out and adapt</em>
 * 
 * @author cse
 */
public class ModelCompareEditor extends EditorPart implements IReusableEditor, IDDLScriptContext {

    public static final String ID = "com.byterefinery.rmbench.editors.modelCompareEditor";
    
    private static final String GROUP_EXEC = "group_exec";
    private static final String ANNOTATION_TYPE = "rmbench.modelCompareAnnotation";

    private CompareConfiguration compareConfig;
    
    private IDatabaseInfo databaseInfo;
    
    private IDocumentProvider documentProvider;
    private IElementStateListener elementStateListener = new IElementStateListener() {
        
        public void elementDirtyStateChanged(Object element, boolean isDirty) {
            firePropertyChange(PROP_DIRTY);
        }
        public void elementContentAboutToBeReplaced(Object element) {
        }
        public void elementContentReplaced(Object element) {
        }
        public void elementDeleted(Object element) {
        }
        public void elementMoved(Object originalElement, Object movedElement) {
        }
    };
    
    private ScriptUtil scriptUtil;
    
    private Splitter splitter;
    private ModelDiffViewer diffViewer;
    private SourceViewer sourceViewer;
    private DDLExecutionRulerColumn executionRuler;
    
    private ChooseDBModelContribution dbmodelContribution;
    private EventManager.Listener connectionListener;

    private AnnotationModel annotationModel;
    private AnnotationPainter annotationPainter;
    
    private UndoRedoActionGroup undoRedoGroup;
    
    /*
     * needed for AnnotationPainter
     */
    private IAnnotationAccess annotationAccess = new IAnnotationAccess() {
        public Object getType(Annotation annotation) {
            return annotation.getType();
        }

        public boolean isMultiLine(Annotation annotation) {
            return true;
        }

        public boolean isTemporary(Annotation annotation) {
            return true;
        }
    };

    /*
     * listener for font preference changes.
     */
    private IPropertyChangeListener preferenceListener = new IPropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent event) {
            if(event.getProperty().equals(PreferenceHandler.PREF_DDL_SCRIPTFONT)) {
                //this should not currently happen, as there is no GUI
                setFont(PreferenceHandler.getFont(PreferenceHandler.PREF_DDL_SCRIPTFONT));
            }
            else if(event.getProperty().equals(JFaceResources.TEXT_FONT)) {
                //for now, sync up with JFaceResources
                setFont(JFaceResources.getTextFont());
            }
        }
    };
    
    /*
     * listener for selection in the structure pane, which causes the highlighted annotations to 
     * be set in the text pane accordingly
     */
    private ISelectionChangedListener diffSelectionChangedListener = new ISelectionChangedListener() {
        public void selectionChanged(SelectionChangedEvent event) {
            IStructuredSelection sel = (IStructuredSelection)event.getSelection();
            
            DiffNode diffNode = (DiffNode)sel.getFirstElement();
            if(diffNode == null)
            	return;
            IComparisonNode node = (IComparisonNode)diffNode.getLeft();
            if(node == null)
                node = (IComparisonNode)diffNode.getRight(); //might be a drop
            
            annotationModel.removeAllAnnotations();
            if(node != null) {
                IDDLScript.Range[] ranges = node.getStatementRanges();
                if(ranges != null && ranges.length > 0) {
                    for (int i = 0; i < ranges.length; i++) {
                        Annotation annotation = new Annotation(ANNOTATION_TYPE, false, "test");
                        annotationModel.addAnnotation(annotation, new Position(ranges[i].position, ranges[i].length));
                    }
                    sourceViewer.invalidateTextPresentation();
                }
            }
        }
    };
    
    /*
     * action that switches the editor into editable mode 
     */
    private class EditorModeAction extends Action {
        
        EditorModeAction() {
            super();
            setId(getClass().getName());
            setText(Messages.ExportEditor_makeEditable);
            setToolTipText(Messages.ExportEditor_makeEditable);
            setImageDescriptor(RMBenchPlugin.getImageDescriptor(ImageConstants.EDIT));
        }
        
        public void run() {
            diffViewer.removeSelectionChangedListener(diffSelectionChangedListener);
            annotationModel.removeAllAnnotations();
            sourceViewer.setEditable(true);
            setEnabled(false);
        }
    }

    /*
     * subclass that makes the FileEditorInput non-persistable. This prevents the editor state from being 
     * stored after a saveAs operation. If the DDL was stored to a file, it must be reopened manually
     * into a plain DDLEditor
     */
    private static class NonPersistableFileEditorInput extends FileEditorInput {
        public NonPersistableFileEditorInput(IFile file) {
            super(file);
        }

        public IPersistableElement getPersistable() {
            return null;
        }
    }
    
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        setSite(site);
        setInput(input);
        
        undoRedoGroup = new UndoRedoActionGroup(getSite(), RMBenchOperation.CONTEXT, false);
        undoRedoGroup.fillActionBars(getEditorSite().getActionBars());
    }

    public void setInput(IEditorInput input) {
        if(input instanceof ModelCompareEditorInput) {
            documentProvider = new GeneratedDDLDocumentProvider();
            compareConfig = ((ModelCompareEditorInput)input).getCompareConfiguration();
            databaseInfo = ((ModelCompareEditorInput)input).getModelDatabaseInfo();
        }
        else {
            //we are called with a FileEditorInput if the generated DDL was saved to disk
        	//this can only happen after an initial display
            if(diffViewer == null)
                throw new IllegalArgumentException();

            documentProvider.disconnect(getEditorInput());
            documentProvider.removeElementStateListener(elementStateListener);
            
            documentProvider = new DDLDocumentProvider();
            firePropertyChange(PROP_INPUT);
        }
        try {
            documentProvider.connect(input);
        }
        catch (CoreException e) {
            Shell shell= getSite().getShell();
            ErrorDialog.openError(
                    shell, 
                    Messages.CreateEditorError_Title, 
                    Messages.CreateEditorError_MsgInput, 
                    e.getStatus());
        }
        documentProvider.addElementStateListener(elementStateListener);
        annotationModel = (AnnotationModel)documentProvider.getAnnotationModel(input);
        
        if(sourceViewer != null)
            sourceViewer.setDocument(documentProvider.getDocument(input), annotationModel);

        super.setInput(input);
    }
    
    public void createPartControl(Composite parent) {
        splitter = new Splitter(parent, SWT.VERTICAL);
        
        CompareViewerPane upperPane = new CompareViewerPane(splitter, SWT.BORDER | SWT.FLAT);
        diffViewer = new ModelDiffViewer(upperPane, compareConfig);
        
        upperPane.setContent(diffViewer.getControl());
        upperPane.setText(Messages.StructurePane_Title);
        upperPane.setImage(RMBenchPlugin.getImage(ImageConstants.MODEL));
        diffViewer.addSelectionChangedListener(diffSelectionChangedListener);
        diffViewer.addDoubleClickListener(new IDoubleClickListener() {
        	
            public void doubleClick(DoubleClickEvent event) {
                IStructuredSelection selection = (IStructuredSelection)event.getSelection();
                DiffNode diffNode = (DiffNode)selection.getFirstElement();
                IModelComparisonNode modelNode = (IModelComparisonNode)diffNode.getLeft();
                if(modelNode != null && modelNode.getValue() != null) {
                    IDBComparisonNode dbNode = (IDBComparisonNode)diffNode.getRight();
                    if(dbNode != null && dbNode.getValue() != null) {
                        CompareValuesDialog dialog = new CompareValuesDialog(
                                getSite().getShell(), 
                                modelNode.getValue(), 
                                dbNode.getValue());
                        dialog.open();
                    }
                }
            }
        });
        diffViewer.addListener(new ModelDiffViewer.Listener() {

			public void nodesRemoved(DiffNode node) {
				ModelCompareEditorInput input = (ModelCompareEditorInput)getEditorInput();
				input.reset();
				try {
					documentProvider.resetDocument(input);
			        annotationModel = (AnnotationModel)documentProvider.getAnnotationModel(input);
			        sourceViewer.setDocument(documentProvider.getDocument(input), annotationModel);
			        sourceViewer.invalidateTextPresentation();
				} 
				catch (CoreException e) {
					RMBenchPlugin.logError(e);
				}
			}
        });
        ModelCompareEditorInput mceInput = (ModelCompareEditorInput)getEditorInput();
        diffViewer.setInput(mceInput.getCompareResult());
        diffViewer.setModel(mceInput.getModel());
        
        CompareViewerPane lowerPane = new CompareViewerPane(splitter, SWT.BORDER | SWT.FLAT);
        
        scriptUtil = new ScriptUtil(mceInput.getStatementTerminator());
        
        createSourceViewer(lowerPane, mceInput);
        createActions(lowerPane);
        
        //the first line below is only symbolic - script font is not configurable
        PreferenceHandler.addPreferenceChangeListener(preferenceListener);
        //for now, sync with JFace default text font
        JFaceResources.getFontRegistry().addListener(preferenceListener);
        
        splitter.setWeights(new int[] { 30, 70 });
        splitter.layout();
    }

    private void createSourceViewer(CompareViewerPane lowerPane, ModelCompareEditorInput input) {
        
        executionRuler = new DDLExecutionRulerColumn();
        CompositeRuler compositeRuler = new CompositeRuler();
        compositeRuler.addDecorator(0, executionRuler);
        
        sourceViewer = new SourceViewer(lowerPane, compositeRuler, SWT.H_SCROLL + SWT.V_SCROLL);
        lowerPane.setContent(sourceViewer.getControl());
        lowerPane.setText(Messages.ScriptPane_Title);
        lowerPane.setImage(RMBenchPlugin.getImage(ImageConstants.SQL_FILE));
        
        sourceViewer.setEditable(false);
        sourceViewer.configure(new DDLSourceViewerConfiguration());
        
        sourceViewer.setDocument(documentProvider.getDocument(input), annotationModel);
        
        annotationPainter = new AnnotationPainter(sourceViewer, annotationAccess);
        annotationPainter.addHighlightAnnotationType(ANNOTATION_TYPE);
        annotationPainter.setAnnotationTypeColor(ANNOTATION_TYPE, ColorConstants.lightGray);
        sourceViewer.addPainter(annotationPainter);
        sourceViewer.addTextPresentationListener(annotationPainter);
        
        setFont(PreferenceHandler.getFont(PreferenceHandler.PREF_DDL_SCRIPTFONT));
    }

    private void createActions(CompareViewerPane lowerPane) {
        ToolBarManager toolbar = CompareViewerPane.getToolBarManager(lowerPane);
        
        final Action executeScriptAction = new ExecuteScriptAction(this);
        final Action executeStmtAction = new ExecuteStatementAction(this);
        
        dbmodelContribution = new ChooseDBModelContribution(databaseInfo);
        dbmodelContribution.addConnectionListener(new ChooseDBModelContribution.Listener() {
            public void dbModelSelected(DBModel dbmodel) {
                executeScriptAction.setEnabled(dbmodel != null);
                executeStmtAction.setEnabled(dbmodel != null);
            }
        });
        connectionListener = new ChooseDBModelContribution.EventManagerListener(toolbar);
        connectionListener.register();
        
        toolbar.add(new Separator(GROUP_EXEC));
        toolbar.appendToGroup(GROUP_EXEC, dbmodelContribution);
        toolbar.appendToGroup(GROUP_EXEC, executeScriptAction);
        toolbar.appendToGroup(GROUP_EXEC, executeStmtAction);
        toolbar.add(new Separator());
        toolbar.add(new EditorModeAction());
        toolbar.update(true);
    }

    public void dispose() {
        super.dispose();
        connectionListener.unregister();
        annotationPainter.dispose();
        
        documentProvider.disconnect(getEditorInput());
        documentProvider.removeElementStateListener(elementStateListener);
        
        dbmodelContribution.closeExecutors();
        
        undoRedoGroup.dispose();
        
        PreferenceHandler.removePreferenceChangeListener(preferenceListener );
        JFaceResources.getFontRegistry().removeListener(preferenceListener);
    }

    public void setFont(Font font) {
        if (sourceViewer.getDocument() != null) {

            Point selection = sourceViewer.getSelectedRange();
            int topIndex = sourceViewer.getTopIndex();

            StyledText styledText= sourceViewer.getTextWidget();
            Control parent = styledText;
            if (sourceViewer instanceof ITextViewerExtension) {
                ITextViewerExtension extension = (ITextViewerExtension) sourceViewer;
                parent = extension.getControl();
            }

            parent.setRedraw(false);

            styledText.setFont(font);

            sourceViewer.setSelectedRange(selection.x , selection.y);
            sourceViewer.setTopIndex(topIndex);

            if (parent instanceof Composite) {
                Composite composite = (Composite) parent;
                composite.layout(true);
            }

            parent.setRedraw(true);
        } 
        else {
            StyledText styledText = sourceViewer.getTextWidget();
            styledText.setFont(font);
        }
    }
    
    public void doSave(IProgressMonitor monitor) {
        if (documentProvider.isDeleted(getEditorInput())) {
            performSaveAs(monitor);
        } 
        else {
            performSave(false, monitor);
        }
    }

    public boolean isDirty() {
        return documentProvider.canSaveDocument(getEditorInput());
    }

    public boolean isSaveAsAllowed() {
        return true;
    }

    public void doSaveAs() {
        performSaveAs(getProgressMonitor());
    }

    public void setFocus() {
    }

    public DBModel getSelectedDBModel() {
        return dbmodelContribution.getSelectedDBModel();
    }

    public Shell getShell() {
        return getSite().getShell();
    }

    public IProgressMonitor getProgressMonitor() {
        return getEditorSite().getActionBars().getStatusLineManager().getProgressMonitor();
    }
    
    public IDDLScriptContext.Statement getSelectedStatement() {
        int caret = sourceViewer.getTextWidget().getCaretOffset();
        return scriptUtil.parseStatement(sourceViewer.getDocument(), caret);
    }

    public IDDLScriptContext.Statement[] getAllStatements() {
        return scriptUtil.parseStatements(sourceViewer.getDocument());
    }

    public void aboutToExecute(Statement statement) {
        executionRuler.setStatement(statement);
    }
    
    public void executed(Statement statement, SystemException error) {
        try {
        	//set the cursor to the line after the current statement
            int lastLine = sourceViewer.getDocument().getLineOfOffset(statement.offset+statement.length);
            int nextOffset = sourceViewer.getDocument().getLineOffset(lastLine+1);
            sourceViewer.getTextWidget().setCaretOffset(nextOffset);
        }
        catch (BadLocationException e) {
        }
    }
    
    /**
     * ask the user for the workspace path of a file resource and save the document there.
     * <p><em>
     * copied from {@link org.eclipse.ui.editors.text.TextEditor}. Sorry for that</em>
     * 
     * @param progressMonitor the progress monitor to be used
     */
    protected void performSaveAs(IProgressMonitor progressMonitor) {
        Shell shell= getSite().getShell();
        IEditorInput input= getEditorInput();

        SaveAsDialog dialog= new SaveAsDialog(shell);

        IFile original= (input instanceof IFileEditorInput) ? ((IFileEditorInput) input).getFile() : null;
        if (original != null)
            dialog.setOriginalFile(original);

        dialog.create();

        if (documentProvider == null) {
            // editor has programmatically been  closed while the dialog was open
            return;
        }

        if (documentProvider.isDeleted(input) && original != null) {
            String message= MessageFormat.format(Messages.MCEditor_warning_save_delete, new Object[] { original.getName() });
            dialog.setErrorMessage(null);
            dialog.setMessage(message, IMessageProvider.WARNING);
        }

        if (dialog.open() == Window.CANCEL) {
            if (progressMonitor != null)
                progressMonitor.setCanceled(true);
            return;
        }

        IPath filePath= dialog.getResult();
        if (filePath == null) {
            if (progressMonitor != null)
                progressMonitor.setCanceled(true);
            return;
        }

        IWorkspace workspace= ResourcesPlugin.getWorkspace();
        IFile file= workspace.getRoot().getFile(filePath);
        final IEditorInput newInput = new NonPersistableFileEditorInput(file);

        boolean success= false;
        try {

            documentProvider.aboutToChange(newInput);
            documentProvider.saveDocument(progressMonitor, newInput, documentProvider.getDocument(input), true);
            success= true;

        } catch (CoreException x) {
            IStatus status= x.getStatus();
            if (status == null || status.getSeverity() != IStatus.CANCEL) {
                String title = Messages.MCEditor_error_save_title;
                String msg= MessageFormat.format(Messages.MCEditor_error_save_message, new Object[] { x.getMessage() });

                if (status != null) {
                    switch (status.getSeverity()) {
                        case IStatus.INFO:
                            MessageDialog.openInformation(shell, title, msg);
                        break;
                        case IStatus.WARNING:
                            MessageDialog.openWarning(shell, title, msg);
                        break;
                        default:
                            MessageDialog.openError(shell, title, msg);
                    }
                } else {
                    MessageDialog.openError(shell, title, msg);
                }
            }
        } finally {
            documentProvider.changed(newInput);
            if (success)
                setInput(newInput);
        }

        if (progressMonitor != null)
            progressMonitor.setCanceled(!success);
    }

    /**
     * Performs the save and handles errors appropriately.
     * <p><em>
     * copied from {@link org.eclipse.ui.editors.text.AbstractTextEditor}. Sorry for that</em>
     *
     * @param overwrite indicates whether or not overwriting is allowed
     * @param progressMonitor the monitor in which to run the operation
     */
    protected void performSave(boolean overwrite, IProgressMonitor progressMonitor) {

        try {

            documentProvider.aboutToChange(getEditorInput());
            IEditorInput input = getEditorInput();
            documentProvider.saveDocument(progressMonitor, input, documentProvider.getDocument(input), overwrite);
            editorSaved();

        } catch (CoreException x) {
            IStatus status= x.getStatus();
            if (status == null || status.getSeverity() != IStatus.CANCEL) {
                //TODO V1: MessageBox?
                RMBenchPlugin.logError(x);
            }
        } finally {
            documentProvider.changed(getEditorInput());
        }
    }

    protected void editorSaved() {
        INavigationLocation[] locations= getSite().getPage().getNavigationHistory().getLocations();
        IEditorInput input= getEditorInput();
        for (int i= 0; i < locations.length; i++) {
            if (locations[i] instanceof TextSelectionNavigationLocation) {
                if(input.equals(locations[i].getInput())) {
                    TextSelectionNavigationLocation location= (TextSelectionNavigationLocation) locations[i];
                    location.partSaved(this);
                }
            }
        }
    }
}
