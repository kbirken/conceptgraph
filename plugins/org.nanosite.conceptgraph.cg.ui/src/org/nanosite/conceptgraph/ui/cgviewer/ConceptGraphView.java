package org.nanosite.conceptgraph.ui.cgviewer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.xtext.resource.XtextResource;
//import org.eclipse.xtext.parsetree.AbstractNode;
//import org.eclipse.xtext.parsetree.CompositeNode;
//import org.eclipse.xtext.parsetree.NodeUtil;
//import org.eclipse.xtext.parsetree.ParseTreeUtil;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.ui.editor.model.IXtextModelListener;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.nanosite.conceptgraph.cg.Model;

public class ConceptGraphView extends ViewPart implements IPartListener, ISelectionChangedListener {

	private static String viewId = "org.nanosite.conceptgraph.ui.cgviewer";

	private static ConceptGraphView instance;

	private XtextEditor activeEditor;
	private IFile activeFile;
	private Model activeModel;

	private IPartListener partListener;
	private IResourceChangeListener resourceChangeListener;
	private CgControlAdapter controlAdapter;
	private ViewUpdateOnModelChange modelChangeListener;
	
	public Composite parent;
	
//	private GraphViewer viewer;
//	private ScrollingGraphicalViewer viewer;
	private ConceptGraphViewer viewer;
	private FigureCanvas canvas;

	// GEF-EditDomain cares for state of the view
//	private EditDomain editDomain = new EditDomain();

	public ConceptGraphView() {
//		this.previousIntermediateModel = null;
//		this.displayLabel = false;
	}
	
	@Override
	public void init (IViewSite site) throws PartInitException {
		super.init(site);
		site.getPage().addPartListener(this);
	}

	@Override
	public void dispose() {
		removeModelListener();
		getSite().getPage().removePartListener(this);
		super.dispose();
	}

    public static ConceptGraphView getInstance() {
    	if (instance == null) {
	        IWorkbenchPage activePage = getActivePage();
	        if (activePage != null) {
	            instance = (ConceptGraphView) activePage.findView(viewId);
	        }
    	}
        return instance;
    }
    
	private static IWorkbenchPage getActivePage() {
		if (PlatformUI.getWorkbench().getActiveWorkbenchWindow() != null) {
			IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			if (activePage != null) {
				return activePage;
			}
		}
		return null;
	}



	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		partListener = new CgEditorPartListener();
		resourceChangeListener = new ResourceChangeListener();
		controlAdapter = new CgControlAdapter();
		parent.addControlListener(controlAdapter);
		
		IWorkbenchPage activePage = getActivePage();
		if (activePage != null) {
			activePage.addPartListener(partListener);
		}
		ResourcesPlugin.getWorkspace().addResourceChangeListener(resourceChangeListener, IResourceChangeEvent.POST_BUILD);
		
//		selectionListener = new GraphSelectionListener();
//		injector.injectMembers(selectionListener);
//		graph = new Graph(parent, SWT.NONE);
//		graph.addSelectionListener(selectionListener);
		activeEditor = EditorUtils.getActiveXtextEditor();
		if (activeEditor != null) {
			activeFile = (IFile) activeEditor.getEditorInput().getAdapter(IFile.class);
		}

		viewer = new ConceptGraphViewer();
		canvas = (FigureCanvas)viewer.createControl(parent);

		updateModel(false);
	}
	

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public void applyLayout() {
//		graph.applyLayout();
	}
	

	public IFile getActiveFile() {
		return activeFile;
	}
	
	public void setActiveFile(IFile activeFile) {
		this.activeFile = activeFile;
	}
	
	public void setActiveEditor(XtextEditor activeEditor) {
		this.activeEditor = activeEditor;
	}
	
	public XtextEditor getActiveEditor() {
		return activeEditor;
	}
	
	public Model getActiveModel() {
		return activeModel;
	}

	/**
	 * Updates the contract viewer's internal model. 
	 * 
	 * @param forceUpdate if true, the update will be performed even if there are no structural changes between the previous and the current states of the graph model. 
	 */
	public void updateModel(final boolean forceUpdate) {
		if (activeEditor != null) {
			if (viewer!=null) {
				viewer.setInput(activeEditor.getDocument());
			}

			modelChangeListener = new ViewUpdateOnModelChange(viewer, this);

//			activeEditor.getDocument().readOnly(new IUnitOfWork.Void<XtextResource>() {
//				@Override
//				public void process(XtextResource resource) throws Exception {
//					if (resource != null) {
//						for (EObject obj : resource.getContents()) {
//							// TODO
////							if (obj instanceof FModel) {
////								activeModel = (FModel) obj;
////								intermediateModel = new IntermediateFrancaGraphModel(activeModel, displayLabel);
////								break;
////							}
//						}
//					}
//				}
//			});
		}
	
//		Display.getDefault().asyncExec(new Runnable() {
//			@Override
//			public void run() {
//				// TODO
//				if (forceUpdate || (intermediateModel != null && (previousIntermediateModel == null || !previousIntermediateModel.equals(intermediateModel)))) {
//					graph.setVisible(false);
//					graph.clear();
//					intermediateModel.getGraphNodes(graph);
//					intermediateModel.getGraphConnections(graph);
//					graph.applyLayoutNow();
//					graph.setVisible(true);
//					previousIntermediateModel = intermediateModel;
//				}
//			}
//		});
	}

	
	/**
	 * Clears all the data structures related to the view (e.g. resets the viewer).
	 */
	public void clear() {
		this.activeEditor = null;
		this.activeFile = null;
		this.activeModel = null;
//		this.intermediateModel = null;
//		this.previousIntermediateModel = null;
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				// TODO
//				if (!graph.isDisposed()) {
//					graph.clear();					
//				}
			}
		});
	}

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


//	@Override
//	public void createObservingPartControl(Composite parent) {
////		viewer = new GraphViewer(parent.getShell(), SWT.NONE);
////		viewer.setContentProvider(new MyContentProvider());
////		viewer.setLabelProvider(new MyLabelProvider());
////		viewer.setLayoutAlgorithm(new SpringLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING));
////		viewer.setInput(new Object());
//
//		//createGraphicalViewer(parent);
//
//		viewer = new ConceptGraphViewer();
//		FigureCanvas canvas = (FigureCanvas)viewer.createControl(parent);
//
//	}
//

//	protected void createGraphicalViewer(Composite parent) {
//		final RulerComposite rc = new RulerComposite(parent, SWT.NONE);
//		viewer = new ScrollingGraphicalViewer();
//		viewer.createControl(rc);
//		editDomain.addViewer(viewer);
//		rc.setGraphicalViewer(viewer);
//		RulerProvider rp = new RulerProvider() {
//			public Object getRuler() {
//				// Minimalimplementierung. Gibt sich selbst als Ruler-Modell zur���ck
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
//				// Selektionen im Viewer ���bersetzen
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

//	@Override
//	public Viewer getViewer() {
//		return viewer;
//	}

//	@Override
//	public void selectionChanged(SelectionChangedEvent event) {
//		try {
//			ISelection selection = event.getSelection();
//			if (!selection.isEmpty() && selection instanceof ITextSelection) {
//				final ITextSelection textSel = (ITextSelection) selection;
//				int offset = textSel.getOffset();
//				System.out.println("selectionChanged(offset=" + offset + ")");
//				CompositeNode rootNode = getRootAST();
//				AbstractNode node =
//					ParseTreeUtil.getCurrentOrFollowingNodeByOffset(rootNode, offset);
//				EObject obj = NodeUtil.getNearestSemanticObject(node);
//				System.out.println("selection is " + obj.toString());
//
//				while (obj!=null && !(obj instanceof Model) && !(obj instanceof ConceptDef)) {
//					obj = obj.eContainer();
//				}
//				if (obj instanceof ConceptDef) {
//					viewer.selectionChanged((ConceptDef)obj);
//				} else {
//					viewer.selectionChanged(null);
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}
	
   private void extractModel (IXtextDocument document) {
      Model newRoot = document.readOnly(new IUnitOfWork<Model, XtextResource>()
      {
         public Model exec(final XtextResource state) throws Exception
         {
//        	 rootAST = null;
//        	 IParseResult parseResult = state.getParseResult();
//        	 if (parseResult!=null) {
//        		 rootAST = parseResult.getRootNode();
//        	 }

            if (state.getContents().isEmpty()) {
               return null;
            }

            EObject root = state.getContents().get(0);
            if (! (root instanceof Model))
            	return null;
            return (Model)root;
         }
      });
   }
   
   
   private static final class ViewUpdateOnModelChange implements IXtextModelListener
   {
      private final ConceptGraphViewer viewer;
      private final ConceptGraphView view;

      private ViewUpdateOnModelChange (ConceptGraphViewer v, ConceptGraphView view) {
         this.viewer = v;
         this.view = view;
      }

      public void modelChanged (XtextResource resource) {
         runInSWTThread(new Runnable()
         {
            public void run()
            {
               IXtextDocument document = view.activeEditor.getDocument();
               view.extractModel(document);
               viewer.setInput(document);
               viewer.refresh();
            }

         });
      }

      /**
       * Runs the runnable in the SWT thread. (Simply runs the runnable if the current
       * thread is the UI thread, otherwise calls the runnable in asyncexec.)
       */
      private void runInSWTThread(Runnable runnable)
      {
         if(Display.getCurrent() == null) {
            Display.getDefault().asyncExec(runnable);
         } else {
            runnable.run();
         }
      }
   }

	public final void partActivated(IWorkbenchPart part) {
		if (part instanceof XtextEditor) {
			if (viewer.getInput() != ((XtextEditor) part).getDocument()) {
				removeModelListener();
				activeEditor = (XtextEditor) part;
				extractModel(activeEditor.getDocument());
				viewer.setInput(activeEditor.getDocument());
				activeEditor.getDocument()
						.addModelListener(modelChangeListener);

				ISelectionProvider selectionProvider = activeEditor.getSelectionProvider();
				if (selectionProvider instanceof IPostSelectionProvider) {
					IPostSelectionProvider provider = (IPostSelectionProvider) selectionProvider;
					provider.addPostSelectionChangedListener(this);
				} else {
					selectionProvider.addSelectionChangedListener(this);
				}
			}
		} else if (part instanceof EditorPart) {
			removeModelListener();
			viewer.setInput(null);
		}
	}

	private void removeModelListener() {
		if (activeEditor != null && activeEditor.getDocument() != null) {
			activeEditor.getDocument().removeModelListener(
					modelChangeListener);

			ISelectionProvider selectionProvider = activeEditor.getSelectionProvider();
			if (selectionProvider instanceof IPostSelectionProvider) {
				IPostSelectionProvider provider = (IPostSelectionProvider) selectionProvider;
				provider.removePostSelectionChangedListener(this);
			} else {
				selectionProvider.removeSelectionChangedListener(this);
			}
		}
		activeEditor = null;
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void partBroughtToTop(IWorkbenchPart part) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void partClosed(IWorkbenchPart part) {
	      if (part==activeEditor)
	      {
	         removeModelListener();
	         viewer.setInput(null);
	      }
	}

	@Override
	public void partDeactivated(IWorkbenchPart part) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void partOpened(IWorkbenchPart part) {
		// TODO Auto-generated method stub
		
	}


}
