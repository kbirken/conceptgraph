package org.nanosite.conceptgraph.ui.cgviewer;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditDomain;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.xtext.parsetree.AbstractNode;
import org.eclipse.xtext.parsetree.CompositeNode;
import org.eclipse.xtext.parsetree.NodeUtil;
import org.eclipse.xtext.parsetree.ParseTreeUtil;
import org.nanosite.conceptgraph.cg.ConceptDef;
import org.nanosite.conceptgraph.cg.Model;

public class ConceptGraphView extends XtextEditorObservingView {

//	private GraphViewer viewer;
//	private ScrollingGraphicalViewer viewer;
	private ConceptGraphViewer viewer;

	// GEF-EditDomain cares for state of the view
	private EditDomain editDomain = new EditDomain();



//	static class MyContentProvider implements IGraphContentProvider {
//
//		public Object getSource(Object rel) {
//			if ("Rock2Paper".equals(rel)) {
//				return "Rock";
//			} else if ("Paper2Scissors".equals(rel)) {
//				return "Paper";
//			} else if ("Scissors2Rock".equals(rel)) {
//				return "Scissors";
//			}
//			return null;
//		}
//
//		public Object[] getElements(Object input) {
//			return new Object[] { "Rock2Paper", "Paper2Scissors", "Scissors2Rock" };
//		}
//
//		public Object getDestination(Object rel) {
//			if ("Rock2Paper".equals(rel)) {
//				return "Paper";
//			} else if ("Paper2Scissors".equals(rel)) {
//				return "Scissors";
//			} else if ("Scissors2Rock".equals(rel)) {
//				return "Rock";
//			}
//			return null;
//		}
//
//		public double getWeight(Object connection) {
//			return 0;
//		}
//
//		public void dispose() {
//		}
//
//		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
//		}
//
//	}

//	static class MyLabelProvider extends LabelProvider {
//		final Image image = Display.getDefault().getSystemImage(SWT.ICON_WARNING);
//
//		public Image getImage(Object element) {
//			if (element.equals("Rock") || element.equals("Paper") || element.equals("Scissors")) {
//				return image;
//			}
//			return null;
//		}
//
//		public String getText(Object element) {
//			return element.toString();
//		}
//
//	}


	@Override
	public void createObservingPartControl(Composite parent) {
//		viewer = new GraphViewer(parent.getShell(), SWT.NONE);
//		viewer.setContentProvider(new MyContentProvider());
//		viewer.setLabelProvider(new MyLabelProvider());
//		viewer.setLayoutAlgorithm(new SpringLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING));
//		viewer.setInput(new Object());

		//createGraphicalViewer(parent);

		viewer = new ConceptGraphViewer();
		FigureCanvas canvas = (FigureCanvas)viewer.createControl(parent);

	}


//	protected void createGraphicalViewer(Composite parent) {
//		final RulerComposite rc = new RulerComposite(parent, SWT.NONE);
//		viewer = new ScrollingGraphicalViewer();
//		viewer.createControl(rc);
//		editDomain.addViewer(viewer);
//		rc.setGraphicalViewer(viewer);
//		RulerProvider rp = new RulerProvider() {
//			public Object getRuler() {
//				// Minimalimplementierung. Gibt sich selbst als Ruler-Modell zur�ck
//				return this;
//			}
//
//			public int getUnit() {
//				return RulerProvider.UNIT_PIXELS;
//			}
//		};
//
//		viewer.setProperty(RulerProvider.PROPERTY_HORIZONTAL_RULER, rp);
//		viewer.setProperty(RulerProvider.PROPERTY_RULER_VISIBILITY, Boolean.TRUE);
//
//		configureGraphicalViewer();
//		getSite().setSelectionProvider(viewer);
//		// Den Viewer mit einem MouseListener versehen
//		viewer.getControl().addMouseListener(new MouseAdapter() {
//			public void mouseUp(MouseEvent e) {
//				// EditPart anhand der Mausposition suchen
//				EditPart part = viewer.findObjectAt(new Point(e.x, e.y));
//				// Selektion im Viewer auf dieses Objekt setzen
//				viewer.setSelection(new StructuredSelection(part));
//			}
//		});
//		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
//			public void selectionChanged(SelectionChangedEvent event) {
//				// Selektionen im Viewer �bersetzen
//				Object selPart = ((IStructuredSelection) event.getSelection()).getFirstElement();
//				Object sel = (selPart instanceof EditPart) ? ((EditPart) selPart).getModel()
//						: null;
//						// Das Datenmodell des selektierten Objekts
//						if (selectedObject != sel) {
//							// Die Selektion weitermelden
//							selectedObject = sel;
//							fireSelectionChanged();
//						}
//			}
//		});
//	}
//
//	private void configureGraphicalViewer() {
//		viewer.getControl().setBackground(ColorConstants.listBackground);
//		viewer.setEditPartFactory(new EditPartFactory() {
//			public EditPart createEditPart(EditPart context, Object model) {
//				return (model instanceof Project) ? new GanttProjectEditPart((Project) model) :
//					(model instanceof Task) ? new GanttTaskEditPart(
//							session, (Task) model) : null;
//
//
//			}
//		});
//	}

	@Override
	public Viewer getViewer() {
		return viewer;
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		try {
			ISelection selection = event.getSelection();
			if (!selection.isEmpty() && selection instanceof ITextSelection) {
				final ITextSelection textSel = (ITextSelection) selection;
				int offset = textSel.getOffset();
				System.out.println("selectionChanged(offset=" + offset + ")");
				CompositeNode rootNode = getRootAST();
				AbstractNode node =
					ParseTreeUtil.getCurrentOrFollowingNodeByOffset(rootNode, offset);
				EObject obj = NodeUtil.getNearestSemanticObject(node);
				System.out.println("selection is " + obj.toString());

				while (obj!=null && !(obj instanceof Model) && !(obj instanceof ConceptDef)) {
					obj = obj.eContainer();
				}
				if (obj instanceof ConceptDef) {
					viewer.selectionChanged((ConceptDef)obj);
				} else {
					viewer.selectionChanged(null);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
