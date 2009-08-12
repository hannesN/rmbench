/*
 * created 18.10.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: ModelCompareEditorInput.java 669 2007-10-27 08:23:31Z cse $
 */
package com.byterefinery.rmbench.export;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.compare.structuremergeviewer.IDiffElement;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.PlatformUI;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.dialogs.ExceptionDialog;
import com.byterefinery.rmbench.exceptions.SystemException;
import com.byterefinery.rmbench.export.diff.IDBComparisonNode;
import com.byterefinery.rmbench.export.diff.IModelComparisonNode;
import com.byterefinery.rmbench.export.diff.StructureNode;
import com.byterefinery.rmbench.external.IDDLFormatter;
import com.byterefinery.rmbench.external.IDDLGenerator;
import com.byterefinery.rmbench.external.IDDLScript;
import com.byterefinery.rmbench.external.IDatabaseInfo;
import com.byterefinery.rmbench.model.Model;
import com.byterefinery.rmbench.model.dbimport.DBModel;

/**
 * Editor input for use in exporting a RMBench model to a database. Although this subclasses
 * a class from the eclipse compare framework, it is used in a slightly non-standard way.
 * 
 * @author cse
 */
public class ModelCompareEditorInput extends CompareEditorInput implements IStorageEditorInput {

    private final Model model;
    private final DBModel dbmodel;

    private final IDDLGenerator generator;
    private final IDDLScript script;
    private final IDDLFormatter formatter;
    private final boolean generateDrop;
    private final boolean ignoreCase;
    
    private Storage storage = new Storage();
    private final Shell shell;
    
    /**
     * @param model model to export
     * @param dbmodel database model to export to
     * @param ddlGenerator a ready configured DDL generator
     * @param drop true if SQL DROP statements should be generated for elements found 
     * in the database but not in the model  
     * @param shell the parent shell for error & connection dialogs
     */
    public ModelCompareEditorInput(
            Model model, 
            DBModel dbmodel, 
            IDDLGenerator generator,
            IDDLFormatter formatter,
            IDDLScript script,
            boolean generateDrop,
            boolean ignoreCase,
            Shell shell) {
        
        super(createCompareConfiguration());
        this.model = model;
        this.dbmodel = dbmodel;
        this.shell = shell;
        this.generator = generator;
        this.script = script;
        this.formatter = formatter;
        this.ignoreCase = ignoreCase;
        this.generateDrop = generateDrop;
    }

	public Model getModel() {
		return model;
	}
	
    /**
     * unused method
     * @throws UnsupportedOperationException
     */
    public Viewer createDiffViewer(Composite parent) {
        throw new UnsupportedOperationException();
    }

    /**
     * Perform the model comparison, displaying error dialogs if the comparison fails or 
     * no differences are found. 
     * 
     * @return true if successful
     */
    public boolean compareResultOK() {
        try {
            PlatformUI.getWorkbench().getProgressService().run(true, false, this);
            
            String message = getMessage();
            if (message != null) {
                MessageDialog.openError(shell, Messages.CompareInput_compareFailed, message);
                return false;
            }
            
            if (dbmodel.isLoaded() && getCompareResult() == null) {
                MessageDialog.openInformation(
                        shell, 
                        Messages.CompareInput_DialogTitle, 
                        Messages.CompareInput_noDifferences);
                return false;
            }
            
            return true;

        } catch (InterruptedException x) {
            // cancelled by user        
        } catch (InvocationTargetException x) {
            RMBenchPlugin.logError(x.getTargetException());
            String msg = x.getTargetException().getMessage();
            if(msg == null)
                msg = x.getTargetException().getClass().getName();
            ExceptionDialog.openError(
                    shell,
                    Messages.CompareInput_compareFailed,
                    SystemException.getStatus(x.getTargetException(), msg));
        }
        return false;
    }

    private static CompareConfiguration createCompareConfiguration() {
        CompareConfiguration config = new CompareConfiguration();
        config.setProperty(CompareConfiguration.SHOW_PSEUDO_CONFLICTS, Boolean.TRUE);
        return config;
    }
    
    protected Object prepareInput(IProgressMonitor monitor) 
        throws InvocationTargetException, InterruptedException {
        
        monitor.beginTask(Messages.CompareInput_beginTask, IProgressMonitor.UNKNOWN);
        setTitle(Messages.CompareInput_Title);

        try {
            monitor.subTask(Messages.CompareInput_subtaskLoad);
            if(!dbmodel.load(shell))
                return null;
            
            //create diff node trees for both models
            StructureNode left = new com.byterefinery.rmbench.export.diff.model.ModelNode(model, dbmodel.getPublicSchemaNames());
            StructureNode right = new com.byterefinery.rmbench.export.diff.db.ModelNode(dbmodel.getName(), dbmodel.getSchemas());
            if(ignoreCase) {
            	left.setIgnoreCase(true);
            	right.setIgnoreCase(true);
            }
            
            //run the differ over the 2 trees
            Differencer diff = new Differencer();
            return diff.findDifferences(false, monitor, null, null, left, right);
        } 
        catch (Exception ex) {
            throw new InvocationTargetException(ex);
        } 
        finally {
            monitor.done();
        }
    }

    /**
     * @return the statement terminator used by the underlying script
     */
    public String getStatementTerminator() {
        return script.getStatementTerminator();
    }
    
    private void generateDDL(DiffNode node) {

        IDiffElement[] diffElements = node.getChildren();
        
        for (int i = 0; i < diffElements.length; i++) {
            DiffNode childNode = (DiffNode)diffElements[i];
            switch(childNode.getKind()) {
                case Differencer.ADDITION: {
                    if(generateDrop)
                        ((IDBComparisonNode)childNode.getRight()).generateDropDDL(generator, script);
                    break;
                }
                case Differencer.DELETION: {
                    ((IModelComparisonNode)childNode.getLeft()).generateCreateDDL(generator, script);
                    break;
                }
                case Differencer.CHANGE: {
                    IModelComparisonNode modelNode = (IModelComparisonNode)childNode.getLeft();
                    if(modelNode.getValue() != null) {
	                    IDBComparisonNode dbNode = (IDBComparisonNode)childNode.getRight();
	                    modelNode.generateAlterDDL(generator, dbNode.getElement(), script);
                    }
                    break;
                }
            }
            generateDDL((DiffNode)diffElements[i]);
        }
    }
    
    public IDatabaseInfo getModelDatabaseInfo() {
        return model.getDatabaseInfo();
    }

    public IStorage getStorage() {
        return storage;
    }
    
    public void reset() {
    	try {
    		script.reset();
			storage.generateContents();
		} catch (CoreException e) {
			RMBenchPlugin.logError(e);
		}
    }
    
    private class Storage implements IStorage {

        private byte[] ddl;
        
        public InputStream getContents() throws CoreException {
            if(ddl == null) {
            	generateContents();
            }
            return new ByteArrayInputStream(ddl);
        }

        public void generateContents() throws CoreException {
            DiffNode diffNode = (DiffNode)getCompareResult();
            if(diffNode == null)
                throw new CoreException(SystemException.getStatus(null, "compare result not set"));
            
            generateDDL(diffNode);
            ddl = script.generate(formatter).getBytes();
		}

		public IPath getFullPath() {
            return null;
        }

        public String getName() {
            return ModelCompareEditorInput.this.getName();
        }

        public boolean isReadOnly() {
            return false;
        }

        @SuppressWarnings("unchecked")
		public Object getAdapter(Class adapter) {
            return null;
        }
    }
}
