package compiler;

import compiler.AST.*;
import compiler.lib.*;
import compiler.exc.*;
import static compiler.lib.FOOLlib.*;
/*
 * AIM
 * procede bottom up a creare una stringa che contiene tutto il codice
 * l'albero è sicuramente completo perchè ho superato la fase di frontend
 * */

/*
 * prima di eseguire il corpo del programma deve allocare l'activation record 
 * (seguendo il layout nel file di esempio(devono rispettare gli offset scelti)) perchè
 * il corpo del programma poi userà le variabili che altrimenti non potrebbero essere raggiungibili
 * DICHIARAZIONI
 * "dec": varDec: dichiarazioni di variabili; 
 * Le dichiarazioni: partono da offset -2 [stanno stack]	
 *
 * nlJoin:
 * alla prima dichairazione il risultato è visita di dec perchè null viene ignorato dalla funzione
 * poi concatena il risutlato della visita successiva,
 * quindi alloca le variabili una ad una"
 */
public class CodeGenerationASTVisitor extends BaseASTVisitor<String, VoidException> {

  CodeGenerationASTVisitor() {}
  CodeGenerationASTVisitor(boolean debug) {super(false,debug);} //enables print for debugging

	@Override
	public String visitNode(ProgLetInNode n) {
		if (print) printNode(n);
		String declCode = null;/*qui metto il codice generato considerando le dichiarazioni nel loro ordine.		*/
		
		for (Node dec : n.declist) declCode = nlJoin(declCode, visit(dec));//l'istruzione seguente genera il codice di allocazione per ogni variabile,aggiunge a declcode il risultato della visita alla dichiarazione
		
		return nlJoin(
				"push 0", /*devo impostare un return address fittizio a 0 non verrà letto perchè il main va in halt(=terminazione programma), ma mi serve per uniformare l'ambiente globale a quello delle funzioni se no gli offset ecc. mi si scazzano tutti*/
				declCode,// generate code for declarations (allocation); mette sullo stack il valore con cui le dichiarazioni sono inizializzate
				visit(n.exp),//visito il corpo del programma
				"halt",// halt(=terminazione programma)
				getCode());//recupera dalla classe che mette a disposizione le funzioni di libreria il codice creato per l'intero programma; fool lib c'è come un campo apposito
	}
/*
 * programma con corpo SENZA dichiarazioni di variabili ecc.
 * AIM: devo solo ritornare il risultato
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
 * Qui stiamo terminando l'AR che ha iniziato a costruire la call
 * 
 * fare in modo che la posizione di riferimento punti all'access link; essendo che call lo ha appena messo; la prima chosa che faccio è settare fp in modo che punti; così sono a posto
 * il chiamante ha usato js per saltare qui, js salta all'inidirizzo che c'pè sulla cima della stack e memorizza in il return address ossia l'inirizzo dell'istruzione successiva al js; devo memorizzarlo sullo stack; in quanto parte dell'AR , è l'indirizzo al quale dovrò tornare
 * necessario allorale dichiarazioni locali della funzione, ognuna alloca una cosa sola, in casso di variabile il risultato della loro espressione di inizializzazione, o l'indirizzo della funzione dichiarata in caso di funzioni
 * 
 * NB i parametri della callNode sono stati messi in ordine inverso per allocati con offset da 1 a N, mentre le dichiarazioni hanno offset da -2 a -N sono nello stack
 * NB perché è fondamentale settare fp all'inizio? Dentro declCode posso avere dichiarazioni di variabili che sono inizializzati con espressioni che devono essere valutate in quale ambiente? Potrebbe usare per nella sua espresssione variaili che abbiamo appena dichiarato, che quindi deve cercare nell'activation record in cui siamo, quindi fp deve essere già settato.
 * 
 * visito il corpo della funzione
 * salvo in temp il risultato della funzione 
 * inizio a smontare l'ar
 * rimuovo le local declaration
 * rimuovo il return addrees, mettendolo in ra così ce l'ho per quando devo saltare indietro al chaimante
 * rimuovo l'access link(ultima cosa allocata dal chiamante)
 * rimuovo gli argomenti (allocati dal chiamante prima di chiamarci)
 * rimuovo il control link, mi serviva per ripristinare il frame pointer alla posizione di riferimento del fram del chiamante perchè questi possa continuare l'esecuzione come se nulla fosse successo
 * recupero il risultato della funzione carico il return address da ra
 * e salto indietro al chiamante
 * 
 * REMEMBER
 * allocare la dichairazione di:
 * - una funzione vuol dire metterci il suo indirizzo 
 * - una variabile vuole dire metterci il valore con cui è inizializzata
 */
	@Override
	public String visitNode(FunNode n) {
		if (print) printNode(n,n.id);
		String declCode = null, popDecl = null, popParl = null;
		for (Node dec : n.declist) {
			declCode = nlJoin(declCode,visit(dec)); //inserisce il valore con cui è inizializzata ogni dichiarazione
			popDecl = nlJoin(popDecl,"pop");	//inserisce un pop per ogni dichiarazione presente, perchè: dopo andranno tutte rimosse dallo stack, la funzione deve lasciare lo stack inalterato, aggiungendovi solo il suo risultato.
		}
		for (int i=0;i<n.parlist.size();i++) popParl = nlJoin(popParl,"pop"); //insierisce un pop per ogni argomento presente NB gli argomenti sono già stati messi sullo stack da call
		String funl = freshFunLabel();//viene allocata tramite push 
		//questo è il momento giusto perchè ascolta i primi 10 minuti
		putCode(
			nlJoin( //devo finire di allocare l'activation record creato da call [chiamante
				funl+":",
				"cfp", // set $fp to $sp value
				"lra", // load $ra value //il chiamante ha usato js per arrivare a noi, nel js c'è l'istruzione successiva a js alla quale dobbiamo tornare
				declCode, // generate code for local declarations (they use the new $fp!!!)		
				visit(n.exp), // generate code for function body expression
				"stm", // set $tm to popped value (function result)
				
				//inizio a rimuovere l'activation record
				popDecl, // remove local declarations from stack
				"sra", // set $ra to popped value
				"pop", // remove Access Link from stack
				popParl, // remove parameters from stack
				"sfp", // set $fp to popped value (Control Link)
				"ltm", // load $tm value (function result)
				"lra", // load $ra value
				"js"  // jump to to popped address
			)
		);
		return "push "+funl;	
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
				"beq "+l1,	//se sono uguali salto allo then //if(cond)then{1}else{0}; //(0 == false e 1== true)
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
			"add"/*codice che prende i valori sullo stack e fa il risultato della somma*/
		);/*ci metto l'nljoin perchè ogni volta che ho più di un argomento lo devo usare*/
	}
	
	@Override
	public String visitNode(CallNode n) {
		if (print) printNode(n,n.id);
		String argCode = null, getAR = null;
		for (int i=n.arglist.size()-1;i>=0;i--) argCode=
				nlJoin(argCode,visit(n.arglist.get(i)));//parametri li metto in ordine inverso perchè primo parametro ha offset 1, secondo 2 (?) ma le dichiarazioni le mettiamo nello stesso ordine in cui compaiono del codice 
		for (int i = 0;i<n.nl-n.entry.nl;i++) getAR=nlJoin(getAR,"lw");
		return nlJoin(
				"lfp", // load Control Link (pointer to frame of function "id" caller)
				argCode, // generate code for argument expressions in reversed order
				"lfp", getAR, // retrieve address of frame containing "id" declaration
													//dichiarazione della funzione che chiamo 
	                          // by following the static chain (of Access Links)
	            "stm", // set $tm to popped value (with the aim of duplicating top of stack)
	            "ltm", // load Access Link (pointer to frame of function "id" declaration)
	            "ltm", // duplicate top of stack
	            "push"+n.entry.offset, "add", // compute address of "id" declaration
				"lw", // load address of "id" function
	            "js"  // jump to popped address (saving address of subsequent instruction in $ra)
			);//tutto per capire dove devo saltare
	}
	/*	getAr:
	 *  -    per raggiungere il frame corretto si deve risalire la catena degli AL dopo aver fatto lw 
			punto al frame che mi richiude sintatticamente ed in particolare punto al suo AL.
	 *  -   codice che mi serve per raggiunger l'ar della dichiarazione; 
	 *  	iterando sulla differenza di nesting level ad ogni iterazione aggiungo una lw alla stringa.
	 *  		n.nl =nesting level a cui è fatta la chiamata
	 *  		n.entry.nl= nesting level a cui è dichiarata la funzione
	 */
	@Override
	public String visitNode(IdNode n) {
		if (print) printNode(n,n.id);	
		String getAR = null;
		for (int i = 0; i < n.nl - n.entry.nl; i++) getAR = nlJoin(getAR, "lw");	
		return nlJoin(
				"lfp",	//retrieve address of frame containing "id" declaration
				getAR, // by following the static chain (of Access Links)
				"push" +n.entry.offset,	"add",//compute address of "id" declaration (addr=indirizzo AL + offset)
				"lw" 					/*prende l'indirizzo sulla cima dello stack, lo poppa e mette al suo posto i valore della variabile corrispondente*/
										//load value of "id" variable
				);
	}

	@Override
	public String visitNode(BoolNode n) {
		if (print) printNode(n,n.val.toString());
		return "push "+(n.val?1:0);
	}
	
	//remeber: da rispettare l'invariante lo stack va lasciato come lo si è trovato con il regalo per la mamma in cima
	@Override
	public String visitNode(IntNode n) {
		if (print) printNode(n,n.val.toString());
		return "push "+n.val; //la conversione viene fatta in un automatico (questo è quello che passo alla famosa funzione del punto 4)
	}
}