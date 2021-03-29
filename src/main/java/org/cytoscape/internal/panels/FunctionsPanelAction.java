package org.cytoscape.internal.panels;

import java.awt.event.ActionEvent;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;

public class FunctionsPanelAction extends AbstractCyAction {

    private static final long serialVersionUID = 91456847100L;
    private CySwingApplication csApp;
    private final CytoPanel cytoPanelWest;
    private FunctionsPanel panel;

    public FunctionsPanelAction(CySwingApplication csApp,
                                FunctionsPanel mcPanel) {
        super("Function Panel");
        setPreferredMenu("Apps");

        this.csApp = csApp;

        this.cytoPanelWest = this.csApp.getCytoPanel(CytoPanelName.WEST);
        this.panel = mcPanel;
    }

    public void actionPerformed(ActionEvent e) {
        if (cytoPanelWest.getState() == CytoPanelState.HIDE) {
            cytoPanelWest.setState(CytoPanelState.DOCK);
        }

        int index = cytoPanelWest.indexOfComponent(panel);
        if (index == -1) {
            return;
        }
        cytoPanelWest.setSelectedIndex(index);
    }

}
