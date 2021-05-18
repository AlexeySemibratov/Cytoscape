package org.cytoscape.internal;

import java.awt.*;
import java.util.Properties;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.internal.motiffs.*;
import org.cytoscape.internal.panels.DefaultPanelAction;
import org.cytoscape.internal.panels.bn_functions_panel.FunctionsPanel;
import org.cytoscape.internal.panels.motiffs_panel.MotiffsPanel;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.internal.panels.bn_control_panel.BNetPanel;
import org.cytoscape.internal.boolnet.functions.FunctionsManager;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.session.CySessionManager;
import org.cytoscape.session.events.SessionAboutToBeSavedListener;
import org.cytoscape.session.events.SessionLoadedListener;
import org.osgi.framework.BundleContext;

@SuppressWarnings(value = "unused")
public class CyActivator extends AbstractCyActivator {

    private BundleContext bundleContext;
    private CySwingApplication cytoscapeDesktopService;
    private CyApplicationManager cyApplicationManager;
    private CyNetworkManager netMgr;
    private CyNetworkFactory cyNetworkFactory;
    private CyNetworkNaming namingUtil;
    private CySessionManager sessionManager;

    public CyActivator() {
        super();
    }


    public void start(BundleContext bc) {
        initServices(bc);
        initMotiffs();

        //Custom Managers

        FunctionsManager info = new FunctionsManager();
        registerService(bc, info, FunctionsManager.class, new Properties());

        //Panels

        BNetPanel bnControlPanel = new BNetPanel(
                cyApplicationManager,
                getService(bc, FunctionsManager.class),
                cyNetworkFactory,
                namingUtil,
                netMgr);

        FunctionsPanel functionsPanel = new FunctionsPanel(cyApplicationManager, getService(bc, FunctionsManager.class), sessionManager);

        registerCytoPanel(bnControlPanel, bnControlPanel.getTitle());
        registerCytoPanel(functionsPanel, "Functions panel");
        registerCytoPanel(new MotiffsPanel(cyApplicationManager), "Motiffs panel");

        // SessionListeners

        registerService(bc, functionsPanel, SessionAboutToBeSavedListener.class, new Properties());
        registerService(bc, functionsPanel, SessionLoadedListener.class, new Properties());
    }

    private void registerCytoPanel(Component panel, String name) {
        registerService(
                bundleContext,
                panel,
                CytoPanelComponent.class,
                new Properties()
        );
        registerService(
                bundleContext,
                new DefaultPanelAction(cytoscapeDesktopService, panel, name),
                CyAction.class,
                new Properties()
        );
    }

    private void initServices(BundleContext bc) {
        bundleContext = bc;
        cytoscapeDesktopService = getService(bc, CySwingApplication.class);
        cyApplicationManager = getService(bc, CyApplicationManager.class);
        netMgr = getService(bc, CyNetworkManager.class);
        cyNetworkFactory = getService(bc, CyNetworkFactory.class);
        namingUtil = getService(bc, CyNetworkNaming.class);
        sessionManager = getService(bc, CySessionManager.class);
    }

    private void initMotiffs() {
        MotiffsManager manager = MotiffsManager.getInstance();
        manager.addMotiff(new Bifan());
        manager.addMotiff(new Biparallel());
        manager.addMotiff(new FeedForwardLoop());
        manager.addMotiff(new FourNodeFeedbackLoop());
        manager.addMotiff(new ThreeChain());
        manager.addMotiff(new ThreeNodeFeedbackLoop());
        manager.addMotiff(new FeedbackWithTwoMutualDyads());
        manager.addMotiff(new UplinkedMutualDyad());
        manager.addMotiff(new FullyConnectedTriad());
    }
}

