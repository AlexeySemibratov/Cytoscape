package org.cytoscape.internal.load;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class LoadManager {

    //TestComment

    private List<File> netFiles;

    public LoadManager(){
        netFiles = new ArrayList<>();
    }

    public LoadManager(List<File> files){
        netFiles = files;
    }

    public File getNetworkFile(String networkName){
        if(netFiles.isEmpty() || netFiles.size() == 0) return null;

        for(File f:netFiles){
            if(f.getName().equals(networkName + "_function_table.cnet")) return f;
        }

        return null;
    }
}
