package org.nanosite.conceptgraph.validation;

import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.CheckType;
import org.nanosite.conceptgraph.cg.ActualParam;
import org.nanosite.conceptgraph.cg.Application;
import org.nanosite.conceptgraph.cg.CgPackage;
import org.nanosite.conceptgraph.cg.ConceptDef;
import org.nanosite.conceptgraph.cg.FormalParam;


public class CgJavaValidator extends AbstractCgJavaValidator {

	@Check(CheckType.FAST)
	public void checkConceptApplication(Application appl)
	{
		ConceptDef c = appl.getConcept();
		int cParams = c.getParam()==null ? 0 : c.getParam().size();
		int aParams = appl.getParam()==null ? 0 : appl.getParam().size();
		if (cParams != aParams) {
			error("Concept applied with wrong number of params (" +
					aParams + ", expected " + cParams + ")",
					appl,
					CgPackage.APPLICATION);
			return;
		}

		for(int i=0; i<c.getParam().size(); i++) {
			FormalParam fp = c.getParam().get(i);
			ActualParam ap = appl.getParam().get(i);
			boolean wantString = fp.getOpt()!=null && fp.getOpt().equals("$");
			if (wantString && ap.getString()==null) {
				error("Concept expected string parameter", ap, CgPackage.ACTUAL_PARAM);
			}
			if ((! wantString) && ap.getConcept()==null) {
				error("Concept expected concept parameter", ap, CgPackage.ACTUAL_PARAM);
			}
		}
	}

}
