package compiler;

import compiler.AST.*;
import compiler.exc.*;
import compiler.lib.*;
import static compiler.lib.FOOLlib.*;
/*
 * CAMBIAMENTI rispetto al lab10
 * trasformato in Eastvisitor perchè è stato aggiunto il metodo che visita le entry
 * 
 * AIM
 * tutti sti controlli servono per gestire le possibili incompletezze sulle entry
 * 
 * PROBLEMA
 * Errori di sintassi: le produzioni vengono matchate solo parzialmente da antlr se sono incomplete;
 * ->il mio obiettivo è non lanciare dei null pointer exception 
 * 
 */

//visitSTentry(s) ritorna, per una STentry s, il tipo contenuto al suo interno
public class TypeCheckEASTVisitor extends BaseEASTVisitor<TypeNode,TypeException> {

	TypeCheckEASTVisitor() { super(true); } // enables incomplete tree exceptions 
	TypeCheckEASTVisitor(boolean debug) { super(true,debug); } // enables print for debugging

	//checks that a type object is visitable (not incomplete) 
	private TypeNode ckvisit(TypeNode t) throws TypeException {
		visit(t);
		return t;
	} 
	
	@Override
	public TypeNode visitNode(ProgLetInNode n) throws TypeException {
		if (print) printNode(n);
		for (Node dec : n.declist)
			try {
				visit(dec);
			} catch (IncomplException e) { 
			} catch (TypeException e) {
				System.out.println("Type checking error in a declaration: " + e.text);
			}
		return visit(n.exp);
	}

	@Override
	public TypeNode visitNode(ProgNode n) throws TypeException {
		if (print) printNode(n);
		return visit(n.exp);
	}

	@Override
	public TypeNode visitNode(FunNode n) throws TypeException {
		if (print) printNode(n,n.id);
		for (Node dec : n.declist)
			try {
				visit(dec);
			} catch (IncomplException e) { 
			} catch (TypeException e) {
				System.out.println("Type checking error in a declaration: " + e.text);
			}
		if ( !isSubtype(visit(n.exp),ckvisit(n.retType)) ) //check
			throw new TypeException("Wrong return type for function " + n.id,n.getLine());
		return null;
	}

	@Override
	public TypeNode visitNode(VarNode n) throws TypeException {
		if (print) printNode(n,n.id);
		if ( !isSubtype(visit(n.exp),ckvisit(n.type)) ) //check
			throw new TypeException("Incompatible value for variable " + n.id,n.getLine());
		return null;
	}

	@Override
	public TypeNode visitNode(PrintNode n) throws TypeException {
		if (print) printNode(n);
		return visit(n.exp);
	}

	@Override
	public TypeNode visitNode(IfNode n) throws TypeException {
		if (print) printNode(n);
		if ( !(isSubtype(visit(n.cond), new BoolTypeNode())) )
			throw new TypeException("Non boolean condition in if",n.getLine());
		TypeNode t = visit(n.th);
		TypeNode e = visit(n.el);
		if (isSubtype(t, e)) return e;
		if (isSubtype(e, t)) return t;
		throw new TypeException("Incompatible types in then-else branches",n.getLine());
	}

	@Override
	public TypeNode visitNode(EqualNode n) throws TypeException {
		if (print) printNode(n);
		TypeNode l = visit(n.left);
		TypeNode r = visit(n.right);
		if ( !(isSubtype(l, r) || isSubtype(r, l)) )
			throw new TypeException("Incompatible types in equal",n.getLine());
		return new BoolTypeNode();
	}

	@Override
	public TypeNode visitNode(TimesNode n) throws TypeException {
		if (print) printNode(n);
		if ( !(isSubtype(visit(n.left), new IntTypeNode())
				&& isSubtype(visit(n.right), new IntTypeNode())) )
			throw new TypeException("Non integers in multiplication",n.getLine());
		return new IntTypeNode();
	}

	@Override
	public TypeNode visitNode(PlusNode n) throws TypeException {
		if (print) printNode(n);
		if ( !(isSubtype(visit(n.left), new IntTypeNode())
				&& isSubtype(visit(n.right), new IntTypeNode())) )
			throw new TypeException("Non integers in sum",n.getLine());
		return new IntTypeNode();
	}
/*
 * NB la pallina è stata inizializzata da SymbolTAbleASTVisitor
 * 
 * A
 * dentro la pallina ho una marea di cose io controllo che sia un arrowtype node  se non lo è lancio errore 
 * 
 * B
 * narglist contiene i miei figli che sono gli argomenti che uso per choaare la funzione
 * at.parlist.size(): numero degli argomenti con cui è dichiarata						
 * n.arglist.size(): numero argomenti effettivi
 * -----> quando viene  inizializzata una  l'altra
 * 
 * C
 * controllo 
 * 
 * torno il tipo che nella dichiarazione di questa funzione è il tipo di ritorno (Campo ret della classe ArrowTypeNode
 * 
 * PROBLEMA:
 * la entry potrebbe essere null: in caso di errori; per questo visito la netry
 * 
 */
	@Override
	public TypeNode visitNode(CallNode n) throws TypeException {
		if (print) printNode(n,n.id);
		TypeNode t = visit(n.entry); // STentry visit
		if ( !(t instanceof ArrowTypeNode) ) //A
			throw new TypeException("Invocation of a non-function "+n.id,n.getLine());
		ArrowTypeNode at = (ArrowTypeNode) t;
		if ( !(at.parlist.size() == n.arglist.size()) ) //B
			throw new TypeException("Wrong number of parameters in the invocation of "+n.id,n.getLine());
		for (int i = 0; i < n.arglist.size(); i++)
			if ( !(FOOLlib.isSubtype(visit(n.arglist.get(i)),at.parlist.get(i))) ) //C
				throw new TypeException("Wrong type for "+(i+1)+"-th parameter in the invocation of "+n.id,n.getLine());
		return at.ret;
	}

	@Override
	public TypeNode visitNode(IdNode n) throws TypeException {
		if (print) printNode(n,n.id);
		TypeNode t = visit(n.entry); // STentry visit
		if (t instanceof ArrowTypeNode)
			throw new TypeException("Wrong usage of function identifier " + n.id,n.getLine());
		return t;
	}

	@Override
	public TypeNode visitNode(BoolNode n) {
		if (print) printNode(n,n.val.toString());
		return new BoolTypeNode();
	}

	@Override
	public TypeNode visitNode(IntNode n) {
		if (print) printNode(n,n.val.toString());
		return new IntTypeNode();
	}

// gestione tipi incompleti	(se lo sono lancia eccezione)
	
	@Override
	public TypeNode visitNode(ArrowTypeNode n) throws TypeException {
		if (print) printNode(n);
		for (Node par: n.parlist) visit(par);
		visit(n.ret,"->"); //marks return type
		return null;
	}

	@Override
	public TypeNode visitNode(BoolTypeNode n) {
		if (print) printNode(n);
		return null;
	}

	@Override
	public TypeNode visitNode(IntTypeNode n) {
		if (print) printNode(n);
		return null;
	}

/*
 * WHY
 * -Questo metodo è la ragione per cui la classe è diventata EAST
 * 
 * OVERVIEW
 * Aggiunta la visit della entry perchè ritorni il tipo di ritorno della entry
 * risolve il problema perchè se al post del parametro entry passo null (caso di errore)
 * BaseASTVisitor lancia una incomplete exception (dal metodo visit)
 * 
 * PROBLEMA
 *  anche il tipo che ritorno potrebbe essere incompleto:
 * potrebbero mancargli tipi di parametri, o tipi di ritorno 
 * (errori sintattici sono quelli che causano il null dentro)
 * 
 * SOLUZIONE
 * come risolvo il problema?
 * visito anche i tipi:
 * - se visito un tipo ritorno null (come per le dichairazini) perchè faccio un controllo interno 
 * 	sulla consistenza della dicahiarazione e torno null se trova un errore lancia una complete exception
 * - visito il tipo poi lo utilizzo; per evitare di scrivere due cose 
 * 									(questo check va fatto tutte le volte che visito un tipo che sta in un campo di un nodo)
 *	per evitare di visita e ritornare il tipo è stata aggiunta un metodo che checka il tipo attraverso la visita:
 *	le fa il check e poi ritorna il tipo
 */

	@Override
	public TypeNode visitSTentry(STentry entry) throws TypeException {
		if (print) printSTentry("type");
		return ckvisit(entry.type); //check: fa una visita e ritorna il tipo su cui è stata chiamata la visita
	}

}