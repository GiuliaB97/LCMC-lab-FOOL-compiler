package compiler;

import java.util.*;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import compiler.AST.*;
import compiler.FOOLParser.*;
import compiler.lib.*;
import static compiler.lib.FOOLlib.*;

/*
 * I metodi di visita sono presentati nello stesso ordine della grammatica; per gli elementi sintattici che già c'erano non è cambiato nulla;
 * l'unica differenza è che nella stampa è stata messa una cosa automatica: utilizzando la reflection: idea metto 'var : prod applicata'
 * andando ad indagare tramite reflection sul tipo di C: quando va ad analizzarre il simbolo va nella grammatica .g4 e prende il nome che segue # ossia il nome della classe
 * per recuperare exp devo guardare da che classe eredita la c considerata; le prod di una variabili ereditano da ...
 * es c è un paramentro con la sua classe ottengo times e poi vedo che eredeita da exp e lo metto dopo i due punti (?)
 * 
 *  Quando ho un unica produzione faccio il visit solo sul progcontext perchè quello è il nodo dell'albero e la produzione appplicata è l'unica che c'è
 */
/**
 * Bool node: valore node (è una foglia ) 
 * Booltype node : rappresentano il tipo dei paramentri delle produzioni/variabili/tipi
 * 
 * discorso analogo vale per int
 *
 */
public class ASTGenerationSTVisitor extends FOOLBaseVisitor<Node> {

	String indent;
    public boolean print;
	
    ASTGenerationSTVisitor() {}    
    ASTGenerationSTVisitor(boolean debug) { print=debug; }

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
		return visit(c.progbody());
	}
	
	/*
	 * Caso del LET IN
	 * sto costruendo l'ast quello che devo fare è:
	 * creare un nodo apposta(al posto del progviisto)
	 * questo nodo si chiamerà ProgLetInNode(?9
	 * 
	 */

	@Override
	public Node visitLetInProg(LetInProgContext c) { 
		if (print) printVarAndProdName(c);
		List<Node> declist =new ArrayList<>();
		for (DecContext dec : c.dec())  declist.add(visit(dec));		/*ciclo su tutti i figli dec (le dichairazioni delle var) c.dec: 
																			restituisce una lista di tutti i figli dell'albero sintattico di dec; io li scorro tutti chiamandoci una visita;
																			 che ci faccio con sti node li colleziono e li menttto in un campo declist
																			*/
		visit(c.exp());
		return new ProgLetInNode(declist, visit(c.exp()));
	}

	/*
	 * VIsita a quello senza dichiarazioni
	 */
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
		List<Node> declist =new ArrayList<>();
		for (DecContext dec : c.dec()) visit(dec);
		visit(c.type(0)); 
		Node n= new FunNode(c.ID(0).getText(), visit(c.type(0)),declist, visit(c.exp()));//occhio che qui ho più di un ID quindi devo specificare in quale nodo trovo il nome della funzione   
																							//in generale quando chiamo la visita su un tipo finisco in inttype o in bool tuyp che crearanno un bool type node o un intype node
		n.setLine(c.FUN().getSymbol().getLine());//questo mi recupera il numero di linea
	
		return n;
			
	}

	@Override
	public Node visitIntType(IntTypeContext c) {
		if (print) printVarAndProdName(c);
		return new IntTypeNode();					//ricontrolla
	}

	@Override
	public Node visitBoolType(BoolTypeContext c) {
		if (print) printVarAndProdName(c);
		return new BoolTypeNode();					//ricontrilla
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
	public Node visitId(IdContext c) {				/*
														dichiarazione di una var
													*/
		if (print) printVarAndProdName(c);
		Node n= new IdNode(c.ID().getText());
		n.setLine(c.ID().getSymbol().getLine());//questo mi ricorda il numero di linea
		
		return n;
	}
	/*
	 * chiamata a funzione
	 */
	@Override
	public Node visitCall(CallContext c) {	
												
		if (print) printVarAndProdName(c);
		Node n= new CallNode(c.ID().getText());
		n.setLine(c.ID().getSymbol().getLine());//questo mi ricorda il numero di linea
		
		return n;
	}
}

//n.setLine(c.ID().getSymbol().getLine());
