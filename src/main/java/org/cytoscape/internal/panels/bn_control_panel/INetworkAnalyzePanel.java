package org.cytoscape.internal.panels.bn_control_panel;

import javax.swing.*;

public interface INetworkAnalyzePanel {

    void clearAttractorsPanel();

    void showAttractorsPanel();

    void hideAttractorsPanel();

    void addAttractorPane(JTabbedPane tabbedPane);
}
