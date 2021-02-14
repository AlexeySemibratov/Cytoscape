package org.cytoscape.internal.boolnet.functions;

import org.cytoscape.internal.utils.PopupMessage;

import java.util.Arrays;

public class NodeTable {
	
	private String node;
	private String[] arguments;
	private int[][] table;
	private int m,n;
	
	public NodeTable(String[] arguments, String node, Integer[] values) {
		this.arguments = arguments;
		this.node = node;
		this.m = (int) Math.pow(2.0, arguments.length);
		this.n = arguments.length + 1;
		table = new int[m][n];
		fillTable(values);
	}

	public NodeTable(String[] arguments, String node) {
		this.arguments = arguments;
		this.node = node;
		this.m = (int) Math.pow(2.0, arguments.length);
		this.n = arguments.length + 1;
		table = new int[m][n];
		Integer[] zeroValues = new Integer[m];
		Arrays.fill(zeroValues,0);
		fillTable(zeroValues);
	}
	
	private void fillTable(Integer[] values) {
		int s = 1;
		int r = m;
		for(int j=0; j<(n-1); j++) {
			r = r/2;
			for(int i = 0;i<m;i++) 
			{
				if(s<=r) table[i][j] = 0;
				else table[i][j] = 1;
				if(s==2*r) s=0;
				s++;
			}
		}
		
		for(int i = 0; i<m;i++) {
			table[i][n-1] = values[i];
		}
		
	}
	
	public int getValue(String[] args, int[] values) {
		
		if(args.length!=values.length) {
			PopupMessage.ErrorMessage(new IllegalArgumentException("The number of variables does not match the number of set values."));
		}

		int[] localArgs = new int[arguments.length];
		
		for(int k=0; k<args.length; k++)
		{
			for(int s=0;s<arguments.length;s++) {
				if(arguments[s].equals(args[k])) {
					localArgs[s] = values[k];
					break;
				}
			}
		}
		
		return getValuebyLocalArgs(localArgs);
	}
	
	private int getValuebyLocalArgs(int[] args) {
		int i=0;
		boolean flag = true;
		
		while(i<m) {
			for(int j=0;j < n-1; j++) {
				if(args[j]!=table[i][j]) {
					flag = false;
					break;
				}
			}
			if(flag) return table[i][n-1];
			i++;
			flag = true;
		}
		return -1;
	}
	
	public String toString() {
		return "[" + node +"; " + Arrays.toString(arguments) + "] ";
	}

	public void setValues(int[] values){
		if(values.length!=m) {
			PopupMessage.ErrorMessage(new IllegalArgumentException("The number of variables does not match the number of set values."));
		}
		else {
			for(int i=0;i<m;i++) table[i][n-1] = values[i];
		}
	}

	public int[] getValues(){
		int[] values = new int[m];
		for(int i=0;i<m;i++) values[i] = table[i][n-1];
		return values;
	}

	public String[] getArguments() {
		return arguments;
	}

	public int getRowCount(){
		return m;
	}

	public int getColumnCount(){
		return n;
	}

	public String getNode()
	{
		return node;
	}

	public int[][] getTable(){
		return table;
	}
}
