package compiler.lib;

public interface Visitable {
/*
 * Anche Visitable deve lanciare un'eccezione
 * perchè se no l'eccezione verrebbe persa
 * 
 * l'interfaccia è estesa in modo che estenda dal nuovo BaseAST
 * Questa è la classe binder
 */
	<S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E;

}
