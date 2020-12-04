package compiler_lab9;

import java.util.*;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import compiler.FOOLBaseVisitor;
import compiler.FOOLParser.*;
import compiler.lib.*;
import compiler_lab9.AST.*;

import static compiler.lib.FOOLlib.*;

/*
 * 
 * ASTGenerationSTVisitor:
 * -visitor che a partire dal ST genera l'AST
 * --perchè stampare prima di eseguire la visita?
 * ---per dire cosa c'è in quel punto dell'albero
 * 
 *  
 * I metodi di visita sono presentati nello stesso ordine della grammatica; per gli elementi sintattici che già c'erano non è cambiato nulla;
 * l'unica differenza è che nella stampa è stata messa una cosa automatica: utilizzando la reflection: idea metto 'var : prod applicata'
 * andando ad indagare tramite reflection sul tipo di C: quando va ad analizzarre il simbolo va nella grammatica .g4 e prende il nome che segue # ossia il nome della classe
 * per recuperare exp devo guardare da che classe eredita la c considerata; le prod di una variabili ereditano da ...
 * es c è un paramentro con la sua classe ottengo times e poi vedo che eredeita da exp e lo metto dopo i due punti (?)
 * 
 *  Quando ho un unica produzione faccio il visit solo sul progcontext perchè quello è il nodo dell'albero e la produzione appplicata è l'unica che c'è
 */

/*
 * stampa automatica utilizzando la reflection:
 * idea: mettere "var : prod applicata" --> viene fatto automaticamente tramite reflection
 * 		passando al metodo di stampa un istanza della classe dalla quale lui può recuperare il tipo
 * 		exp è più complesso: devo guardare la classe da cui eredita c: la classe TimesContext eredita dalla classe della var ossia expContext
 * 		ANTLR vede le produzioni di una variabile come classi che ereditano dal genitore;
 * 		se parto ed ho c il mio parametro: con la sua classe ottengo (per esempio) Times poi guardo la classe da cui eredita e da li ottengo la exp per mettere i :
 *		quando ho un unica produzione visito direttamente la variabile (es produzione)
 */

/* In questa classe che estende il visitor di default è possibile notare come:
 * ANTLR rifugga dall'overloading dando ad ogni metodo visit un nome specifico 
 * 
 */

public class ASTGenerationSTVisitor extends FOOLBaseVisitor<Node> {

	String indent;
    public boolean print;
	
    ASTGenerationSTVisitor() {}    
    ASTGenerationSTVisitor(boolean debug) { print=debug; }
    /*
     * con la reflection prendo la classe di ctx (quello che prima era c
     * e prendo anche la sua superclasse poi dico: 
     * - se è parserulecontext c già il nome della var
     * - allora c è il nome della prod e il nome lo ottengo dalla super classe
     * 	 prima devo estrarre il nome perchè queste classi quando faccio getName danno il nome completo della classe con pkg ecc.
     * 	 con le funzioni di libreria mi libero della roba inutile
     */ 

    private void printVarAndProdName(ParserRuleContext ctx) {
        String prefix="";        
        					
    	Class<?> ctxClass=ctx.getClass(), parentClass=ctxClass.getSuperclass(); //getCLass prende la classe con la reflection e getsuperclass prende quella sopra
        if (!parentClass.equals(ParserRuleContext.class)) 						// parentClass is the var context (and not ctxClass itself) ; se è parse rule context è già una var se no vuol dire che lo devo ottenre dal genitore        												
        	prefix=lowerizeFirstChar(extractCtxName(parentClass.getName()))+": production #"; //è il caso in cui il var context lo ottengo dalla superclasse; prima estraendo il nome della var
        																						//utilizza delle piccole funzioni di libreria per tagliare e formattare il nome come ci serve con delle funzioni in FFOLli
    	System.out.println(indent+prefix+lowerizeFirstChar(extractCtxName(ctxClass.getName())));    //prefisso nome var : prodction # e nome vero della var
    																								//nb se ctx conteneva già il nome della var prefix resta vuoto e stampodirettamente il nome della var 
    }
    
    
    @Override
	public Node visit(ParseTree t) {
        String temp=indent;
        indent=(indent==null)?"":indent+"  ";
        Node result = super.visit(t);
        indent=temp;
        return result; 
	}
/*
 * Introdotto passaggio intermedio al progbody: lui restituisce il node che gli restituisce la vsiita al prog body
 */
	@Override
	public Node visitProg(ProgContext c) {
		if (print) printVarAndProdName(c);
		return visit(c.progbody());//quando visito il prgo restituisco il progbody che poi andrà nei due casi
	}
	
	/*
	 * Caso del LET IN: primo figlio di progin
	 * sto costruendo l'ast quello che devo fare è:
	 * creare un nodo apposta ProgLetInNode che avrà 
	 * un array di figli: le sue dichiarazioni e il figlio 
	 * con il corpo del programma
	 *
	 *	ciclo su tutti i figli dec (le dichairazioni delle var) c.dec: 
	 *	restituisce una lista di tutti i figli dell'albero sintattico di dec; 
	 *	io li scorro tutti chiamandoci una visita;
	 *	li colleziono e li menttto in un campo declist
	 */
	@Override
	public Node visitLetInProg(LetInProgContext c) { 
		if (print) printVarAndProdName(c);
		List<Node> declist =new ArrayList<>();
		for (DecContext dec : c.dec())  declist.add(visit(dec));		
		visit(c.exp());
		return new ProgLetInNode(declist, visit(c.exp()));
	}

	/*
	 * Visita a quello senza dichiarazioni
	 */
	@Override
	public Node visitNoDecProg(NoDecProgContext c) {
		if (print) printVarAndProdName(c);
		//creo un oggetto prog e gli do come figlio la visita di exp
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
        Node n= new VarNode(c.ID().getText(), visit(c.type()), visit(c.exp()));/*Creo un nuovo nodo con il nome della var, il tipo(?) e ...(?) e
																		         il numero della linea in cui appare nel src con  tutta sta roba la metto
																		          in una var per settare il campo del node in modo da sapere in che linea si trova
																		         */
        n.setLine(c.VAR().getSymbol().getLine());//questo mi ricorda il numero di linea
        
        return n;
	}

	@Override
	public Node visitFundec(FundecContext c) {
		if (print) printVarAndProdName(c);
		// esercizio
		List<ParNode> parList = new ArrayList<>();
		for (int i = 1; i < c.ID().size(); i++) { 
			ParNode p = new ParNode(c.ID(i).getText(), visit(c.type(i)));
			p.setLine(c.ID(i).getSymbol().getLine());
			parList.add(p);
		}
		//
		List<Node> decList = new ArrayList<>();
		for (DecContext dec : c.dec()) decList.add(visit(dec));
		Node n = new FunNode( c.ID(0).getText(), visit(c.type(0)), parList, decList, visit(c.exp()) );
		n.setLine(c.FUN().getSymbol().getLine());
        return n;
	}
	/**
	 * Bool node: valore node (è una foglia ) 
	 * Booltype node : rappresentano il tipo dei paramentri delle produzioni/variabili/tipi
	 * 
	 * discorso analogo vale per int
	 *
	 */
	@Override
	public Node visitIntType(IntTypeContext c) {
		if (print) printVarAndProdName(c);
		return new IntTypeNode();					
	}

	@Override
	public Node visitBoolType(BoolTypeContext c) {
		if (print) printVarAndProdName(c);
		return new BoolTypeNode();					
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
		Node n = new IdNode(c.ID().getText());
		n.setLine(c.ID().getSymbol().getLine());
		return n;
	}
	/*
	 * chiamata a funzione
	 */
	@Override
	public Node visitCall(CallContext c) {
		if (print) printVarAndProdName(c);		
		List<Node> arglist = new ArrayList<>();
		for (ExpContext arg : c.exp()) arglist.add(visit(arg));
		Node n = new CallNode(c.ID().getText(),arglist);
		n.setLine(c.ID().getSymbol().getLine());
		return n;
	}
}

//n.setLine(c.ID().getSymbol().getLine());
