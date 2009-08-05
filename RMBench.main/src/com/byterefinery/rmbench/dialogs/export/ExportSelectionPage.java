/*
 * created 15.05.2006
 * 
 * $Id$
 */
package com.byterefinery.rmbench.dialogs.export;

import org.eclipse.draw2d.IFigure;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.dialogs.Messages;
import com.byterefinery.rmbench.extension.ImageExporterExtension;
import com.byterefinery.rmbench.extension.ModelExporterExtension;
import com.byterefinery.rmbench.external.IExportable;
import com.byterefinery.rmbench.external.IImageExporter;
import com.byterefinery.rmbench.external.IModelExporter;

/**
 * page that lets you choose the export type
 * 
 * @author cse
 */
class ExportSelectionPage extends WizardPage {

    private static final ImageExporterExtension[] IMAGE_EXTS = RMBenchPlugin.getExtensionManager().getImageExporterExtensions();
    private static final ModelExporterExtension[] MODEL_EXTS = RMBenchPlugin.getExtensionManager().getModelExporterExtensions();
    
    private final IExportable.DiagramExport diagramExport;
    private final IExportable.ModelExport modelExport;
    
    private IImageExporter imageExporter;
    private ModelExporterExtension modelExporterExtension;
    private IModelExporter modelExporter;
    private IWizardPage[] modelPages;

    private Button diagramRadio;
    private Button modelRadio;
    
    private IFigure exportFigure;
    private ComboViewer imagesViewer;
    private Button selectedFigureButton;
    private ComboViewer modelsViewer;
    
    private boolean isImageExport;
    
    protected ExportSelectionPage(IExportable.DiagramExport diagramExport, IExportable.ModelExport modelExport) {
        super(ExportSelectionPage.class.getName());
        
        this.diagramExport = diagramExport;
        this.modelExport = modelExport;
        
        if(diagramExport == null && modelExport != null)
        	setTitle(Messages.ExportWizard_modelTitle);
        else if(diagramExport != null && modelExport == null)
        	setTitle(Messages.ExportWizard_diagramTitle);
        else
        	setTitle(Messages.ExportWizard_mainTitle);
        
        if((diagramExport == null || IMAGE_EXTS.length == 0) && (modelExport == null || MODEL_EXTS.length == 0))
            setDescription(Messages.ExportWizard_noSelection);
        else
            setDescription(Messages.ExportWizard_selectionDescription);
        
        isImageExport = IMAGE_EXTS.length > 0 && diagramExport != null;
    }

    public IImageExporter getImageExporter() {
        return imageExporter;
    }


    public IModelExporter getModelExporter() {
        if(modelExporter == null && modelExporterExtension != null)
            modelExporter = modelExporterExtension.getModelExporter(modelExport);
        return modelExporter;
    }

    public void createControl(Composite parent) {
        Composite control = new Composite(parent, SWT.NONE);
        control.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        control.setLayout(new GridLayout());
        
        //if presenting both export types, add radio buttons
        boolean selectExportTypes = diagramExport != null && modelExport != null;
        if(selectExportTypes) {
            diagramRadio = new Button(control, SWT.RADIO);
            diagramRadio.setText(Messages.ExportWizard_diagramExportRadio);
            GridData gd = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
            diagramRadio.setLayoutData(gd);
            diagramRadio.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    isImageExport = true;
                    imagesViewer.getCombo().setEnabled(true);
	                modelsViewer.getCombo().setEnabled(false);
                }
            });
        }
        //Diagram export
        if(diagramExport != null) {
        	createDiagramControls(control, selectExportTypes);
        }        
        if(selectExportTypes) {
	        modelRadio = new Button(control, SWT.RADIO);
	        modelRadio.setText(Messages.ExportWizard_modelExportRadio);
	        GridData gd = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
	        gd.verticalIndent = 20;
	        modelRadio.setLayoutData(gd);
	        modelRadio.addSelectionListener(new SelectionAdapter() {
	            public void widgetSelected(SelectionEvent e) {
	                isImageExport = false;
	                modelsViewer.getCombo().setEnabled(true);
                    imagesViewer.getCombo().setEnabled(false);
	            }
	        });
        }
        //Model export
        if(modelExport != null) {
        	createModelControls(control, selectExportTypes);
        } 
        if(selectExportTypes) {
	        boolean diagramEnabled = diagramExport != null && IMAGE_EXTS.length > 0;
	        diagramRadio.setEnabled(diagramEnabled);
	        imagesViewer.getCombo().setEnabled(diagramEnabled);
	        
	        boolean modelEnabled = MODEL_EXTS.length > 0 && modelExport != null;
	        modelRadio.setEnabled(modelEnabled);
	        modelsViewer.getCombo().setEnabled(modelEnabled);
	
	        if(diagramEnabled) {
	            diagramRadio.setSelection(true);
	            modelsViewer.getCombo().setEnabled(false);
	        }
	        else if(modelExport != null) {
	            modelRadio.setSelection(true);
	            imagesViewer.getCombo().setEnabled(false);
	        }
        }
        checkPageComplete();
        setControl(control);
    }

    private void checkPageComplete() {
        setPageComplete(imageExporter != null || modelExporterExtension != null);
	}

	/*
     * create controls for diagram export format
     */
    private void createDiagramControls(Composite control, boolean doIndent) {
        
        Composite imageGroup = new Composite(control, SWT.NONE);
        GridData gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
        gd.horizontalIndent = doIndent ? 20 : 0;
        imageGroup.setLayoutData(gd);
        GridLayout layout = new GridLayout(3, false);
        layout.horizontalSpacing = 10;
        imageGroup.setLayout(layout);
        
        Label label = new Label(imageGroup, SWT.NONE);
        label.setText(Messages.ExportWizard_format);
        
        imagesViewer = new ComboViewer(imageGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
        imagesViewer.setContentProvider(new ArrayContentProvider());
        imagesViewer.setLabelProvider(new LabelProvider() {
            public String getText(Object element) {
                ImageExporterExtension extension = (ImageExporterExtension)element;
                return extension.getDescription();
            }
        });
        imagesViewer.setInput(IMAGE_EXTS);
        imagesViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = (IStructuredSelection)event.getSelection();
                imageExporter = 
                    ((ImageExporterExtension)selection.getFirstElement()).getImageExporter();
                checkPageComplete();
            }
        });
        if(IMAGE_EXTS.length > 0) {
        	imageExporter = IMAGE_EXTS[0].getImageExporter();
            imagesViewer.setSelection(new StructuredSelection(IMAGE_EXTS[0]));
        }
        //let the user select if he wants to export the selected figure only
        selectedFigureButton = new Button(control, SWT.CHECK);
        gd = new GridData(SWT.FILL, SWT.BEGINNING, false, false);
        gd.horizontalIndent = 20;
        selectedFigureButton.setLayoutData(gd);
        selectedFigureButton.setText(Messages.ExportWizard_selectionOnly);
        selectedFigureButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                if(selectedFigureButton.getSelection())
                    exportFigure = diagramExport.getExportFigure();
                else
                    exportFigure = diagramExport.getExportDiagramFigure();
            }
        });
        //by default, we export the diagram as a whole
        if(diagramExport != null) {
            exportFigure = diagramExport.getExportDiagramFigure();
            if(exportFigure == null) {
                exportFigure = diagramExport.getExportFigure();
                selectedFigureButton.setEnabled(false);
            }
        }
        else
            selectedFigureButton.setEnabled(false);
    }

    /*
     * create controls for diagram export format
     */
    private void createModelControls(Composite control, boolean doIndent) {
        
        Composite modelGroup = new Composite(control, SWT.NONE);
        GridData gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
        gd.horizontalIndent = doIndent ? 20 : 0;
        modelGroup.setLayoutData(gd);
        GridLayout layout = new GridLayout(3, false);
        layout.horizontalSpacing = 10;
        modelGroup.setLayout(layout);
        
        Label label = new Label(modelGroup, SWT.NONE);
        label.setText(Messages.ExportWizard_format);
        
        modelsViewer = new ComboViewer(modelGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
        modelsViewer.setContentProvider(new ArrayContentProvider());
        modelsViewer.setLabelProvider(new LabelProvider() {
            public String getText(Object element) {
                return ((ModelExporterExtension)element).getDescription();
            }
        });
        
        modelsViewer.setInput(MODEL_EXTS);
        modelsViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = (IStructuredSelection)event.getSelection();
                modelExporterExtension = (ModelExporterExtension)selection.getFirstElement();
                modelPages = null;
                modelExporter = null;
                checkPageComplete();
            }
        });
        if(MODEL_EXTS.length > 0) {
        	modelExporterExtension = MODEL_EXTS[0];
            modelsViewer.setSelection(new StructuredSelection(modelExporterExtension));
        }
        else
        	modelsViewer.getControl().setEnabled(false);
    }

    /**
     * @return whether image export is chosen
     */
    public boolean isImageExport() {
        return isImageExport;
    }

    /**
     * @return the figure to export, if image export is chosen
     */
    public IFigure getExportFigure() {
        return exportFigure;
    }

    /**
     * @return the configuration pages for the currently selected model export, or null
     * if no model export is chosen or the model export does not define configuration pages
     */
    public IWizardPage[] getModelPages() {
        if(isImageExport || modelExporterExtension == null)
            return null;
        else {
            if(modelPages == null)
                modelPages = modelExporterExtension.createConfigPages(getModelExporter());
            return modelPages;
        }
    }
}
