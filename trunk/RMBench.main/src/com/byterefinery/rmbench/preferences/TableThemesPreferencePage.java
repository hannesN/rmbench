/*
 * created 20.09.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: TableThemesPreferencePage.java 678 2008-02-17 22:52:09Z cse $
 */
package com.byterefinery.rmbench.preferences;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.extension.TableThemeExtension;
import com.byterefinery.rmbench.extension.TableTypeExtension;
import com.byterefinery.rmbench.util.ColorRegistry;

/**
 * preference page for editing the color settings for predefined table type themes
 * 
 * @author cse
 */
public class TableThemesPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    private static final RGB NULL_RGB = new RGB(0, 0, 0);
    private ColorRegistry colorRegistry = new ColorRegistry();
    
    private Button changeThemeButton;
    private Button deleteThemeButton;
    
    private TreeViewer themesViewer;
    
    private final Map<TableThemeExtension, ColorDefinition[]> colorDefinitionMap = 
    	new TreeMap<TableThemeExtension, ColorDefinition[]>(new Comparator<TableThemeExtension>() {
	        public int compare(TableThemeExtension o1, TableThemeExtension o2) {
	            return o1.getLabel().compareTo(o2.getLabel());
	        }
    });
    
    private ColorDefinition selectedColorDefinition;

    
    protected Control createContents(Composite parent) {
        
        Composite mainComposite = new Composite(parent, SWT.NULL);
        mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout layout = new GridLayout();
        layout.verticalSpacing = 10;
        mainComposite.setLayout(layout);

        createTypeThemesGroup(mainComposite);

        return mainComposite;
    }

    public void init(IWorkbench workbench) {
        setPreferenceStore(RMBenchPlugin.getDefault().getPreferenceStore());
    }

    protected void performDefaults() {
        for (ColorDefinition[] colorDefinitions : colorDefinitionMap.values()) {
            for (int i = 0; i < colorDefinitions.length; i++) {
                colorDefinitions[i].setRGB(PreferenceHandler.getDefaultRGB(colorDefinitions[i].id), true);
                themesViewer.update(colorDefinitions[i], null);
            }
        }
        super.performDefaults();
    }

    public boolean performOk() {
        
        for (ColorDefinition[] colorDefinitions : colorDefinitionMap.values()) {
            for (int i = 0; i < colorDefinitions.length; i++) {
                if(colorDefinitions[i].isChanged) {
                    if(colorDefinitions[i].isDefault)
                        PreferenceHandler.setDefaultRGB(colorDefinitions[i].id);
                    else
                        PreferenceHandler.setRGB(colorDefinitions[i].id, colorDefinitions[i].value);
                }
            }
        }
        colorRegistry.dispose();
        return super.performOk();
    }

    private void createTypeThemesGroup(Composite mainComposite) {

        Composite themesGroup = new Composite(mainComposite, SWT.NULL);
        themesGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout layout = new GridLayout(2, false);
        themesGroup.setLayout(layout);

        themesViewer = new TreeViewer(themesGroup, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        data.heightHint = Math.max(175, convertHeightInCharsToPixels(10));
        themesViewer.getTree().setLayoutData(data);

        themesViewer.setContentProvider(new ThemeContentProvider());
        themesViewer.setLabelProvider(new TableThemeLabelProvider());
        initColorDefinitions();
        themesViewer.setInput(colorDefinitionMap);
        themesViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                Object selection = ((StructuredSelection)themesViewer.getSelection()).getFirstElement();
                if(selection instanceof ColorDefinition) {
                    selectedColorDefinition = (ColorDefinition)selection; 
                    setThemeButtonsEnabled(true);
                }
                else
                    setThemeButtonsEnabled(false);
            }
        });

        Composite themeButtonsGroup = new Composite(themesGroup, SWT.NULL);
        themeButtonsGroup.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
        themeButtonsGroup.setLayout(new GridLayout());
        
        changeThemeButton = new Button(themeButtonsGroup, SWT.PUSH);
        changeThemeButton.setText(Messages.buttonChange);
        changeThemeButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
        changeThemeButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                ColorDialog colorDialog = new ColorDialog(themesViewer.getTree().getShell());
                if(selectedColorDefinition.value != null)
                    colorDialog.setRGB(selectedColorDefinition.value);
                RGB newColor = colorDialog.open();
                if (newColor != null) {
                    selectedColorDefinition.setRGB(newColor, false);
                    themesViewer.update(selectedColorDefinition, null);
                }
            }
        });
        deleteThemeButton = new Button(themeButtonsGroup, SWT.PUSH);
        deleteThemeButton.setText(Messages.buttonDelete);
        deleteThemeButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
        deleteThemeButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                selectedColorDefinition.setRGB(null, false);
                themesViewer.update(selectedColorDefinition, null);
            }
        });
        setThemeButtonsEnabled(false);
    }

    private void initColorDefinitions() {
        TableThemeExtension[] themeExtensions = RMBenchPlugin.getExtensionManager().getTableThemeExtensions();
        for (int i = 0; i < themeExtensions.length; i++) {
            TableThemeExtension extension = themeExtensions[i];
            
            colorDefinitionMap.put(extension, new ColorDefinition[] {
                    new ColorDefinition(
                            extension.getTitleBgId(),
                            Messages.titleBgColor, 
                            extension.titleBgColor),
                    new ColorDefinition(
                            extension.getTitleFgId(),
                            Messages.titleFgColor, 
                            extension.titleFgColor),
                    new ColorDefinition(
                            extension.getBodyBgId(),
                            Messages.bodyBgColor, 
                            extension.bodyBgColor),
                    new ColorDefinition(
                            extension.getBodyFgId(),
                            Messages.bodyFgColor, 
                            extension.bodyFgColor) 
            });
        }
    }

    private void setThemeButtonsEnabled(boolean enabled) {
        changeThemeButton.setEnabled(enabled);
        deleteThemeButton.setEnabled(enabled);
    }
    
    private class TableThemeLabelProvider extends LabelProvider {

        private HashMap<Color, Image> images = new HashMap<Color, Image>();
        private int imageSize = -1;
        private int usableImageSize = -1;
        private Image emptyImage;

        
        public void dispose() {
            super.dispose();
            for (Image image : images.values()) {
                image.dispose();
            }
            images.clear();

            if (emptyImage != null) {
                emptyImage.dispose();
                emptyImage = null;
            }
        }

        public Image getImage(Object element) {
            if (element instanceof ColorDefinition) {
                Color c = ((ColorDefinition) element).getColor();
                if(c == null) return null;
                
                Image image = (Image) images.get(c);
                if (image == null) {
                    Display display = Display.getCurrent();
                    ensureImageSize(display);
                    image = new Image(display, imageSize, imageSize);

                    GC gc = new GC(image);
                    gc.setBackground(themesViewer.getControl().getBackground());
                    gc.setForeground(themesViewer.getControl().getBackground());
                    gc.drawRectangle(0, 0, imageSize - 1, imageSize - 1);

                    gc.setForeground(themesViewer.getControl().getForeground());
                    gc.setBackground(c);

                    int offset = (imageSize - usableImageSize) / 2;
                    gc.drawRectangle(offset, offset, usableImageSize - offset, usableImageSize
                            - offset);
                    gc.fillRectangle(offset + 1, offset + 1, usableImageSize - offset - 1,
                            usableImageSize - offset - 1);
                    gc.dispose();

                    images.put(c, image);
                }
                return image;

            }
            else {
                return null;
            }
        }

        private void ensureImageSize(Display display) {
            if (imageSize == -1) {
                imageSize = themesViewer.getTree().getItemHeight();
                usableImageSize = Math.max(1, imageSize - 4);
            }
        }

        public String getText(Object element) {
            if (element instanceof ColorDefinition)
                return ((ColorDefinition)element).label;
            else {
                TableThemeExtension themeExtension = (TableThemeExtension) element;
                StringBuffer buf = new StringBuffer(themeExtension.getLabel());
                buf.append(" [");
                TableTypeExtension[] typeExtensions = themeExtension.typeExtensions;
                for (int i = 0; i < typeExtensions.length; i++) {
                    buf.append(typeExtensions[i].getLabel());
                    if(i < typeExtensions.length - 1)
                        buf.append(",");
                }
                buf.append("]");
                return buf.toString();
            }
        }
    }

    private class ColorDefinition {
        final String id;
        final String label;
        
        RGB value;
        boolean isDefault = true;
        boolean isChanged = false;
        
        private ColorDefinition(String id, String label, RGB value) {
            this.id = id;
            this.label = label;
            RGB preferenceRgb = PreferenceHandler.getRGB(id);
            this.value = preferenceRgb;// != null ? preferenceRgb : value;
        }

        public void setRGB(RGB newColor, boolean isDefault) {
            value = newColor;
            colorRegistry.put(id, newColor != null ? newColor : NULL_RGB);
            this.isDefault = isDefault;
            this.isChanged = true;
        }

        public Color getColor() {
            if(value == null) 
                return null;
            
            Color color = colorRegistry.get(id);
            if(color == null) {
                colorRegistry.put(id, value);
                color = colorRegistry.get(id);
            }
            return color;
        }
    }
    
    private class ThemeContentProvider implements ITreeContentProvider {

        public Object[] getChildren(Object parentElement) {
            TableThemeExtension extension = (TableThemeExtension) parentElement;
            return (ColorDefinition[])colorDefinitionMap.get(extension);
        }

        public Object getParent(Object element) {
            return null;
        }

        public boolean hasChildren(Object element) {
            return element instanceof TableThemeExtension;
        }

        public Object[] getElements(Object inputElement) {
            return ((Map<?,?>)inputElement).keySet().toArray();
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }
}
