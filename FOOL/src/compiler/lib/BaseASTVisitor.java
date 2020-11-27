package compiler.lib;

import compiler.AST.*;
import static compiler.lib.FOOLlib.*;

public class BaseASTVisitor<S> {

	protected boolean print;
	protected String indent;

	protected BaseASTVisitor() {}
	protected BaseASTVisitor(boolean p) { print = p; }

	protected void printNode(Node n) {
		System.out.println(indent+extractNodeName(n.getClass().getName()));
	}

	protected void printNode(Node n, String s) {
		System.out.println(indent+extractNodeName(n.getClass().getName())+": "+s);
	}

	public S visit(Visitable v) {
		return visit(v, "  ");
	}

	public S visit(Visitable v, String s) {
		if (print) {
			String temp = indent;
			indent = (indent == null) ? "" : indent + s;
			S result = visitByAcc(v);
			indent = temp;
			return result;
		} else 
			return visitByAcc(v);
	}

	S visitByAcc(Visitable v) { /*qui fa il dynamic binding sul 
									tipo corretto*/
		return v.accept(this);
	}

	public S visitNode(ProgNode n) {throw new UnimplException();}
	public S visitNode(IntNode n) {throw new UnimplException();}
	public S visitNode(PlusNode n) {throw new UnimplException();}
	public S visitNode(TimesNode n) {throw new UnimplException();}
	public S visitNode(EqualNode n) {throw new UnimplException();}
	public S visitNode(BoolNode n) {throw new UnimplException();}
	public S visitNode(IfNode n) {throw new UnimplException();}
	public S visitNode(PrintNode n) {throw new UnimplException();}
//
	public S visitNode(ProgLetInNode n) {throw new UnimplException();}
	public S visitNode(BoolTypeNode n) {throw new UnimplException();}
	public S visitNode(IntTypeNode n) {throw new UnimplException();}
	public S visitNode(VarNode n) {throw new UnimplException();}
	public S visitNode(FunNode n) {throw new UnimplException();}
	public S visitNode(IdNode n) {throw new UnimplException();}
	public S visitNode(CallNode n) {throw new UnimplException();}
}