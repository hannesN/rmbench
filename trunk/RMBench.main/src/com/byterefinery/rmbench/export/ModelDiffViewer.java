/*
 * created 26.09.2007
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.compare.structuremergeviewer.DiffTreeViewer;
import org.eclipse.compare.structuremergeviewer.IDiffContainer;
import org.eclipse.compare.structuremergeviewer.IDiffElement;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;

import com.byterefinery.rmbench.export.diff.IDBComparisonNode;
import com.byterefinery.rmbench.export.diff.IModelComparisonNode;
import com.byterefinery.rmbench.model.Model;
import com.byterefinery.rmbench.operations.CompoundOperation;
import com.byterefinery.rmbench.operations.RMBenchOperation;

/**
 * subclassed to add customer context menu entries
 * 
 * @author cse
 */
public class ModelDiffViewer extends DiffTreeViewer {

	public interface Listener {
		/**
		 * notification that a node was removed from the tree. This removal includes all
		 * child nodes (if any) and all empty parent nodes as well.
		 * 
		 * @param node the node that was removed
		 */
		void nodesRemoved(DiffNode node);
	}
	
	private final Action importAction;
	private Model model;
	private List<Listener> listeners = new ArrayList<Listener>(2);
	
	
	public ModelDiffViewer(Tree tree, CompareConfiguration configuration) {
		super(tree, configuration);
		this.importAction = createImportAction();
	}

	public ModelDiffViewer(Composite parent, CompareConfiguration configuration) {
		super(parent, configuration);
		importAction = createImportAction();
	}

	/**
	 * @param model the model, which is used to apply DB changes back to the model
	 */
	public void setModel(Model model) {
		this.model = model;
	}
	
	private Action createImportAction() {
		Action action = new Action() {

			@Override
			public void run() {
				ISelection selection = getSelection();
				if (selection instanceof IStructuredSelection) {
					Iterator<?> elements= ((IStructuredSelection)selection).iterator();
					while (elements.hasNext()) {
						DiffNode node = (DiffNode)elements.next();
						
						RMBenchOperation operation = createChangeOperation(node);
						
						if(operation != null && operation.execute(this)) {
							IDiffContainer root=node.getParent(), tmp;
							for(tmp=root; tmp != null; tmp=tmp.getParent()) 
								root = tmp;
							
							node.getParent().removeToRoot(node);
							refresh(root);
							for (Listener listener : listeners) {
								listener.nodesRemoved(node);
							}
							updateSelection(new StructuredSelection(root));
						}
					}
				}
			}
		};
		action.setText(Messages.ModelDiffViewer_importText);
		action.setToolTipText(Messages.ModelDiffViewer_importDescription);
		action.setDescription(Messages.ModelDiffViewer_importDescription);
		
		
		return action;
	}

	protected RMBenchOperation createChangeOperation(DiffNode node) {
		
		IModelComparisonNode modelNode = (IModelComparisonNode)node.getLeft();
		IDBComparisonNode dbNode = (IDBComparisonNode)node.getRight();

		if(modelNode == null) {
			//add element to model
			if(model != null)
				return dbNode.getAddToModelOperation(model);
		}
		else if(dbNode == null) {
			//delete element from model
			return modelNode.getModifyOperation(null);
		}
		else {
			//modify element in model
			RMBenchOperation modifyOperation = modelNode.getModifyOperation(dbNode.getElement());
			
			if(node.hasChildren()) {
				//create a compound operation so that all subelement changes get applied as well
				CompoundOperation cop = new CompoundOperation(Messages.ModelDiffViewer_importOperation);
				if(modifyOperation != null)
					cop.add(modifyOperation);
				for (IDiffElement element : node.getChildren()) {
					RMBenchOperation childOp = createChangeOperation((DiffNode)element);
					if(childOp != null)
						cop.add(childOp);
				}
				return cop.size() > 0 ? cop : null;
			}
			else {
				return modifyOperation;
			}
		}
		return null;
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}
	
	protected void fillContextMenu(IMenuManager manager) {
		super.fillContextMenu(manager);
		manager.add(importAction);
	}

    protected void initialSelection() {
    }
}
