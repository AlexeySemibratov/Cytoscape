package org.cytoscape.internal.boolnet.functions;

public class State {

    int[] state;

    public State(int[] state) {
        this.state = state;
    }

    public int[] toArray() {
        return state;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof State)) return false;

        int[] objArray = ((State) obj).toArray();

        if (state.length != objArray.length) return false;

        for (int i = 0; i < state.length; i++) {
            if (state[i] != objArray[i]) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hashCode = 7;
        for (int i = 0; i < state.length; i++) {
            hashCode = hashCode * 37 + state[i];
        }
        return hashCode;
    }


}
