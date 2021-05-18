package org.cytoscape.internal.panels.motiffs_panel;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.internal.motiffs.IMotiff;
import org.cytoscape.internal.motiffs.MotiffsManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MotiffsPanel extends JPanel implements CytoPanelComponent {

    private final List<IMotiff> motiffsList;
    private final CyApplicationManager cyAppManager;

    public MotiffsPanel(CyApplicationManager cyAppManager) {
        motiffsList = MotiffsManager.getInstance().getMotiffList();
        this.cyAppManager = cyAppManager;
        initPanel();
    }

    private void initPanel() {
        motiffsList.forEach(m -> {
            JButton button = new JButton(m.getName());
            button.addActionListener(e -> m.create(cyAppManager.getCurrentNetwork()));
            this.add(button);
        });
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public CytoPanelName getCytoPanelName() {
        return CytoPanelName.WEST;
    }

    @Override
    public String getTitle() {
        return "Motiffs Panel";
    }

    @Override
    public Icon getIcon() {
        return null;
    }
}
