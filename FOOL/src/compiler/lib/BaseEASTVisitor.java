package compiler.lib;

import compiler.*;

public class BaseEASTVisitor<S> extends BaseASTVisitor<S>  {
	
	protected BaseEASTVisitor() {}
    protected BaseEASTVisitor(boolean p) { super(p); }
    
    protected void printSTentry(String s) {
    	System.out.println(indent+"STentry: "+s);
	}
	//questo metodo è la ragione dell'esistenza di questa classe 
	public S visitSTentry(STentry s) {throw new UnimplException();}
}
