package org.nanosite.conceptgraph;

import org.nanosite.conceptgraph.cg.ConceptDef;

public class CGHelper {
	class Edge {
		ConceptDef source = null;
		ConceptDef target = null;

		Edge (ConceptDef source, ConceptDef target) {
			this.source = source;
			this.target = target;
		}
	}
}
