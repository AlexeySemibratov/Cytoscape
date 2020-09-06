package org.cytoscape.sample.internal;

import java.util.Properties;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.equations.EquationCompiler;
import org.cytoscape.equations.EquationParser;
import org.cytoscape.sample.internal.motives.MotivesPanel;
import org.cytoscape.sample.internal.motives.MotivesPanelAction;
import org.cytoscape.service.util.AbstractCyActivator;
import org.osgi.framework.BundleContext;


public class CyActivator extends AbstractCyActivator {
	public CyActivator() {
		super();
	}


	public void start(BundleContext bc) {
		
		CySwingApplication cytoscapeDesktopService = getService(bc,CySwingApplication.class);
		EquationCompiler eqCompilerRef = getService(bc,EquationCompiler.class);
		CyApplicationManager cyApplicationManager = getService(bc, CyApplicationManager.class);
		
		//Functions
		
		final EquationParser theParser = eqCompilerRef.getParser();
		theParser.registerFunction(new IXorFunction());
		
		
		/*Properties cptProp = new Properties();
		cptProp.setProperty("preferredAction", "NEW");
		cptProp.setProperty("preferredMenu", "Motives");*/
		
		//Actions
		
		
		//Tasks
		
		
		//Panels
		
		MotivesPanel mcPanel = new MotivesPanel(cyApplicationManager);
		MotivesPanelAction controlPanelAction = new MotivesPanelAction(cytoscapeDesktopService,mcPanel);
		
		registerService(bc,mcPanel,CytoPanelComponent.class, new Properties());
		registerService(bc,controlPanelAction,CyAction.class, new Properties());
		
	}
}

