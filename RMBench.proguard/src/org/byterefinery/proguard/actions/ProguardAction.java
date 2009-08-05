/*
 * created 26.05.2005
 * 
 * &copy; 2005, ByteRefinery
 * 
 * $Id$
 */
package org.byterefinery.proguard.actions;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.byterefinery.proguard.ProguardPlugin;
import org.byterefinery.proguard.dialogs.RetraceFilesDialog;
import org.byterefinery.proguard.retrace.StackTrace;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.console.MessageConsoleStream;

import proguard.obfuscate.MappingReader;

/**
 * this workbench action lets the user choose an obfuscated stacktrace file which will be 
 * printed in de-obfuscated form to the console
 */
public class ProguardAction implements IWorkbenchWindowActionDelegate {
    
    private static String mappingsFile;
	private IWorkbenchWindow window;

    MessageConsoleStream consoleStream;
    
	public ProguardAction() {
	}

    public void run(IAction action) {
        
        RetraceFilesDialog dialog = new RetraceFilesDialog(window.getShell(), mappingsFile, true);
        if(dialog.open() == Window.OK) {
            
            File mappingFile = new File(dialog.getMappingsFile());
            String stackText = dialog.getStacktraceString();
            try {
                StackTrace stackTrace = new StackTrace(dialog.isVerbose());
                if(stackText != null)
                    stackTrace.read(stackText);
                else {
                    File stackFile = new File(dialog.getStacktraceFile());
                    stackTrace.read(stackFile);
                }
                retrace(stackTrace, mappingFile);
            }
            catch (IOException e) {
                Status status = new Status(Status.ERROR, "ProguardPlugin", 0, "an error occurred", e);
                ProguardPlugin.getDefault().getLog().log(status);
            }
        }
    }
    
    public void retrace(StackTrace stackTrace, File mappings) throws IOException {
        
        consoleStream = 
            new MessageConsoleStream(ProguardPlugin.getDefault().getMessageConsole());
        consoleStream.setActivateOnWrite(true);
        
        MappingReader reader = new MappingReader(mappings);

        // Resolve the obfuscated stack trace by means of the mapping file.
        reader.pump(stackTrace);

        PrintStream ps = new PrintStream(consoleStream); 
        stackTrace.print(ps);
        try {
            ps.close();
            consoleStream.close();
        }
        catch (IOException e) {
        }
    }
    
	public void selectionChanged(IAction action, ISelection selection) {
	}

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
}