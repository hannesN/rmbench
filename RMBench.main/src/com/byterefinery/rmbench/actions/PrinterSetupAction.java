package com.byterefinery.rmbench.actions;

import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.dialogs.PrinterSetupDialog;
import com.byterefinery.rmbench.figures.PageOutlineLayer;
import com.byterefinery.rmbench.util.PrintState;

/**
 * an action for configuring the printer that is used to print the current
 * diagram. Setting the printer will also possibly change the page layout grid
 * displayed in the current diagram.
 */
public class PrinterSetupAction extends Action {

	public static final String ID = "com.byterefinery.rmbench.PrinterSetupAction";
	
	private final PageOutlineLayer layer;
    private final Shell dialogParent;   
	
	/**
	 * @param dialogParent parent shell for printer chooser dialog
	 * @param layerManager for obtaining the PageOutlineLayer - usually a 
	 * FreeformGraphicalRootEditPart
	 */
	public PrinterSetupAction(Shell dialogParent, LayerManager layerManager) {
		super(Messages.PrinterSetupAction_Title, AS_PUSH_BUTTON);
        
        setEnabled(Printer.getPrinterList().length > 0);
		this.layer = (PageOutlineLayer)layerManager.getLayer(PageOutlineLayer.LAYER_ID);
        this.dialogParent = dialogParent;
        
        setId(ID);
	}

    public void run() {
		PrinterSetupDialog dialog = new PrinterSetupDialog(dialogParent);
		dialog.open();	
		
        PrintState printState = RMBenchPlugin.getPrintState(); 
		if(printState.printer != null) {
			Printer printer = new Printer(printState.printer);
            //int margin = dialog.getPrintMargin();
            int margin = (int)(printState.margin * Display.getCurrent().getDPI().x);
            layer.setPrinter(printer, printState.mode, new Insets(margin, margin, margin, margin));
			if(layer.isEnabled())
				layer.repaint();
		}
	}
}
