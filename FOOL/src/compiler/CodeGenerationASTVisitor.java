package compiler;

import compiler.AST.*;
import compiler.lib.*;
import compiler.exc.*;
import static compiler.lib.FOOLlib.*;
/*
 * 
 * sta volta le visite ritornano un astringhe procede bottom up a creare una stringa che contiene tutto il codice
 * l'albero è sicuramente completo perchè ho superato la fase di frontend
 * */
/*
 * COSE NON CHIARE
 * EQUAL NODE, IF NODE
 */
public class CodeGenerationASTVisitor extends BaseASTVisitor<String, VoidException> {

  CodeGenerationASTVisitor() {}
  CodeGenerationASTVisitor(boolean debug) {super(false,debug);} //enables print for debugging
/*
 * prima di eseguire il corpo del programma deve allocare l'activation record 
 * (seguendo il layout nel file di esempio(devono rispettare gli offset scelti)) perchè
 * il corpod el programma poi userà le variabiali che altrimenti non potrebebro essere raggiungili
 * 
 * alla prima dichairazione il risultato è visita di dec perchè null viene ignorato dalla funzione
 * poi concatena il risutlato della visita successiva,
 * quindi alloca le variabili una ad una"
 */
	@Override
	public String visitNode(ProgLetInNode n) {
		if (print) printNode(n);
		String declCode = null;/*qui metto il codice per le dichiarazioni:
								 *lo genero un po' per volta visitando le dichairazioni
								 */
		for (Node dec : n.declist) declCode = nlJoin(declCode, visit(dec));//aggiungo a declcode il risultaoto della funzione
		return nlJoin(
				declCode,// generate code for declarations (allocation)
				visit(n.exp),
				"halt");
	}
/*
 * programma con corpo senza dichiarazioni di variabili ecc
 * devo solo ritornare il risultato
 * occhio che qui devo uscire dalla vm
 * alla fine del main c'è halt
 */
	@Override
	public String visitNode(ProgNode n) {
		if (print) printNode(n);
		return nlJoin(
				visit(n.exp),
				"halt"
		);
	}

	@Override
	public String visitNode(FunNode n) {
		if (print) printNode(n,n.id);
		for (ParNode par : n.parlist) visit(par);
		for (Node dec : n.declist) visit(dec);
		visit(n.exp);
		return null;
		//return nlJoin();
	}
/*
 * var node deve allocare sullo stack il valore calcolato per la variabile
 * nella sua inizializzazione
 * faccio la visita: che calcola l'expr e il risultato viene messo sullo stack 
 * così facendo viene allocata la variabile
 * 
 */
	@Override
	public String visitNode(VarNode n) {
		if (print) printNode(n,n.id);
		return visit(n.exp);
	}
/*
 * passa il valore dell'espr stampandolo
 * la vm ha un istruzione magica che accede alle istruzioni del so 
 * prende il top dello stack e lo stampa
 * 
 */
	@Override
	public String visitNode(PrintNode n) {
		if (print) printNode(n);
		return nlJoin(
		visit(n.exp), //genera il codice della sottoexpr, lasciando il risutlato sullo stack
		"print"		//stampa la cima dello stack lasciandolo inalterato 
		);
	}

/*IF-THEN-ELSE funzionale
 * Ha 3 figli :
 * -if
 * -then
 * -cond: 0 o 1
 * push 1 controllo se sono uguali: se vero salto ad l1, se tiro dritto devo ritornarnare l'else
 * 
 */
	@Override
	public String visitNode(IfNode n) {
		if (print) printNode(n);
		String l1 = freshLabel();	//creazione fresh label
		String l2 = freshLabel();
		
		visit(n.cond);
		visit(n.th);
		visit(n.el);
		return nlJoin(
				visit(n.cond), 
				"push 1", 
				"beq "+l1,
				visit(n.el),
				"b "+l2, 
				l1+":",
				visit(n.th),
				l2+":"
				);
	}
/*
 * ha due expr figlie:
 * 1 passo le visito perchè il loro risultato venga messo sullo stack
 * 2 passo: uso il branchequ: poppa due expr controllo se sono uguali
 * se è vero salta ad un etichetta se no procede
 * 
 * Getsione dell'etichetta: devo generare un etichetta fresh
 * --->freshLabel() in fool.lib
 */
	@Override
	public String visitNode(EqualNode n) {
		if (print) printNode(n);
		String l1 = freshLabel();	//creazione fresh label
		String l2 = freshLabel();
		return nlJoin(
				visit(n.left),
				visit(n.right),
				"beq "+l1, 
				"push 0", //caso in cui non sono uguali
				"b "+ l2, //branch incondizionato 
				l1+":",
				"push 1",
				l2+":"
				);
	}

	@Override
	public String visitNode(TimesNode n) {
		if (print) printNode(n);
		return nlJoin(
				visit(n.left), 
				visit(n.right),
				"mult"//codice che prende i valori sullo stack e fa il risultato del prodotto
		);
	}
/*
 * è un sottonodo dell'albero con due sottoespressioni:
 * prima genero codice per la prima expr1 fa quello che deve poi lascia il risultato sullos tack
 * lo stesso fa expr2---> come visito: che mi restituisce una stringa di codice (cgen dei lucidi 
 * quando ho il risultato dell'expr di sx e di dx sullo stack adesso faccio add
 * 
 */
	@Override
	public String visitNode(PlusNode n) {
		if (print) printNode(n);
		return nlJoin(
			visit(n.left), 
			visit(n.right),
			"add"//codice che prende i valori sullo stack e fa il risultato della somma
		);/*ci metto l'nljoin perchè ogni volta che ho più di un argomento lo devo usare*/
	}

	@Override
	public String visitNode(CallNode n) {
		if (print) printNode(n,n.id);
		for (Node arg : n.arglist) visit(arg);
		return null;
		//return nlJoin();
	}

	@Override
	public String visitNode(IdNode n) {
		if (print) printNode(n,n.id);
		
		return nlJoin(
				"lfp",					//prendo il valore fp e lo metto sulla cima dello stack
										//load address of current frame (containing "id" declaration)
				"push" +n.entry.offset,	//metto l'offset sullo stack
										//compute address of "id" declaration
				"add",					//così metto sullo stack l'indiriizzo della var
				"lw" 					//prende l'indirizzo sulla cima dello stack, lo poppa e mette al suo posto i valore della variabile corrisposndente
										//load value of "id" variable
				);
	}

	@Override
	public String visitNode(BoolNode n) {
		if (print) printNode(n,n.val.toString());
		return "push "+(n.val?1:0);
	}

	@Override
	public String visitNode(IntNode n) {
		if (print) printNode(n,n.val.toString());
		return "push "+n.val; //la conversione viene fatta in un atuomatico (questo è quello che passo alla famosa funzione del punto 4)
							//remeber: da rispettare l'invariante lo stack va lasciato come lo si è trovato con il regalo per la mamma in cima
	}
}

//	String getAR = null;
//	for (int i = 0; i < n.nl - n.entry.nl; i++) getAR = nlJoin(getAR, "lw");

// by following the static chain (of Access Links)


//	String funl = freshFunLabel();


//	for (int i = n.arglist.size() - 1; i >= 0; i--) argCode = nlJoin(argCode, visit(n.arglist.get(i)));

// load Control Link (pointer to frame of function "id" caller)
// generate code for argument expressions in reversed order

// set $tm to popped value (with the aim of duplicating top of stack)
// load Access Link (pointer to frame of function "id" declaration)
// duplicate top of stack 

// jump to popped address (saving address of subsequent instruction in $ra)
