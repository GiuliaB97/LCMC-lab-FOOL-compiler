package compiler.lib;

import compiler.AST.*;
import compiler.exc.*;

import static compiler.lib.FOOLlib.*;
/*
 * Ogni metodo di visita può lanciare un eccezione 
 */
public class BaseASTVisitor<S,E extends Exception> {
	/*
	 * Se chiamo il costruttore di default non le abilito perchè di default i campi bool in JAva sono a false
	 * 
	 */
	private boolean incomplExc; // enables throwing IncomplException
	protected boolean print; // enables printing
	protected String indent;
		
	protected BaseASTVisitor() {}
	protected BaseASTVisitor(boolean ie) {incomplExc = ie;  }
	protected BaseASTVisitor(boolean ie, boolean p) { incomplExc = ie; print = p;  }
	/**
	 * 2020-11-16
	 * 
	 * Doto ogni classe node[AST.java] di un metodo accept (){ che chiami visit(this)}
	 * questo serve perchè così quando sono nel visitor faccio diventare la n presa come parametro 
	 * nel metodo protected void printNode(Node n) {} la faccio diventare soggetto.
	 * chiamando il metodo accept sulla classe effettiva così java può fare l'rtti
	 * Quando arrivo alla classe visitor il metodo visit che richiamo è visit(Node n)
	 * qui quando arriva ad eseguire il metodo accept fa l'rtti sul soggetto 
	 * e quindi verrà chiamata l'accept sulla classe specifica che quindi chiamerà visit(this)
	 * facendo così; chiama visit sull'istanza della classe che su cui è chiamata accept quindi da 
	 * lì fa la visita specifica sull'oggetto che mi interessa
	 * 
	 * tuttavia come faccio a gestire il riferimento al Visitor lo passo al node con this
	 * (così ogni classe specifica riceve l'oggetto esatvisitor e su questo chiama visit)
	 * ---> questa cosa nell'ultima implementazione è stata messa nell'interfaccia Visitable
	 * 
	 */
	protected void printNode(Node n) {
		System.out.println(indent+extractNodeName(n.getClass().getName()));
	}

	protected void printNode(Node n, String s) {
		System.out.println(indent+extractNodeName(n.getClass().getName())+": "+s);
	}
	/*se mi chiamano con un argomento solo (senza marca)
	 * richiamo il metodo con la marca vuota
	 */
	public S visit(Visitable v) throws E {
	return visit(v, "");                //performs unmarked visit
	}
/*do al metodo la posssibilità di avere 
 * una marca  come secondo argomento per gestire la freccia 
 * ( roba futile per gestire la stampa indentata correttamenteP)
 */
	public S visit(Visitable v, String mark) throws E { //when printing marks this visit with string mark
		if (v==null) 
			if (incomplExc) throw new IncomplException();
			else return null;
		if (print) {
			String temp = indent;
			indent = (indent == null) ? "" : indent + "  ";
			indent += mark; //insert mark
			try {
				/*problema se questa lancia un eccezione io perdo 
				il ripristino dell'indentazione
				*/
				S result = visitByAcc(v);
				return result;
			} finally { indent = temp; }/*sia che accada un eccezione o
											che ritorni normalmente
											prima di tornare il controllo lui fa indent =temp; 
											*/
		} else 
			return visitByAcc(v);
	}

	S visitByAcc(Visitable v) throws E {
		return v.accept(this);
	}

	public S visitNode(ProgLetInNode n) throws E {throw new UnimplException();}
	public S visitNode(ProgNode n) throws E {throw new UnimplException();}
	public S visitNode(FunNode n) throws E {throw new UnimplException();}
	public S visitNode(ParNode n) throws E {throw new UnimplException();}
	public S visitNode(VarNode n) throws E {throw new UnimplException();}
	public S visitNode(ArrowTypeNode n) throws E {throw new UnimplException();}
	public S visitNode(BoolTypeNode n) throws E {throw new UnimplException();}
	public S visitNode(IntTypeNode n) throws E {throw new UnimplException();}
	public S visitNode(PrintNode n) throws E {throw new UnimplException();}
	public S visitNode(IfNode n) throws E {throw new UnimplException();}
	public S visitNode(EqualNode n) throws E {throw new UnimplException();}
	public S visitNode(TimesNode n) throws E {throw new UnimplException();}
	public S visitNode(PlusNode n) throws E {throw new UnimplException();}
	public S visitNode(CallNode n) throws E {throw new UnimplException();}
	public S visitNode(IdNode n) throws E {throw new UnimplException();}
	public S visitNode(BoolNode n) throws E {throw new UnimplException();}
	public S visitNode(IntNode n) throws E {throw new UnimplException();}	
	
}






//





