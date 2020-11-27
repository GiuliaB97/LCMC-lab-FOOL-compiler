package compiler_lab7;

import org.antlr.v4.runtime.tree.*;

import compiler.FOOLBaseVisitor;
import compiler.FOOLParser.*;
import compiler.lib.*;
import compiler_lab7.AST.*;

/*'ASTGeneration': perchè sto facendo questa cosa: per generare l'AST 'STVisitor':
suffisso cosa sto visitando le classi context*/
public class ASTGenerationSTVisitor extends FOOLBaseVisitor<Node> {

	String indent;
	//Questa è la fase in cui creo i Node
	
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
		return new TimesNode( visit(c.exp(0)), visit(c.exp(1)) ); /*quando stampiamo stiamo dicendo in quel 
																	*punto dell'albero cosa c'è; in quel punto
																	* dell'albero c'è exp che ha come figli quelli 
																	* della produzione times exp TIMES exp (cioè 
																	* quello che vado a stampare con le visite
																	*/
	}

	@Override
	public Node visitMediumPriOp(MediumPriOpContext c) {
		System.out.println(indent+"exp: prod with PLUS");
		return new PlusNode( visit(c.exp(0)), visit(c.exp(1)) );
	}
	
	@Override
	public Node visitLowPriOp(LowPriOpContext c) {
		System.out.println(indent+"exp: prod with EQ");
		return new EqualNode( visit(c.exp(0)), visit(c.exp(1)) ); //exp(0): ciò che c'è prima del '=='
																	// queste mi ritorneranno un node che sarà la radice del sotto albero
	}
	
	@Override
	public Node visitPars(ParsContext c) {
		System.out.println(indent+"exp: prod with LPAR RPAR");
		return visit(c.exp());
	}

	@Override
	public Node visitInteger(IntegerContext c) {
		int v=Integer.parseInt(c.NUM().getText());	
		boolean minus=(c.MINUS()!=null);
		int res=minus?-v:v;
		System.out.println(indent+"exp: prod with "+(minus?"MINUS ":"")+"NUM "+res );
		return new IntNode(res);
	}

	@Override
	public Node visitTrue(TrueContext c) {
		System.out.println(indent+"exp: prod with TRUE");
		return new BoolNode(true);
	}

	@Override
	public Node visitFalse(FalseContext c) {
		System.out.println(indent+"exp: prod with FALSE");
		return new BoolNode(false);
	}

	@Override
	public Node visitIf(IfContext c) {
		System.out.println(indent+"exp: prod with IF THEN CLPAR CRPAR ELSE CLPAR CRPAR"); /*questo nodo exp ha come figli 
																							*exp0, exp1, exp2,  e  
																							IF THEN CLPAR CRPAR ELSE CLPAR CRPAR: 
																							tutti i figli che...*/
		return new IfNode( visit(c.exp(0)), visit(c.exp(1)), visit(c.exp(2)) ); 
	}

	@Override
	public Node visitPrint(PrintContext c) {
		System.out.println(indent+"exp: prod with PRINT LPAR RPAR");
		return new PrintNode( visit(c.exp()) );
	}

}
