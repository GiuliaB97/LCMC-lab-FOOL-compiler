package compiler10;

import java.util.*;

import compiler.exc.*;
import compiler.lib.*;
import compiler10.AST.*;

public class SymbolTableASTVisitor extends BaseASTVisitor<Void,VoidException> {
	
	private List<Map<String, STentry>> symTable = new ArrayList<>();
	private int nestingLevel=0;
	int stErrors=0;

	SymbolTableASTVisitor() {}
	SymbolTableASTVisitor(boolean debug) {super(debug);} // enables print for debugging

	private STentry stLookup(String id) {
		int j = nestingLevel;
		STentry entry = null;
		while (j >= 0 && entry == null) 
			entry = symTable.get(j--).get(id);	
		return entry;
	}

	@Override
	public Void visitNode(ProgLetInNode n) {
		if(print) printNode(n);
		Map<String, STentry> hm = new HashMap<>();
		symTable.add(hm);
	    for (Node dec : n.declist) visit(dec);
		visit(n.exp);
		symTable.remove(0);
		return null;
	}

	@Override
	public Void visitNode(ProgNode n) {
		if(print) printNode(n);
		visit(n.exp);
		return null;
	}
	
	@Override
	public Void visitNode(FunNode n) {
		if(print) printNode(n);
		Map<String, STentry> hm = symTable.get(nestingLevel);
		/*qui mi serve il tipo funzionale come secondo parametro perchè????
		*lo devo creare
		*parTYpes: lista coi tipi dei parametri; tipi dei parametri sono da andare a predere dentro la fun node
		* rettypetipo di ritorno nella fun node è già in una var
		*/
		List<TypeNode> parTypes = new ArrayList<>();  /*da dentro la parlist va
		 												a prendere tutti i tipi di pars*/
		for (ParNode par : n.parlist) parTypes.add(par.type);
		STentry entry = new STentry(nestingLevel, new ArrowTypeNode(parTypes, n.retType));
		//inserimento di ID nella symtable
		if (hm.put(n.id, entry) != null) {
			System.out.println("Fun id " + n.id + " at line "+ n.getLine() +" already declared");
			stErrors++;
		} 
		//creare una nuova hashmap per la symTable
		nestingLevel++;
		Map<String, STentry> hmn = new HashMap<>();
		symTable.add(hmn);
		for (ParNode par : n.parlist)
			//aggiungo come secondo parametro par.type perchè è il tipo di parNode
			if (hmn.put(par.id, new STentry(nestingLevel, par.type)) != null) {
				System.out.println("Par id " + par.id + " at line "+ n.getLine() +" already declared");
				stErrors++;
			}
		for (Node dec : n.declist) visit(dec);
		visit(n.exp);
		//rimuovere la hashmap corrente poiche' esco dallo scope               
		symTable.remove(nestingLevel--);
		return null;
	}
	
	@Override
	public Void visitNode(VarNode n) {
		if(print) printNode(n);
		visit(n.exp);
		Map<String, STentry> hm = symTable.get(nestingLevel);
		//il costruttore cambia per includere anche l'informazione sul tipo a cui è dichairata la va
		//adesso nella pallina viene inclusa anche questa informazione
		//così dal basso verso l'alto posso dire che il "+" per esempio typechecka
		STentry entry = new STentry(nestingLevel, n.type);
		//inserimento di ID nella symtable
		if (hm.put(n.id, entry) != null) {
			System.out.println("Var id " + n.id + " at line "+ n.getLine() +" already declared");
			stErrors++;
		}
		return null;
	}

	@Override
	public Void visitNode(PrintNode n) {
		if(print) printNode(n);
		visit(n.exp);
		return null;
	}

	@Override
	public Void visitNode(IfNode n) {
		if(print) printNode(n);
		visit(n.cond);
		visit(n.th);
		visit(n.el);
		return null;
	}
	
	@Override
	public Void visitNode(EqualNode n) {
		if(print) printNode(n);
		visit(n.left);
		visit(n.right);
		return null;
	}
	
	@Override
	public Void visitNode(TimesNode n) {
		if(print) printNode(n);
		visit(n.left);
		visit(n.right);
		return null;
	}
	
	@Override
	public Void visitNode(PlusNode n) {
		if(print) printNode(n);
		visit(n.left);
		visit(n.right);
		return null;
	}

	@Override
	public Void visitNode(CallNode n) {
		if(print) printNode(n);
		STentry entry = stLookup(n.id);
		if (entry == null) {
			System.out.println("Fun id " + n.id + " at line "+ n.getLine() + " not declared");
			stErrors++;
		} else 
			n.entry = entry;
		for (Node arg : n.arglist) 
			visit(arg);
		return null;
	}

	@Override
	public Void visitNode(IdNode n) {
		if(print) printNode(n);
		STentry entry = stLookup(n.id);
		if (entry == null) {
			System.out.println("Var or Par id " + n.id + " at line "+ n.getLine() + " not declared");
			stErrors++;
		} else 
			n.entry = entry;
		return null;
	}

	@Override
	public Void visitNode(BoolNode n) {
		if(print) printNode(n, n.val.toString());
		return null;
	}

	@Override
	public Void visitNode(IntNode n) {
		if(print) printNode(n, n.val.toString());
		return null;
	}
}

//List<TypeNode> parTypes = new ArrayList<>();  
//for (ParNode par : n.parlist) parTypes.add(par.type); 

