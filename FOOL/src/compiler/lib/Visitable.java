package compiler.lib;

public interface Visitable {

	<S> S accept(BaseASTVisitor<S> visitor);

}
