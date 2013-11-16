package org.nanosite.conceptgraph.ui.cgviewer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;

class DeltaVisitor implements IResourceDeltaVisitor {

    public boolean visit(IResourceDelta delta) {
        IResource res = delta.getResource();
        if (res instanceof IFile && delta.getKind() == IResourceDelta.CHANGED) {
            IFile file = (IFile) res;
            ConceptGraphView view = ConceptGraphView.getInstance();
            if (view != null) {
            	if (file.equals(view.getActiveFile())) {
            		view.updateModel(false);
            	}
            }
            return false;
        }
        return true;
    }
}
