package org.cytoscape.internal.motiffs;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

public class FullyConnectedTriad implements IMotiff {
    @Override
    public void create(CyNetwork network) {
        CyNode node1 = network.addNode();
        CyNode node2 = network.addNode();
        CyNode node3 = network.addNode();
        network.addEdge(node1, node2, true);
        network.addEdge(node2, node1, true);
        network.addEdge(node2, node3, true);
        network.addEdge(node3, node2, true);
        network.addEdge(node1, node3, true);
        network.addEdge(node3, node1, true);
    }

    @Override
    public String getName() {
        return "Fully connected triad";
    }
}
