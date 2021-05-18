package org.cytoscape.internal.panels.bn_control_panel;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.internal.boolnet.functions.FunctionsManager;
import org.cytoscape.internal.boolnet.functions.State;
import org.cytoscape.internal.boolnet.functions.alhoritms.AttractorSearch;
import org.cytoscape.internal.boolnet.functions.alhoritms.SynchNetwork;
import org.cytoscape.internal.utils.PopupMessage;
import org.cytoscape.internal.utils.TableUtils;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.session.CyNetworkNaming;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;


public class BoolNetPresenter {

    private final CyApplicationManager cyAppManager;
    private final FunctionsManager fManager;
    private final CyNetworkFactory cnf;
    private final CyNetworkNaming cyNetNaming;
    private final CyNetworkManager cyNetManager;

    private final INetworkAnalyzePanel panel;

    public BoolNetPresenter(
            CyApplicationManager cyAppManager,
            FunctionsManager fManager,
            CyNetworkFactory cnf,
            CyNetworkNaming cyNetNaming,
            CyNetworkManager cyNetManager,
            INetworkAnalyzePanel panel
    ) {
        this.cyAppManager = cyAppManager;
        this.fManager = fManager;
        this.cnf = cnf;
        this.cyNetNaming = cyNetNaming;
        this.cyNetManager = cyNetManager;
        this.panel = panel;
    }

    public void searchAttractors(int mode) {
        panel.hideAttractorsPanel();
        panel.clearAttractorsPanel();

        AttractorSearch attractorSearchAlhoritm = new AttractorSearch(mode);

        long start = System.nanoTime();
        ArrayList<ArrayList<State>> attractorsSet = attractorSearchAlhoritm.search(fManager);
        PopupMessage.Message("Execution time = " + (System.nanoTime() - start) / 1000000 + " ms.");

        if (attractorsSet.isEmpty()) {
            String message;
            if (mode == AttractorSearch.FULL_ATTRACTORS_MODE) {
                message = "No attractors found";
            } else {
                message = "No fixed points found";
            }
            PopupMessage.Message(message);
            return;
        }

        String[] arguments = fManager.getAllNodeFunctions().keySet().toArray(new String[0]);

        JTabbedPane tabbedPane = new JTabbedPane();

        for (ArrayList<State> attractor : attractorsSet) {
            DefaultTableModel tableModel = TableUtils.createTableWithNoEditableCells();
            tableModel.setColumnIdentifiers(arguments);

            for (State state : attractor) {
                tableModel.addRow(Arrays.stream(state.toArray()).boxed().toArray());
            }

            JTable tab = new JTable(tableModel);
            tab.setPreferredScrollableViewportSize(new Dimension(350, 200));
            JScrollPane pane = new JScrollPane(tab);
            pane.setPreferredSize(new Dimension(350, 300));

            String title;
            if (mode == AttractorSearch.FULL_ATTRACTORS_MODE) {
                title = "Attractor #" + (attractorsSet.indexOf(attractor) + 1);
            } else {
                title = "Fixed Point #" + (attractorsSet.indexOf(attractor) + 1);
            }

            tabbedPane.addTab(title, pane);
        }
        panel.addAttractorPane(tabbedPane);
        panel.showAttractorsPanel();
    }

    public void createSynchNet() {
        if (fManager.getAllNodeFunctions().size() >= 20) {
            PopupMessage.ErrorMessage("The total number of nodes in a synchronous graph is too large. The function is not available.");
            return;
        }
        CyNetwork network = SynchNetwork.create(fManager.getAllNodeFunctions(), cnf);
        String currentNetName = cyAppManager.getCurrentNetwork().getRow(cyAppManager.getCurrentNetwork()).get("name", String.class);
        network.getRow(network).set(CyNetwork.NAME, cyNetNaming.getSuggestedNetworkTitle(currentNetName + "_synch"));
        cyNetManager.addNetwork(network);
        cyAppManager.setCurrentNetwork(network);
    }
}
