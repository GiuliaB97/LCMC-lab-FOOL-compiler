package compiler;

import java.util.*;
import java.util.Map;

import compiler.AST.*;
import compiler.lib.*;
import compiler.lib.Node;

public class SymbolTableASTVisitor extends BaseASTVisitor<Void> {
	
	int stErrors=0;
	private List<Map<String, STentry>> symTable = new ArrayList<>();
	private int nestingLevel=0;
	//livello ambiente con dichiarazioni piu' esterno è 0 (prima posizione ArrayList) invece che 1 (slides)
	//il "fronte" della lista di tabelle è symTable.get(nestingLevel)

	SymbolTableASTVisitor() {}
	SymbolTableASTVisitor(boolean debug) {super(debug);} // p=true enables print for debugging

	@Override
	public Void visitNode(ProgNode n) {
		if (print) printNode(n);
		visit(n.exp);
		return null;
	}
	
	@Override
	public Void visitNode(IntNode n) {
		if (print) printNode(n, n.val.toString());
		return null;
	}
	
	@Override
	public Void visitNode(PlusNode n) {
		if (print) printNode(n);
		visit(n.left);
		visit(n.right);
		return null;
	}
	
	@Override
	public Void visitNode(TimesNode n) {
		if (print) printNode(n);
		visit(n.left);
		visit(n.right);
		return null;
	}
	
	@Override
	public Void visitNode(EqualNode n) {
		if (print) printNode(n);
		visit(n.left);
		visit(n.right);
		return null;
	}
	
	@Override
	public Void visitNode(BoolNode n) {
		if (print) printNode(n, n.val.toString());
		return null;
	}
	
	@Override
	public Void visitNode(IfNode n) {
		if (print) printNode(n);
		visit(n.cond);
		visit(n.th);
		visit(n.el);
		return null;
	}
	
	@Override
	public Void visitNode(PrintNode n) {
		if (print) printNode(n);
		visit(n.exp);
		return null;
	}

//
	@Override
	public Void visitNode(ProgLetInNode n) {
		if (print) printNode(n);
		for (Node dec : n.declist) visit(dec);
		visit(n.exp);
		return null;
	}

	@Override
	public Void visitNode(VarNode n) {
		if (print) printNode(n,n.id);
		visit(n.exp);
		return null;
	}

	@Override
	public Void visitNode(FunNode n) {
		if (print) printNode(n,n.id);
		// for (ParNode par : n.parlist) visit(par);
		for (Node dec : n.declist) visit(dec);
		visit(n.exp);
		return null;
	}

//	private STentry stLookup(String id) {}
	
	@Override
	public Void visitNode(IdNode n) {
		if (print) printNode(n);
		return null;
	}

	@Override
	public Void visitNode(CallNode n) {
		if (print) printNode(n);
		// for (Node arg : n.arglist) visit(arg);
		return null;
	}
}
//System.out.println("Var id " + n.id + " at line "+ n.getLine() +" already declared");
