/*
 * created 03.04.2005
 * 
 * $Id:OutlinePage.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.editors;

import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.parts.ScrollableThumbnail;
import org.eclipse.draw2d.parts.Thumbnail;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.parts.ContentOutlinePage;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.PageBook;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.editparts.DiagramEditPart;
import com.byterefinery.rmbench.editparts.DiagramTreeEditPart;
import com.byterefinery.rmbench.editparts.TableEditPart;
import com.byterefinery.rmbench.editparts.TableTreeEditPart;
import com.byterefinery.rmbench.util.ImageConstants;

/**
 * The outline page for the schema editor. This page actually consists of
 * a pagebook which will alternatively show an outline page with table and 
 * column items in a tree, and a graphical overview page which allows 
 * thumbnail navigation
 * 
 * @author cse
 */
class OutlinePage extends ContentOutlinePage implements IAdaptable {

    private final DiagramEditor editor;
    
    private PageBook pageBook;
    private Control outline;
    private Canvas overview;
    private IAction showOutlineAction, showOverviewAction;
    private Thumbnail thumbnail;
    private DisposeListener disposeListener;

    public OutlinePage(DiagramEditor editor) {
        super(new TreeViewer());
        this.editor = editor;
    }

    public void init(IPageSite pageSite) {
        super.init(pageSite);
        ActionRegistry registry = editor.getActionRegistry();
        IActionBars bars = pageSite.getActionBars();
        String id = ActionFactory.UNDO.getId();
        bars.setGlobalActionHandler(id, registry.getAction(id));
        id = ActionFactory.REDO.getId();
        bars.setGlobalActionHandler(id, registry.getAction(id));
        id = ActionFactory.DELETE.getId();
        bars.setGlobalActionHandler(id, registry.getAction(id));
        bars.updateActionBars();
    }

    public void createControl(Composite parent) {
        pageBook = new PageBook(parent, SWT.NONE);
        overview = new Canvas(pageBook, SWT.NONE);
        outline = getViewer().createControl(pageBook);
        
        getViewer().setEditDomain(editor.getEditDomain());
        getViewer().setEditPartFactory(new TreePartFactory());
        
        IToolBarManager tbm = getSite().getActionBars().getToolBarManager();
        showOverviewAction = new Action() {
            public void run() {
                showOverviewPage();
            }
        };
        showOverviewAction.setImageDescriptor(
                RMBenchPlugin.getImageDescriptor(ImageConstants.OVERVIEW)); //$NON-NLS-1$
        tbm.add(showOverviewAction);
        
        showOutlineAction = new Action() {
            public void run() {
                showOutlinePage();
            }
        };
        showOutlineAction.setImageDescriptor(
                RMBenchPlugin.getImageDescriptor(ImageConstants.OUTLINE)); //$NON-NLS-1$
        tbm.add(showOutlineAction);

        getViewer().setContents(editor.getDiagram());
        editor.getSelectionSynchronizer().addViewer(getViewer());

        showOutlinePage();
    }

    public void dispose() {
        editor.getSelectionSynchronizer().removeViewer(getViewer());
        
        if(disposeListener != null && 
           editor.getViewerControl() != null && 
           !editor.getViewerControl().isDisposed()) {
            
            editor.getViewerControl().removeDisposeListener(disposeListener);
        }
        if (thumbnail != null) {
            thumbnail.deactivate();
            thumbnail = null;
        }
        super.dispose();
    }

    @SuppressWarnings("unchecked")
	public Object getAdapter(Class type) {
        if (type == ZoomManager.class)
            return editor.getViewer().getProperty(ZoomManager.class.toString());
        return null;
    }

    public Control getControl() {
        return pageBook;
    }

    private void initializeOverview() {
        LightweightSystem lws = new LightweightSystem(overview);
        ScalableFreeformRootEditPart root = 
            (ScalableFreeformRootEditPart) editor.getViewer().getRootEditPart();
        
        thumbnail = new ScrollableThumbnail((Viewport) root.getFigure());
        thumbnail.setBorder(new MarginBorder(3));
        thumbnail.setSource(root.getLayer(LayerConstants.PRINTABLE_LAYERS));
        lws.setContents(thumbnail);
        
        disposeListener = new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                if (thumbnail != null) {
                    thumbnail.deactivate();
                    thumbnail = null;
                }
            }
        };
        editor.getViewerControl().addDisposeListener(disposeListener);
    }

    private void showOutlinePage() {
        showOutlineAction.setChecked(true);
        showOverviewAction.setChecked(false);
        
        pageBook.showPage(outline);
        if (thumbnail != null)
            thumbnail.setVisible(false);
    }

    private void showOverviewPage() {
        if (thumbnail == null)
            initializeOverview();
        
        showOutlineAction.setChecked(false);
        showOverviewAction.setChecked(true);
        
        thumbnail.setVisible(true);
        pageBook.showPage(overview);
    }
    
    private class TreePartFactory implements EditPartFactory {

        public EditPart createEditPart(EditPart context, Object model) {
            Map<?, ?> registry = editor.getViewer().getEditPartRegistry();
            EditPart mainPart = (EditPart)registry.get(model);
            
            if (mainPart instanceof DiagramEditPart)
                return new DiagramTreeEditPart((DiagramEditPart)mainPart);
            if (mainPart instanceof TableEditPart)
                return new TableTreeEditPart((TableEditPart)mainPart);
            return null;
        }
    }
}
