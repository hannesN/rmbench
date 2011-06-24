/*
 * created 26.05.2005
 *
 * &copy; 2005, DynaBEAN Consulting
 * 
 * $Id: TreeNode.java 650 2007-08-30 16:31:11Z cse $
 */
package com.byterefinery.rmbench.views.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertySource;

import com.byterefinery.rmbench.RMBenchMessages;
import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.external.IExportable;
import com.byterefinery.rmbench.external.model.IModel;
import com.byterefinery.rmbench.external.model.IModelElement;
import com.byterefinery.rmbench.model.diagram.DTable;
import com.byterefinery.rmbench.model.diagram.Diagram;
import com.byterefinery.rmbench.model.schema.Schema;
import com.byterefinery.rmbench.model.schema.Table;
import com.byterefinery.rmbench.util.ImageConstants;
import com.byterefinery.rmbench.views.property.DiagramPropertySource;
import com.byterefinery.rmbench.views.property.SchemaPropertySource;

/**
 * representation of model elements in the context of a tree view 
 */
class TreeNode implements IAdaptable, IExportable {
    
    public final TreeNode parent;
    public final Object element;
    
    public Image image;
    public String name;
    
    private List<TreeNode> children;
    
    private IPropertySource propertySource;
    
    private boolean expanded;
    
    private final ModelExport modelExport = new ModelExport() {

		public List<IModelElement> getModelElements() {
	        return null; //TODO: must return list of IXxx elements
		}

		public IModel getModel() {
			return null;
		}
    };

    /**
     * create a new tree node with an empty child list
     * 
     * @param element the node label
     */
	public TreeNode(String element) {
		this(null, element, null);
	}
	
    /**
     * create a new node, using the given child elements for node types that 
     * do not have a child API of themselves (e.g., String) 
     */
    @SuppressWarnings("rawtypes")
	private TreeNode(TreeNode parent, Object element, List childElements) {
        
        this.parent = parent;
        
        if(element == RMBenchMessages.ModelView_SCHEMA_GROUP) {
            this.element = element;
            this.name = (String)element;
            this.image = RMBenchPlugin.getImage(ImageConstants.SCHEMA_FOLDER);
            setChildren(childElements);
        }
        else if(element == RMBenchMessages.ModelView_EMPTY_MODEL_INFO) {
            this.element = element;
            this.name = (String)element;
            this.image = null;
            setChildren(childElements);
        }
        else if(element == RMBenchMessages.ModelView_DIAGRAM_GROUP) {
            this.element = element;
            this.name = (String)element;
            this.image = RMBenchPlugin.getImage(ImageConstants.DIAGRAMS_FOLDER);
            setChildren(childElements);
        }
        else if(element == RMBenchMessages.ModelView_TABLES_GROUP) {
            this.element = element;
            this.name = (String)element;
            this.image = RMBenchPlugin.getImage(ImageConstants.DBOBJ_FOLDER);
            setChildren(childElements);
        }
        else if(element == RMBenchMessages.ModelView_VIEWS_GROUP) {
            this.element = element;
            this.name = (String)element;
            this.image = RMBenchPlugin.getImage(ImageConstants.DBOBJ_FOLDER);
            setChildren(childElements);
        }
        else if(element == RMBenchMessages.ModelView_SEQUENCES_GROUP) {
            this.element = element;
            this.name = (String)element;
            this.image = RMBenchPlugin.getImage(ImageConstants.DBOBJ_FOLDER);
            setChildren(childElements);
        }
        else if(element instanceof Diagram) {
            Diagram diagram = (Diagram)element;
            this.element = element;
            this.name = diagram.getName();
            this.image = RMBenchPlugin.getImage(ImageConstants.DIAGRAM);
            setChildren(diagram.getDTables());
        }
        else if(element instanceof Schema) {
            Schema schema = (Schema)element;
            this.element = element;
            this.name = schema.getName();
            this.image = RMBenchPlugin.getImage(ImageConstants.SCHEMA2);
            
            TreeNode[] nodes = new TreeNode[3];
            nodes[0] = new TreeNode(this, 
                    RMBenchMessages.ModelView_TABLES_GROUP, 
                    schema.getTables());
            nodes[1] = new TreeNode(this, 
                    RMBenchMessages.ModelView_VIEWS_GROUP, 
                    schema.getViews());
            nodes[2] = new TreeNode(this, 
                    RMBenchMessages.ModelView_SEQUENCES_GROUP, 
                    schema.getSequences());
            this.children = Arrays.asList(nodes);
        }
        else if(element instanceof Table) {
            Table table = (Table)element;
            this.element = element;
            this.name = table.getName();
            this.image = RMBenchPlugin.getImage(ImageConstants.TABLE2);
            noChildren();
        }
        else if(element instanceof DTable) {
            Table table = ((DTable)element).getTable();
            this.element = element;
            this.name = table.getName();
            this.image = RMBenchPlugin.getImage(ImageConstants.TABLE2);
            noChildren();
        }
        else {
            throw new IllegalArgumentException();
        }
    }
    
	private void noChildren() {
        children = Collections.emptyList();
	}

	/**
     * set the children by wrapping them with TreeNodes
     * @param childElements an array of child model objects (not TreeNodes)
     */
    protected void setChildren(Object[] childElements) {
        if(childElements == null) {
        	children = new ArrayList<TreeNode>();
            return;
        }
        List<TreeNode> nodes = new ArrayList<TreeNode>(childElements.length);
        for (int i = 0; i < childElements.length; i++) {
            nodes.add(new TreeNode(this, childElements[i], null));
        }
        this.children = nodes;
    }

    /**
     * set the children by wrapping them with TreeNodes
     * @param childElements child model objects (not TreeNodes)
     */
    protected void setChildren(Collection<?> childElements) {
        if(childElements == null || childElements == Collections.EMPTY_LIST) {
        	children = new ArrayList<TreeNode>();
        	return;
        }
        List<TreeNode> nodes = new ArrayList<TreeNode>(childElements.size());
        for (Iterator<?> iter = childElements.iterator(); iter.hasNext();) {
            nodes.add(new TreeNode(this, iter.next(), null));
        }
        this.children = nodes;
    }
    
    /**
     * add a child by wrapping it with a TreeNode
     * @param childElement a child (not a TreeNode)
     * @return the newly created TreeNode
     */
    public TreeNode addChild(Object childElement) {
        TreeNode node = new TreeNode(this, childElement, null);
        children.add(node);
        return node;
    }

    /**
     * remove a child node
     * @param table the table wrapped by the child node
     * @return the removed node, or <code>null</code>
     */
    public TreeNode removeChild(Object childElement) {
        for (Iterator<TreeNode> it = children.iterator(); it.hasNext();) {
            TreeNode node = (TreeNode) it.next();
            if(node.element == childElement) {
                it.remove();
                return node;
            }
        }
        return null;
    }
    
    public boolean hasChildren() {
        return children.size() > 0;
    }

    /**
     * @return the children, as TreeNodes
     */
    public Object[] getChildren() {
        //have to create the array manually, 'cause toArray() doesn't work :(
        TreeNode tmpArray[]=new TreeNode[children.size()];
        tmpArray=(TreeNode[]) children.toArray(tmpArray);
        return tmpArray;
    }

    /**
     * @return the child elements as a Table array.
     * @throws ClassCastException if any child does not hold a table element
     */
    public Table[] asTables() {
        Table[] tables = new Table[children.size()];
        for (int i=0; i<tables.length; i++) {
            TreeNode childNode = (TreeNode) children.get(i);
            tables[i] = (Table)childNode.element;
        }
        return tables;
    }
    
    /**
     * @param theName the name to look for
     * @return the fist child with a matching name
     */
    public TreeNode getChildNamed(String theName) {
        for (Iterator<TreeNode> it = children.iterator(); it.hasNext();) {
            TreeNode child = (TreeNode) it.next();
            if(child.name.equals(theName))
                return child;
        }
        return null;
    }

    /**
     * @param element the wrapped element
     * @return the child node that wraps the given element
     */
    public TreeNode getChild(Object element) {
        for (Iterator<TreeNode> it = children.iterator(); it.hasNext();) {
            TreeNode child = it.next();
            if(child.element == element)
                return child;
        }
        return null;
    }

    /**
     * @return an iterator over the child nodes
     */
    public Iterator<TreeNode> children() {
        return children.iterator();
    }

    /**
     * @return true if this node represents a schema element
     */
    public boolean isSchemaElement() {
        return 
            element == RMBenchMessages.ModelView_SCHEMA_GROUP ||
            element == RMBenchMessages.ModelView_TABLES_GROUP ||
            element == RMBenchMessages.ModelView_VIEWS_GROUP ||
            element == RMBenchMessages.ModelView_SEQUENCES_GROUP ||
            element instanceof Schema ||
            element instanceof Table ||
            element instanceof DTable;
        //TODO V2: views & sequences
    }

    /**
     * add schema elements represented by this node to the given list
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public void addSchemaElements(List result) {
        if(element == RMBenchMessages.ModelView_TABLES_GROUP) {
            for (Iterator it = children.iterator(); it.hasNext();) {
                TreeNode node = (TreeNode) it.next();
                result.add(node.element);
            }
        }
        else if(element == RMBenchMessages.ModelView_VIEWS_GROUP) {
            //TODO V2: support views
        }
        else if(element == RMBenchMessages.ModelView_SEQUENCES_GROUP) {
            //TODO V2: support sequences
        }
        else
            result.add(element);
    }

    /**
     * @return true if this node can serve as a target for table drop operations
     */
    public boolean isDropTablesNode() {
        return element == RMBenchMessages.ModelView_TABLES_GROUP || element instanceof Schema;
    }

    /**
     * @return the neares schema object along the ancestory line
     */
    public Schema getSchema() {
        Object currElement = element;
        while(currElement != null && !(currElement instanceof Schema))
            currElement = parent.element;
        return (Schema)currElement;
    }

    @SuppressWarnings({ "rawtypes" })
	public Object getAdapter(Class adapter) {
        if (adapter == IPropertySource.class) {
            if (propertySource == null) {
                if (element instanceof Diagram)
                    propertySource = new DiagramPropertySource((Diagram)element);
                else if (element instanceof Schema)
                    propertySource = new SchemaPropertySource((Schema)element);
            }
            return propertySource;
        }
            
        return null;
    }
    
    /**
     * Updates the nodes for schemas and diagramms, if the element name changed
     */
    public void updateNodeName() {
        if (element instanceof Schema) {
            name = ((Schema)element).getName();
        }
        if (element instanceof Diagram) {
            name = ((Diagram)element).getName();
        }
    }
    
    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public ModelExport getModelExport() {
        return modelExport;
    }

    public DiagramExport getDiagramExport() {
        return null;
    }
}
