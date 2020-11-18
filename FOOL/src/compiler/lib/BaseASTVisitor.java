package compiler.lib;

import compiler.AST.*;

public class BaseASTVisitor<S> {

    protected boolean print=false;
    String indent;

    protected BaseASTVisitor() {}
    protected BaseASTVisitor(boolean p) { print=p; }
    	
    protected void printNode(Node n,String s) {
    	String className = n.getClass().getName(); //returns a string compiler.AST$ClassName
    	String nodeName = className.substring(className.lastIndexOf('$')+1,className.length()-4);
 	    System.out.println(indent+nodeName+s);  
    }

    protected void printNode(Node n) {
		printNode(n, "");	   
    }
    
	public S visit(Node n) { 
      if (print) {
	    String temp=indent;
	    indent=(indent==null)?"":indent+"  ";
	    S result=visitByAcc(n); 
	    indent=temp;
	    return result; 
      }
	  else return visitByAcc(n);
	}
	
	S visitByAcc(Node n) { 
	    return n.accept(this); //performs the "n"-specific visit
	}
		
	public S visit(ProgNode n) { throw new UnimplException(); } 
	public S visit(PlusNode n) { throw new UnimplException(); }
	public S visit(TimesNode n) { throw new UnimplException(); }
	public S visit(IntNode n) { throw new UnimplException(); }
}

	//il dynamic binding funziona solo sul soggetto che chiama il metodo non sui suoi parametri; in questo caso risolve il problema del biding a tempo statico non a run time
	//binding connessione tra il chiamante e il chiamato
	//vede che il chiamante è node e quindi questa cosa a runtime non cambia
	//in C# c'è una cast speciale che permette il cast dynamic fa il biding a runtime e quindi non chiama a runtime a il metodo che ha come parametro il generico node, ma quello che ha il tipo specifico
	
