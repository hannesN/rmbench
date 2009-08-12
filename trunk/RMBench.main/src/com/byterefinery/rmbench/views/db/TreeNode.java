/*
 * created 26.05.2005
 *
 * &copy; 2005, DynaBEAN Consulting
 * 
 * $Id: TreeNode.java 666 2007-10-01 19:32:51Z cse $
 */
package com.byterefinery.rmbench.views.db;

import java.util.List;

import org.eclipse.swt.graphics.Image;

import com.byterefinery.rmbench.RMBenchMessages;
import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.model.dbimport.DBColumn;
import com.byterefinery.rmbench.model.dbimport.DBModel;
import com.byterefinery.rmbench.model.dbimport.DBSchema;
import com.byterefinery.rmbench.model.dbimport.DBTable;
import com.byterefinery.rmbench.util.ImageConstants;

/**
 * representation of model elements in the context of a tree view 
 */
@SuppressWarnings("unchecked")
class TreeNode implements Comparable {
    
    public Object element;
    public final Object parent;
    public final int level;
    
    public Image image;
    public String name;
    public TreeNode[] children;
    
    /**
     * create a new node, deriving the child elements from the node type
     */
    public TreeNode(Object parent, Object element) {
        this(parent, element, null);
    }
    
    /**
     * create a new node, using the given child elements for node types that 
     * do not have a child API of themselves (e.g., String) 
     */
    public TreeNode(Object parent, Object element, List childElements) {
        
        this.parent = parent;
        this.element = element;
        
        if(element instanceof DBModel) {
            DBModel dbm = (DBModel)element;
            this.level = 0;
            this.name = dbm.getName();
            this.image = dbm.isLoaded() ? 
                    RMBenchPlugin.getImage(ImageConstants.CONNECTED):
                    RMBenchPlugin.getImage(ImageConstants.DISCONNECTED);
            this.children =  makeChildren(dbm.getSchemaList());
        }
        else if(element instanceof DBSchema) {
            DBSchema schema = (DBSchema)element;
            this.level = 1;
            this.name = schema.getName();
            this.image = RMBenchPlugin.getImage(ImageConstants.SCHEMA2);
            
            TreeNode[] nodes = new TreeNode[2];
            nodes[0] = new TreeNode(this, 
                    RMBenchMessages.ImportView_TablesNode, 
                    schema.getTables());
            nodes[1] = new TreeNode(this, 
                    RMBenchMessages.ImportView_ViewsNode, 
                    schema.getViews());
            this.children = nodes;
        }
        else if(element instanceof String) {
            this.name = (String)element;
            this.level = 2;
            this.image = RMBenchPlugin.getImage(ImageConstants.DBOBJ_FOLDER);
            this.children = makeChildren(childElements);
        }
        else if(element instanceof DBTable) {
            DBTable table = (DBTable)element;
            this.name = table.getName();
            this.level = 3;
            this.image = RMBenchPlugin.getImage(ImageConstants.TABLE2);
        }
        else {
        	//TODO V2: views & sequences
            this.level = 3;
            throw new UnsupportedOperationException();
        }
    }
    
    public void rebuildChildren(List childElements) {
        children = makeChildren(childElements);
    }

    private TreeNode[] makeChildren(List childElements) {
        TreeNode[] nodes = new TreeNode[childElements.size()];
        for(int i = 0; i < nodes.length; i++) {
            nodes[i] = new TreeNode(this, childElements.get(i));
        }
        return nodes;
    }

	public boolean canImport() {
        if(element instanceof DBModel) {
            DBModel dbmodel = (DBModel)element;
            return dbmodel.isLoaded();
        }
        else 
            return !(element instanceof DBColumn);
	}
	
	public int countImportElements() {
		if(element instanceof DBColumn) {
			return 0;
		}
        else if (element instanceof DBModel) {
            DBModel dbmodel = (DBModel)element;
            return dbmodel.isLoaded() ? dbmodel.getSchemaList().size() : 0;
        }
        if(element == RMBenchMessages.ImportView_TablesNode) {
        	return children.length;
        }
        else if(element == RMBenchMessages.ImportView_ViewsNode) {
            return children.length;
        }
        else {
            return 1;
        }
	}

	public void addImportElements(List list) {
        if(element == RMBenchMessages.ImportView_TablesNode) {
        	for (int i = 0; i < children.length; i++) {
				list.add(children[i].element);
			}
        }
        else if(element == RMBenchMessages.ImportView_ViewsNode) {
        	//TODO V2: support views?
        }
        else if(element instanceof DBModel) {
            DBModel dbmodel = (DBModel)element;
            list.addAll(dbmodel.getSchemaList());
        }
        else
        	list.add(element);
	}

    public void updateDBModel(DBModel connection, boolean childrenAlso) {
        element = connection;
        this.name = connection.getName();
        if(childrenAlso)
            this.children =  makeChildren(connection.getSchemaList());
    }

    public int compareTo(Object other) {
        return name.compareTo(((TreeNode)other).name);
    }
}

