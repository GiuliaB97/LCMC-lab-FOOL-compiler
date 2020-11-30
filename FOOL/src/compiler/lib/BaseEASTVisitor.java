package compiler.lib;

import compiler.*;
import compiler.exc.UnimplException;
/*
 * Anche lui riceve la classe su cui utilizzo le eccezioni E
 * e dichaira di gettare E
 */
public class BaseEASTVisitor<S,E extends Exception> extends BaseASTVisitor<S,E>  {
	
	protected BaseEASTVisitor() {}//Se uso questo non lo abilito
	protected BaseEASTVisitor(boolean ie) { super(ie); } 
    protected BaseEASTVisitor(boolean ie, boolean p) { super(ie, p); }
    
    protected void printSTentry(String s) {
    	System.out.println(indent+"STentry: "+s);
	}
	
	public S visitSTentry(STentry s) throws E {throw new UnimplException();}
}

// 
