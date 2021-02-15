package org.cytoscape.internal.parser;

public class BUtil {

	public static Integer[] parseFunction(String function, String[] arguments) throws ParserException
	{
		BFParser par = new BFParser();
		int n = arguments.length;
		int k = (int) Math.pow(2, n);
		int[][] T = new int[k][n+1];
		int m = k;
		int s = 1;

		Integer[] result = new Integer[k];
		
		for(int j = 0; j < n; j++) {
			m = m/2;
			for(int i = 0; i < k; i++)
			{
				if (s <= m) T[i][j] = 0;
				else T[i][j] = 1;
				if(s == 2*m) s=0;
				s++;
			}
		}
		
		for(int i = 0; i < k; i++) {
			for(int j = 0; j < n + 1; j++)
			{
				if (j == n)
				{	
					for(int r = 0; r < n; r++)	par.setVariable(arguments[r], T[i][r]);
						result[i] = par.Parse(function);
				}
			}
		}

		return result;
		
	}
}
