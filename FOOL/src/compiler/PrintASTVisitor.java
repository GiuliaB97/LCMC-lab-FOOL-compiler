package compiler;

import compiler.AST.*;
import compiler.lib.*;

public class PrintASTVisitor extends BaseASTVisitor<Void> {

    PrintASTVisitor() { super(true); }
    	
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
		if (print) printNode(n);
		visit(n.left);
		visit(n.right);
        return null;		
	}

	@Override
	public Void visit(BoolNode n) {
		if (print) printNode(n,": "+n.val);
		return null;
	}
	
	@Override	
	public Void visit(IfNode n) {
		if (print) printNode(n);
		visit(n.cond);
		visit(n.th);
		visit(n.el); 
        return null;		
	}

	@Override	
	public Void visit(PrintNode n) {
		if (print) printNode(n);
		visit(n.exp); 
        return null;		
	}
}

	//il dynamic binding funziona solo sul soggetto che chiama il metodo non sui suoi parametri; in questo caso risolve il problema del biding a tempo statico non a run time
	//binding connessione tra il chiamante e il chiamato
	//vede che il chiamante è node e quindi questa cosa a runtime non cambia
	//in C# c'è una cast speciale che permette il cast dynamic fa il biding a runtime e quindi non chiama a runtime a il metodo che ha come parametro il generico node, ma quello che ha il tipo specifico
