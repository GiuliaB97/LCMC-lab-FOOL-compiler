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





