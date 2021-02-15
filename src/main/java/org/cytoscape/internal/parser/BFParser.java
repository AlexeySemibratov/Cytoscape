package org.cytoscape.internal.parser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class BFParser {

	public static Set<Character> DEFAULT_SYMBOLS = new HashSet<>();
	
	private HashMap<String, Integer> variables;
	
	public BFParser()
	{
		variables = new HashMap<>();
		initSymbolsSet();
	}

	private void initSymbolsSet(){
		DEFAULT_SYMBOLS.add(Symbols.AND);
		DEFAULT_SYMBOLS.add(Symbols.OR);
		DEFAULT_SYMBOLS.add(Symbols.NOT);
		DEFAULT_SYMBOLS.add('(');
		DEFAULT_SYMBOLS.add(')');
	}
	
	public void setVariable(String v_name, Integer value)
	{
		variables.put(v_name, value);
	}
	
	public Integer getVariable(String c)
	{
		if(!variables.containsKey(c))
		{
			return -1;
		}
		return variables.get(c);
	}

	
	private String Form(String s)
	{
		String p;
		p = s.replaceAll(" ", "");
		p = p.replace("OR", "|");
		p = p.replace("AND", "&");
		p = p.replace("NOT", "!");
		p = p.replace("||", "|");
		p = p.replace("&&", "&");
		return p;
	}
	
	public Integer Parse(String s) throws ParserException {
		s = Form(s);
		Result result = Or(s);
		if(!result.rest.isEmpty())
		{
			throw new ParserException("Error when parsing string <" + result.rest + ">.");
		}
		return result.cur;
	}
	
	private Result Or(String s) throws ParserException {
		Result current = And(s);
		int acc = current.cur;
		
		while(current.rest.length() > 0)
		{
			if(current.rest.charAt(0) != '|') break;
			
			String next = current.rest.substring(1);
			current = And(next);
			
			if(current.cur == 1 || acc == 1) acc = 1;
			else acc = 0;
			
		}	
		
		return new Result(acc, current.rest);
	}
	
	private Result And(String s) throws ParserException {
		Result current = Not(s);
		int acc = current.cur;
		while(true)
		{
			if(current.rest.length() == 0) {
				return current;
			}
			
			char sign = current.rest.charAt(0);
			if(sign != '&') return current;
			String next = current.rest.substring(1);
			Result right = Not(next);
			if(acc == 0 || right.cur == 0) acc = 0;
			else acc = 1;
			
			current = new Result(acc,right.rest);
			
		}

	}
	
	private Result Not(String s) throws ParserException {
		char c = s.charAt(0);
		if(c == '!' && s.charAt(1) != '(')
		{
			int v;
			String var = readVariable(s.substring(1));
			if(getVariable(var) == 0) v = 1;
			else v = 0;
			return new Result(v,s.substring(var.length()+1));
		}
		if(c == '!' && s.charAt(1) == '(')
		{		
			Result res =  Bracket(s.substring(1));
			if(res.cur == 0) res.cur = 1;
			else res.cur = 0;
			return res;
		}
		return Bracket(s);
	}
	
	private Result Bracket(String s) throws ParserException {
		
		if(s.charAt(0) == '(')
		{
			Result result = Or(s.substring(1));
			if(result.rest.isEmpty() || result.rest.charAt(0) != ')')
				throw new ParserException("Expected: <)>, but there was: <" + result.rest + ">.");
			else
				result.rest = result.rest.substring(1);
			return result;		
		}
		return Var(s);
	}
	
	private Result Var(String s) throws ParserException {
		String c = readVariable(s);
		if(!variables.containsKey(c))
		{
			throw new ParserException("Variable <" + c + "> does not exist! Error when parsing <" + s + ">");
		}
		return new Result(getVariable(c),s.substring(c.length()));
	}

	private String readVariable(String c){
		StringBuilder var = new StringBuilder();
		int i = 0;
		while(!DEFAULT_SYMBOLS.contains(c.charAt(i))){
			var.append(c.charAt(i));
			i++;
			if(i == c.length()) break;
		}
		return var.toString();
	}

}
