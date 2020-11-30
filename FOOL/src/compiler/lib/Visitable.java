package compiler.lib;

public interface Visitable {
/*
 * Anche Visitable deve lanciare un'eccezione
 * perch� se no l'eccezione verrebbe persa
 * 
 * l'interfaccia � estesa in modo che estenda dal nuovo BaseAST
 * Questa � la classe binder
 */
	<S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E;

}
