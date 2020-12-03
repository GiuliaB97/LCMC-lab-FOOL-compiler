package compiler_lab9;

import compiler.lib.BaseASTVisitor;
import compiler.lib.BaseEASTVisitor;
import compiler.lib.Visitable;

//import compiler.lib.*;
/*
 * Non sono nodi dell'albero ma parte con cui lo abbiamo arrriccchito per poterli visitare devo implementare un interfaccia visitor 
 */
public class STentry implements Visitable {
	/*
	 * Quando creo la pallina la creo già col nesting level; quando incontro una dichiarazione di un identificatore; creo una pallina stentry specificando a quale nesting level questa si trova
	 */
	int nl;
	public STentry(int n) {nl=n;}


	@Override
	public <S> S accept(BaseASTVisitor<S> visitor) {				//accept chiamava semplicemtne visit node su this qui faccio lo stesso con visitSTentry
																	//il downcast qui è sicuro: l'accept chiaamta su un soggetto che è un east ?? può essere chiamata solo da east visitior???????
		return ((BaseEASTVisitor<S>) visitor).visitSTentry(this);
	}
	
}

