package org.nanosite.conceptgraph.ui.cgviewer;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.runtime.CoreException;

public class ResourceChangeListener implements IResourceChangeListener {

    public void resourceChanged(IResourceChangeEvent event) {
        if (event.getType() == IResourceChangeEvent.POST_BUILD) {
            try {
                event.getDelta().accept(new DeltaVisitor());
            } catch (CoreException e) {
                e.printStackTrace();
            }
        }
    }
}