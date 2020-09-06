package org.cytoscape.sample.internal.motives;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.List;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableUtil;

public class MotivesPanel extends JPanel implements CytoPanelComponent {
	
	private static final long serialVersionUID = 8292406955891826912L;
	
	private CyApplicationManager cyApplicationManager;

	private JButton btn, btn1, btn2;
	
	public MotivesPanel(CyApplicationManager cyAppManager){
		cyApplicationManager=cyAppManager;
		
		btn = new JButton("Combine 2 nodes");
		btn1 = new JButton("Add Path");
		btn2 = new JButton("Add Cycle");
		this.add(btn1);
		this.add(btn2);
		this.add(btn);
		addInputListeners();
		this.setVisible(true);
	}
	

	private void addInputListeners() {
		btn1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {	
				
				String s = JOptionPane.showInputDialog(null, "Path lenght:");
				int length = Integer.parseInt(s);
				if(length <= 0 || length >=100 ) {
					JOptionPane.showMessageDialog(null, "Invalid length " + length);
					return;
				}
				MotivesConstructor.CreatePath(cyApplicationManager.getCurrentNetwork(), length);
                }
            });
		btn2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {	
				
				String s = JOptionPane.showInputDialog(null, "Cycle lenght:");
				int length = Integer.parseInt(s);
				if(length <= 0 || length >=100 ) {
					JOptionPane.showMessageDialog(null, "Invalid length " + length);
					return;
				}
				MotivesConstructor.CreateCycle(cyApplicationManager.getCurrentNetwork(), length);
                }
            });
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				CyNetwork network = cyApplicationManager.getCurrentNetwork();
				List<CyNode> nodes = CyTableUtil.getNodesInState(network,"selected",true);
				
				if(nodes.size()!=2) {
					JOptionPane.showMessageDialog(null, "You must select 2 nodes! Currently selected " + nodes.size());
					return;
				}
				
				CyNode mainNode = null;
				CyNode removeNode = null;
				
				String nameNode1 = network.getDefaultNodeTable().getRow(nodes.get(0).getSUID()).get("name", String.class);
				String nameNode2 = network.getDefaultNodeTable().getRow(nodes.get(1).getSUID()).get("name", String.class);
				String[] s= {nameNode1 + " (SUID = " + nodes.get(0).getSUID() + ")",
						nameNode2 + " (SUID = " + nodes.get(1).getSUID() + ")",
						"Cancel"};
				
				//JOptionPane.showMessageDialog(null, "mainNode = " + mainNode.getSUID() + " name = " + nameNode1);
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
                }
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
		return "Motives list";
	}

	@Override
	public Icon getIcon() {
		return null;
	}
	
	

}
