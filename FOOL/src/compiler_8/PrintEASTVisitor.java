package compiler_8;

import compiler.lib.*;
import compiler_8.AST.*;


/*
 * RIGUARDA E CHIEDI AIUTO IL DISCORSO NON E' CHIARO!!!!!!!!!!!!!
 */

//legame con ANTLR che rifugge dall'overloading : lui ha una gerarhia noi abbiamo dei nodi che stanno per i fatti loro.
public class PrintEASTVisitor extends BaseEASTVisitor<Void> {

	PrintEASTVisitor() { super(true); }
	
	/*le visit sono diventate visitNode per evitare criticità con l'verloading di Java: prima usavamo 
	 * solo visit chiamandolo su un node gnereico (java si basa sul tipo statico dell'argomento non dinamico; 
	 * quindi la eseguiva sul node che pi trasformava l'ggetto in soggetto
	 */
	
	@Override
	public Void visitNode(ProgNode n) {
		printNode(n);
		visit(n.exp);
		return null;
	}
	
	@Override
	public Void visitNode(IntNode n) {
		printNode(n, n.val.toString());
		return null;
	}

	@Override
	public Void visitNode(PlusNode n) {
		printNode(n);
		visit(n.left);
		visit(n.right);
		return null;
	}
	
	@Override
	public Void visitNode(TimesNode n) {
		printNode(n);
		visit(n.left);
		visit(n.right);
		return null;
	}

	@Override
	public Void visitNode(EqualNode n) {
		printNode(n);
		visit(n.left);
		visit(n.right);
		return null;
	}
	
	@Override
	public Void visitNode(BoolNode n) {
		printNode(n, n.val.toString());
		return null;
	}


	@Override
	public Void visitNode(IfNode n) {
		printNode(n);
		visit(n.cond);
		visit(n.th);
		visit(n.el);
		return null;
	}
	
//	
	@Override
	public Void visitNode(ProgLetInNode n) {
		printNode(n);
		for (Node dec : n.declist) visit(dec);
		visit(n.exp);
		return null;
	}

	@Override
	public Void visitNode(BoolTypeNode n) {
		printNode(n);
		return null;
	}

	@Override
	public Void visitNode(IntTypeNode n) {
		printNode(n);
		return null;
	}

	@Override
	public Void visitNode(VarNode n) {
		printNode(n,n.id);
		visit(n.type);
		visit(n.exp);
		return null;
	}

	@Override
	public Void visitNode(FunNode n) {
		printNode(n,n.id);
		visit(n.retType);
		for (compiler_8.AST.ParNode par : n.parlist) visit(par); 
														/*di che tipo statico è par(?) par node così chiamarebbe direttamente parnode 
														 * senza chiamare node che chiama accept-> problema per quello che riguarda l'indentazione
														 * Soluzioni: fare una var node, far un cast (in particolare un upcast)
														 * CHIEDI AIUTO
														 */
													
		for (Node dec : n.declist) visit(dec);
		visit(n.exp);
		return null;
	}
	
	@Override
	public Void visitNode(IdNode n) {
		printNode(n,n.id);
		if(n.entry!=null) visitSTentry(n.entry);
		return null;
	}

	@Override
	public Void visitNode(CallNode n) {
		printNode(n,n.id);							
		for (Node arg : n.arglist) visit(arg);
		if(n.entry!=null) visitSTentry(n.entry);
		return null;
	}
	@Override
	public Void visitNode(PrintNode n) {
		printNode(n);
		visit(n.exp);
		return null;
	}

	@Override
	public Void visitSTentry(STentry entry) {
		printSTentry("nestlev "+entry.nl);
		return null;
	}
	
	
	@Override
	public Void visitNode(ParNode n) {
		printNode(n,n.id);
		visit(n.type);			/*cast obbligato a causa della porcata
											*fatta nella classe ParNode per cui type
											*non è di tipo Ndoe ma di tipo parContext
											*/
		return null;
	}
}