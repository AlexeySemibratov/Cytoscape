package org.cytoscape.internal.panels;

import org.cytoscape.application.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;

public class DefaultPanelAction extends AbstractCyAction {

    private final CytoPanel cytoPanelWest;
    private final Component panelComponent;

    public DefaultPanelAction(CySwingApplication csApp, Component panelComponent, String name) {
        super(name);
        setPreferredMenu("Apps");
        this.cytoPanelWest = csApp.getCytoPanel(CytoPanelName.WEST);
        this.panelComponent = panelComponent;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (cytoPanelWest.getState() == CytoPanelState.HIDE) {
            cytoPanelWest.setState(CytoPanelState.DOCK);
        }

        int index = cytoPanelWest.indexOfComponent(panelComponent);
        if (index == -1) {
            return;
        }
        cytoPanelWest.setSelectedIndex(index);
    }
}
