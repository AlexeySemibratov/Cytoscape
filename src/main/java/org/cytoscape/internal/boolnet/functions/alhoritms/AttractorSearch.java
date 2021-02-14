package org.cytoscape.internal.boolnet.functions.alhoritms;

import org.cytoscape.internal.utils.MathUtils;
import org.cytoscape.internal.boolnet.functions.FunctionsManager;
import org.cytoscape.internal.boolnet.functions.NodeTable;
import org.cytoscape.internal.boolnet.functions.State;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class AttractorSearch {

    private HashMap<State, Integer> attractorAssigmentsMap;

    public AttractorSearch(){
        attractorAssigmentsMap = new HashMap<>();
    }

    public ArrayList<ArrayList<State>> search(FunctionsManager manager){

        HashMap<String, NodeTable> nodeFunctions = manager.getAllNodeFunctions();
        String[] arguments = nodeFunctions.keySet().toArray(new String[nodeFunctions.size()]);
        int n = arguments.length;
        int m = (int) Math.pow(2,n);

        HashSet<State> startStates = initStartStates(m,n);
        ArrayList<ArrayList<State>> result = new ArrayList<>();

        int currentAttractor = 0;

        State current;

        for(State startState:startStates){
            if(attractorAssigment(startState) == 0) {
                current = startState;
                currentAttractor++;
                while(attractorAssigment(current) == 0) {
                    setAttractorAssigment(current, currentAttractor);
                    current = MathUtils.synchStep(nodeFunctions, current);
                }
                if(attractorAssigment(current) == currentAttractor) {
                    State attractorStart = current;
                    ArrayList<State> attractor = new ArrayList<>();
                    do {
                        attractor.add(current);
                        current = MathUtils.synchStep(nodeFunctions,current);
                    } while(!current.equals(attractorStart));
                    result.add(attractor);
                } else {
                    State attractorStart = current;
                    current = startState;
                    while(!current.equals(attractorStart)) {
                        setAttractorAssigment(current,attractorAssigment(attractorStart));
                        current = MathUtils.synchStep(nodeFunctions,current);
                    }
                }
            }
        }

        return result;
    }

    private HashSet<State> initStartStates(int m, int n){
        HashSet<State> startStates = new HashSet<>();
        int[][] table = MathUtils.getDefaultNodeTruthTable(m,n);
        for(int i=0;i<m;i++){
            startStates.add(new State(table[i]));
        }
        return startStates;
    }

    private int attractorAssigment(State state){
        return attractorAssigmentsMap.containsKey(state) ? attractorAssigmentsMap.get(state) : 0;
    }

    private void setAttractorAssigment(State state, int var) {
        attractorAssigmentsMap.put(state,var);
    }
}
