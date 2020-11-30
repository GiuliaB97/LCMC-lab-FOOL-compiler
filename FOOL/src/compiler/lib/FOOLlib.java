package compiler.lib;

import compiler.AST.*;

public class FOOLlib {

	public static String extractNodeName(String s) { // s is in the form compiler.AST$NameNode
    	return s.substring(s.lastIndexOf('$')+1,s.length()-4);
    }

	public static String extractCtxName(String s) { // s is in the form compiler.FOOLParser$NameContext
		return s.substring(s.lastIndexOf('$')+1,s.length()-7);
    }
	
	public static String lowerizeFirstChar(String s) {
    	return Character.toLowerCase(s.charAt(0))+s.substring(1,s.length());
    }
    
	public static int typeErrors = 0;
	/*
	 * idea : questo metodo definisce la relazione di subtyping
	 * ora consideriamo i boolean sottotipi di interi assumento che true=1 e false=0
	 * questa non è l'unica possibilità 
	 * il subtyping fa parte del linguaggio che sis ta creando.
	 * 
	 * valuta se il tipo "a" e' <= al tipo "b", 
	 * dove "a" e "b" sono tipi di base: IntTypeNode o BoolTypeNode
	 * a e b sono tipi di base: i casi in cui mi va bene sono:
	 * 	sono entrambi dello stesso tipo; quindi sono sottotipi(<= considera ilc asod ell'ugualianza)
	 * ma esiste anche il caso in cui a sia effettivamente sottotipo di b 
	 */
	public static boolean isSubtype(TypeNode a, TypeNode b) {
		return a.getClass().equals(b.getClass()) || ((a instanceof BoolTypeNode) && (b instanceof IntTypeNode));
	}
}
	




