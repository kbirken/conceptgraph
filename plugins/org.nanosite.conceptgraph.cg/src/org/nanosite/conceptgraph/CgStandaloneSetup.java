
package org.nanosite.conceptgraph;

/**
 * Initialization support for running Xtext languages 
 * without equinox extension registry
 */
public class CgStandaloneSetup extends CgStandaloneSetupGenerated{

	public static void doSetup() {
		new CgStandaloneSetup().createInjectorAndDoEMFRegistration();
	}
}

