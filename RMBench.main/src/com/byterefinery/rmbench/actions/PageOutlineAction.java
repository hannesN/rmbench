/*
 * created 24.04.2005
 * 
 * $Id: PageOutlineAction.java 265 2006-02-25 21:13:28Z thomasp $
 */
package com.byterefinery.rmbench.actions;

import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.dialogs.PrinterSetupDialog;
import com.byterefinery.rmbench.figures.PageOutlineLayer;
import com.byterefinery.rmbench.util.PrintState;

/**
 * an action that toggles the display of a printer page outline
 *  
 * @author cse
 */
public class PageOutlineAction extends Action {

	public static final String ID = "com.byterefinery.rmbench.PageOutlineAction";
	
	private final PageOutlineLayer layer;
    private final PrinterData[] printers;
    private final Shell dialogParent;
	
	/**
	 * @param dialogParent parent shell for printer chooser dialog
	 * @param layerManager for obtaining the PageOutlineLayer - usually a 
	 * FreeformGraphicalRootEditPart
	 */
	public PageOutlineAction(Shell dialogParent, LayerManager layerManager) {
		super(Messages.PageOutlineAction_Title, AS_CHECK_BOX);
        
		this.layer = (PageOutlineLayer)layerManager.getLayer(PageOutlineLayer.LAYER_ID);
        this.layer.setLayerManager(layerManager);
        this.printers = Printer.getPrinterList();
        this.dialogParent = dialogParent;
        
        setId(ID);
	}

	public boolean isEnabled() {
        return printers.length > 0;
    }

    public void run() {
		
		if(layer.isEnabled()) {
			layer.setEnabled(false);
		}
		else {
			if(layer.isConfigured()) {
				layer.setEnabled(true);
			} else {
                PrinterSetupDialog dialog = new PrinterSetupDialog(dialogParent);
				if (dialog.open() == Window.OK) {
                    PrintState printState = RMBenchPlugin.getPrintState();
					Printer printer = new Printer(printState.printer);
                    //int margin = dialog.getPrintMargin();
                    int margin = (int)(printState.margin * Display.getCurrent().getDPI().x);
                    layer.setPrinter(printer, printState.mode, new Insets(margin, margin, margin, margin));                    
					layer.setEnabled(true);
				} else {
					setChecked(false);
				}
			}
		}
	}
}
