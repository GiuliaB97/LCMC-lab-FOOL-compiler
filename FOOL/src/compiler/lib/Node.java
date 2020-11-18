package compiler.lib;

public interface Node {
	
	<S> S accept(BaseASTVisitor<S> visitor);

}

