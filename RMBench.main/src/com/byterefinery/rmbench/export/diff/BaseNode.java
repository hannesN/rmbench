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
 * $Id: BaseNode.java 471 2006-08-21 14:41:12Z cse $
 */
package com.byterefinery.rmbench.export.diff;

import org.eclipse.swt.graphics.Image;

import com.byterefinery.rmbench.external.IDDLScript;

/**
 * base class for model comparison nodes
 * 
 * @author cse
 */
public abstract class BaseNode implements IComparisonNode {

    private static final String TYPE = "compare_node";
    
    private String name;
    private final Image image;
    private IDDLScript.Context context;
    
    BaseNode(String name, Image image) {
        this.name = name;
        this.image = image;
    }
    
    public String getName() {
        return name;
    }

    public Image getImage() {
        return image;
    }

    /**
     * the type is required by the eclipse compare API, but not used in our context
     */
    public String getType() {
        return TYPE;
    }
    
    /**
     * determine whether case matters in comparing the node names. This method should only be 
     * called after the tree has been fully constructed
     * 
     * @param ignoreCase true if case should be ignored
     */
    public void setIgnoreCase(boolean ignoreCase) {
    	name = name.toUpperCase();
    }
    
    /**
     * store the DDL statement generation context
     * 
     * @param script the DDL script that holds the context
     */
    protected void setStatementContext(IDDLScript.Context context) {
        this.context = context;
    }
    
    /**
     * @return the ranges in the generated script that correspond to the statements generated
     * for this node, or <code>null</code> if no code was generated
     */
    public IDDLScript.Range[] getStatementRanges() {
        return context != null ? context.getRanges() : null;
    }
    
    public boolean equals(Object other) {
        if (other instanceof IComparisonNode) {
        	IComparisonNode otherNode = (IComparisonNode) other;
            return getNodeType().equals(otherNode.getNodeType()) && getName().equals(otherNode.getName());
        }
        return super.equals(other);
    }

    public int hashCode() {
        return getNodeType().hashCode() ^ getName().hashCode();
    }
}
