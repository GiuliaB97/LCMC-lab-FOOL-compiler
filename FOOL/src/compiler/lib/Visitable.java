package compiler.lib;

public interface Visitable {
/*
 * Anche Visitable deve lanciare un'eccezione
 * perch� se no l'eccezione verrebbe persa
 * 
 * l'interfaccia � estesa in modo che estenda dal nuovo BaseAST
 * Questa � la classe binder
 * 
 * Visitable: ha un metodo in cui gli passo visitor perch� se no non ho un istanza di questa classe sulla quale chiamare il visitr
 */
	<S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E;

}
