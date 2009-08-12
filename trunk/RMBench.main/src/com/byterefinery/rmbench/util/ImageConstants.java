/*
 * created 08.04.2005
 * 
 * $Id:ImageConstants.java 2 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.util;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.byterefinery.rmbench.RMBenchConstants;
import com.byterefinery.rmbench.RMBenchPlugin;

/**
 * constants and utility methods for image handling
 * @author cse
 */
public class ImageConstants {

    public static final String PK_VIEW = "icons/pk_view.gif";
    public static final String COL_VIEW = "icons/col_view.gif";
    public static final String INDEX = "icons/index.gif";
    public static final String ADD = "icons/add.gif";
    public static final String ADD_disabled = "icons/add_da.gif";
    public static final String OUTLINE = "icons/outline.gif";
    public static final String BLANK = "icons/blank.gif";
    public static final String OVERVIEW = "icons/overview.gif";
    public static final String MODEL = "icons/modelview.gif";
    public static final String DIAGRAM = "icons/diagram.gif";
    public static final String DIAGRAM2 = "icons/diagram2.gif";
    public static final String SCHEMA = "icons/schema.gif";
    public static final String SCHEMA2 = "icons/schema2.gif";
    public static final String FOREIGN_KEY = "icons/foreignkey.gif";
    public static final String FK_IN = "icons/fk_in.gif";
    public static final String FK_OUT = "icons/fk_out.gif";
    public static final String TABLE = "icons/table.gif";
    public static final String TABLE2 = "icons/table2.gif";
    public static final String TABLE_DETAILS = "icons/table_details.gif";
    public static final String COLUMN = "icons/column.gif";
    public static final String LAYOUT_AUTO = "icons/layout_auto.gif";
    public static final String LAYOUT = "icons/layout.gif";
    public static final String KEY = "icons/key.gif";
    public static final String KEY2 = "icons/key2.gif";
    public static final String FK_OVR = "icons/fk_ovr.gif";
    public static final String CHECKED = "icons/checked.gif";
    public static final String UP = "icons/up.gif";
    public static final String DOWN = "icons/down.gif";
    public static final String EDIT = "icons/edit.gif";
    public static final String EDIT_disabled = "icons/edit_da.gif";
    public static final String COL_PKFK = "col_pk_fk";
    public static final String COL_FK = "col_fk";
    public static final String DISCONNECTED = "icons/disconnected.gif";
    public static final String DISCONNECT = "icons/disconnect.gif";
	public static final String CONNECTED = "icons/connected.gif";
    public static final String DBOBJ_FOLDER = "icons/dbfolder.gif";
    public static final String COLUMN2 = "icons/column2.gif";
	public static final String CONNECTION_WIZARD = "icons/connection_wiz.gif";
    public static final String VIEW_MIN = "icons/view_min.gif";
    public static final String VIEW_MAX = "icons/view_max.gif";
    public static final String LOADMETA = "icons/loadmeta.gif";
    public static final String EXTERNAL_JAR = "icons/external_jar.gif";
    public static final String REFRESH = "icons/refresh.gif";
    public static final String NEWMODEL = "icons/newmodel.gif";
    public static final String PROPERTIES = "icons/properties.gif";
    public static final String OPEN = "icons/open.gif";
    public static final String SAVE = "icons/save.gif";
    public static final String SAVE_DISABLED = "icons/save_da.gif";
    public static final String INFO = "icons/info.gif";
	public static final String REMOVE = "icons/remove.gif";
    public static final String MODEL_WIZARD = "icons/model_wiz.gif";
    public static final String CONSTRAINT = "icons/constraint.gif";
    public static final String CONSTRAINT_WIZARD = "icons/constraint_wizban.gif";
    public static final String DBEXPORT = "icons/dbexport.gif";
    public static final String SQL_FILE = "icons/sql.gif";
    public static final String EXECUTE = "icons/run_exc.gif";
    public static final String EXECUTE_CARET = "icons/run_caret.gif";
    public static final String DDLEXPORT_WIZARD = "icons/ddlexport_wiz.gif";
    public static final String DDL_EXEC = "icons/ddl_exec.gif";
    public static final String LEFT_ARROW = "icons/left_arrow.gif";
    public static final String RIGHT_ARROW= "icons/right_arrow.gif";
    public static final String LEFT_RIGHT_ARROW= "icons/left_right_arrow.gif";
    public static final String RMBENCH_INFO = "icons/RMBench_info.gif";
    public static final String EMPTY_CHECKBOX = "icons/empty_cb.gif";
    public static final String CHECKED_CHECKBOX = "icons/checked_cb.gif";
    public static final String CLOSE_FOLDER = "icons/closefolder.gif";
    public static final String COLUMNTTAB_BUTTON = "icons/button.gif";
    
    
    public static final String SCHEMA_FOLDER = DBOBJ_FOLDER;
    public static final String RELATIONSHIPS_FOLDER = FOREIGN_KEY;
    public static final String DIAGRAMS_FOLDER = DIAGRAM2;
    
    public static ImageDescriptor WIZARD_DESC;
    public static ImageDescriptor COPY_DESC;
    public static ImageDescriptor COPY_DISABLED_DESC;
    public static ImageDescriptor DELETE_DESC;
    public static ImageDescriptor DELETE_DISABLED_DESC;
    public static Image DELETE_IMG;
    public static Image DELETE_DISABLED_IMG;
    
    public static void initializeRegistry(ImageRegistry registry) {
        String[] all = new String[]{
                PK_VIEW,
                COL_VIEW,
                INDEX,
                ADD,
                ADD_disabled,
                VIEW_MIN,
                VIEW_MAX,
                FK_OUT,
                FK_IN,
                OUTLINE,
                BLANK,
                OVERVIEW,
                MODEL,
                SCHEMA,
                DIAGRAM,
                DIAGRAM2,
                FOREIGN_KEY,
                TABLE,
                TABLE_DETAILS,
                COLUMN,
                LAYOUT_AUTO,
                LAYOUT,
                KEY,
                KEY2,
                FK_OVR,
                CHECKED,
                UP,
                DOWN,
                EDIT,
                EDIT_disabled,
                DISCONNECTED,
                DISCONNECT,
                CONNECTED,
                SCHEMA2,
                DBOBJ_FOLDER,
                TABLE2,
                COLUMN2,
                LOADMETA,
                CONNECTION_WIZARD,
                EXTERNAL_JAR,
                REFRESH,
                PROPERTIES,
                NEWMODEL,
                DBEXPORT,
                SQL_FILE,
                EXECUTE,
                EXECUTE_CARET,
                OPEN,
                SAVE,
                SAVE_DISABLED,
                INFO,
                REMOVE,
                MODEL_WIZARD,
                CONSTRAINT,
                CONSTRAINT_WIZARD,
                DDLEXPORT_WIZARD,
                DDL_EXEC,
                LEFT_ARROW,
                RIGHT_ARROW,
                LEFT_RIGHT_ARROW,
                RMBENCH_INFO,
                EMPTY_CHECKBOX,
                CHECKED_CHECKBOX,
                CLOSE_FOLDER,
                COLUMNTTAB_BUTTON
        };
        for (int i = 0; i < all.length; i++) {
            ImageDescriptor desc = AbstractUIPlugin.imageDescriptorFromPlugin(
                    RMBenchConstants.PLUGIN_ID, all[i]);
            registry.put(all[i], desc);
        }
        ImageDescriptor desc = new OverlayedImageDescriptor(
                RMBenchPlugin.getImageDescriptor(ImageConstants.KEY2),
                RMBenchPlugin.getImageDescriptor(ImageConstants.FK_OVR));
        registry.put(COL_PKFK, desc);
        desc = new OverlayedImageDescriptor(
                RMBenchPlugin.getImageDescriptor(ImageConstants.FK_OVR));
        registry.put(COL_FK, desc);
        
        WIZARD_DESC = PlatformUI.getWorkbench().
            getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_NEW_WIZARD);
        COPY_DESC = PlatformUI.getWorkbench().
        	getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_COPY);
        COPY_DISABLED_DESC = PlatformUI.getWorkbench().
        	getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_COPY_DISABLED);
        DELETE_DESC = PlatformUI.getWorkbench().
            getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_DELETE);
        DELETE_DISABLED_DESC = PlatformUI.getWorkbench().
            getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_DELETE_DISABLED);
        DELETE_IMG = PlatformUI.getWorkbench().
            getSharedImages().getImage(ISharedImages.IMG_TOOL_DELETE);
        DELETE_DISABLED_IMG = PlatformUI.getWorkbench().
            getSharedImages().getImage(ISharedImages.IMG_TOOL_DELETE_DISABLED);
    }
}
