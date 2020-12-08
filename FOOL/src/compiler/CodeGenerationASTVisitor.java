package compiler;

import compiler.AST.*;
import compiler.lib.*;
import compiler.exc.*;
import static compiler.lib.FOOLlib.*;
/*
 * 
 * sta volta le visite ritornano stringhe 
 * procede bottom up a creare una stringa che contiene tutto il codice
 * l'albero è sicuramente completo perchè ho superato la fase di frontend
 * */
/*
 * COSE NON CHIARE
 * EQUAL NODE, IF NODE, ID NODE: doppio lw (access kubj
 */
public class CodeGenerationASTVisitor extends BaseASTVisitor<String, VoidException> {

  CodeGenerationASTVisitor() {}
  CodeGenerationASTVisitor(boolean debug) {super(false,debug);} //enables print for debugging
/*
 * prima di eseguire il corpo del programma deve allocare l'activation record 
 * (seguendo il layout nel file di esempio(devono rispettare gli offset scelti)) perchè
 * il corpo del programma poi userà le variabili che altrimenti non potrebebro essere raggiungibili
 *
 * nlJoin:
 * alla prima dichairazione il risultato è visita di dec perchè null viene ignorato dalla funzione
 * poi concatena il risutlato della visita successiva,
 * quindi alloca le variabili una ad una"
 */
	@Override
	public String visitNode(ProgLetInNode n) {
		if (print) printNode(n);
		String declCode = null;/*qui metto il codice generato considerando le dichiarazioni 
								nel loro ordine
		 						per le dichiarazioni: partendo da offset -1
								 *lo genero un po' per volta visitando le dichairazioni
								 *le "dec" sono var dec: dichiarazioni di variabili
								 */
		//l'istruzione seguente genera il codice di allocazione per ogni variabile
		for (Node dec : n.declist) declCode = nlJoin(declCode, visit(dec));//aggiungo a declcode il risultaoto della funzione
		
		return nlJoin(
				"push 0", /*devo impostare un return address fittizio a 0 o 
							quello che mi pare nessuno lo legegerà ma devo 
							uniformare l'ambiente globale a quello delle funzioni
							*/
				declCode,// generate code for declarations (allocation)
							//lei mi deve mettere nello stack il suo indirizzo
				visit(n.exp),
				"halt", 
				getCode());
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
/*
 * allocare la dichairazione di una funzione 
 * vuol dire metterci il suo indirizzo 
 */
	@Override
	public String visitNode(FunNode n) {
		if (print) printNode(n,n.id);
		String funl = freshFunLabel();	//creazione fresh label
		String declCode = null;
		String localPar=null;
		/*
		 * 		String localdec=null;
		if (print) printNode(n,n.id);
		for (Node arg : n.arglist) visit(arg);
		for (int i = n.arglist.size() - 1; i >= 0; i--) argCode = nlJoin(argCode, visit(n.arglist.get(i)));//Devo visitarle in ordine inverso perchè?che cosa sto salvando?
		String getAR = null;
		for (int i = 0; i < n.nl - n.entry.nl; i++) getAR = nlJoin(getAR, "lw");//getAr codice che mi serve per raggiunger l'ar della dichiarazione; iterando sulla differenza di nesting level ad ogni iterazione aggiungo una lw alla stringa

		 */
		for (ParNode par : n.parlist) localPar=nlJoin(localPar,visit(par));
		for (Node dec : n.declist) declCode=nlJoin(declCode,visit(dec));
		visit(n.exp);
		visit(n.exp);
		putCode(nlJoin(funl+ ":" +
				/*codice corpo funzione*/
				"push",
				"cfp",
				"lra",
				declCode,
				localPar,
				"stm",
				"pop",
				"pop",
				"pop",
				"push",
				"ltm",
				"lra"
				//jump
				)); /*posto dove colleziono tutti i codici delle funzioni e
		 														quando ho finito li metto tutti dopo
		 														qui faccio push di un'etichetta a cui poi dovrò andare a mettere il corpo della funzione*/
		return "push" + funl;
	}
/*
 * var node deve allocare sullo stack il valore calcolato per la variabile
 * nella sua inizializzazione
 * faccio la visita: che calcola l'expr e il risultato viene messo sullo stack 
 * così facendo viene allocata la variabile
 * //Devo allocare il valore che serve per inizializzare la variabile 
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
				"push 1", 	//pusho e controllo se è uguale al risultato della visita	
				"beq "+l1,	//se sono uguali salto allo then 
							//if(cond)then{1}else{0}; 
							//(0 == false e 1== true)
				visit(n.el),
				"b "+l2,	//Se ho fatto l'else salto alla fine 
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
 * Gestione dell'etichetta: devo generare un etichetta fresh
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
				"beq "+l1,	//Se sono uguali salto direttamente a l1 
				"push 0",	//caso in cui non sono uguali: 0 ==false
				"b "+ l2,	//branch incondizionato se arrivo qui 
							//salto a l2 per evitare di eseguire l1
				l1+":",
					"push 1",//dico il risultato della beq 
							//ha tornato true quindi  n.left==n.right
				l2+":"		//do nothing by default
				);
	}

	@Override
	public String visitNode(TimesNode n) {
		if (print) printNode(n);
		return nlJoin(
				visit(n.left), 
				visit(n.right),
				"mult"//codice che prende i valori sullo stack e 
					//fa il risultato del prodotto
		);
	}
/*
 * è un sottonodo dell'albero con due sottoespressioni:
 * prima genero codice per la prima expr1 fa quello che deve poi lascia il
 *  risultato sullos tack
 * lo stesso fa expr2---> come visito: che mi restituisce una 
 * stringa di codice (cgen dei lucidi )
 * quando ho il risultato dell'expr di sx e di dx sullo stack adesso faccio add
 * 
 */
	@Override
	public String visitNode(PlusNode n) {
		if (print) printNode(n);
		return nlJoin(
			visit(n.left), 
			visit(n.right),
			"add"/*codice che prende i valori sullo stack e 
				fa il risultato della somma*/
		);/*ci metto l'nljoin perchè ogni volta che ho più di 
			un argomento lo devo usare*/
	}

	@Override
	public String visitNode(CallNode n) {
		String argCode=null;
		if (print) printNode(n,n.id);
		for (Node arg : n.arglist) visit(arg);
		for (int i = n.arglist.size() - 1; i >= 0; i--) argCode = nlJoin(argCode, visit(n.arglist.get(i)));
				//Devo visitarle in ordine inverso perchè?che cosa sto salvando?
		String getAR = null;
		for (int i = 0; i < n.nl - n.entry.nl; i++) getAR = 
				nlJoin(getAR, "lw");/*getAr codice che mi serve per raggiungere l'ar della dichiarazione; 
									 iterando sulla differenza di nesting level ad ogni iterazione 
									 aggiungo una lw alla stringa*/

		return nlJoin(
				"lfp",	/*prima cosa da fare caricare sullo stack il control link; 
						il frame dove sono adesso è lfp: ossia il frame del chiamante (sono io)
						*/
						// load Control Link (pointer to frame of function "id" caller)
				argCode,/*mette il valore degli argomenti sullo stack*/
							// generate code for argument expressions in reversed order
						/*il valore dell'access link è il puntatore al frame dove è dichairata la funzione ; 
						lo stesso frame dove è dichairata la funzione mi serve anche per l'indirizzo della 
						funzione(che troverò tramite l'offset)*/
				"lfp",		// retrieve address of frame containing "id" declaration
				getAR,		// by following the static chain (of Access Links)
				"stm",  /*poppa un valore dallo stack e lo mette nel?? ltm la prima volta setta l'accesslink, 
						l'altro lo uso per recueprare l'indirizzo della funzione ed effettjujare il salto*/
							// set $tm to popped value (with the aim of duplicating top of stack)
				"ltm",		// load Access Link (pointer to frame of function "id" declaration)
				"ltm",		// duplicate top of stack
				"push " + n.entry.offset, "add",// compute address of "id" declaration)
				"lw",		// load address of "id" function
				"js"		// jump to popped address (saving address of subsequent instruction in $ra)
				);
	}

	@Override
	public String visitNode(IdNode n) {
		if (print) printNode(n,n.id);	
		String getAR = null;
		for (int i = 0; i < n.nl - n.entry.nl; i++) getAR = nlJoin(getAR, "lw");
		/*getAr codice che mi serve per raggiunger l'ar della dichiarazione; 
		iterando sulla differenza di nesting level ad ogni iterazione aggiungo una lw alla stringa
		*/
	
		return nlJoin(
				"lfp",					//prendo il valore fp e lo metto sulla cima dello stack;
											//retrieve address of frame containing "id" declaration
				getAR, 					/*per raggiungere il frame corretto devo risalire la catena 
											degli access link dopo aver fatto lw punto al fram che mi 
											richiude sintatticamente ed in particolare punto al suo access link
											*/
											// by following the static chain (of Access Links)
				"push" +n.entry.offset,	//metto l'offset sullo stack
											//compute address of "id" declaration
				"add",					//metto sullo stack l'indiriizzo della var
				"lw" 					/*prende l'indirizzo sulla cima dello stack, 
										lo poppa e mette al suo posto i valore della variabile corrisposndente
										*/
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
		return "push "+n.val; //la conversione viene fatta in un automatico (questo è quello che passo alla famosa funzione del punto 4)
							//remeber: da rispettare l'invariante lo stack va lasciato come lo si è trovato con il regalo per la mamma in cima
	}
}