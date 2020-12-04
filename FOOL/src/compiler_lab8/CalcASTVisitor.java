package compiler_lab8;

import compiler.lib.*;
import compiler_lab8.AST.*;

public class CalcASTVisitor extends BaseASTVisitor<Integer> {

    CalcASTVisitor() {}
    CalcASTVisitor(boolean debug) { super(debug); } // enables print for debugging
    
	@Override
	public Integer visit(ProgNode n) {
	   if (print) printNode(n);			//qui la stampa è opzionale perchè non usiamo il costruttore di default
	   return visit(n.exp);
	}

	@Override
	public Integer visit(PlusNode n) {
		if (print) printNode(n);  
	    return visit(n.left)+visit(n.right);
	}

	@Override
	public Integer visit(TimesNode n) {
		if (print) printNode(n);
	    return visit(n.left)*visit(n.right); 
	}

	@Override
	public Integer visit(IntNode n) {
		if (print) printNode(n,": "+n.val);  
        return n.val;
	}

///
	@Override
	public Integer visit(EqualNode n) {
		if (print) printNode(n);
	    return visit(n.left)==visit(n.right)?1:0; //lavoriamo con java quindi 1:true e 0: false
	}

	@Override
	public Integer visit(BoolNode n) {
		if (print) printNode(n);
	    return n.val?1:0; 
	}
	
	@Override	
	public Integer visit(IfNode n) {
		if (print) printNode(n);
	    return visit(n.cond)==1?visit(n.th):visit(n.el); 
	}

	@Override	
	public Integer visit(PrintNode n) {
		if (print) printNode(n);
	    return visit(n.exp); 
	}	
}

	//il dynamic binding funziona solo sul soggetto che chiama il metodo non sui suoi parametri; in questo caso risolve il problema del biding a tempo statico non a run time
	//binding connessione tra il chiamante e il chiamato
	//vede che il chiamante è node e quindi questa cosa a runtime non cambia
	//in C# c'è una cast speciale che permette il cast dynamic fa il biding a runtime e quindi non chiama a runtime a il metodo che ha come parametro il generico node, ma quello che ha il tipo specifico

