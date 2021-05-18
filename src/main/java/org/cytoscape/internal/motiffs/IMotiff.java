package org.cytoscape.internal.motiffs;

import org.cytoscape.model.CyNetwork;

public interface IMotiff {

    void create(CyNetwork network);

    String getName();
}
