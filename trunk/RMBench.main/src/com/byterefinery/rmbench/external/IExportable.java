/*
 * created 25.02.2006
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
package com.byterefinery.rmbench.external;

import java.util.List;

import org.eclipse.draw2d.IFigure;

import com.byterefinery.rmbench.external.model.IModel;
import com.byterefinery.rmbench.external.model.IModelElement;


/**
 * specification of an item that can be exported to an external format
 * 
 * @author cse
 */
public interface IExportable {

    /**
     * a model export provides access to the model elements immediately selected for export, 
     * and the parent model
     */
    public interface ModelExport {
    	/**
    	 * @return the model elements corresponding to the current selection, or <code>null</code> if 
    	 * the model itself was selected
    	 */
        public List<IModelElement> getModelElements();
        /**
         * @return the model
         */
        public IModel getModel();
    }

    /**
     * a diagram export provides access to the figure immediately selected for export, 
     * and the parent diagram figure
     */
    public interface DiagramExport {
        /**
         * @return the figure to export
         */
        IFigure getExportFigure();
        /**
         * @return the parent diagram figure, if available
         */
        IFigure getExportDiagramFigure();
    }
    
    /**
     * @return the model export element, if this is a model export, <code>null</code> otherwise
     */
    ModelExport getModelExport();
    
    /**
     * @return the diagram export element, if this is a diagram export, <code>false</code> otherwise
     */
    DiagramExport getDiagramExport();
}
