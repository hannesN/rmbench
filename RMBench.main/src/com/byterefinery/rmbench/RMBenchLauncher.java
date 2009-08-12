/*
 * created 31.05.2005
 *
 * &copy; 2005, DynaBEAN Consulting
 * 
 * $Id: RMBenchLauncher.java 317 2006-03-24 23:49:23Z cse $
 */
package com.byterefinery.rmbench;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.ui.IEditorLauncher;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

import com.byterefinery.rmbench.exceptions.ExceptionMessages;
import com.byterefinery.rmbench.exceptions.SystemException;
import com.byterefinery.rmbench.util.FileResourceModelStorage;
import com.byterefinery.rmbench.util.IModelStorage;
import com.byterefinery.rmbench.views.model.ModelView;

/**
 * an editor launcher that will load the given file, which is assumed to contain
 * a RMBench model definition, into the model view. By default, the RMBench 
 * perspective will also be displayed 
 * 
 * @author cse
 */
public class RMBenchLauncher implements IEditorLauncher {

    public void open(IPath path) {
        
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        try {
            try {
                PlatformUI.getWorkbench().showPerspective(RMBenchPerspective.PERSPECTIVE_ID, window);
            }
            catch (WorkbenchException e) {
                RMBenchPlugin.logError(e);
            }
            if(!RMBenchPlugin.getModelManager().canSwitchModel(window.getShell()))
                return;
            
            IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocation(path);
            if(files.length > 0) {
                FileResourceModelStorage modelStorage = new FileResourceModelStorage(files[0]);
                
                IModelStorage.DefaultLoadListener loadListener = new IModelStorage.DefaultLoadListener();
                modelStorage.load(loadListener);
                
                RMBenchPlugin.getModelManager().setModelStorage(modelStorage, loadListener.dirty);
                
                PlatformUI.getWorkbench().
                    getActiveWorkbenchWindow().getActivePage().showView(ModelView.VIEW_ID);
            }
            else {
                //should not happen, as the launcher is invoked when the user clicks on a file
                RMBenchPlugin.logError("file not found: "+path);
            }
        }
        catch (PartInitException e) {
            RMBenchPlugin.logError(e);
        }
        catch (SystemException e) {
            ErrorDialog.openError(
                    window.getShell(), null, null, e.getStatus(ExceptionMessages.errorOpeningModelFile));
            RMBenchPlugin.logError(e);
        }
    }
}
