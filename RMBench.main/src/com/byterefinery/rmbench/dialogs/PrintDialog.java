/*
 * created 02.08.2005 by cse
 *
 * $Id: PrintDialog.java 666 2007-10-01 19:32:51Z cse $
 */
package com.byterefinery.rmbench.dialogs;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PrintFigureOperation;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.operations.RMBenchPrintOperation;
import com.byterefinery.rmbench.util.PrintState;

/**
 * a dialog that allows to choose the printer and to configure print options
 * 
 * @author cse
 */
public class PrintDialog extends Dialog {

	private static class PrintMode {
		final String name;
		final int value;
		
		PrintMode(String name, int value) {
			this.name = name;
			this.value = value;
		}
	}
	private static final PrintMode[] PRINT_MODES = new PrintMode[] {
		new PrintMode(Messages.PrintMode_tile, PrintFigureOperation.TILE),
		new PrintMode(Messages.PrintMode_fitPage, PrintFigureOperation.FIT_PAGE),
		new PrintMode(Messages.PrintMode_fitWidth, PrintFigureOperation.FIT_WIDTH),
		new PrintMode(Messages.PrintMode_fitHeight, PrintFigureOperation.FIT_HEIGHT)
	};
	    
    private Shell parentShell;
	private ComboViewer printerCombo;
    private Label pagesNumberLabel;
    private Button allButton;
    private Button pagesButton;
    private Text pagesText;
    private Spinner copiesSpinner;
    private Button checkCollate;
    private Text marginText;
    private ComboViewer modeCombo;
    
    private boolean doPrint;
	     
    private Dimension maxDim = new Dimension();
    private IFigure printableLayer;
	private PrinterData selectedPrinter, result;
    private int[] pages = new int[0];
	private PrintMode selectedPrintMode = PRINT_MODES[0];
    /** Dialog intern print margin value in display resolution. (DPI)*/
	private int printMargin = 0;
	
    /**
     * create a new dialog
     * 
     * @param parentShell
     * @param doPrint whether the OK button should be labeled for printing
     */
	public PrintDialog(Shell shell, IFigure layer, boolean doPrint) {
        super(shell);
        this.parentShell = shell;
        this.doPrint = doPrint;     
        this.printableLayer = layer;
        
        // calculate print area
        RMBenchPrintOperation.calcChildrensArea(layer, this.maxDim);
        
        // load printer state 
        PrintState printState = RMBenchPlugin.getPrintState();
        selectedPrinter = printState.printer;
        // PRINT MODE
        if(this.doPrint){
            for(int i = 0;i < PRINT_MODES.length;i++){
                if(PRINT_MODES[i].value == printState.mode){
                    selectedPrintMode = PRINT_MODES[i];
                    break;
                }
            }
        }
        // calculate print margin value in display pixels        
        printMargin = (int)(printState.margin * Display.getCurrent().getDPI().x);
	}

    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(Messages.PrintDialog_Title);
    }
    
    protected Control createDialogArea(Composite parent) {
        Composite mainComposite = (Composite) super.createDialogArea(parent);        
        
        GridData gd;
        
        /*****************************************************************
         * Printer Group                                                                                                        *
         *****************************************************************/
        final Group printerGroup = new Group(mainComposite, SWT.NONE);
        printerGroup.setLayout(new GridLayout(3, false));
        printerGroup.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false, false));
        printerGroup.setText(Messages.PrintDialog_PrinterGroup);
        // Label "Name:" 
        final Label nameLabel = new Label(printerGroup, SWT.NONE);
        nameLabel.setText(Messages.PrintDialog_Name);
        nameLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        // Printer Combobox
        printerCombo = new ComboViewer(printerGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
        printerCombo.getCombo().setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
        printerCombo.setContentProvider(new ArrayContentProvider());
        printerCombo.setLabelProvider(new LabelProvider() {
			public String getText(Object element) {
				PrinterData printerData = (PrinterData)element;
				return printerData.name;
			}
        });
        initializePrinterData();
        printerCombo.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = (IStructuredSelection)event.getSelection();
                selectedPrinter = (PrinterData)selection.getFirstElement();
                updateDialogWidgets();
                updateOKButton();
            }
        });     
        // Button "Details"
        final Button detailsButton = new Button(printerGroup, SWT.PUSH);
        detailsButton.setText(Messages.PrintDialog_Details);
        detailsButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				org.eclipse.swt.printing.PrintDialog dialog = 
					new org.eclipse.swt.printing.PrintDialog(getShell());
				PrinterData data = dialog.open();
				if(data != null) {
                    //new printers might have been added, so initialize all new
                    initializePrinterData(data);
				}
			}
        });
        
        
        /*****************************************************************
         * Print Range Group                                                                                                *
         *****************************************************************/
        final Group printRangeGroup = new Group(mainComposite,SWT.NONE);
        printRangeGroup.setLayout(new GridLayout(2,false));
        printRangeGroup.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false, false));
        printRangeGroup.setText(Messages.PrintDialog_PrintRangeGroup);        
        // Radio Button "All"
        allButton = new Button(printRangeGroup, SWT.RADIO);
        allButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.NONE, false, false, 1,1));
        allButton.setText(Messages.PrintDialog_rangeAll);
        // Label "Number of Pages"
        pagesNumberLabel = new Label(printRangeGroup, SWT.NONE);              
        // Radio Button "Pages"
        pagesButton = new Button(printRangeGroup, SWT.RADIO);
        pagesButton.setText(Messages.PrintDialog_rangePages);
        // Text "Pages"
        pagesText = new Text(printRangeGroup, SWT.BORDER | SWT.SINGLE);
        gd = new GridData();
        gd.widthHint = convertWidthInCharsToPixels(15);
        pagesText.setLayoutData(gd);
        pagesText.setToolTipText(Messages.PrintDialog_scopeTextTT);
        pagesText.addModifyListener(new ModifyListener(){

            public void modifyText(ModifyEvent e) { 
                if(pagesText.getText().length() < 1){
                    pagesButton.setSelection(false);
                    allButton.setSelection(true); 
                }
                else
                    if(!pagesButton.getSelection()){
                        pagesButton.setSelection(true);
                        allButton.setSelection(false);
                    }                    
            }
        });
        pagesText.addFocusListener(new FocusListener(){

            public void focusGained(FocusEvent e) {
                if(pagesText.getText().length() > 0){
                    allButton.setSelection(false);
                    pagesButton.setSelection(true);
                }                
            }

            public void focusLost(FocusEvent e) {
                // nothing to do
            }
            
        });
        pagesButton.addSelectionListener(new SelectionListener(){

            public void widgetSelected(SelectionEvent e) {
                allButton.setSelection(false);
                pagesButton.setSelection(true);
                pagesText.setFocus();     
            }

            public void widgetDefaultSelected(SelectionEvent e) {
                // do nothing
            }
        });
        allButton.addSelectionListener(new SelectionListener(){

            public void widgetSelected(SelectionEvent e) {
                pagesButton.setSelection(false);
                allButton.setSelection(true);
                allButton.setFocus();
            }

            public void widgetDefaultSelected(SelectionEvent e) {
                // do nothing
            }
        });
        // empty spacer label            
        new Label(printRangeGroup,SWT.NONE);
        // hint text for pages format
        final Label pagesHintLabel = new Label(printRangeGroup, SWT.NONE);
        pagesHintLabel.setText(Messages.PrintDialog_pagesHintText);
        
        /*****************************************************************
         * Page Handling Group                                                                                            *
         *****************************************************************/
        final Group pageHandlingGroup = new Group(mainComposite, SWT.NONE);
        pageHandlingGroup.setLayout(new GridLayout(3, false));
        pageHandlingGroup.setLayoutData(new GridData(SWT.FILL, SWT.NONE,false, false));
        pageHandlingGroup.setText(Messages.PrintDialog_HandlingGroup);
        // Label "Copies"
        final Label copiesLabel = new Label(pageHandlingGroup, SWT.NONE);
        copiesLabel.setText(Messages.PrintDialog_copiesName);
        // Spinner "Copies"
        copiesSpinner = new Spinner(pageHandlingGroup, SWT.BORDER);
        copiesSpinner.setMinimum(1);
        copiesSpinner.setMaximum(99);
        copiesSpinner.setToolTipText(Messages.PrintDialog_copiesSpinnerTT);
        // Checkbutton "collate"        
        checkCollate = new Button(pageHandlingGroup, SWT.CHECK);
        checkCollate.setLayoutData(new GridData(SWT.CENTER, SWT.NONE, true, false));
        checkCollate.setText(Messages.PrintDialog_collate);
        updateCollateButton(copiesSpinner, checkCollate);
        // register a ModifyListener for the spinner to set control state 
        copiesSpinner.addModifyListener(new ModifyListener(){
            
            public void modifyText(ModifyEvent e) { 
                updateCollateButton(copiesSpinner, checkCollate);
            }
            
        });
        // Label "Margin:"
        final Label marginLabel = new Label(pageHandlingGroup, SWT.NONE);
        marginLabel.setText(Messages.PrintDialog_Margin);
        marginLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        // Composite margin text and unit label
        final Composite marginComp = new Composite(pageHandlingGroup, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.marginWidth = 0;
        marginComp.setLayout(layout);
        marginComp.setLayoutData(new GridData(SWT.BEGINNING | SWT.FILL,  SWT.NONE, false, false, 2, 1));
        // Text "Margin"
        marginText = new Text(marginComp, SWT.BORDER | SWT.SINGLE);     
        gd = new GridData(SWT.LEFT, SWT.NONE, false, false);
        gd.widthHint = convertWidthInCharsToPixels(5);
        marginText.setLayoutData(gd);
        marginText.setTextLimit(5);
        Double margin = new Double(printMargin / (double)Display.getCurrent().getDPI().x);
        marginText.setText(margin.toString());
        marginText.setToolTipText(Messages.PrintDialog_marginTextTT);        
        marginText.addVerifyListener(new VerifyListener() {
			public void verifyText(VerifyEvent e) {
				e.doit = Character.isISOControl(e.character) || Character.isDigit(e.character) || (e.character == '.');
			}
        });
        marginText.addModifyListener(new ModifyListener() {            
			public void modifyText(ModifyEvent e) {               
                String text = marginText.getText();
                try{
                    if(text.length() > 0)
                        printMargin = (int)(Double.parseDouble(text) * Display.getCurrent().getDPI().x);
                    // empty field
                    else
                        printMargin = 0;
                    updatePagesNumberLabel(selectedPrinter);
                }catch(Exception ex){
                    printMargin = 0;  
                }
			}            
        });
        marginText.addFocusListener(new FocusListener(){

            public void focusGained(FocusEvent e) {
                // nothing to do
            }

            public void focusLost(FocusEvent e) {
                // if field ist empty --> set to default "0"
                if(marginText.getText().length() < 1)
                    marginText.setText("0");
            }
            
        }) ;
        // Label "margin unit"
        final Label marginUnit = new Label(marginComp, SWT.NONE);
        marginUnit.setText(Messages.PrintDialog_MarginUnit);
        // Label "Mode"
        final Label modeLabel = new Label(pageHandlingGroup, SWT.NONE);
        modeLabel.setText("Mode:");
        modeLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        // Combobox "Mode"
        modeCombo = new ComboViewer(pageHandlingGroup, SWT.DROP_DOWN | SWT.READ_ONLY);        
        modeCombo.setContentProvider(new ArrayContentProvider());
        modeCombo.getCombo().setLayoutData(new GridData(SWT.BEGINNING, SWT.NONE, false, false, 2 ,1));
        modeCombo.setLabelProvider(new LabelProvider() {
			public String getText(Object element) {
				PrintMode mode = (PrintMode)element;
				return mode.name;
			}
        });
        modeCombo.setInput(PRINT_MODES);
        modeCombo.setSelection(new StructuredSelection(selectedPrintMode));
        modeCombo.getCombo().setToolTipText(Messages.PrintDialog_modeComboTT);
        modeCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection)event.getSelection();
				selectedPrintMode = (PrintMode)selection.getFirstElement();
                if(selectedPrinter != null)
                    updatePagesNumberLabel(selectedPrinter);
			}
        });
        
        updateDialogWidgets();
               
        return mainComposite;
    }

    private void initializePrinterData() {
        PrinterData[] printers = Printer.getPrinterList();
        
        if(printers.length > 0) {
            if(selectedPrinter == null)
                selectedPrinter = Printer.getDefaultPrinterData();
        
            boolean found = false;
            for (int i = 0; i < printers.length; i++) {
                if(printers[i].name.equals(selectedPrinter.name)){
                    found = true;
                    printers[i] = selectedPrinter;
                    break;
                }
            }
            //formerly selected printer not found in list --> fall back to default printer 
            if(!found){
                selectedPrinter = Printer.getDefaultPrinterData();
                for(int i = 0;i < printers.length; i++){
                    if(printers[i].name.equals(selectedPrinter.name)){
                        printers[i] = selectedPrinter;
                        break;
                    }
                }
            }
            printerCombo.setInput(printers);
            printerCombo.setSelection(new StructuredSelection(selectedPrinter));
        }else{
            selectedPrinter = null;
            printerCombo.setInput(printers);
        }
            
    }
    
    private void initializePrinterData(PrinterData selected) {
        PrinterData[] printers = Printer.getPrinterList();
        
        selectedPrinter  = selected;
        for (int i = 0; i < printers.length; i++) {
            if(printers[i].name.equals(selected.name)) {
                printers[i] = selected;
                break;
            }
        }
        printerCombo.setInput(printers);
        printerCombo.setSelection(new StructuredSelection(selected));       
    }

    protected void updateDialogWidgets() {
		if(selectedPrinter != null) {
            
		    //number of pages
            updatePagesNumberLabel(selectedPrinter);
            
            // TODO Implement reload of page scope values
            allButton.setSelection(true);
            pagesButton.setSelection(false);
            pagesText.setText("");
            
            // copies
           copiesSpinner.setSelection(selectedPrinter.copyCount);
           // collate
           checkCollate.setSelection(selectedPrinter.collate);           
		}
        // no printer selected
		else {
            allButton.setSelection(true);
            pagesButton.setSelection(false);
            pagesText.setText("");
            copiesSpinner.setSelection(1);
            checkCollate.setSelection(true);
		}
        double marginVal = printMargin / (double)Display.getCurrent().getDPI().x;
        marginText.setText(new Double(marginVal).toString());
        modeCombo.setSelection(new StructuredSelection(selectedPrintMode));        
	}
    
    private void updatePagesNumberLabel(PrinterData pData){        
        Object[] params = new Object[1];
        params[0] = new Integer(getNumberOfPages(pData));
        pagesNumberLabel.setText(MessageFormat.format(Messages.PrintDialog_numberOfPages, params));        
    }

    protected void createButtonsForButtonBar(Composite parent) {
        String okLabel = doPrint ? 
                Messages.PrintDialog_printButton : IDialogConstants.OK_LABEL;
        Button okButton = createButton(parent, IDialogConstants.OK_ID, okLabel, true);
        okButton.setEnabled(selectedPrinter != null);
        
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }
    
    private void updateOKButton() {
        getButton(IDialogConstants.OK_ID).setEnabled(selectedPrinter != null);
    }
    
    private void updateCollateButton(Spinner spinner, Button collateButton){
        collateButton.setEnabled(spinner.getSelection() < 2 ? false : true);
    }
    
    private boolean validMargin(int margin){
        if(selectedPrinter != null){
            Printer printer = new Printer(selectedPrinter);
            Rectangle rect =  printer.getClientArea();
            //scale to Display resolution
            double dpiScale = Display.getCurrent().getDPI().x / (double) printer.getDPI().x;
            rect.width *= dpiScale;
            rect.height *= dpiScale;
            // get shorter page side
            int val = rect.width <= rect.height ? rect.width : rect.height;
            // margin should be shorter than the half of the shortest page side
            if(margin < val/2)
                return true;
        }
        return false;
    }
    
    protected void okPressed() {
        
        //save state
        if(selectedPrinter != null){
            
            List<Integer> pageList = new ArrayList<Integer>();
            int maxPages = getNumberOfPages(selectedPrinter);
            
            if(allButton.getSelection()){
                selectedPrinter.scope = PrinterData.ALL_PAGES;
                // save this to prevend for-loop from recalculating number of pages every round
                int nop = getNumberOfPages(selectedPrinter);
                for(int i = 1;i <= nop; i++)
                    pageList.add(new Integer(i));
            }
            if(pagesButton.getSelection()){
                String[] buttons = {"Ok"};
                
                String[] scopes = pagesText.getText().split(",");
                for(int i=0; i<scopes.length; i++){
                    scopes[i] = scopes[i].trim();
                    if(scopes[i].matches("\\d+ *- *\\d+") || scopes[i].matches("\\d+")){
                        String[] values = scopes[i].split("-");
                        try{
                            if(values.length > 1){                               
                                int start = Integer.parseInt(values[0].trim());
                                int end =  Integer.parseInt(values[1].trim());
                                if((start <= end) && (end <= maxPages))
                                    for(int j = start; j<=end; j++){
                                        Integer val = new Integer(j); 
                                        if(!pageList.contains(val))
                                            pageList.add(val);
                                    }
                                else
                                    throw new NumberFormatException();
                            }
                            // values.length == 1
                            else{
                                Integer val = new Integer(values[0]);
                                if(val.intValue() > maxPages)
                                    throw new NumberFormatException();
                                else
                                    if(!pageList.contains(val))
                                        pageList.add(val);
                            }
                        
                        }catch(NumberFormatException e){
                            MessageDialog mDialog = new MessageDialog(parentShell, "Error", null, Messages.PrintDialog_scopeValueError, MessageDialog.ERROR, buttons, 0);
                            mDialog.open();
                            pagesText.setFocus();
                            return;
                        }
                    }                    
                    else{
                        MessageDialog mDialog = new MessageDialog(parentShell, "Error", null, Messages.PrintDialog_scopeFormatError, MessageDialog.ERROR, buttons, 0);
                        mDialog.open();
                        pagesText.setFocus();
                        return;
                    }
                }                
            }
            // check valid printer margin value
            if(!validMargin(printMargin)){
                String[] buttons = {"Ok"};
                MessageDialog mDialog = new MessageDialog(parentShell, "Error", null, Messages.PrintDialog_marginValueError, MessageDialog.ERROR, buttons, 0);
                mDialog.open();
                marginText.setFocus();
                return;    
            }

            selectedPrinter.copyCount = copiesSpinner.getSelection();
            selectedPrinter.collate = checkCollate.getSelection();
            
            pages = new int[pageList.size()];
            for (int i = 0; i < pages.length; i++) {
                pages[i] = ((Integer)pageList.get(i)).intValue();
            }
            PrintState printState = RMBenchPlugin.getPrintState();
            printState.printer = selectedPrinter;
            printState.margin = printMargin / (double) Display.getCurrent().getDPI().x;
            printState.mode = selectedPrintMode.value;
        }
        
    	result = selectedPrinter;
		super.okPressed();
	}
    
    private int getNumberOfPages(PrinterData pdata){

        // if printer margin not valid --> no pages would be printed
        if(!validMargin(printMargin))
            return 0;

        // get access to Printerinformations
        Printer printer = new Printer(pdata);
        // get DPI scale factor: printer / display
        double dpiScale = (double)printer.getDPI().x / Display.getCurrent().getDPI().x;
        // get printable area in printer
        Rectangle printerArea = printer.getClientArea();
        // get printable area in display scaling
        Dimension printingDim = new Dimension(
                (int)(printerArea.width / dpiScale) - 2*printMargin,
                (int)(printerArea.height / dpiScale) - 2*printMargin
                );
        
        // configure printable layer dimensions according to print mode
        double scaleFactor = 1;
        switch(selectedPrintMode.value){
        case PrintFigureOperation.FIT_HEIGHT:
            scaleFactor = (printingDim.height / (double)maxDim.height);
            break;
        case PrintFigureOperation.FIT_WIDTH:
            scaleFactor = (printingDim.width / (double)maxDim.width);
            break;
        case PrintFigureOperation.FIT_PAGE:
            scaleFactor = Math.min((printingDim.width / (double)maxDim.width),  (printingDim.height / (double) maxDim.height));
            break;
        default: scaleFactor = 1;
        }
        
        int numberOfPages = 0;
        int x = 0;
        int y = 0;
        while(y < maxDim.height * scaleFactor){
            while(x < maxDim.width * scaleFactor){
                org.eclipse.draw2d.geometry.Rectangle printArea = new org.eclipse.draw2d.geometry.Rectangle(new Point(x,y), printingDim);
                if(RMBenchPrintOperation.containsPrintableElements(printableLayer, printArea, scaleFactor))
                    numberOfPages++;
                x += printingDim.width;                
            }
            x = 0;
            y += printingDim.height;           
        }
        
        return numberOfPages;
        
    }

	/**
	 * @return the selected printer, or <code>null</code> if the dialog was cancelled
	 */
	public PrinterData getPrinter() {
		return result;
	}
	
	public int getPrintMode() {
		return selectedPrintMode.value;
	}
	
	public int getPrintMargin() {
		return printMargin;
	}
    
    public int[] getPages() {
        return pages;
    }
}
