package compiler;

import java.util.*;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import compiler.AST.*;
import compiler.FOOLParser.*;
import compiler.lib.*;
import static compiler.lib.FOOLlib.*;

public class ASTGenerationSTVisitor extends FOOLBaseVisitor<Node> {

	String indent;
    public boolean print;
	
    ASTGenerationSTVisitor() {}    
    ASTGenerationSTVisitor(boolean debug) { print=debug; }
        /*
         * lui prende in generale 
         */
    private void printVarAndProdName(ParserRuleContext ctx) {
        String prefix="";        
        					
    	Class<?> ctxClass=ctx.getClass(), parentClass=ctxClass.getSuperclass(); //getCLass prende la classe con la reflection e getsuperclass prende quella sopra
        if (!parentClass.equals(ParserRuleContext.class)) // parentClass is the var context (and not ctxClass itself) ; se è parse rule context è già una var se no vuol dire che lo devo ottenre dal genitore        												
        	prefix=lowerizeFirstChar(extractCtxName(parentClass.getName()))+": production #"; //è il caso in cui il var context lo ottengo dalla superclasse; prima estraendo il nome della var
        																						//utilizza delle piccole funzioni di libreria per tagliare e formattare il nome come ci serve con delle funzioni in FFOLli
    	System.out.println(indent+prefix+lowerizeFirstChar(extractCtxName(ctxClass.getName())));    //prefisso nome var : prodction # e nome vero della var
    																								//nb se ctx conteneva già il nome della var prefix resta vuoto e stampodirettamente il nome della var 
    }
    /*
     * I metodi di visita sono presentati nello stesso ordine della grammatica; per gli elementi sintattici che già c'erano non è cambiato nulla;
     * l'unica differenza è che nella stampa è stata messa una cosa automatica: utilizzando la reflection: idea metto 'var : prod applicata'
     * andando ad indagare tramite reflection sul tipo di C: quando va ad analizzarre il simbolo va nella grammatica .g4 e prende il nome che segue # ossia il nome della classe
     * per recuperare exp devo guardare da che classe eredita la c considerata; le prod di una variabili ereditano da ...
     * es c è un paramentro con la sua classe ottengo times e poi vedo che eredeita da exp e lo metto dopo i due punti (?)
     * 
     *  Quando ho un unica produzione faccio il visit solo sul progcontext perchè quello è il nodo dell'albero e la produzione appplicata è l'unica che c'è
     */
    
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
		if (print) printVarAndProdName(c);
		return visit(c.progbody());
	}

	@Override
	public Node visitLetInProg(LetInProgContext c) { 
		if (print) printVarAndProdName(c);
		for (DecContext dec : c.dec()) visit(dec);
		visit(c.exp());
		return null;
	}

	@Override
	public Node visitNoDecProg(NoDecProgContext c) {
		if (print) printVarAndProdName(c);
		return new ProgNode(visit(c.exp()));
	}

	@Override
	public Node visitTimes(TimesContext c) {
		if (print) printVarAndProdName(c);
		return new TimesNode(visit(c.exp(0)), visit(c.exp(1)));
	}

	@Override
	public Node visitPlus(PlusContext c) {
		if (print) printVarAndProdName(c);
		return new PlusNode(visit(c.exp(0)), visit(c.exp(1)));
	}

	@Override
	public Node visitEq(EqContext c) {
		if (print) printVarAndProdName(c);
		return new EqualNode(visit(c.exp(0)), visit(c.exp(1)));
	}

	@Override
	public Node visitVardec(VardecContext c) {
		if (print) printVarAndProdName(c);
		visit(c.type()); 
		visit(c.exp());
        return null;
	}

	@Override
	public Node visitFundec(FundecContext c) {
		if (print) printVarAndProdName(c);
		for (DecContext dec : c.dec()) visit(dec);
		visit(c.type(0)); 
		visit(c.exp());
		return null;
	}

	@Override
	public Node visitIntType(IntTypeContext c) {
		if (print) printVarAndProdName(c);
		return null;
	}

	@Override
	public Node visitBoolType(BoolTypeContext c) {
		if (print) printVarAndProdName(c);
		return null;
	}

	@Override
	public Node visitInteger(IntegerContext c) {
		if (print) printVarAndProdName(c);
		int v = Integer.parseInt(c.NUM().getText());
		return new IntNode(c.MINUS()==null?v:-v);
	}

	@Override
	public Node visitTrue(TrueContext c) {
		if (print) printVarAndProdName(c);
		return new BoolNode(true);
	}

	@Override
	public Node visitFalse(FalseContext c) {
		if (print) printVarAndProdName(c);
		return new BoolNode(false);
	}

	@Override
	public Node visitIf(IfContext c) {
		if (print) printVarAndProdName(c);
		Node ifNode = visit(c.exp(0));
		Node thenNode = visit(c.exp(1));
		Node elseNode = visit(c.exp(2));
		return new IfNode(ifNode, thenNode, elseNode);
	}

	@Override
	public Node visitPrint(PrintContext c) {
		if (print) printVarAndProdName(c);
		return new PrintNode(visit(c.exp()));
	}

	@Override
	public Node visitPars(ParsContext c) {
		if (print) printVarAndProdName(c);
		return visit(c.exp());
	}

	@Override
	public Node visitId(IdContext c) {
		if (print) printVarAndProdName(c);
		return null;
	}

	@Override
	public Node visitCall(CallContext c) {
		if (print) printVarAndProdName(c);
		return null;
	}
}

//n.setLine(c.ID().getSymbol().getLine());
