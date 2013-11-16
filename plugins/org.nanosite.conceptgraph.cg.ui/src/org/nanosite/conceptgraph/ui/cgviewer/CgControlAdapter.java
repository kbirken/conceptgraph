package org.nanosite.conceptgraph.ui.cgviewer;

import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;

public class CgControlAdapter extends ControlAdapter {

	@Override
	public void controlResized(ControlEvent e) {
		ConceptGraphView view = ConceptGraphView.getInstance();
		if (view != null) {
			view.applyLayout();
		}
		super.controlResized(e);
	}	
}
