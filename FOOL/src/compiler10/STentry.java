package compiler10;

import compiler.lib.*;

public class STentry implements Visitable {
	int nl;
	//devo aggiungere un campo type per le mie palline
	TypeNode type;
	public STentry(int n, TypeNode t) { nl = n; type=t;}
	
	@Override
	public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {
		return ((BaseEASTVisitor<S,E>) visitor).visitSTentry(this);
	}
}
