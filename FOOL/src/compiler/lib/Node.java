package compiler.lib;
/*
 * Node non � pi� interfaccia; lo utilizzavamo cos� perch� tutti i nodi dell'AST lo implementavano ora � una classe astratta perch� in realt� mi serve il campo line
 * tale campo mi serve per gli errori (per quelli sintattici se ne occupa ANTLR 
 */
public class Node implements Visitable{
	
	int line=-1;  // line -1 means unset
	
	public void setLine(int l) { line=l; }

	public int getLine() { return line; }

	@Override
	public <S> S accept(BaseASTVisitor<S> visitor) {
		// TODO Auto-generated method stub
		return null;
	}

	//public abstract <S> S accept(BaseASTVisitor<S> visitor);
}

	  