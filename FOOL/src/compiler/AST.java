package compiler;

import compiler.lib.*;

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
	
	
	/*
	 * Esercizio 1
	 */
	public static class EqualNode implements Node {
		Node left;
		Node right;
		EqualNode(Node l, Node r) { left = l; right = r; }

		@Override
		public <S> S accept(BaseASTVisitor<S> visitor) { return visitor.visit(this); }
	}
	
	public static class BoolNode implements Node {
		Boolean val;
		BoolNode(Boolean n) { val = n; }

		@Override
		public <S> S accept(BaseASTVisitor<S> visitor) { return visitor.visit(this); }
	}
	
	public static class IfNode implements Node {
		Node left;
		Node right;
		Node center;
		IfNode (Node l, Node c, Node r ) { left = l; right = r; center=c;}

		@Override
		public <S> S accept(BaseASTVisitor<S> visitor) { return visitor.visit(this); }
	}
	
	public static class PrintNode implements Node {

		@Override
		public <S> S accept(BaseASTVisitor<S> visitor) { return visitor.visit(this); }
		/*Bool val;
		BoolNode(bool n) { val = n; }

		@Override
		public <S> S accept(BaseASTVisitor<S> visitor) { return visitor.visit(this); }
		*/
	}
}