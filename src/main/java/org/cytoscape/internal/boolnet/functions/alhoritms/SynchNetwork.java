package org.cytoscape.internal.boolnet.functions.alhoritms;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.internal.boolnet.functions.NodeTable;

public class SynchNetwork {

	public static CyNetwork create(HashMap<String, NodeTable> nodeFunctions, CyNetworkFactory cnf) {
		CyNetwork network = cnf.createNetwork();
		String[] arguments = nodeFunctions.keySet().toArray(new String[nodeFunctions.size()]);
		
		int n = nodeFunctions.size();
		int m = (int) Math.pow(2.0, n);
		
		int[][] allValues = new int[m][n];
		
		int s = 1;
		int r = m;
		for(int j=0; j<n; j++) {
			r = r/2;
			for(int i = 0;i<m;i++) 
			{
				if(s<=r) allValues[i][j] = 0;
				else allValues[i][j] = 1;
				if(s==2*r) s=0;
				s++;
			}
		}
		
		CyNode addedNode;
		
		for(int i=0; i<m;i++) {
			addedNode = network.addNode();
			network.getDefaultNodeTable().getRow(addedNode.getSUID()).set("name", Arrays.toString(allValues[i]));
		}
		
		CyNode firstNode = null, secondNode = null;
		int[] secondNodeValue = new int[n];
		
		List<CyRow> rows = network.getDefaultNodeTable().getAllRows();
		
		for(int i=0; i<m;i++) {
			
			for(CyRow row:rows) {
				if(row.get("name", String.class).equals(Arrays.toString(allValues[i]))) {
					firstNode = network.getNode(row.get("SUID", Long.class));
					break;
				}
			}
			
			for(int j=0; j<n;j++) {
				secondNodeValue[j]=nodeFunctions.get(arguments[j]).getValue(arguments, allValues[i]);	
			}
			
			for(CyRow row:rows) {
				if(row.get("name", String.class).equals(Arrays.toString(secondNodeValue))) {
					secondNode = network.getNode(row.get("SUID", Long.class));
					break;
				}
			}
			if(firstNode != null && secondNode != null )
				network.addEdge(firstNode, secondNode, true);

		}
		return network;
	}
	
}
