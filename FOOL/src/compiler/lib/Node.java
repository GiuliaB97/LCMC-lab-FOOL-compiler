package compiler.lib;
/*
 * Node non è più interfaccia; lo utilizzavamo così perchè tutti i nodi dell'AST lo implementavano ora è una classe astratta perchè in realtà mi serve il campo line
 * tale campo mi serve per gli errori (per quelli sintattici se ne occupa ANTLR 
 */
public abstract class Node {
	
	int line=-1;  // line -1 means unset
	
	public void setLine(int l) { line=l; }

	public int getLine() { return line; }

	public abstract <S> S accept(BaseASTVisitor<S> visitor);
}

	  