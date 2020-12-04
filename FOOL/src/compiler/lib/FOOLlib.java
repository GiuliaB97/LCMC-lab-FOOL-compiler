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

	// valuta se il tipo "a" e' <= al tipo "b", dove "a" e "b" sono tipi di base: IntTypeNode o BoolTypeNode
	public static boolean isSubtype(TypeNode a, TypeNode b) {
		return a.getClass().equals(b.getClass()) || ((a instanceof BoolTypeNode) && (b instanceof IntTypeNode));
	}

	// crea un'unica stringa a partire da un insieme di stringhe concatenadole e 
	// introducendo, all'interno, dei newline "\n" come separatore tra le stringhe
	/*
	 * (4) : nlJoin in fool.lib: ha un nuomero variabile di argomenti : 
	 * idea qui ci metto le istruzioni che voglio generare e lui me le concatena 
	 * e le separa da dei new line: concatena le stringhe che gli passo come argomento, 
	 * ignora i null e quindi gli argomenti che sono effettivamente delle stringhe 
	 * le concatena in una stringa code mettendo in mezzo dei \n 
	 * (mi raccomando sono in mezzo non alla fine)
 
	 */
	public static String nlJoin(String... lines) { //argomenti null ignorati 
		String code = null;
		for (int i = 0; i<lines.length; i++) 
			if (lines[i]!=null) code = (code==null?"":code+"\n")+lines[i]; 
		return code;
	}

	private static int labCount = 0;

	public static String freshLabel() {
		return "label" + (labCount++);
	}

	private static int funlabCount = 0;

	public static String freshFunLabel() {
		return "function" + (funlabCount++);
	}

	private static String funCode = null;

	public static void putCode(String c) {
		funCode = nlJoin(funCode, "", c); //linea vuota di separazione prima di codice funzione
	}

	public static String getCode() {
		return funCode;
	}
}
