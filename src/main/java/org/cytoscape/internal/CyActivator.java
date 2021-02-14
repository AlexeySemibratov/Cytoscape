package org.cytoscape.internal;

import java.util.Properties;

import org.cytoscape.app.CyAppAdapter;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.internal.panels.BNetPanelAction;
import org.cytoscape.internal.panels.FunctionsPanel;
import org.cytoscape.internal.panels.FunctionsPanelAction;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.internal.panels.BNetPanel;
import org.cytoscape.internal.boolnet.functions.FunctionsManager;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.session.CySessionManager;
import org.cytoscape.session.events.SessionAboutToBeSavedListener;
import org.cytoscape.session.events.SessionLoadedListener;
import org.osgi.framework.BundleContext;


public class CyActivator extends AbstractCyActivator {
	public CyActivator() {
		super();
	}


	public void start(BundleContext bc) {
		CySwingApplication cytoscapeDesktopService = getService(bc,CySwingApplication.class);
		CyApplicationManager cyApplicationManager = getService(bc, CyApplicationManager.class);
		CyNetworkManager netMgr = getService(bc, CyNetworkManager.class);
		CyNetworkFactory cyNetworkFactory = getService(bc, CyNetworkFactory.class);
		CyNetworkNaming namingUtil = getService(bc, CyNetworkNaming.class);
		CySessionManager sessionManager = getService(bc, CySessionManager.class);
		//Managers
		
		FunctionsManager info = new FunctionsManager();
		registerService(bc,info, FunctionsManager.class, new Properties());
		
		
		//Panels
		BNetPanel mcPanel = new BNetPanel(cyApplicationManager, cytoscapeDesktopService, 
				getService(bc, FunctionsManager.class), cyNetworkFactory, namingUtil,netMgr);
		BNetPanelAction controlPanelAction = new BNetPanelAction(cytoscapeDesktopService,mcPanel);
		
		registerService(bc,mcPanel,CytoPanelComponent.class, new Properties());
		registerService(bc,controlPanelAction,CyAction.class, new Properties());
		
		FunctionsPanel panel = new FunctionsPanel(cyApplicationManager, getService(bc, FunctionsManager.class),sessionManager);
		FunctionsPanelAction FunctionsPanelAction = new FunctionsPanelAction(cytoscapeDesktopService,panel);
		
		registerService(bc,panel,CytoPanelComponent.class, new Properties());
		registerService(bc,FunctionsPanelAction,CyAction.class, new Properties());
		
		// SessionListeners
		
		registerService(bc,panel,SessionAboutToBeSavedListener.class, new Properties());
		registerService(bc,panel,SessionLoadedListener.class, new Properties());
	
	}
}

