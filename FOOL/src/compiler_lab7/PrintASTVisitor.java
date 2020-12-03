package compiler_lab7;

import compiler.lib.*;
//PrintASTVisitor: visitot l'AST e cosa faccio stampo
import compiler_lab7.AST.*;

//la stampa del nome viene fatta direttaemnte con la reflection 


public class PrintASTVisitor extends BaseASTVisitor<Void> {

    PrintASTVisitor() { super(true); }

	/*
	 * 	il dynamic binding funziona solo sul soggetto che chiama il metodo 
	 * non sui suoi parametri; 
	 * in questo caso risolve il problema del biding a tempo statico 
	 * non a run time binding connessione tra il chiamante e il chiamato
	 * vede che il chiamante è node e quindi questa cosa a runtime non cambia
	 * in C# c'è una cast speciale che permette il cast dynamic fa il 
	 * biding a runtime e quindi non chiama a runtime a il metodo che 
	 * ha come parametro il generico node, ma quello che ha il tipo specifico
	*/	
	@Override
	public Void visit(ProgNode n) {
		printNode(n);	   
		visit(n.exp);
		return null;
	}

	@Override
	public Void visit(PlusNode n) {
		printNode(n);	   
	    visit(n.left);  
        visit(n.right);
        return null;
	}

	@Override
	public Void visit(TimesNode n) {
		printNode(n);	   
	    visit(n.left);
        visit(n.right); 
        return null;
	}

	@Override
	public Void visit(IntNode n) {
		printNode(n,": "+n.val);	   
        return null;
	}

	///
	@Override
	public Void visit(EqualNode n) {
		printNode(n);
		visit(n.left);
		visit(n.right);
        return null;		
	}

	@Override
	public Void visit(BoolNode n) {
		printNode(n,": "+n.val);
		return null;
	}
	
	@Override	
	public Void visit(IfNode n) {
		printNode(n);
		visit(n.cond);
		visit(n.th);
		visit(n.el); 
        return null;		
	}

	@Override	
	public Void visit(PrintNode n) {
		printNode(n);
		visit(n.exp); 
        return null;		
	}
}
