/*
 * created 19.01.2006
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
package com.byterefinery.rmbench.util;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.operations.IOperationHistoryListener;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.OperationHistoryEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.dialogs.SaveAsDialog;

import com.byterefinery.rmbench.RMBenchConstants;
import com.byterefinery.rmbench.RMBenchMessages;
import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.editors.DiagramEditor;
import com.byterefinery.rmbench.exceptions.ExceptionMessages;
import com.byterefinery.rmbench.exceptions.SystemException;
import com.byterefinery.rmbench.model.Model;
import com.byterefinery.rmbench.operations.RMBenchOperation;

/**
 * The model manager maintains the model that the RMBench views and editors operate upon. 
 * One model manager instance is available from the {@link com.byterefinery.rmbench.RMBenchPlugin} class.
 * <p>
 * Model manager tracks model changes (by listening to the undo history), makes sure all associated parts are 
 * notified. It also tracks the open diagram editors in order that, when the model is replaced e.g. by 
 * loading a new model file, all editors are closed
 * 
 * @author cse
 */
public class ModelManager {

    public interface Listener {
        /**
         * notification that the model is about to be replaced
         * @param model the model
         */
        void modelAboutToBeReplaced(Model model);

        /**
         * notification that the model has been replaced
         * @param model the model
         */
        void modelReplaced(Model model);

        /**
         * @param model
         * @param isDirty
         */
        void dirtyStateChanged(Model model, boolean isDirty);
    }
    
    private final static String MODELFILE = "model.openfile";
    
    private FileResourceModelStorage modelStorage;
    private boolean storing;
    private boolean isDirty;
    
    private List<DiagramEditor> openEditors = new ArrayList<DiagramEditor>(2);
    
    private List<Listener> listeners = new ArrayList<Listener>(2);
    
    /* listener for resource changes */
    private IResourceChangeListener resourceListener = new ResourceListener();
    
    private IUndoableOperation syncOperation = null;
    
    /* listener for history events, used to determine the dirty state */
    private IOperationHistoryListener historyListener = new IOperationHistoryListener() {

        public void historyNotification(OperationHistoryEvent event) {
            if(event.getOperation().hasContext(RMBenchOperation.CONTEXT)) {
                if(event.getEventType() == OperationHistoryEvent.OPERATION_REMOVED &&
                        event.getOperation() == syncOperation) {
                    syncOperation = null;
                }
                else if(event.getEventType() == OperationHistoryEvent.OPERATION_ADDED ||
                        event.getEventType() == OperationHistoryEvent.UNDONE ||
                        event.getEventType() == OperationHistoryEvent.REDONE) {
                    
                    IUndoableOperation op = 
                        event.getHistory().getUndoOperation(RMBenchOperation.CONTEXT);
                    setDirty(op != syncOperation);
                }
            }
        }
    };
    
    /* listener for editor management */
    private IPartListener editorListener = new IPartListener() {

        public void partOpened(IWorkbenchPart part) {
            if(part instanceof DiagramEditor) {
                DiagramEditor editor = (DiagramEditor)part;
                if(editor.getDiagram().getModel() == getModel())
                    openEditors.add(editor);
            }
        }

        public void partClosed(IWorkbenchPart part) {
            if(part instanceof DiagramEditor) {
                DiagramEditor editor = (DiagramEditor)part;
                if(editor.getDiagram().getModel() == getModel())
                    openEditors.remove(editor);
            }
        }

        public void partDeactivated(IWorkbenchPart part) {
        }

        public void partActivated(IWorkbenchPart part) {
        }

        public void partBroughtToTop(IWorkbenchPart part) {
        }
    };
    private IPartService partService;
    
    
    public ModelManager() {
    }
    
    public void activate() {
        String modelfile = RMBenchPlugin.getDefault().getPreferenceStore().getString(MODELFILE);
        if(modelfile.length() > 0) {
            Path path = new Path(modelfile);
            IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
            FileResourceModelStorage storage = new FileResourceModelStorage(file);
            
            try {
                IModelStorage.DefaultLoadListener loadListener = new IModelStorage.DefaultLoadListener();
                storage.load(loadListener);
                setModelStorage(storage, false, loadListener.dirty);
            } 
            catch (SystemException e) {
                RMBenchPlugin.logError(e);
            }
        }
        partService = RMBenchPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getPartService();
        partService.addPartListener(editorListener);
        RMBenchPlugin.getOperationHistory().addOperationHistoryListener(historyListener);
    }
    
    public void deactivate() {
        unhookModelStorage();
        partService.removePartListener(editorListener);
        RMBenchPlugin.getOperationHistory().removeOperationHistoryListener(historyListener);
        String name;
        if (modelStorage!=null)
            name = modelStorage.getFile().getFullPath().toOSString();
        else
            name="";
        RMBenchPlugin.getDefault().getPreferenceStore().putValue(MODELFILE, name);
       
    }
    
    public void addListener(Listener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }
    
    /**
     * activate the model stored under the given key. The model contents will be loaded from the
     * persistent store, unless the current model has been changed or is already persistent. In the 
     * latter cas a message will be written to the log and the current model will be unchanged.
     * <p>
     * This method should only be used upon activation of a RMBench workbench part (view or editor)
     * which saves its model state, under the assumption that all parts at one time operate on the same
     * model.    
     * 
     * @param storageKey the key, possibly a file path
     * @listener a listener whose {@link Listener#modelReplaced(Model)} method will be called
     * if the model was already activated (if not, registered listeners will be notified). May be null
     * 
     * @return <code>true</code> if the model storage was (already) activated, <code>false</code> if 
     * one of these conditions applies: 
     * <ul>
     * <li>activation was rejected because another persistent model is already activated</li>
     * <li>the current model has been modified</li>
     * <li>an error occurred while loading the model file. The error is logged</li>
     * </ul>
     */
    
    public boolean activateModelStorage(String storageKey, Listener listener) {
        
        if(modelStorage != null && (!modelStorage.isNew() || isDirty)) {
            if(!modelStorage.isNew() && !getModelStorageKey().equals(storageKey))
                return false;
            if(listener != null)
                listener.modelReplaced(modelStorage.getModel());
        }
        else {
            Path path = new Path(storageKey);
            IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
            FileResourceModelStorage storage = new FileResourceModelStorage(file);
            try {
                IModelStorage.DefaultLoadListener loadListener = new IModelStorage.DefaultLoadListener();
                storage.load(loadListener);
                
                //we cannot be sure that this is a true replace
                setModelStorage(storage, false, loadListener.dirty);
            }
            catch (SystemException e) {
                closeEditors();
                
                RMBenchPlugin.logError(e);
                return false;
            }
        }
        return true;
    }
    
    /**
     * save the model to a persistent store. If the model was not previously persistent,
     * a save as dialog will be opened
     * 
     * @param shell the dialog parent shell
     * @param progressMonitor a progress monitor
     */
    public void doSave(Shell shell, IProgressMonitor progressMonitor) {
        
        if(modelStorage.isNew()) {
            doSaveAs(shell, progressMonitor);
        }
        else {
            try {
                storing = true;
                modelStorage.store();
                progressMonitor.done();
                IUndoableOperation[] history = RMBenchPlugin.getOperationHistory().getUndoHistory(RMBenchOperation.CONTEXT);
                if (history.length>0)
                    syncOperation = history[history.length-1];
                else
                    syncOperation=null;
                setDirty(false);
            }
            catch (SystemException x) {
                progressMonitor.setCanceled(true);
                RMBenchPlugin.logError(x);
                
                String title = ExceptionMessages.errorTitle;
                String msg = MessageFormat.format(
                        ExceptionMessages.errorFileSave_message, new Object[] { x.getMessage() });

                MessageDialog.openError(shell, title, msg);
            }
            finally {
                storing = false;
            }
        }
    }

    /**
     * @see #doSaveAs(Shell, IProgressMonitor)
     * @param viewSite used to obtain shell and progressmonitor
     */
    public void doSaveAs(IViewSite viewSite) 
    {
        IProgressMonitor progressMonitor =
            viewSite.getActionBars().getStatusLineManager().getProgressMonitor();
        doSaveAs(viewSite.getShell(), progressMonitor);
    }
    
    /**
     * open a save as dialog to save the model to a new persistent store
     * 
     * @param shell the dialog parent shell
     * @param progressMonitor the progress monitor
     */
    public void doSaveAs(Shell shell, IProgressMonitor progressMonitor) {
        SaveAsDialog dialog = new SaveAsDialog(shell);
        dialog.setTitle(RMBenchMessages.ModelView_SaveDlg_Title);

        IFile original = ((FileResourceModelStorage)modelStorage).getFile();
        if (original != null) {
            dialog.setOriginalFile(original);
        }
        else {
            dialog.setOriginalName(
                    modelStorage.getModel().getName()+"."+RMBenchConstants.MODEL_FILE_EXTENSION);
        }

        dialog.create();
        if (dialog.open() == Window.CANCEL) {
            if (progressMonitor != null)
                progressMonitor.setCanceled(true);
            return;
        }

        IPath filePath = dialog.getResult();
        if (filePath == null) {
            progressMonitor.setCanceled(true);
            return;
        }

        IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(filePath);
        FileResourceModelStorage storage = new FileResourceModelStorage(file);
        storage.setModel(getModel());
        storage.setProgressMonitor(progressMonitor);
        
        try {
            storage.store();
            progressMonitor.done();
            setModelStorage(storage, false, false);
            IUndoableOperation[] history = RMBenchPlugin.getOperationHistory().getUndoHistory(RMBenchOperation.CONTEXT);
            if (history.length>0)
                syncOperation = history[history.length-1];
            else
                syncOperation=null;
        } 
        catch (SystemException x) {
            progressMonitor.setCanceled(true);
            RMBenchPlugin.logError(x);
            
            ErrorDialog.openError(
                    shell, 
                    ExceptionMessages.errorTitle, null,
                    x.getStatus(RMBenchMessages.ModelView_SaveError));
        }
    }

    /*
     * close all editors, without prompting for unsaved changes
     */
    private void closeEditors() {
        IWorkbenchPage[] pages = RMBenchPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getPages();
        List<IEditorReference> drefs = new ArrayList<IEditorReference>();
        
        for (int i = 0; i < pages.length; i++) {
            IEditorReference[] erefs = pages[i].getEditorReferences();
            for (int j = 0; j < erefs.length; j++) {
                if(DiagramEditor.ID.equals(erefs[j].getId()))
                    drefs.add(erefs[j]);
            }
            pages[i].closeEditors(
                    (IEditorReference[])drefs.toArray(new IEditorReference[drefs.size()]), false);
            drefs.clear();
        }
    }

    /**
     * determine whether the model may be switched. If the model si unchanged, <code>true</code> is
     * returned. Otherwise, the user is presented with a dialog with 3 options:
     * <ul>
     * <li>the "yes" option will save the model and return true</li>
     * <li>the "no" option will discard the model and return true</li>
     * <li>the "cancel" option will leave the model unchanged, and return false</li>
     * </ul>
     * 
     * @return true if the model may be switched
     */
    public boolean canSwitchModel(Shell shell) {
        return !isDirty() || querySaveModel(shell);
    }
    
    /**
     * show a 3-option dialog asking the user whether the current model should be saved
     * 
     * @return false if the dialog was cancelled, and thus the operation should not 
     * proceed
     */
    private boolean querySaveModel(final Shell shell) {
        
        MessageDialog dialog = new MessageDialog(
                shell, 
                RMBenchMessages.ModelView_SaveDlg_Title, 
                null, 
                RMBenchMessages.ModelView_SaveChanged_Message, 
                MessageDialog.QUESTION,
                new String[] {
                        IDialogConstants.YES_LABEL,
                        IDialogConstants.NO_LABEL,
                        IDialogConstants.CANCEL_LABEL
                        },
                0);
        int result = dialog.open();
        if(result == 0) { 
            IRunnableWithProgress runnable = new IRunnableWithProgress() {

                public void run(IProgressMonitor monitor) 
                    throws InvocationTargetException, InterruptedException {
                    
                    doSave(shell, monitor);
                }
            };
            try {
                new ProgressMonitorDialog(shell).run(false, true, runnable);
            }
            catch (InvocationTargetException e) {
                RMBenchPlugin.logError(e);
            }
            catch (InterruptedException e) {
                RMBenchPlugin.logError(e);
            }
        }
        
        return result != 2;
    }

    /**
     * replace the current model storage. Events will be fired
     * @param newStorage the storage container for the model
     * @param isDirty the initial dirty state
     */
    public void setModelStorage(FileResourceModelStorage newStorage, boolean isDirty) {
        setModelStorage(newStorage, true, isDirty);
    }
    
    /**
     * replace the model storage
     * @param newModelStorage the new storage
     * @param trueReplace whether the model proper is being replaced (or just the storage)
     * @param dirty the new dirty state
     */
    private void setModelStorage(FileResourceModelStorage newModelStorage, boolean trueReplace, boolean dirty) {
        
        if (trueReplace && modelStorage != null) {
        	fireModelAboutToBeReplaced();
            unhookModelStorage();
        }
        modelStorage = newModelStorage;
        hookModelStorage();
        
        if(trueReplace) {
            closeEditors();
            fireModelReplaced(modelStorage.getModel());
            RMBenchPlugin.getEventManager().fireTableSelected(this, null);
        }
        setDirty(dirty);
        disposeHistory(); 
    }

    public void closeModel() {
        if(modelStorage != null) {
        	fireModelAboutToBeReplaced();
            unhookModelStorage();
        }
        modelStorage = null;
        isDirty=false;
        closeEditors();
        
        fireModelReplaced(null);
    }
    
    /**
     * set the dirty state and trigger appropriate events if the value was changed
     * 
     * @param dirty the new dirty state
     */
    public void setDirty(boolean dirty) {
        if (isDirty != dirty) {
            isDirty = dirty;
            for (Listener listener : listeners) {
                listener.dirtyStateChanged(modelStorage.getModel(), isDirty);
            }
            for (DiagramEditor editor : openEditors) {
                editor.dirtyChanged(isDirty);
            }
        }
    }
    
    public boolean isDirty() {
        return isDirty;
    }

    /**
     * @return the key under which the model storage is identified. Typically a file path
     */
    public String getModelStorageKey() {
        if (modelStorage==null)
            return null;
        return modelStorage.isNew() ? null: modelStorage.getFile().getFullPath().toString();
    }

    public Model getModel() {
        return modelStorage != null ? modelStorage.getModel() : null;
    }

    /**
     * @return true if the model storage has not been previously stored
     */
    public boolean isNewStorage() {
        return (modelStorage!=null) ? modelStorage.isNew() : false;
    }
    
    /**
     * @return a new model storage with an empty model
     */
    public FileResourceModelStorage createDefaultModelStorage() {
        
        Model model = new Model(
                RMBenchMessages.NewModel, 
                RMBenchPlugin.getStandardDatabaseInfo(),
                RMBenchPlugin.getDefaultNameGenerator());
        return new FileResourceModelStorage(model);
    }

    /**
     * dispose of the undo history 
     */
    private void disposeHistory() {
        RMBenchPlugin.getOperationHistory().dispose(RMBenchOperation.CONTEXT, true, true, true);
        syncOperation = null;
    }

    private void hookModelStorage() {
        if(modelStorage != null && !modelStorage.isNew()) {
            modelStorage.getFile().getWorkspace().addResourceChangeListener(resourceListener);
        }
    }

    private void unhookModelStorage() {
        if(modelStorage != null && !modelStorage.isNew()) {
            modelStorage.getFile().getWorkspace().removeResourceChangeListener(resourceListener);
        }
    }
    
	private void fireModelAboutToBeReplaced() {
        for (Listener listener : listeners) {
            listener.modelAboutToBeReplaced(modelStorage.getModel());
        }
	}

	private void fireModelReplaced(Model model) {
        for (Listener listener : listeners) {
            listener.modelReplaced(model);
        }
	}
    
 
	/* listener for resource changes, which detects external mainpulations to the modelStorage */
    private class ResourceListener implements IResourceChangeListener {

        public void resourceChanged(IResourceChangeEvent event) {
            
        	if(modelStorage == null || modelStorage.getFile() == null)
        		return;
        	
            IResourceDelta delta = event.getDelta();
            if(delta != null)
                delta = delta.findMember(modelStorage.getFile().getFullPath());
            if(delta != null) {
                if (delta.getKind() == IResourceDelta.REMOVED) {
                    if ((IResourceDelta.MOVED_TO & delta.getFlags()) == 0) { //file was deleted
                        if (!isDirty()) {
                            Display.getDefault().asyncExec(new Runnable() {
                                public void run() {
                            		closeModel();
                                }
                            });
                        }
                    }
                    else { // file was moved or renamed
                        final IFile newFile = ResourcesPlugin.getWorkspace().getRoot().getFile(
                                delta.getMovedToPath());
                        Display.getDefault().asyncExec(new Runnable() {
                            public void run() {
                                setModelStorage(new FileResourceModelStorage(newFile, getModel()), false, false);
                            }
                        });
                    }
                }
                else if(!storing && delta.getKind() == IResourceDelta.CHANGED) {
                    // the file was overwritten somehow (could have been replaced by another
                    // version in the respository)
                    final IFile newFile = ResourcesPlugin.getWorkspace().getRoot().getFile(delta.getFullPath());
                    Display.getDefault().asyncExec(new Runnable() {
                        public void run() {
                            FileResourceModelStorage storage = new FileResourceModelStorage(newFile);
                            try {
                                storage.load(null);
                                setModelStorage(storage, false);
                            }
                            catch (SystemException e) {
                                RMBenchPlugin.logError(e);
                            }
                        }
                    });
                }
            }
        }
    }
}
