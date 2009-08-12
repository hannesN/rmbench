/*
 * created 16.08.2005 by sell
 *
 * $Id: NewModelWizardPage2.java 118 2006-01-24 22:56:05Z csell $
 */
package com.byterefinery.rmbench.dialogs;

import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

import com.byterefinery.rmbench.RMBenchConstants;
import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.model.Model;
import com.byterefinery.rmbench.util.xml.ModelWriter;

/**
 * wizard page for specifying the model file
 * 
 * @author sell
 */
public class NewModelWizardPage2 extends WizardNewFileCreationPage {

    public NewModelWizardPage2(IStructuredSelection selection) {
        super("NewModelWizardPage2", selection);
        setTitle(Messages.NewModelWizard2_Title);
        setDescription(Messages.NewModelWizard2_Description);
    }

    protected boolean validatePage() {
        if (super.validatePage()) {
            String fileName = getFileName();
            int dotLoc = fileName.lastIndexOf('.');
            if (dotLoc != -1) {
                String ext = fileName.substring(dotLoc + 1);
                if(!ext.equalsIgnoreCase(RMBenchConstants.MODEL_FILE_EXTENSION)) {
                    setErrorMessage(
                            NLS.bind(
                                    Messages.NewModelWizard2_ErrExtension, 
                                    RMBenchConstants.MODEL_FILE_EXTENSION));
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    protected IFile createFileHandle(IPath filePath) {
        if(filePath.getFileExtension() == null) {
            filePath = filePath.addFileExtension(RMBenchConstants.MODEL_FILE_EXTENSION);
        }
        return super.createFileHandle(filePath);
    }

    protected InputStream getInitialContents() {
        NewModelWizardPage1 propertiesPage = (NewModelWizardPage1)getPreviousPage();
        Model model = new Model(
                propertiesPage.getModelName(), 
                propertiesPage.getDatabaseExtension().getDatabaseInfo(),
                propertiesPage.getNameGeneratorExtension().getNameGenerator());
        
        try {
            PipedInputStream inStream = new PipedInputStream();
            PipedOutputStream outStream = new PipedOutputStream(inStream);
            
            ModelWriter.write(model, outStream);
            return inStream;
        } 
        catch(Exception x) {
            RMBenchPlugin.logError(x);
            return null;
        }
    }
}
