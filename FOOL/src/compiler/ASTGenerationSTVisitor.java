package compiler;

import org.antlr.v4.runtime.tree.*;
import compiler.FOOLParser.*;
import compiler.AST.*;
import compiler.lib.*;

public class ASTGenerationSTVisitor extends FOOLBaseVisitor<Node> {

	String indent;
	
    @Override
	public Node visit(ParseTree t) {
        String temp=indent;
        indent=(indent==null)?"":indent+"  ";
        Node result = super.visit(t);
        indent=temp;
        return result;       
	}

	@Override
	public Node visitProg(ProgContext c) {
		System.out.println(indent+"prog");
		return new ProgNode( visit(c.exp()) );
	}
	
	@Override
	public Node visitHighPriOp(HighPriOpContext c) {
		System.out.println(indent+"exp: prod with TIMES");
		return new TimesNode( visit(c.exp(0)), visit(c.exp(1)) );
	}

	@Override
	public Node visitMediumPriOp(MediumPriOpContext c) {
		System.out.println(indent+"exp: prod with PLUS");
		return new PlusNode( visit(c.exp(0)), visit(c.exp(1)) );
	}

	@Override
	public Node visitPars(ParsContext c) {
		System.out.println(indent+"exp: prod with LPAR RPAR");
		return visit(c.exp());
	}

	@Override
	public Node visitInteger(IntegerContext c) {
		int v=Integer.parseInt(c.NUM().getText());	
		boolean minus= c.MINUS( ) != null;
		int res= minus?-v:v;
		System.out.println(indent+"exp: prod with "+(minus?"MINUS ":"")+"NUM "+res );
		return new IntNode(res);
	}
}
