/*
 * created 02.08.2005 by sell
 *
 * $Id: Messages.java 657 2007-08-31 23:20:24Z cse $
 */
package com.byterefinery.rmbench.dialogs;

import org.eclipse.osgi.util.NLS;

/**
 * translatable strings for dialogs
 *  
 * @author sell
 */
public class Messages extends NLS {
	
    private static final String BUNDLE_NAME = "com.byterefinery.rmbench.dialogs.Messages"; //$NON-NLS-1$
    static {
	       NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String ConnectionDialog_connection_name;
	public static String ConnectionDialog_database_name;
	public static String ConnectionDialog_dialog_title;
	public static String PrintMode_tile;
	public static String PrintMode_fitPage;
	public static String PrintMode_fitWidth;
	public static String PrintMode_fitHeight;
	public static String PrintDialog_Title;
	public static String PrintDialog_PrinterGroup;
	public static String PrintDialog_Name;
	public static String PrintDialog_Details;
	public static String PrintDialog_Margin;
    public static String PrintDialog_MarginUnit;
    public static String PrintDialog_scopeAll;
    public static String PrintDialog_scopeSelection;
    public static String PrintDialog_scopeRange;
    public static String PrintDialog_scopeName;
    public static String PrintDialog_copiesName;
    public static String PrintDialog_printButton;
    public static String PrintDialog_PrintRangeGroup;
    public static String PrintDialog_rangeAll;
    public static String PrintDialog_rangePages;
    public static String PrintDialog_HandlingGroup;
    public static String PrintDialog_collate;
    public static String PrintDialog_numberOfPages;
    public static String PrintDialog_pagesHintText;
    public static String PrintDialog_scopeFormatError;
    public static String PrintDialog_scopeValueError;
    public static String PrintDialog_marginValueError;
    public static String PrintDialog_scopeTextTT;
    public static String PrintDialog_copiesSpinnerTT;
    public static String PrintDialog_marginTextTT;
    public static String PrintDialog_modeComboTT;
    
    public static String PrinterSetupDialog_Title;
    public static String PrinterSetupDialog_OutlineGroup;
    
    public static String ModelPropertiesDialog_Title;
    public static String ModelPropertiesDialog_Name;
    public static String ModelPropertiesDialog_Database;
    public static String ModelPropertiesDialog_NameGenerator;
    public static String ModelPropertiesDialog_ChangeModel_Title;
    public static String ModelPropertiesDialog_ChangeModel_Message;
    public static String ModelPropertiesDialog_Location;
    
    public static String IndexEditorDialog_Title;
    public static String IndexEditorDialog_Name;
    public static String IndexEditorDialog_Unique;
    public static String IndexEditorDialog_Column_Name;
    public static String IndexEditorDialog_Column_Order;
    public static String IndexEditorDialog_msg_invalidname;
    public static String IndexEditorDialog_msg_duplicatename;
    
    public static String TargetTableChooser_title;
    public static String TargetTableChooser_message;
    public static String TargetTableChooser_importCheck;
    
    public static String NewModelWizard1_Title;
    public static String NewModelWizard1_Description;
    public static String NewModelWizard1_ModelName;
    public static String NewModelWizard1_newModel_name;
    public static String NewModelWizard1_ErrModelName;
    public static String NewModelWizard2_Title;
    public static String NewModelWizard2_Description;
    public static String NewModelWizard2_ErrExtension;
    
    public static String JdbcConnectionWizard_New_Title;
    public static String JdbcConnectionWizard_Edit_Title;
    public static String JdbcConnectionWizardPage_new_title;
    public static String JdbcConnectionWizardPage_edit_title;
    public static String JdbcConnectionWizardPage1_description;
    public static String JdbcConnectionWizardPage1_name;
    public static String JdbcConnectionWizardPage1_JarFiles;
    public static String JdbcConnectionWizardPage1_ZipFiles;
    public static String JdbcConnectionWizardPage1_AllFiles;
    public static String JdbcConnectionWizardPage1_addExternalJar;
    public static String JdbcConnectionWizardPage1_removeJar;
    public static String JdbcConnectionWizardPage1_DriverType;
    public static String JdbcConnectionWizardPage1_driverClassName;
    public static String JdbcConnectionWizardPage1_DriverJars;
    public static String JdbcConnectionWizardPage2_description;
    public static String JdbcConnectionWizardPage2_prompt;
    public static String JdbcConnectionWizardPage2_password;
    public static String JdbcConnectionWizardPage2_repeat;
    public static String JdbcConnectionWizardPage2_userid;
    public static String JdbcConnectionWizardPage2_passwords_not_equal;
    public static String JdbcConnectionWizardPage3_description;
    public static String JdbcConnectionWizardPage3_schemaRule;
    public static String JdbcConnectionWizardPage3_allSchemas;
    public static String JdbcConnectionWizardPage3_selectedSchemas;
    public static String JdbcConnectionWizardPage3_selectSchemasTitle;
    public static String JdbcConnectionWizardPage3_passwordTitle;
    public static String JdbcConnectionWizardPage3_passwordMessage;
	public static String JdbcConnectionWizardPage4_description;
	public static String JdbcConnectionWizardPage4_indexes;
	public static String JdbcConnectionWizardPage4_keyindexes;
	public static String JdbcConnectionWizardPage4_comments;
	public static String JdbcParametersDialog_Title;
	public static String JdbcParametersDialog_Col_Name;
	public static String JdbcParametersDialog_Col_Value;
    
    public static String ForeignKeyConfigurator_title;
    public static String ForeignKeyConfigurator_Columns;
    public static String ForeignKeyConfigurator_TargetTable;
    public static String ForeignKeyConfigurator_Table_Schema;
    public static String ForeignKeyConfigurator_Table_Name;
    
    public static String ConstraintWizard_TypeTitle;
    public static String ConstraintWizard_TypeDescription;
    public static String ConstraintWizard_Type_Group;
    public static String ConstraintWizard_Type_CHECK;
    public static String ConstraintWizard_Type_UNIQUE;
    public static String ConstraintWizard_Type_PRIMARY;
    public static String ConstraintWizard_CheckTitle;
    public static String ConstraintWizard_CheckDescription;
    public static String ConstraintWizard_UniqueTitle;
    public static String ConstraintWizard_UniqueDescription;
    public static String ConstraintWizard_Name_Group;
    public static String ConstraintWizard_CheckGenerate;
    public static String ConstraintWizard_PrimaryTitle;
    public static String ConstraintWizard_PrimaryDescription;
    public static String ConstraintWizard_nameError;
    public static String ConstraintWizard_emptyNameError;

    public static String NewDiagramDialog_NewDiagramName;
    public static String NewDiagramDialog_Title;
    public static String NewDiagramDialog_Name;
    public static String NewDiagramDialog_TargetSchema;
    
    public static String NewDiagramWizard_NewDiagramName;
    public static String NewDiagramWizard_Title;
    public static String NewDiagramWizard_Name;
    public static String NewDiagramWizard_TargetSchema;
    public static String NewDiagramWizard_UseAvailableSchema;
    public static String NewDiagramWizard_CreateNewSchema;
    public static String NewDiagramWizard_Description;
    public static String NewDiagramWizard_TargetSchema_Desc;
    
    public static String DDLExportWizard_Title;
    public static String DDLExportWizard1_Description;
    public static String DDLExportWizard1_connection_label;
    public static String DDLExportWizard1_generator_label;
    public static String DDLExportWizard1_generateDrop_text;
    public static String DDLExportWizard1_generateDiff_label;
    public static String DDLExportWizard1_NoConnection;
    public static String DDLExportWizard2_Description;
    
    public static String FileResourceSelectionDialog_refreshTooltip;
    public static String DependencyDialog_defaultMessage;
    public static String DependencyDialog_optionalDialogActions;
    public static String DependencyDialog_dialogActionDelete;
    public static String DependencyDialog_dialogActionSetSchema;
    public static String DependencyDialog_title;
    public static String ColumnDependencyDialog_GroupName;
    public static String ColumnDependencyDialog_DeleteButton;
    public static String ColumnDependencyDialog_RemoveButton;
    public static String PrimaryKeyDependencyDialog_GroupTitle;
    public static String PrimaryKeyDependencyDialog_DeleteButton;
    public static String PrimaryKeyDependencyDialog_RemoveButton;
    public static String CheckConstraintEditorDialog_Title;
    public static String CheckConstraintEditorDialog_Name;
    public static String CheckConstraintEditorDialog_Expression;
    
    public static String ExportWizard_mainTitle;
    public static String ExportWizard_modelTitle;
    public static String ExportWizard_diagramTitle;
    public static String ExportWizard_fileDescription;
    public static String ExportWizard_selectionDescription;
    public static String ExportWizard_diagramExportRadio;
    public static String ExportWizard_modelExportRadio;
    public static String ExportWizard_format;
    public static String ExportWizard_selectionOnly;
    public static String ExportWizard_noSelection;
    public static String ExportWizard_outputDirectory;
    public static String ExportWizard_chooseDirectory;
    public static String ExportWizard_chooseDirDescription;
    
    public static String TableDependencyDialog_dependenciesOccurred;
    public static String TableDependencyDialog_actionRemoveForeignKey;
    public static String TableDependencyDialog_actionDeleteTable;
    public static String TableDependencyDialog_impliedActions;
    public static String TableDependencyDialog_actionRemoveTable;
    
    public static String JdbcConnectionWizardPage3_select_all_button_label;
    public static String JdbcConnectionWizardPage3_deselect_all_button_label;
    public static String JdbcConnectionWizardPage2_userid_required;
    public static String JdbcConnectionWizardPage2_password_required;
    public static String JdbcConnectionWizardPage3_loading_schemas;
    public static String JdbcConnectionWizardPage1_loading_drivers;

    public static String LicenseDetailsDialog_Title;
    public static String LicenseDetailsDialog_Col_Key;
    public static String LicenseDetailsDialog_Col_Value;
}
