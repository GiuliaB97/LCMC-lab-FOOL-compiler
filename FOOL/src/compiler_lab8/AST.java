package compiler_lab8;

import compiler.lib.*;
/*Syntax Tree albero di parsing che data una grammatica e 
*una stringa appartenente ad essa c'è un albero in cui questa 
*si può leggere nelle foglie; 
*questo viene generato da antlr.
*
*Noi partiamo da questa versione concreta dell'albero e generiamo una versione astratta
*rappresentata da questi nodi;
*/

///node è l'interfaccia che ha il metodo accept

public class AST {

	public static class ProgNode implements Node {
		Node exp;
		ProgNode(Node e) { exp = e; }

		@Override
		public <S> S accept(BaseASTVisitor<S> visitor) { return visitor.visit(this); }
	}

	public static class IntNode implements Node {
		Integer val;
		IntNode(Integer n) { val = n; }

		@Override
		public <S> S accept(BaseASTVisitor<S> visitor) { return visitor.visit(this); }
	}

	public static class PlusNode implements Node {
		Node left;
		Node right;
		PlusNode(Node l, Node r) { left = l; right = r; }

		@Override
		public <S> S accept(BaseASTVisitor<S> visitor) { return visitor.visit(this); }
	}

	public static class TimesNode implements Node {
		Node left;
		Node right;
		TimesNode(Node l, Node r) { left = l; right = r; }

		@Override
		public <S> S accept(BaseASTVisitor<S> visitor) { return visitor.visit(this); }
	}

	public static class EqualNode implements Node {
		Node left;
		Node right;
		EqualNode(Node l, Node r) { left = l; right = r; }

		@Override
		public <S> S accept(BaseASTVisitor<S> visitor) { return visitor.visit(this); }
	}
	
	public static class BoolNode implements Node {
		Boolean val;
		BoolNode(Boolean b) { val = b; }

		@Override
		public <S> S accept(BaseASTVisitor<S> visitor) { return visitor.visit(this); }
	}

	public static class IfNode implements Node {
		//sono sottocondizioni perchè ognuna può avere un sottoalbero
		Node cond;
		Node th;
		Node el;

		IfNode(Node c, Node t, Node e) { cond = c; th = t; el = e; }

		@Override
		public <S> S accept(BaseASTVisitor<S> visitor) { return visitor.visit(this); }	
	}

	public static class PrintNode implements Node {
		Node exp;
		PrintNode(Node e) { exp = e; }

		@Override
		public <S> S accept(BaseASTVisitor<S> visitor) { return visitor.visit(this); }
	}

}
