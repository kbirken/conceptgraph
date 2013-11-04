package org.nanosite.conceptgraph.ui.cgviewer;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.swt.graphics.Color;
import org.nanosite.conceptgraph.cg.ConceptDef;

public class ConceptFigure extends Figure {

	private ConceptDef cdef = null;

	public static Color color = new Color(null,255,255,206);
	private CompartmentFigure details = new CompartmentFigure();
	//private CompartmentFigure other = new CompartmentFigure();

	public ConceptFigure (ConceptDef cdef, Label name) {
		this.cdef = cdef;

		ToolbarLayout layout = new ToolbarLayout();
		setLayoutManager(layout);
		setBorder(new LineBorder(ColorConstants.black,1));
		setBackgroundColor(color);
		setOpaque(true);

		add(name);
		add(details);
	}

	public CompartmentFigure getDetailsCompartment() {
		return details;
	}

	public ConceptDef getConceptDef() {
		return cdef;
	}
}
