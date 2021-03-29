package org.cytoscape.internal.boolnet.functions;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class FunctionsManager {

    private HashMap<String, NodeTable> nodeFunctions;
    private HashMap<String, Integer> argumentsMatcherMap;

    public FunctionsManager() {
        this.nodeFunctions = new LinkedHashMap<String, NodeTable>();
        this.argumentsMatcherMap = new HashMap<String, Integer>();
    }

    public void addNodeTable(String[] arguments, String node, Integer[] values) {
        nodeFunctions.put(node, new NodeTable(arguments, node, values));
        addNodeToMatcherMap(node);
    }

    public void addNodeTable(String[] arguments, String node) {
        nodeFunctions.put(node, new NodeTable(arguments, node));
        addNodeToMatcherMap(node);
    }

    private void addNodeToMatcherMap(String node) {
        if (!argumentsMatcherMap.containsKey(argumentsMatcherMap.size() + 1))
            argumentsMatcherMap.put(node, argumentsMatcherMap.size() + 1);
        else {
            for (int key = 1; key <= (argumentsMatcherMap.size() + 1); key++)
                if (!argumentsMatcherMap.containsKey(key)) {
                    argumentsMatcherMap.put(node, key);
                    return;
                }
        }
    }

    public NodeTable getNodeTablebyNode(String node) {
        return nodeFunctions.get(node);
    }

    public boolean containsNode(String node) {
        return nodeFunctions.containsKey(node);
    }

    public boolean nodeTableIsEmpty() {
        return nodeFunctions.isEmpty();
    }

    public void removeNodeTable(String node) {
        nodeFunctions.remove(node);
        argumentsMatcherMap.remove(node);
    }

    public void removeAllNodeTables() {
        nodeFunctions.clear();
        argumentsMatcherMap.clear();
    }

    public int size() {
        return this.nodeFunctions.size();
    }

    public HashMap<String, NodeTable> getAllNodeFunctions() {
        return nodeFunctions;
    }

    public Integer getArgumentIndex(String argument) {
        if (!argumentsMatcherMap.containsKey(argument)) return -1;
        return argumentsMatcherMap.get(argument);
    }

}
