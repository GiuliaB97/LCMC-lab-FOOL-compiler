package compiler;

import compiler.AST.*;
import compiler.lib.*;

public class CalcASTVisitor extends BaseASTVisitor<Integer> {

    CalcASTVisitor() {}
    CalcASTVisitor(boolean debug) { super(debug); } // enables print for debugging
    
	@Override
	public Integer visit(ProgNode n) {
	   if (print) printNode(n);
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

}
	//il dynamic binding funziona solo sul soggetto che chiama il metodo non sui suoi parametri; in questo caso risolve il problema del biding a tempo statico non a run time
	//binding connessione tra il chiamante e il chiamato
	//vede che il chiamante è node e quindi questa cosa a runtime non cambia
	//in C# c'è una cast speciale che permette il cast dynamic fa il biding a runtime e quindi non chiama a runtime a il metodo che ha come parametro il generico node, ma quello che ha il tipo specifico

