package org.cytoscape.internal.motiffs;

import java.util.ArrayList;
import java.util.List;

public class MotiffsManager {

    private static MotiffsManager instance;

    private final List<IMotiff> motiffList;

    private MotiffsManager() {
        motiffList = new ArrayList<>();
    }

    public static MotiffsManager getInstance() {
        if (instance == null) {
            instance = new MotiffsManager();
        }
        return instance;
    }

    public void addMotiff(IMotiff motiff) {
        motiffList.add(motiff);
    }

    public List<IMotiff> getMotiffList() {
        return motiffList;
    }

}
