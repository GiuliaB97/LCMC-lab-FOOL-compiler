package compiler.lib;

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
    
}
