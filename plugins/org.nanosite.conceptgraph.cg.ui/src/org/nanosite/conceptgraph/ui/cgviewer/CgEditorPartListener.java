package org.nanosite.conceptgraph.ui.cgviewer;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.xtext.ui.editor.XtextEditor;

public class CgEditorPartListener implements IPartListener {

	private static String cgEditorId = "org.nanosite.conceptgraph.Cg";

	@Override
	public void partActivated(IWorkbenchPart part) {
		handleModelRegistration(part);
	}

	@Override
	public void partBroughtToTop(IWorkbenchPart part) {
		
	}

	@Override
	public void partClosed(IWorkbenchPart part) {
		if (part instanceof ConceptGraphView) {
			part.dispose();
		}
		else {
			handleModelUnregistration(part);
		}
	}

	@Override
	public void partDeactivated(IWorkbenchPart part) {
		
	}

	@Override
	public void partOpened(IWorkbenchPart part) {
		
	}
	
	private void handleModelUnregistration(IWorkbenchPart part) {
		if (part != null && part instanceof XtextEditor) {
			XtextEditor editor = (XtextEditor) part;

			if (editor.getEditorSite().getId().equals(cgEditorId)) {
				ConceptGraphView view = ConceptGraphView.getInstance();
				if (view != null && editor.equals(view.getActiveEditor())) {
					view.clear();
					view.updateModel(false);
				}
			}
		}
	}

	private void handleModelRegistration(IWorkbenchPart part) {
		if (part instanceof XtextEditor) {
			XtextEditor editor = (XtextEditor) part;

			if (editor.getEditorSite().getId().equals(cgEditorId)) {
				Object adapted = editor.getEditorInput().getAdapter(IFile.class);
				ConceptGraphView view = ConceptGraphView.getInstance();
				if (view != null && !editor.equals(view.getActiveEditor()) && adapted != null) {
					IFile file = (IFile) adapted;
					view.setActiveEditor(editor);
					view.setActiveFile(file);
					view.updateModel(false);
				}
			}
		}
	}
}
