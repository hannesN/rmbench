/*
 * created 04.06.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: DForeignKey.java 174 2006-02-13 13:21:55Z hannesn $
 */
package com.byterefinery.rmbench.model.diagram;

import com.byterefinery.rmbench.model.schema.ForeignKey;

/**
 * Wrapper for ForeignKey connections, used by the GEF.
 * 
 * @author cse
 */
public class DForeignKey {

    private final ForeignKey foreignKey;
    
    private int sourceSlot;
    private int sourceEdge;
    
    private int targetSlot;
    private int targetEdge;
    
    private boolean sourceValid;
    private boolean targetValid;
    
    

    public DForeignKey(ForeignKey foreignKey) {
        this.foreignKey = foreignKey;
    }

    public ForeignKey getForeignKey() {
        return foreignKey;
    }
    
    /**
     * @return true, if the foreignkeys of this and obj are equal<br />
     * false else
     */
    public boolean equals(Object obj) {
        if (obj instanceof DForeignKey)
            return foreignKey.equals(((DForeignKey)obj).foreignKey);
        
        return false;
    }

    public int hashCode() {
        return foreignKey.hashCode();
    }

    public int getSourceEdge() {
        return sourceEdge;
    }

    public void setSourceEdge(int sourcePosition) {
        this.sourceEdge = sourcePosition;
    }

    public int getSourceSlot() {
        return sourceSlot;
    }

    public void setSourceSlot(int sourceSlot) {
        this.sourceSlot = sourceSlot;
    }

    public int getTargetEdge() {
        return targetEdge;
    }

    public void setTargetEdge(int targetPosition) {
        this.targetEdge = targetPosition;
    }

    public int getTargetSlot() {
        return targetSlot;
    }

    public void setTargetSlot(int targetSlot) {
        this.targetSlot = targetSlot;
    }

    /**
     * Checks if a the target attributes are valid and may be used.
     * @return true if valid <br/>
     *         false else
     */
    public boolean isTargetValid() {
        return targetValid;
    }

    /**
     * Sets the flag if a the target attributes are valid and may be used.
     * @param targetValid the flag vlaue
     */
    public void setTargetValid(boolean targetValid) {
        this.targetValid = targetValid;
    }
    
    /**
     * Checks if a the source attributes are valid and may be used.
     * @return true if valid <br/>
     *         false else
     */
    public boolean isSourceValid() {
        return sourceValid;
    }

    /**
     * Sets the flag if a the source attributes are valid and may be used.
     * @param sourceValid the flag vlaue
     */
    public void setSourceValid(boolean sourceValid) {
        this.sourceValid = sourceValid;
    }
    
    /**
     * Sets source and connection invalid
     *
     */
    public void invalidate() {
        this.sourceValid = false;
        this.targetValid = false;
    }
}
