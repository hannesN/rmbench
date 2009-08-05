/*
 * created 18.05.2006
 * 
 * $Id$
 */
package com.byterefinery.rmbench.dialogs.export;

import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.draw2d.IFigure;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

import com.byterefinery.rmbench.dialogs.Messages;
import com.byterefinery.rmbench.external.IImageExporter;

/**
 * file creation page that provides the initial file contents from the image exporter
 * 
 * @author cse
 */
class ExportFilePage extends WizardNewFileCreationPage {

    private IImageExporter exporter;
    private IFigure figure;
    
    ExportFilePage() {
        super(ExportFilePage.class.getName(), new StructuredSelection());
        setTitle(Messages.ExportWizard_mainTitle);
        setDescription(Messages.ExportWizard_fileDescription);
    }

    IFile createImageFile(IImageExporter exporter, IFigure figure) {
        this.exporter = exporter;
        this.figure = figure;
        return createNewFile();
    }
    
    protected InputStream getInitialContents() {
        return exporter.export(figure);
    }
}
