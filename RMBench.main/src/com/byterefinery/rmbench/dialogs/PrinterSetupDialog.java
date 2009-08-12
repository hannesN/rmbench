/*
 * created 18.02.2006
 *
 * Copyright 2006, DynaBEAN Consulting
 *
 * $Id: PrinterSetupDialog.java 329 2006-04-09 09:58:14Z cse $
 */
package com.byterefinery.rmbench.dialogs;

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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.byterefinery.rmbench.RMBenchPlugin;

public class PrinterSetupDialog extends Dialog {
    
    private ComboViewer printerCombo;
    private Shell parentShell;
    
    private PrinterData selectedPrinter;
    private Text marginText;
    private double margin;

    public PrinterSetupDialog(Shell parentShell) {
        super(parentShell);

        this.parentShell = parentShell;
        this.selectedPrinter = RMBenchPlugin.getPrintState().printer;
        this.margin = RMBenchPlugin.getPrintState().margin;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(Messages.PrinterSetupDialog_Title);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea(Composite parent) {
        Composite mainComposite = (Composite)super.createDialogArea(parent);
        
        // main group
        final Group printersGroup = new Group(mainComposite, SWT.NONE);
        printersGroup.setLayout(new GridLayout(3, false));
        printersGroup.setText(Messages.PrinterSetupDialog_OutlineGroup);
        // Label "Name:" 
        final Label nameLabel = new Label(printersGroup, SWT.NONE);
        nameLabel.setText(Messages.PrintDialog_Name);
        // PrinterCombo
        printerCombo = new ComboViewer(printersGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
        printerCombo.getControl().setLayoutData(new GridData(SWT.BEGINNING, SWT.NONE, true, false, 2, 1));
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
                updateOKButton();
            }
        });                     
        
        // Label "margin"
        final Label marginLabel = new Label(printersGroup, SWT.NONE);
        marginLabel.setText(Messages.PrintDialog_Margin);
        // Text "margin"
        marginText = new Text(printersGroup, SWT.BORDER | SWT.SINGLE);    
        GridData gd = new GridData();
        gd.widthHint = convertWidthInCharsToPixels(5);
        marginText.setLayoutData(gd);
        marginText.setTextLimit(5);        
        marginText.setText(new Double(margin).toString());
        marginText.setToolTipText(Messages.PrintDialog_marginTextTT);        
        marginText.addVerifyListener(new VerifyListener() {
            public void verifyText(VerifyEvent e) {
                e.doit = Character.isISOControl(e.character) || Character.isDigit(e.character)  || (e.character == '.');
            }
        });
        marginText.addModifyListener(new ModifyListener(){

            public void modifyText(ModifyEvent e) {
                if(marginText.getText().length() > 0){
                    try{
                        margin = new Double(marginText.getText()).doubleValue();
                    }catch(NumberFormatException nfe){
                        margin = 0;
                    }
                }
                else
                    margin = 0;
            }
            
        });
        
        // Label "margin unit"
        final Label marginUnit = new Label(printersGroup, SWT.NONE);
        marginUnit.setText(Messages.PrintDialog_MarginUnit);
        
        return mainComposite;
    }
    
    private void updateOKButton() {
        getButton(IDialogConstants.OK_ID).setEnabled(selectedPrinter != null);        
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    protected void okPressed() {
        if(selectedPrinter != null){
            
            // check margin value
            if(!validMargin((int)(margin * Display.getCurrent().getDPI().x))){
                String[] buttons = {"Ok"};
                MessageDialog mDialog = new MessageDialog(parentShell, "Error", null, Messages.PrintDialog_marginValueError, MessageDialog.ERROR, buttons, 0);
                mDialog.open();
                marginText.setFocus();
                return;
            }
            
            RMBenchPlugin.getPrintState().margin = margin;
            RMBenchPlugin.getPrintState().printer = selectedPrinter;
        }
                
        super.okPressed();        
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
}
