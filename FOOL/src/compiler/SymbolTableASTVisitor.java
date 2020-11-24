package compiler;

import java.util.*;
import java.util.Map;

import compiler.AST.*;
import compiler.lib.*;
import compiler.lib.Node;

public class SymbolTableASTVisitor extends BaseASTVisitor<Void> {
	/*
	 * Ho una lista di tabelle organizzata per scope
	 * ambiente globale nesting level 0
	 * la mappa a indice nesting level è quella in cui siamo attualmente.
	 * quando entro in uno scope aumento il nesting level e quando esco lo decremento
	 * 
	 * Quando faccio qualcosa con la nesting table : quando incontro dichiarazioni/usi entro/esco da scope:
	 * 
	 *  
	 * se ho un prog node: programma senza dichairazione non faccio nulla continuo semplicemente la mia visita
	 * uguale se incontro un intero, times, plus, bool ecc.
	 * 
	 * Qusesta visita torna void come la print perchè il suo obiettivo èarrichhire l'albero quando incontra un uso che fa match con una dichiarazione 
	 */
	int stErrors=0;
	private List<Map<String, STentry>> symTable = new ArrayList<>();
	private int nestingLevel=0;
	/*
	 * livello ambiente con dichiarazioni piu' esterno è 0 (prima posizione ArrayList) invece che 1 (slides)
	 * il "fronte" della lista di tabelle è symTable.get(nestingLevel)
	 */

	SymbolTableASTVisitor() {}
	SymbolTableASTVisitor(boolean debug) {super(debug);} // p=true enables print for debugging

	@Override
	public Void visitNode(ProgNode n) {
		if (print) printNode(n);
		visit(n.exp);
		return null;
	}
	
	@Override
	public Void visitNode(IntNode n) {
		if (print) printNode(n, n.val.toString());
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
	public Void visitNode(TimesNode n) {
		if (print) printNode(n);
		visit(n.left);
		visit(n.right);
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
	public Void visitNode(BoolNode n) {
		if (print) printNode(n, n.val.toString());
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
	public Void visitNode(PrintNode n) {
		if (print) printNode(n);
		visit(n.exp);
		return null;
	}

//Iniziano i nodi interessanti
	
	/*
	 * Se sono nel corpo principale del programma
	 * (questa è la radice quando ho un LET IN nel prog body)
	 * Cosa faccio in questo caso?
	 * COsa faccio con le dichairazioni che vedo come figli?
	 * QUi in pratica stiamo entrando nello scope dell'ambiente globale, 
	 * la variabile nesting level parte da 0 quindi va già bene 
	 * ma devo creare la tabella per l'ambiente globale
	 * 
	 */
	@Override
	public Void visitNode(ProgLetInNode n) {
		if (print) printNode(n);
		Map<String, STentry>hm= new HashMap<>();	//tabella che conterrà le dichiarazioni dell'ambiente globale
		symTable.add(hm); 							//aggiungo la tabella alla mia symbol table
		for (Node dec : n.declist) visit(dec);		//visito le dichiarazioni
		visit(n.exp);								//visito il corpo che userà le dichairazioni
		symTable.remove(0);							//ide ala hashmap cresce quando entro in scope e decresce quando esco quando finisce il programma posso buttare via tutto (rimuovo quella dell'abiente globale perchè le altre teoricamente le ho già rimosse tutte)
		return null;
	}
/*
 * Inserisco nel fronte della tabella la dichiarazione della variabile
 */
	@Override
	public Void visitNode(VarNode n) {
		if (print) printNode(n,n.id);				
													//vado ad inserire nella tabella che sta al fronte della lista l'id della variabile , ma devo vedere se c'è già
													//questo lavoro va fatto prima o dopo la visita a exp? Prima.
		visit(n.exp);
		Map<String, STentry>hm=this.symTable.get(nestingLevel); //mi da la tabella dello scope corrente
		STentry entry = new STentry(nestingLevel);				//Creo un anuova pallina
		if(hm.put(n.id, entry)!=null) {				//inserimento id nella symboltable, ma devo controllare se c'era già (in Java il metodo put controlla se la chiave c'era già se put torna null la chiave non esisteva se c'era già ritorna il valore della vecchia chiave

			System.out.println("Var id " + n.id + " at line "+ n.getLine() +" already declared");
			this.stErrors++;
		}
		return null;
	}

	/*
	 * Inserisco nel fronte della tabella la dichiarazione della funzione
	 * In questo caso andiamo come prima ad inserire il nome della funzione (dopo averne incontrato la dichiarazione)n nel fronte della lista 
	 */
	@Override
	public Void visitNode(FunNode n) {
		if (print) printNode(n,n.id);
		Map<String, STentry>hm=this.symTable.get(nestingLevel); //mi da la tabella dello scope corrente
		STentry entry = new STentry(nestingLevel);				//Creo un anuova pallina
		if(hm.put(n.id, entry)!=null) {							//inserimento id nella symboltable, ma devo controllare se c'era già
																//(in Java il metodo put controlla se la chiave c'era già se put torna null 
																//la chiave non esisteva se c'era già ritorna il valore della vecchia chiave
																//NB ora n.id= nome funzione

			System.out.println("Var id " + n.id + " at line "+ n.getLine() +" already declared");
			this.stErrors++;
		}
		 
		// ora devo entrare in un nuovo scope; incremento nesting level + creo una nuova hashmap per il nuovo scope
		this.nestingLevel++;
		Map<String, STentry>nhm= new HashMap<>();	//tabella che conterrà le dichiarazioni dell'ambiente globale
		symTable.add(nhm); 							//aggiungo la tabella alla mia symbol table	
		// for (ParNode par : n.parlist) visit(par);
		for (Node dec : n.declist) visit(dec);		//visito le dichiarazioni di variabili che possono essere variabili o funzioni
		visit(n.exp);								//visito il corpo della mia funzione che può usare cose locali o cose in nesting level inferiori (finoa d arrivare a zero)
		
		//prima di terminare devo uscire dallo scope
		this.symTable.remove(nestingLevel--);
		return null;
	}

	/*
	 * metodo che data l'id della var la va a cercare nella symboltable e ritorna la pallina se la trova e null altrimenti
	 */
	private STentry stLookup(String id) {
		int j= this.nestingLevel; //parto dal nesting level in cui sono 
		STentry entry  =null;
		while (j>=0 && entry==null) {	//continuo la ricerca fino alla prima tabella finchè non trovo la dichiarazione della var che sto cercando
			entry=this.symTable.get(j--).get(id); //mi da inizialmente la tabella a nestinglevel e cerco di farmi dare la var che sto cercando 
		}
		return entry;//se quando esco entry è ancora null allora non ha trovato la pallina al contrario l'ha trovata
	}
	
	@Override
	public Void visitNode(IdNode n) {
		if (print) printNode(n, n.id);
		STentry entry  =stLookup(n.id); 
		if(entry==null) {			//se entry mi torna nullo errore
			System.out.println("Var id " + n.id + " at line "+ n.getLine() +" not declared");
			this.stErrors++;
		}else {//se la trovo attacco la pallina decorando con la dichiarazione con gli usi
			n.entry=entry;//attacco la pallina
		}
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
		// for (Node arg : n.arglist) visit(arg);
		return null;
	}
}
