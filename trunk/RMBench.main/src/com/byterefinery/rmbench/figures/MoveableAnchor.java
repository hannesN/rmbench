/*
 * created 1.12.2005
 * 
 * $Id: MoveableAnchor.java 664 2007-09-28 17:28:39Z cse $
 */
package com.byterefinery.rmbench.figures;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import com.byterefinery.rmbench.model.diagram.DForeignKey;
import com.byterefinery.rmbench.model.diagram.DTable;

/**
 * an anchor that supports relocation along the bounding rectangle of a figure, moving
 * between available slots 
 * 
 * @author hannesn
 */
public class MoveableAnchor extends ChopboxAnchor {

    /** the number of slots allocated along the edges of the figure */
    public static final int NUMBER_OF_SLOTS = 8;

    /** constant signifying the upper edge of the figure */
    public static final int NORTH = 1;

    /** constant signifying the lower edge of the figure */
    public static final int SOUTH = 2;

    /** constant signifying the left edge of the figure */
    public static final int WEST = 4;

    /** constant signifying the right edge of the figure */
    public static final int EAST = 8;

    private int edge;

    private int slotNumber;

    private boolean preview = false;

    private DForeignKey dForeignKey = null;

    private boolean sourceAnchor = false;

    public MoveableAnchor(IFigure figure) {
        super(figure);
        preview = true;
        dForeignKey = new DForeignKey(null);
    }

    public MoveableAnchor(IFigure figure, Point location) {
        super(figure);
        dForeignKey = new DForeignKey(null);
        computeSlot(location);
    }

    public MoveableAnchor(IFigure figure, DForeignKey foreignKey, boolean isSource) {
        super(figure);
        this.dForeignKey = foreignKey;
        sourceAnchor = isSource;
        if ((isSource) && (foreignKey.isSourceValid()) ) {
            this.edge = foreignKey.getSourceEdge();
            this.slotNumber = foreignKey.getSourceSlot();
        } else if ((!isSource) && (foreignKey.isTargetValid()) ) {
            this.edge = foreignKey.getTargetEdge();
            this.slotNumber = foreignKey.getTargetSlot();
        }
    }

    public Point getLocation(Point reference) {
        Point point;
        if ((sourceAnchor) && (!dForeignKey.isSourceValid())) {
            if ((dForeignKey.getForeignKey() != null)
                    && (dForeignKey.getForeignKey().getTable() == dForeignKey.getForeignKey()
                            .getTargetTable())) {
                assignSlot(MoveableAnchor.NUMBER_OF_SLOTS - 1, MoveableAnchor.NORTH);
            }
            else {
                point = super.getLocation(reference);
                computeSlot(point, true);
            }
        }
        else if (!(sourceAnchor) && (!dForeignKey.isTargetValid())) {
            if ((dForeignKey.getForeignKey() != null)
                    && (dForeignKey.getForeignKey().getTable() == dForeignKey.getForeignKey()
                            .getTargetTable())) {
                assignSlot(1, MoveableAnchor.EAST);
            }
            else {
                point = super.getLocation(reference);
                computeSlot(point, true);
            }
        }

        if ((!preview) && (dForeignKey != null)) {
            if (sourceAnchor) {
                edge = dForeignKey.getSourceEdge();
                slotNumber = dForeignKey.getSourceSlot();
            }
            else {
                edge = dForeignKey.getTargetEdge();
                slotNumber = dForeignKey.getTargetSlot();
            }
        }

        Rectangle bounds = getBox();

        point = Point.SINGLETON;
        if (edge == NORTH) {
            point.x = (bounds.width / NUMBER_OF_SLOTS) * slotNumber + bounds.x;
            point.y = bounds.y;
        }
        else if (edge == SOUTH) {
            point.x = (bounds.width / NUMBER_OF_SLOTS) * slotNumber + bounds.x;
            point.y = bounds.y + bounds.height;
        }
        else if (edge == WEST) {
            point.x = bounds.x;
            point.y = (bounds.height / NUMBER_OF_SLOTS) * slotNumber + bounds.y;
        }
        else if (edge == EAST) {
            point.x = bounds.x + bounds.width;
            point.y = (bounds.height / NUMBER_OF_SLOTS) * slotNumber + bounds.y;
        }
        getOwner().translateToAbsolute(point);

        return point;
    }

    /**
     * compute the slot for this anchor based on a given position, which is most likely the 
     * result of a mouse move operation on the connection handle. This method can be 
     * called during relocation requests to force a slot recomputation  
     * 
     * @param point
     */
    public void computeSlot(Point point) {
        computeSlot(point, false);
    }

    /**
     * compute the slot for this anchor based on a given position, which is most likely the 
     * result of a mouse move operation on the connection handle. This method can be 
     * called during relocation requests to force a slot recomputation  
     * 
     * @param point
     * @param avoidDoubleAllocation 
     */
    public void computeSlot(Point point, boolean avoidDoubleAllocation) {
        Rectangle bounds = getBox();
        Point tmpPoint = Point.SINGLETON;

        double distance = Double.MAX_VALUE;
        double slotDistance = bounds.width / NUMBER_OF_SLOTS;
        getOwner().translateToRelative(point);

        for (int i = 0; i <= NUMBER_OF_SLOTS; i++) {
            tmpPoint.x = (int) (bounds.x + i * slotDistance);
            tmpPoint.y = bounds.y;
            if (tmpPoint.getDistance2(point) < distance) {
                slotNumber = i;
                distance = tmpPoint.getDistance2(point);
                edge = NORTH;
            }
            tmpPoint.x = (int) (bounds.x + i * slotDistance);
            tmpPoint.y += bounds.height;
            if (tmpPoint.getDistance2(point) < distance) {
                slotNumber = i;
                distance = tmpPoint.getDistance2(point);
                edge = SOUTH;
            }
        }

        slotDistance = bounds.height / NUMBER_OF_SLOTS;
        for (int i = 0; i <= NUMBER_OF_SLOTS; i++) {
            tmpPoint.y = (int) (bounds.y + i * slotDistance);
            tmpPoint.x = bounds.x;
            if (tmpPoint.getDistance2(point) < distance) {
                slotNumber = i;
                distance = tmpPoint.getDistance2(point);
                edge = WEST;
            }
            tmpPoint.y = (int) (bounds.y + i * slotDistance);
            tmpPoint.x += bounds.width;
            if (tmpPoint.getDistance2(point) < distance) {
                slotNumber = i;
                distance = tmpPoint.getDistance2(point);
                edge = EAST;
            }
        }

        if (avoidDoubleAllocation) {
            int slot = slotNumber;
            while (!isAnchorPositionFree(slot, edge, sourceAnchor)) {
                slot--;
                if (slot < 0) {
                    slot = NUMBER_OF_SLOTS;
                }
                //if we checked all slots we use the already allocated slot calculated by the chobbox algorithm
                // TODO V1 hannes find a better solution
                if (slot == slotNumber)
                    break;
            }
            slotNumber = slot;

        }

        if (!preview)
            assignSlot(slotNumber, edge);
    }

    /**
     * explicitly reserve a slot for this anchor
     *  
     * @param slotNumber the slot number
     * @param edge the edge on which to assign the slot, one of 
     * {@link #NORTH} {@link #SOUTH}, {@link #WEST}, or {@link #EAST}
     */
    public void assignSlot(int slotNumber, int edge) {
        if (slotNumber<0)
            slotNumber=0;
        if (slotNumber>NUMBER_OF_SLOTS)
            slotNumber=NUMBER_OF_SLOTS;
        
        this.slotNumber = slotNumber;
        this.edge = edge;
        if (dForeignKey != null) {
            if (sourceAnchor) {
                dForeignKey.setSourceEdge(edge);
                dForeignKey.setSourceSlot(slotNumber);
                dForeignKey.setSourceValid(true);
            }
            else {
                dForeignKey.setTargetEdge(edge);
                dForeignKey.setTargetSlot(slotNumber);
                dForeignKey.setTargetValid(true);
            }

        }
        fireAnchorMoved();
    }

    public void setPreview(boolean preview) {
        this.preview = preview;
    }

    /**
     * 
     * @return the edge number stored in the dForeignKey
     */
    public int getDForeignKeyEdge() {
        return (sourceAnchor) ? dForeignKey.getSourceEdge() : dForeignKey.getTargetEdge();
    }

    /**
     * 
     * @return the slot number in the dForeignKey
     */
    public int getDForeignKeySlot() {
        return (sourceAnchor) ? dForeignKey.getSourceSlot() : dForeignKey.getTargetSlot();
    }

    /**
     * The returned edge number can be different to the dForeignkey edge number
     * if the anchor is in preview mode, e.g. if a drag operation is still running
     * 
     * @return the edge number locally stored in the anchor
     */
    public int getEdge() {
        return edge;
    }

    /**
     * The returned slot number can be different to the dForeignkey slot number
     * if the anchor is in preview mode, e.g.if a drag operation is still running
     * 
     * @return the slot number locally stored in the anchor
     */
    public int getSlotNumber() {
        return slotNumber;
    }

    /**
     * checks if the given anchor position is already in use in the table
     * @param slot
     * @param edge
     * @param isSourceAnchor
     * @return
     */
    public boolean isAnchorPositionFree(int slot, int edge, boolean isSourceAnchor) {
        DTable dtable = ((TableFigure) getOwner()).getDTable();
        for (DForeignKey dFk : dtable.getReferences()) {
            if ((dFk.isTargetValid()) && (edge == dFk.getTargetEdge()))
                if (slot == dFk.getTargetSlot())
                    return false;
        }

        for (DForeignKey dFk : dtable.getForeignKeys()) {
            if ((dFk.isSourceValid()) && (edge == dFk.getSourceEdge()))
                if (slot == dFk.getSourceSlot())
                    return false;
        }
        return true;
    }

}