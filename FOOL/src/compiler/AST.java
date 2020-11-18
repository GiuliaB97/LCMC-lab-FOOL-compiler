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
}