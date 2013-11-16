package org.nanosite.conceptgraph.ui.cgviewer;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.nanosite.conceptgraph.cg.ConceptDef;
import org.nanosite.conceptgraph.cg.Model;

public class ConceptGraphViewer extends Viewer implements MouseListener {

	private FigureCanvas canvas = null;

	private final LightweightSystem lws = createLightweightSystem();
	IFigure rootFigure;

	private Model root = null;
	private ConceptDef selected = null;


	/**
	 * @see org.eclipse.gef.EditPartViewer#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public final Control createControl (Composite parent) {
		canvas = new FigureCanvas(parent, lws);
//		installRootFigure();
		return canvas;
	}


	/**
	 * Creates the lightweight system used to host figures. Subclasses should
	 * not need to override this method.
	 *
	 * @return the lightweight system
	 */
	protected LightweightSystem createLightweightSystem() {
		return new LightweightSystem();
	}

	/**
	 * Returns the lightweight system.
	 *
	 * @return the system
	 */
	protected LightweightSystem getLightweightSystem() {
		return lws;
	}


	/**
	 * Sets the lightweight system's root figure.
	 *
	 * @param figure
	 *            the root figure
	 */
	protected void setRootFigure(IFigure figure) {
		rootFigure = figure;
		getLightweightSystem().setContents(rootFigure);

		canvas.getViewport().setContentsTracksHeight(true);
		canvas.getViewport().setContentsTracksWidth(true);
		canvas.setLayoutData(new GridData(GridData.FILL_BOTH));
	}


	@Override
	public Control getControl() {
		return canvas;
	}


	@Override
	public Object getInput() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public ISelection getSelection() {
		System.out.println("ConceptGraphViewer getSelection");
		return null;
	}


	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		lws.getUpdateManager().performUpdate();

	}


	@Override
	public void setInput (Object input) {
		if (input==null) {
			root = null;
			inputChanged();
			return;
		}

		IXtextDocument document = (IXtextDocument)input;
		Model newRoot = document.readOnly(new IUnitOfWork<Model, XtextResource>()
				{
					public Model exec(final XtextResource state) throws Exception
					{
						if (state.getContents().isEmpty()) {
							return null;
						}

						EObject root = state.getContents().get(0);
						if (! (root instanceof Model))
							return null;
						return (Model)root;
					}
				});

		// if root switches to null, we continue displaying the current model
		if (newRoot!=null && newRoot!=root) {
			root = newRoot;
			inputChanged();
		}
	}


	@Override
	public void setSelection(ISelection arg0, boolean arg1) {
		System.out.println("ConceptGraphViewer setSelection");
	}


	// this function is typically in an external ContentProvider
	private void inputChanged() {
		if (root==null) {
			setRootFigure(new Figure());
			return;
		}

		System.out.println("setInput with " + root.getConcept().size() + " concepts.");
//		for(ConceptDef cdef : root.getConcept()) {
//			System.out.println("ConceptDef " + cdef.getName());
//		}

		Figure contents = new ConceptGraph2D(root, selected, this).draw();
		setRootFigure(contents);
	}

	public void selectionChanged (ConceptDef cdef) {
		selected = cdef;
		Figure contents = new ConceptGraph2D(root, selected, this).draw();
		setRootFigure(contents);
	}


	@Override
	public void mousePressed(MouseEvent me) {
		ConceptFigure figure = (ConceptFigure)me.getSource();
		System.out.println("mousePressed: " + figure.getConceptDef().getName());
		selectionChanged(figure.getConceptDef());
	}


	@Override
	public void mouseReleased(MouseEvent me) {
		// TODO Auto-generated method stub

	}


	@Override
	public void mouseDoubleClicked(MouseEvent me) {
		// TODO Auto-generated method stub

	}
}
