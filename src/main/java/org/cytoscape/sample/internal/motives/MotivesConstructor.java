package org.cytoscape.sample.internal.motives;


import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

public class MotivesConstructor {
	
	public static void CreatePath(CyNetwork network, int length)
	{	
		CyNode[] nodes = new CyNode[length];
		
		for(int i=0; i<length; i++)
		{
			nodes[i]=network.addNode();
			network.getDefaultNodeTable().getRow(nodes[i].getSUID()).set("name", "Path " + (i+1));
		}
		
		
		for(int i=0; i<length-1; i++)
		{
			network.addEdge(nodes[i], nodes[i+1], true);
		}
		
	}
	
	public static void CreateCycle(CyNetwork network, int length)
	{	
		CyNode[] nodes = new CyNode[length];
		for(int i=0; i<length; i++)
		{
			nodes[i]=network.addNode();
			network.getDefaultNodeTable().getRow(nodes[i].getSUID()).set("name", "Cycle " + (i+1));
		}
		for(int i=0; i<length-1; i++)
		{
			network.addEdge(nodes[i], nodes[i+1], true);
		}
		network.addEdge(nodes[length-1], nodes[0], true);
	}

}
