package compiler;

import java.util.*;
import compiler.AST.*;
import compiler.exc.*;
import compiler.lib.*;
/*
 * all'inizio nel linguaggio in cui non ho annidamenti ho solo il nesting level 0 
 */
public class SymbolTableASTVisitor extends BaseASTVisitor<Void,VoidException> {
	private int decOffset=-2; /* counter for offset of local declarations at current nesting level; 
								*parte da -2 perch� nel mio ar layout ad offset -1 ho il return address
								*/
	private List<Map<String, STentry>> symTable = new ArrayList<>();
	private int nestingLevel=0; // current nesting level
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
/*
 * progletin node  � colui che infoca le visite sui va node
 */
	@Override
	public Void visitNode(ProgLetInNode n) {
		if (print) printNode(n);
		Map<String, STentry> hm = new HashMap<>();
		symTable.add(hm);
	    for (Node dec : n.declist) visit(dec);
		visit(n.exp);
		symTable.remove(0);
		return null;
	}

	@Override
	public Void visitNode(ProgNode n) {
		if (print) printNode(n);
		visit(n.exp);
		return null;
	}
	
	@Override
	public Void visitNode(FunNode n) {
		if (print) printNode(n);
		Map<String, STentry> hm = symTable.get(nestingLevel);
		List<TypeNode> parTypes = new ArrayList<>();  
		for (ParNode par : n.parlist) parTypes.add(par.type); 
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		STentry entry = new STentry(nestingLevel, new ArrowTypeNode(parTypes,n.retType));
		if (hm.put(n.id, entry) != null) {//inserimento di ID nella symtable
			System.out.println("Fun id " + n.id + " at line "+ n.getLine() +" already declared");
			stErrors++;
		} 
		nestingLevel++;
		Map<String, STentry> hmn = new HashMap<>();//creare una nuova hashmap per la symTable
		symTable.add(hmn);
		int prevNLDecOffset=decOffset; // stores counter for offset of declarations at previous nesting level 
		this.decOffset=-2;
		
		int parOffSet=1;				//cos� realizziamo gli offset dei parametri
		for (ParNode par : n.parlist)
			if (hmn.put(par.id, new STentry(nestingLevel,par.type, parOffSet++)) != null) {
				System.out.println("Par id " + par.id + " at line "+ n.getLine() +" already declared");
				stErrors++;
			}
		for (Node dec : n.declist) visit(dec);
		visit(n.exp);    
		symTable.remove(nestingLevel--);//rimuovere la hashmap corrente poiche' esco dallo scope
		decOffset=prevNLDecOffset; // restores counter for offset of declarations at previous nesting level
		return null;
	}
	
	//caso delle varibili
	@Override
	public Void visitNode(VarNode n) {
		if (print) printNode(n);
		visit(n.exp);
		Map<String, STentry> hm = symTable.get(nestingLevel);
		STentry entry = new STentry(nestingLevel,n.type, decOffset--);//nuovo modo di fare palline, lo post decremento cos� � gi� settato per la prossima var
		//inserimento di ID nella symtable
		if (hm.put(n.id, entry) != null) {
			System.out.println("Var id " + n.id + " at line "+ n.getLine() +" already declared");
			stErrors++;
		}
		return null;
	}

	@Override
	public Void visitNode(PrintNode n) {
		if (print) printNode(n);
		visit(n.exp);
		return null;
	}

	@Override
	public Void visitNode(IfNode n) {
		if (print) printNode(n);
		visit(n.cond);
		visit(n.th);
		visit(n.el);
		return null;
	}
	
	@Override
	public Void visitNode(EqualNode n) {
		if (print) printNode(n);
		visit(n.left);
		visit(n.right);
		return null;
	}
	
	@Override
	public Void visitNode(TimesNode n) {
		if (print) printNode(n);
		visit(n.left);
		visit(n.right);
		return null;
	}
	
	@Override
	public Void visitNode(PlusNode n) {
		if (print) printNode(n);
		visit(n.left);
		visit(n.right);
		return null;
	}

	@Override
	public Void visitNode(CallNode n) {
		if (print) printNode(n);
		STentry entry = stLookup(n.id);
		if (entry == null) {
			System.out.println("Fun id " + n.id + " at line "+ n.getLine() + " not declared");
			stErrors++;
		} else {
			n.entry = entry;
			n.nl= this.nestingLevel;//memorizzo il nesting level dell'uso di questo identificatore
		}
		for (Node arg : n.arglist) visit(arg);
		return null;
	}

	@Override
	public Void visitNode(IdNode n) {
		if (print) printNode(n);
		STentry entry = stLookup(n.id);
		if (entry == null) {
			System.out.println("Var or Par id " + n.id + " at line "+ n.getLine() + " not declared");
			stErrors++;
		} else {//caso senza errori
			n.entry = entry;//attacco la pallina alla foglia
			n.nl= this.nestingLevel;//memorizzo il nesting level dell'uso di questo identificatore
		}
			
		return null;
	}

	@Override
	public Void visitNode(BoolNode n) {
		if (print) printNode(n, n.val.toString());
		return null;
	}

	@Override
	public Void visitNode(IntNode n) {
		if (print) printNode(n, n.val.toString());
		return null;
	}
}

//	int prevNLDecOffset=decOffset; // stores counter for offset of declarations at previous nesting level 

//	decOffset=prevNLDecOffset; // restores counter for offset of declarations at previous nesting level 


//	n.nl = nestingLevel; //
