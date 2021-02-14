package org.cytoscape.internal.boolnet.functions.alhoritms;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import org.cytoscape.internal.utils.MathUtils;
import org.cytoscape.internal.boolnet.functions.NodeTable;

public class FixedPoints {
	
	public static HashSet<int[]> simpleIterationMethod(HashMap<String, NodeTable> nodeFunctions, String[] arguments) {
		int n = nodeFunctions.size();
		int m = (int) Math.pow(2.0, n);
		
		int[][] allValues = MathUtils.getDefaultNodeTruthTable(m,n);
		
		/*int s = 1;
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
		}*/
		
		int[] result = new int[n];
		HashSet<int[]> fixedPoints = new HashSet<int[]>();
		fixedPoints.clear();
		
		for(int i=0; i<m;i++) {
			for(int j=0; j<n;j++) {
				result[j]=nodeFunctions.get(arguments[j]).getValue(arguments, allValues[i]);	
			}
			if(Arrays.equals(result, allValues[i])) fixedPoints.add(allValues[i]);
		}
		
		return fixedPoints;
	}

}
