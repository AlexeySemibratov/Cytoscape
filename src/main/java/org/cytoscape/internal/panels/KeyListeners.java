package org.cytoscape.internal.panels;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.internal.boolnet.MotifsConstructor;
import org.cytoscape.internal.utils.PopupMessage;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class KeyListeners {

    private CyApplicationManager cyAppManager;

    public KeyListeners(CyApplicationManager manager){
        this.cyAppManager = manager;
    }

    public ActionListener chain(){
        return e -> {
            String s = JOptionPane.showInputDialog(null, "Chain lenght:");
            int length = Integer.parseInt(s);
            if(length <= 0 || length >=100) {
                PopupMessage.ErrorMessage(new IllegalArgumentException("Invalid length " + length ));
                return;
            }
            MotifsConstructor.CreateChain(cyAppManager.getCurrentNetwork(), length);
        };
    }

    public ActionListener cycle(){
        return e -> {
            String s = JOptionPane.showInputDialog(null, "Cycle lenght:");
            int length = Integer.parseInt(s);
            if(length <= 0 || length >=100 ) {
                PopupMessage.ErrorMessage(new IllegalArgumentException("Invalid length " + length ));
                return;
            }
            MotifsConstructor.CreateCycle(cyAppManager.getCurrentNetwork(), length);
        };
    }

    public ActionListener combine(){
        return e -> {
            CyNetwork network = cyAppManager.getCurrentNetwork();
            List<CyNode> nodes = CyTableUtil.getNodesInState(network,"selected",true);

            if(nodes.size()!=2) {
                PopupMessage.Message("You must select 2 nodes! Currently selected " + nodes.size());
                return;
            }

            CyNode mainNode = null;
            CyNode removeNode = null;

            String nameNode1 = network.getDefaultNodeTable().getRow(nodes.get(0).getSUID()).get("name", String.class);
            String nameNode2 = network.getDefaultNodeTable().getRow(nodes.get(1).getSUID()).get("name", String.class);
            String[] s= {nameNode1 + " (SUID = " + nodes.get(0).getSUID() + ")",
                    nameNode2 + " (SUID = " + nodes.get(1).getSUID() + ")",
                    "Cancel"};

            int select = JOptionPane.showOptionDialog(null, "Select the main node", "Select", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, s, s[2]);


            if(select==2) return;
            if(select==0) {
                mainNode = nodes.get(0);
                removeNode = nodes.get(1);
            }
            if(select==1) {
                mainNode = nodes.get(1);
                removeNode = nodes.get(0);
            }

            List<CyEdge> incomingEdges = network.getAdjacentEdgeList(removeNode, CyEdge.Type.INCOMING);
            List<CyEdge> outgoingEdges = network.getAdjacentEdgeList(removeNode, CyEdge.Type.OUTGOING);

            for(CyEdge i:incomingEdges)
            {
                network.addEdge(i.getSource(), mainNode, true);
            }
            for(CyEdge i:outgoingEdges)
            {
                network.addEdge(mainNode, i.getTarget(), true);
            }
            network.removeEdges(incomingEdges);
            network.removeEdges(outgoingEdges);
            nodes.remove(mainNode);
            network.removeNodes(nodes);
        };
    }


}
