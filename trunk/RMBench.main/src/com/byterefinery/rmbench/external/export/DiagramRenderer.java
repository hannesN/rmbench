/*
 * created 22.05.2006
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Id: DiagramRenderer.java 666 2007-10-01 19:32:51Z cse $
 */
package com.byterefinery.rmbench.external.export;

import java.util.Iterator;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.editparts.CustomEditPartFactory;
import com.byterefinery.rmbench.editparts.TableEditPart;
import com.byterefinery.rmbench.external.model.IDiagram;
import com.byterefinery.rmbench.model.diagram.Diagram;

/**
 * Utility class for rendering diagram images
 * 
 * @author cse
 */
public class DiagramRenderer {

    /**
     * Renders a diagram using the figures of the rmbench diagram editor
     * @param diagram the diagram to render. Note that this must be a diagram instance created by RMBench. 
     * It is not allowed to pass in a custom implementation of the interface
     * @return the figure with the diagram in it
     */
    public static IFigure render(IDiagram diagram) {
    	
    	//make sure we have a valid diagram instance
    	Diagram realDiagram;
    	try {
    		realDiagram = ((Diagram.IDiagramImpl)diagram).getDiagram();
    	}
    	catch(ClassCastException x) {
    		throw new IllegalArgumentException("invalid diagram parameter");
    	}

    	ScrollingGraphicalViewer viewer = new ScrollingGraphicalViewer();
        
    	viewer.createControl(RMBenchPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell());
        viewer.setRootEditPart(new ScalableFreeformRootEditPart());
        viewer.setEditPartFactory(new CustomEditPartFactory());
        viewer.setContents(realDiagram);
        
        AbstractGraphicalEditPart aep = (AbstractGraphicalEditPart) viewer.getRootEditPart();
        refresh(aep);
        
        IFigure root = ((AbstractGraphicalEditPart) viewer.getRootEditPart()).getFigure();
        setPreferedSize(root);       

        return root;
    	
    }

	private static void refresh(EditPart ep) {
		for (Iterator<?> it=ep.getChildren().iterator(); it.hasNext();) {
			refresh((EditPart) it.next());
		}
		
		if (ep instanceof TableEditPart) {
			ep.refresh();
		}
		
	}

	private static void setPreferedSize(IFigure figure) {
		for (Iterator<?> it = figure.getChildren().iterator(); it.hasNext();) {
        	setPreferedSize((IFigure) it.next());
        }
		figure.setSize(figure.getPreferredSize());
	}
}
