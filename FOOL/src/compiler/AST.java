package compiler;

import java.util.*;
import compiler.AST.*;
import compiler.lib.*;

public class AST {

	public static class ProgNode extends Node {
		Node exp;
		ProgNode(Node e) {exp = e;}

		@Override
		public <S> S accept(BaseASTVisitor<S> visitor) { return visitor.visitNode(this); }
	}
	
	public static class IntNode extends Node {
		Integer val;
		IntNode(Integer n) {val = n;}

		@Override
		public <S> S accept(BaseASTVisitor<S> visitor) { return visitor.visitNode(this); }
	}
	
	public static class PlusNode extends Node {
		Node left;
		Node right;
		PlusNode(Node l, Node r) {left = l; right = r;}

		@Override
		public <S> S accept(BaseASTVisitor<S> visitor) { return visitor.visitNode(this); }
	}
	
	public static class TimesNode extends Node {
		Node left;
		Node right;
		TimesNode(Node l, Node r) {left = l; right = r;}

		@Override
		public <S> S accept(BaseASTVisitor<S> visitor) { return visitor.visitNode(this); }
	}
	
	public static class EqualNode extends Node {
		Node left;
		Node right;
		EqualNode(Node l, Node r) {left = l; right = r;}

		@Override
		public <S> S accept(BaseASTVisitor<S> visitor) { return visitor.visitNode(this); }
	}
	
	public static class BoolNode extends Node {
		Boolean val;
		BoolNode(boolean n) {val = n;}

		@Override
		public <S> S accept(BaseASTVisitor<S> visitor) { return visitor.visitNode(this); }
	}
	
	public static class IfNode extends Node {
		Node cond;
		Node th;
		Node el;
		IfNode(Node c, Node t, Node e) {cond = c; th = t; el = e;}

		@Override
		public <S> S accept(BaseASTVisitor<S> visitor) { return visitor.visitNode(this); }
	}
	
	public static class PrintNode extends Node {
		Node exp;
		PrintNode(Node e) {exp = e;}

		@Override
		public <S> S accept(BaseASTVisitor<S> visitor) { return visitor.visitNode(this); }
	}

	public static class ProgLetInNode extends Node {
		List<Node> declist;											//Lista delle dichairazioni contenute 
		Node exp;
		ProgLetInNode(List<Node> d, Node e) {declist = d; exp = e;}

		@Override
		public <S> S accept(BaseASTVisitor<S> visitor) { return visitor.visitNode(this); }
	}

	public static class BoolTypeNode extends Node {

		@Override
		public <S> S accept(BaseASTVisitor<S> visitor) { return visitor.visitNode(this); }
	}

	public static class IntTypeNode extends Node {

		@Override
		public <S> S accept(BaseASTVisitor<S> visitor) { return visitor.visitNode(this); }
	}

	public static class VarNode extends Node {
		String id;
		Node type;
		Node exp;
		VarNode(String i, Node t, Node v) {id = i; type = t; exp = v;}

		@Override
		public <S> S accept(BaseASTVisitor<S> visitor) { return visitor.visitNode(this); }
	}

	public static class FunNode extends Node {
		String id;
		Node retType;
		List<ParNode> parlist;
		List<Node> declist; 
		
		Node exp;
		FunNode(String i, Node rt, List<ParNode> pl,  List<Node> dl, Node e) {
	    	id=i; retType=rt; parlist=pl;  declist=dl; exp=e;}
		
	

		@Override
		public <S> S accept(BaseASTVisitor<S> visitor) { return visitor.visitNode(this); }
	}

	public static class IdNode extends Node {
		String id;
		STentry entry;
		IdNode(String i) {id = i;}

		@Override
		public <S> S accept(BaseASTVisitor<S> visitor) { return visitor.visitNode(this); }
	}

	public static class CallNode extends Node {
		String id;
		List<Node> arglist;
		STentry entry;
		CallNode(String i , List<Node> p ) {id = i; arglist = p;}

		@Override
		public <S> S accept(BaseASTVisitor<S> visitor) { return visitor.visitNode(this); }
	}

}