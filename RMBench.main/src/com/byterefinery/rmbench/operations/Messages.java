/*
 * created 02.08.2005 by sell
 *
 * $Id: Messages.java 673 2007-11-13 19:49:41Z cse $
 */
package com.byterefinery.rmbench.operations;

import org.eclipse.osgi.util.NLS;

/**
 * translatable strings for operations
 *  
 * @author sell
 */
public class Messages extends NLS {
    private static final String BUNDLE_NAME = "com.byterefinery.rmbench.operations.Messages"; //$NON-NLS-1$
    static {
	       NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
    public static String Operation_ChangeTableName;
    public static String Operation_ChangeTableSchema;
    public static String Operation_ChangeTableComment;
    public static String Operation_ChangeTableDescription;
    public static String Operation_NewSchema;
    public static String Operation_NewDiagram;
    public static String Operation_Move;
    public static String Operation_AddForeignKey;
    public static String Operation_AddTable;
    public static String Operation_DeleteForeignKey;
    public static String Operation_DeleteTable;
    public static String Operation_DeleteDiagram;
    public static String Operation_DeleteSchema;
    public static String Operation_CollapseTable;
    public static String Operation_AddColumn;
    public static String Operation_ModifyColumn;
	public static String Operation_DeleteColumn;
    public static String Operation_ImportObjects;
    public static String Operation_AddToDiagram;
    public static String Operation_LayoutTables;
    public static String Operation_RemoveTable;
    public static String Operation_AddIndex;
    public static String Operation_DeleteIndex;
    public static String Operation_EditIndex;
    public static String Operation_AddConstraint;
    public static String Operation_DeleteConstraint;
    public static String Operation_SetTableType;
	public static String Operation_ModifyKeyConstraint;
	public static String Operation_ModifyForeignKey;
    public static String Operation_DiagramSetDefaultSchema;
    public static String Operation_ChangeSchemaName;
    public static String Operation_ChangeDiagramName;
    public static String Operation_ChangeSchemaCatalog;
    public static String Operation_MoveAnchor;
    public static String Operation_ModifyModel;
    public static String Operation_ModifyModelProperties;
    public static String Operation_ModifyCheckConstraint;
	public static String Operation_AddStubbedTables_SelectTablesMessage;
	public static String Operation_AddStubbedTables_SelectTablesTitle;
	public static String Operation_AddStubbedTables;
    public static String Operation_DeleteColumn_messageTitle;
    public static String Operation_DeleteColumn_foreignKey_Message;
    public static String Operation_MoveColumn;
	public static String Operation_TablesNewDiagram_Label;
}
