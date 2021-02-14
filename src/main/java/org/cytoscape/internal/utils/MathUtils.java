package org.cytoscape.internal.utils;

import org.cytoscape.internal.boolnet.functions.NodeTable;
import org.cytoscape.internal.boolnet.functions.State;

import java.util.HashMap;

public class MathUtils {

    public static int[][] getDefaultNodeTruthTable(int m, int n){

        if(m!=(int)Math.pow(2,n)) return null;

        int[][] table = new int[m][n];

        int s = 1;
        int r = m;
        for(int j=0; j<n; j++) {
            r = r/2;
            for(int i = 0;i<m;i++)
            {
                if(s<=r) table[i][j] = 0;
                else table[i][j] = 1;
                if(s==2*r) s=0;
                s++;
            }
        }

        return table;
    }

    public static int[] synchStep(HashMap<String, NodeTable> nodeFunctions, int[] currentValue){

        String[] arguments = nodeFunctions.keySet().toArray(new String[nodeFunctions.size()]);
        int[] nextValue = new int[currentValue.length];

        for(int j=0; j<currentValue.length;j++) {
            nextValue[j]=nodeFunctions.get(arguments[j]).getValue(arguments, currentValue);
        }

        return nextValue;
    }

    public static State synchStep(HashMap<String, NodeTable> nodeFunctions, State state){
        return new State(synchStep(nodeFunctions, state.toArray()));
    }
}
