package org.cytoscape.internal.utils;

import org.cytoscape.internal.boolnet.functions.NodeTable;
import org.cytoscape.internal.boolnet.functions.FunctionsManager;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;

public class SaveAndLoadUtil {

    public static void saveNetwork(BufferedWriter writer, FunctionsManager fManager) throws IOException {
        if (writer == null) return;
        HashMap<String, NodeTable> network = fManager.getAllNodeFunctions();
        String[] arguments = network.keySet().toArray(new String[fManager.getAllNodeFunctions().size()]);
        writer.write(".v ");
        writer.write(Integer.toString(arguments.length));
        writer.newLine();

        for (String str : arguments) {
            writer.write("# " + str + " ");
            writer.write(Integer.toString(fManager.getArgumentIndex(str)));
            writer.newLine();
        }

        NodeTable nodetable;
        for (String str : arguments) {
            nodetable = fManager.getNodeTablebyNode(str);
            writer.newLine();
            writer.write(".n " + fManager.getArgumentIndex(nodetable.getNode()) + " "
                    + nodetable.getArguments().length + " ");
            for (String arg : nodetable.getArguments()) {
                writer.write(fManager.getArgumentIndex(arg) + " ");
            }
            writer.newLine();
            for (int i = 0; i < nodetable.getRowCount(); i++) {
                for (int j = 0; j < nodetable.getColumnCount(); j++) {
                    if (j == nodetable.getColumnCount() - 2) writer.write(nodetable.getTable()[i][j] + " ");
                    else writer.write(Integer.toString(nodetable.getTable()[i][j]));
                }
                writer.newLine();
            }

        }
    }
}
