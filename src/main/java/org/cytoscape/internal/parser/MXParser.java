package org.cytoscape.internal.parser;

import org.cytoscape.internal.utils.MathUtils;
import org.mariuszgromada.math.mxparser.Expression;


public class MXParser {

    public static Integer[] parse(String function, String[] arguments) {
        Expression e = new Expression();
        e.setExpressionString(function);
        e.defineArguments(arguments);
        int n = arguments.length;
        int m = (int) Math.pow(2, n);
        Integer[] result = new Integer[m];
        int[][] argTable = MathUtils.getDefaultNodeTruthTable(m, n);
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                e.setArgumentValue(arguments[j], argTable[i][j]);
            }
            result[i] = (int) e.calculate();
        }
        return result;
    }
}
