package compiler10;

import compiler.lib.*;
import compiler10.AST.*;
import compiler.exc.*;
import static compiler.lib.FOOLlib.*;
/*???
 * IN CHE SENSO FACCIO LE VISIT MA NON LE USO?
* perchè import statica
 * perchè ogni volta devo creare un nuovo tipo???????
 * 	new IntTypeNode()
 * if node e equal node riguarda che cosa ornano
 * 
 * 
 * 
 */
/*visit(n) fa il type checking di un Node n e ritorna: 
* per una espressione, il suo tipo (oggetto BoolTypeNode o IntTypeNode)
*per una dichiarazione, "null"
*/



/*typechecking viene fatto come visita dell'ast arricchito 
 * è ASTVisito e non EAST perchè tecnicamente le stentry le raggiungiamo ma non ci entriamo
 * i metodi visit visitano solo la parte di ast visitor
 * le estentry non le visitiamo ma ne estreiamo le informazioni perchè esse fondamentali.
 * 
 * Mo facciamo il typechecking di tutta la grammatica meno delle chaimate a funzione 
 * 
 * 
 * relazioen di subtyping che prendiamo 
 * 
 * cominciamo dai nodi facili : tutti return null vanno cambiati con un return di un qualche tipo 
 */
public class TypeCheckASTVisitor extends BaseASTVisitor<TypeNode,TypeException> {

	TypeCheckASTVisitor() {super(true);}// enables incomplete tree exceptions 
	TypeCheckASTVisitor(boolean debug) { super(true, debug); } // enables print for debugging
/*
 * ha una parte di dichiarazione e l'espressione
 * lui mette in sym le dichairazioni e poi valuta gli usi in base a ciò che è stato messo nelle sym
 * -che tipo torna per il let in exp
 * +il tipo di exp
 * Il tipo del programma è il tipo del corpo valutato avvalendosi delle dichiarazioni
 * Cosa faccio col risultato della visita delle dichiarazioni (dichairazioni di var  e espressioni)
 * per le dichairazioni tonro null perchè non ho un tipo che mi serve; 
 * faccio un check interno alla dichiarazione  (vedi visitNode(VArNode)
 * 
 * Idea: mettere dentro una try catch la viista di una singola dichiarazione per poter isolare l'errore e 
 * continuare a eseguire il codice per accumulare più errori possibili
 */
	@Override
	public TypeNode visitNode(ProgLetInNode n) throws TypeException {
		if (print) printNode(n);
		for (Node dec : n.declist)  //questo fa il controllo interno alla dichairazione (per par node o fun node), se i controlli vengono superati visito il corpo della programam
			try {
				visit(dec);
			} catch (TypeException e) {
				System.out.println("Type checking error in a declaration: "+e.text);
			} catch (IncomplException e) {}
		return visit(n.exp);//torno il  tipo della visita del corspo
	}
/*
 * vista il main del programma dove non ci sono dichairazioni ; 
 * restituisce il tipo del main
 */
	@Override
	public TypeNode visitNode(ProgNode n) throws TypeException {
		if (print) printNode(n);
		return visit(n.exp);
	}
/*
 * COntrolliamo che nella dichiarazione di funzione 
 */
	@Override
	public TypeNode visitNode(FunNode n) throws TypeException {
		if (print) printNode(n,n.id);
		//lo elimino perchè?
		//for (ParNode par : n.parlist) visit(par);
		for (Node dec : n.declist)
			try {
				visit(dec);
			} catch (TypeException e) {
				System.out.println("Type checking error in a declaration: "+e.text);
			} catch (IncomplException e) {}
		if(!(isSubtype(visit(n.exp), n.retType)))
			throw new TypeException("Wrong return type for function " + n.id,n.getLine());
		return null;
	}
/*
 * controllo devo controllare che come nella regola del let in la visita dell'expr mi dia un sottotipo per il tipo dichairato per la variabile 
*/
	@Override
	public TypeNode visitNode(VarNode n) throws TypeException {
		if (print) printNode(n,n.id);
		if(!(isSubtype(visit(n.exp), n.type)))
			throw new TypeException("Incompatible value for variable " + n.id,n.getLine());
		return null; //come anticipato fa un check interno quindi torna null
	}
/*
 * print node ritorna ciò che gli da la visita senza errori
 */
	@Override
	public TypeNode visitNode(PrintNode n) throws TypeException {
		if (print) printNode(n);
		return visit(n.exp);
	}


	@Override
	public TypeNode visitNode(IfNode n) throws TypeException {
		if (print) printNode(n);
		if(!(isSubtype(visit(n.cond), new BoolTypeNode())))
				throw new TypeException("Non boolean condition in if",n.getLine());
		TypeNode t=visit(n.th);
		TypeNode e=visit(n.el);
		if(!(isSubtype(t, e )))
			return e;
		if(!(isSubtype(e, t )))
			return t;
		throw new TypeException("Incompatible types in then-else branches",n.getLine());
	}

	@Override
	public TypeNode visitNode(EqualNode n) throws TypeException {
		if (print) printNode(n);
		TypeNode l= visit(n.left);
		TypeNode r= visit(n.right);
		//se un figlio è di un tipo sottotipo dell'altro
		if(!(isSubtype(l, r )||isSubtype(r, l)))
			throw new TypeException("Incompatible types in equal",n.getLine());
		return new BoolTypeNode();
	}

	@Override
	public TypeNode visitNode(TimesNode n) throws TypeException {
		if (print) printNode(n);
		if(!(isSubtype(visit(n.left), new IntTypeNode() )
				&& isSubtype(visit(n.right), new IntTypeNode())))
				throw new TypeException("Non integers in multiplation",n.getLine());
		return new IntTypeNode();
	}
/*
 * cosa dobbiamo fare?
 * nella regola tipaggio del plus node mi aspetto di lavorare con gli interi
 * ma potrei avere anche dei sottoipi dei interi; quidni devo controllarre quest'ultimo caso
 * quindi visito i due figli left e right che ritorneranno un tipo ed io devo controllare che questi siano sottotipi di iint
 * 
 */
	@Override
	public TypeNode visitNode(PlusNode n) throws TypeException {
		if (print) printNode(n);
		if(!(isSubtype(visit(n.left), new IntTypeNode() )
				&& isSubtype(visit(n.right), new IntTypeNode())))
				throw new TypeException("Non integers in sum",n.getLine());
		return new IntTypeNode();
	}
	
/*
*saltato per il momento
*/
	@Override
	public TypeNode visitNode(CallNode n) throws TypeException {
		if (print) printNode(n,n.id);
		
		if( n.entry.type instanceof ArrowTypeNode)	/*gli errori li facciamo al contrario perchè voglio sapere quando lanciare l'eccezione e si blocca tutto*/
			throw new TypeException("Invocation of a non-function"+n.id, n.getLine());
		for (Node arg : n.arglist) {
			if(!(isSubtype(visit(arg),  n.entry.type))) {
				throw new TypeException("  Wrong type for"+n.arglist.indexOf(arg)+
						"-th parameter in the invocation of"+n.id, n.getLine());
			}

		}
		return null;
	}
/*
 * Questa è una var che cosa torno?
 * x + 5 id è x (NOT SO SURE?)
 * Se ho un id node : ho x nel codice senza ( perchè sto usando x 
 * x deve essere una variabile o un parametro non può essere una funzione
 * - chi mi dice che non è una funzione?
 * +nessuno, quindi come faccio a rendermene conto?
 * -vado nella pallina e guardo il typo dell'd; 
 * 	ci va bene se non è un arrowtypenode perchè ?
 * + perchè devo controllare che non vengano sommate funzioni con interi
 * 
 */
	@Override
	public TypeNode visitNode(IdNode n) throws TypeException {
		if (print) printNode(n,n.id);
		if( n.entry.type instanceof ArrowTypeNode)	/*gli errori li facciamo al contrario perchè voglio sapere quando lanciare l'eccezione e si blocca tutto*/
			throw new TypeException("Wrong usage of function identifier "+n.id,n.getLine());
		return n.entry.type; //vado dentro la pallina e prendo il type
	}

	@Override
	public TypeNode visitNode(BoolNode n) {
		if (print) printNode(n,n.val.toString());
		return new BoolTypeNode();
	}

	@Override
	public TypeNode visitNode(IntNode n) {
		if (print) printNode(n,n.val.toString());
		return new	IntTypeNode();
	}

}