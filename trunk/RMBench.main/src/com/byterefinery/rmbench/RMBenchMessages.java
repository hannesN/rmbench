/*
 * created 04.04.2005
 * 
 * $Id: RMBenchMessages.java 673 2007-11-13 19:49:41Z cse $
 */
package com.byterefinery.rmbench;

import org.eclipse.osgi.util.NLS;

/**
 * @author cse
 */
public class RMBenchMessages extends NLS {

    private static final String BUNDLE_NAME = 
		"com.byterefinery.rmbench.RMBenchMessages"; //$NON-NLS-1$

    static {
	       NLS.initializeMessages(BUNDLE_NAME, RMBenchMessages.class);
	}
    
    public static String NewModel;
    
    public static String MessageConsole_name;
    public static String AlignSubmenu_ActionLabelText;
    
    public static String PrinterChooser_Title;
    public static String OpenModelDialog_title;
    public static String OpenModelDialog_message;
    
    public static String TableDetailsView_title;
    public static String DBTableView_title;
    
    public static String ModelView_Open_Text;
    public static String ModelView_Open_Description;
    public static String ModelView_SCHEMA_GROUP;
    public static String ModelView_DIAGRAM_GROUP;
    public static String ModelView_TABLES_GROUP;
    public static String ModelView_VIEWS_GROUP;
    public static String ModelView_SEQUENCES_GROUP;
    public static String ModelView_NewDiagram_Text;
    public static String ModelView_NewDiagram_Description;
    public static String ModelView_OpenDiagram_Text;
    public static String ModelView_OpenDiagram_Description;
    public static String ModelView_Delete_Text;
    public static String ModelView_Delete_Description;
    public static String ModelView_New_Text;
    public static String ModelView_New_Description;
    public static String ModelView_NewSchema_Text;
    public static String ModelView_NewSchema_Description;
    public static String ModelView_NewSchemaDlg_Title;
    public static String ModelView_NewSchemaDlg_Msg;
    public static String ModelView_SetTargetSchema;
    public static String ModelView_SaveDlg_Title;
    public static String ModelView_SaveDlg_Message;
    public static String ModelView_SaveError;
    public static String ModelView_SaveChanged_Message;
    public static String ModelView_Properties_Text;
    public static String ModelView_Properties_Description;
    public static String ModelView_NewTable_Text;
    public static String ModelView_NewTable_Description;
    public static String ModelView_DBExportModel_Text;
    public static String ModelView_DBExportModel_Description;
    public static String ModelView_ExportDialog_Title;
    public static String ModelView_ExportDialog_NoGenerator;
    public static String ModelView_EMPTY_MODEL_INFO;
    public static String ModelView_Rename_Text;
    public static String ModelView_Copy_Text;
	public static String ModelView_FindTableDiagram_Text;
	public static String ModelView_FindTableDiagram_Description;
	public static String ModelView_FindTableDiagram_none;
	public static String ModelView_FindTableDiagram_diagrams;
	public static String ModelView_FindTableDiagram_chooseDiagram;
	public static String ModelView_TableNewDiagram_Text;
	public static String ModelView_TableNewDiagram_Description;
    
    public static String ImportView_NO_MODEL_TITLE;
    public static String ImportView_NO_MODEL_MESSAGE;
	public static String ImportView_TablesNode;
	public static String ImportView_ViewsNode;
    public static String ImportView_Add_Text;
    public static String ImportView_Add_Description;
    public static String ImportView_Edit_Text;
    public static String ImportView_Edit_Description;
    public static String ImportView_Copy_Text;
    public static String ImportView_Copy_Description;
    public static String ImportView_Delete_Text;
    public static String ImportView_Delete_Description;
    public static String ImportView_Load_Text;
    public static String ImportView_Load_Description;
    public static String ImportView_Loading_Message;
    public static String ImportView_Import_Text;
    public static String ImportView_Import_Description;
	public static String ImportView_Copy_nameExt;
    
    public static String NameTab_Title;
    public static String NameTab_Description;
    public static String NameTab_FName;
    public static String NameTab_FSchema;
    public static String NameTab_FComment;
    public static String NameTab_FDescription;
    
    public static String ColumnsTab_Title;
    public static String ColumnsTab_Column_Name;
    public static String ColumnsTab_Column_DataType;
    public static String ColumnsTab_Column_Precision;
    public static String ColumnsTab_Column_Scale;
    public static String ColumnsTab_Column_Default;
    public static String ColumnsTab_Column_NotNull;
    public static String ColumnsTab_Column_Primkey;
    public static String ColumnsTab_Column_Comment;
    public static String ColumnsTab_Column_TypeID;
    public static String ColumnsTab_Column_PKIndex;
    public static String ColumnsTab_Description;
    public static String ColumnsTab_AddColumn_Text;
    public static String ColumnsTab_AddColumn_Description;
    public static String ColumnsTab_DeleteColumn_Text;
    public static String ColumnsTab_DeleteColumn_Description;
    public static String ColumnsTab_Err_Numeric;
    public static String ColumnsTab_Err_DuplicateName;
    public static String ColumnsTab_EditColumn_Text;
    public static String ColumnsTab_EditColumn_Description;
    public static String ColumnsTab_Err_PrecisionRequired;
    public static String ColumnsTab_Err_ScaleRequired;
    public static String ColumnsTab_FKMsgMessage;
    public static String ColumnsTab_FKMsgDialogTitle;
    public static String ColumnsTab_KeyMsgToggleText;
    public static String ColumnsTab_PKMsgMessage;
    public static String ColumnsTab_PKMsgDialogTitle;
    public static String ColumnsTab_PK_Deletion_DialogTitle;
    public static String ColumnsTab_PK_Deletion_Message;
    public static String ColumnsTab_NullableInfo_Title;
    public static String ColumnsTab_NullableInfo_Message;
    public static String ColumnsTab_Column_DatatypeDialog;

    public static String PrimaryKeyTab_Title;
    public static String PrimaryKeyTab_Description;
    public static String PrimaryKeyTab_Column_Name;
    public static String PrimaryKeyTab_Column_Index;
    public static String PrimaryKeyTab_Up_Text;
    public static String PrimaryKeyTab_Up_Description;
    public static String PrimaryKeyTab_Down_Text;
    public static String PrimaryKeyTab_Down_Description;

    public static String ForeignkeysTab_Title;
    public static String ForeignkeysTab_Description;
    public static String ForeignkeysTab_Column_Key;
    public static String ForeignkeysTab_Column_Column;
    public static String ForeignkeysTab_Column_Target;
    public static String ForeignKeysTab_Delete_Text;
    public static String ForeignkeysTab_Column_DeleteRule;
    public static String ForeignkeysTab_Column_UpdateRule;
    public static String ForeignKeysTab_Err_DuplicateName;
    public static String ForeignKeysTab_Err_InvalidName;
    public static String ForeignKeysTab_CreateAction_Text;

    public static String ReferencesTab_Title;
    public static String ReferencesTab_Description;
    public static String ReferencesTab_Column_Table;
    public static String ReferencesTab_Column_Key;
    public static String ReferencesTab_Column_Column;
    public static String ReferencesTab_Column_Target;
    
    public static String IndexesTab_Title;
    public static String IndexesTab_Description;
    public static String IndexesTab_Column_Key;
    public static String IndexesTab_Column_Unique;
    public static String IndexesTab_Column_Order;
    public static String IndexesTab_Column_Column;
    public static String IndexesTab_EditIndex_Text;
    public static String IndexesTab_EditIndex_Description;
    public static String IndexesTab_AddIndex_Text;
    public static String IndexesTab_AddIndex_Description;
    public static String IndexesTab_DeleteIndex_Text;
    public static String IndexesTab_DeleteIndex_Description;
    
    public static String ConstraintsTab_Column_Name;
    public static String ConstraintsTab_Column_Type;
    public static String ConstraintsTab_Column_Definition;
    public static String ConstraintsTab_Title;
    public static String ConstraintsTab_Description;
    public static String ConstraintsTab_Add_Text;
    public static String ConstraintsTab_Add_Description;
    public static String ConstraintsTab_Delete_Text;
    public static String ConstraintsTab_Delete_Description;
    public static String ConstraintsTab_Edit_Text;
    public static String ConstraintsTab_Edit_Description;
    
    public static String DiagramEditor_tooltip;
    public static String DiagramEditor_schemaelements;
    public static String DiagramEditor_connection;
    public static String DiagramEditor_connection_desc;
    public static String DiagramEditor_table;
    public static String DiagramEditor_table_desc;
    public static String DiagramEditor_index;
    public static String DiagramEditor_index_desc;
    public static String DiagramEditor_partName;
    public static String DiagramEditor_constraint;
    public static String DiagramEditor_constraint_desc;

    public static String DBModel_connectToDBMS;
    public static String DBModel_enterPassword;
    public static String DBModel_errorsLoading;
    
    public static String DynamicURLSetupGroup_title;
	public static String DynamicURLSetupGroup_addParams;
    public static String SimpleURLSetupControl_label;
	public static String SimpleURLSetupControl_invalidURL;
    
    public static String NewDiagram_Default;
    
    public static String SchemaPropertySource_catalog;
    public static String SchemaPropertySource_name;
    public static String DiagramPropertySource_DefaultSchema;
    public static String DiagramPropertySource_Name;
    
    public static String ModelPropertySource_Database;
    public static String ModelPropertySource_NameGenerator;

    public static String DriverInfo_generic_Jdbc;

    public static String License_Title;
    public static String License_FeatureNotAvailable;
    public static String License_MaxTables;
    public static String License_MaxDiagrams;
    
    public static String Export_noExportAvailable;
}
