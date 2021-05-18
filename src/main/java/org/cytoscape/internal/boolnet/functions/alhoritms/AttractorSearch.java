package org.cytoscape.internal.boolnet.functions.alhoritms;

import org.cytoscape.internal.utils.MathUtils;
import org.cytoscape.internal.boolnet.functions.FunctionsManager;
import org.cytoscape.internal.boolnet.functions.NodeTable;
import org.cytoscape.internal.boolnet.functions.State;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class AttractorSearch {

    public static final int FULL_ATTRACTORS_MODE = 1;
    public static final int FIXED_POINT_MODE = 2;

    private HashMap<State, Integer> attractorAssigmentsMap;

    private int mode;

    public AttractorSearch() {
        attractorAssigmentsMap = new HashMap<>();
    }

    public AttractorSearch(int mode) {
        this();
        if (mode == FULL_ATTRACTORS_MODE) {
            this.mode = FULL_ATTRACTORS_MODE;
        } else if (mode == FIXED_POINT_MODE) {
            this.mode = FIXED_POINT_MODE;
        } else {
            throw new IllegalArgumentException("Invalid mode code + '" + mode + "'.");
        }
    }

    public ArrayList<ArrayList<State>> search(FunctionsManager manager) {
        HashMap<String, NodeTable> nodeFunctions = manager.getAllNodeFunctions();
        int n = nodeFunctions.keySet().size();
        int m = (int) Math.pow(2, n);

        HashSet<State> startStates = initStartStates(m, n);
        ArrayList<ArrayList<State>> result = new ArrayList<>();

        int currentAttractor = 0;

        State current;

        for (State startState : startStates) {
            if (attractorAssigment(startState) == 0) {
                current = startState;
                currentAttractor++;
                while (attractorAssigment(current) == 0) {
                    setAttractorAssigment(current, currentAttractor);
                    current = MathUtils.synchStep(nodeFunctions, current);
                }
                if (attractorAssigment(current) == currentAttractor) {
                    State attractorStart = current;
                    ArrayList<State> attractor = new ArrayList<>();
                    if (mode == FULL_ATTRACTORS_MODE) {
                        do {
                            attractor.add(current);
                            current = MathUtils.synchStep(nodeFunctions, current);
                        } while (!current.equals(attractorStart));
                    } else {
                        if (current.equals(MathUtils.synchStep(nodeFunctions, current))) {
                            attractor.add(current);
                        } else {
                            continue;
                        }
                    }
                    result.add(attractor);
                } else {
                    State attractorStart = current;
                    current = startState;
                    while (!current.equals(attractorStart)) {
                        setAttractorAssigment(current, attractorAssigment(attractorStart));
                        current = MathUtils.synchStep(nodeFunctions, current);
                    }
                }
            }
        }
        return result;
    }

    private HashSet<State> initStartStates(int m, int n) {
        HashSet<State> startStates = new HashSet<>();
        int[][] table = MathUtils.getDefaultNodeTruthTable(m, n);
        for (int i = 0; i < m; i++) {
            startStates.add(new State(table[i]));
        }
        return startStates;
    }

    private int attractorAssigment(State state) {
        return attractorAssigmentsMap.getOrDefault(state, 0);
    }

    private void setAttractorAssigment(State state, int var) {
        attractorAssigmentsMap.put(state, var);
    }
}
