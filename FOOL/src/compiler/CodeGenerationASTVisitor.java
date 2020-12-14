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
 * nell'ambiente globale c'è solo l'ar della funzione
 * visual vm vicino all'istruzione ci sono i numeri di linea non gli indirizzi 
 * stack : a sx
 * heap al centro
 * file fool.asm : a dx
 * la prima dichiarazione + sempre a offset -2 1000=memmsize, 9998
 * prima cosa da fare settare il control link lfp: frame pointer del chiamante
 * avanzo nella creazione dell'ar 
 * aggiungo la dichiarazione dei parametri in ordine inverso(push 5 e 3)ù
 * trovo all'access link, dovrei risalire la catena ma qui è locale quindi dico direttamente che punta all'ar dell'ambiente globale
 * duplico la dichiarazione (mi serve per settare l'access link(9994), e per trovare l'address dove è dichiarata la funzione(9998) (stm ltm ltm)  
 * con lw carico 20, con js salto e metto nel registro ra l'istruzione alla quale devo tornare; 
 * settp fp che deve ora pntare all'ar creato 9994 carico il return address sullo stakc, poi avrei le dichairazionji locali, poi il corpo della funzione, va prendere i due parametri
 * parte da 994 e a offset 1 trova il primo parametro, a offset 2 il secondo 
 */
public class CodeGenerationASTVisitor extends BaseASTVisitor<String, VoidException> {

  CodeGenerationASTVisitor() {}
  CodeGenerationASTVisitor(boolean debug) {super(false,debug);} //enables print for debugging
/*
 * prima di eseguire il corpo del programma deve allocare l'activation record 
 * (seguendo il layout nel file di esempio(devono rispettare gli offset scelti)) perchè
 * il corpo del programma poi userà le variabili che altrimenti non potrebbero essere raggiungibili
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
		String declCode = null, popDecl = null, popParl = null;
		for (Node dec : n.declist) {
			declCode = nlJoin(declCode,visit(dec));
			popDecl = nlJoin(popDecl,"pop");
		}
		for (int i=0;i<n.parlist.size();i++) popParl = nlJoin(popParl,"pop");
		String funl = freshFunLabel();
		//questo è il momento giusto perchè ascolta i primi 10 minuti
		putCode(
			nlJoin(
					//devo finire di allocare l'activation record
				funl+":",
				"cfp", // set $fp to $sp value
				"lra", // load $ra value //il chiamante ha usato js per arrivare a noi, nel js c'è l'istruzione successiva a js alla quale dobbiamo tornare
				//le dichiarazioni delle funzioni allocano ciascuna qualcosa, se è una dichiarazione di var le allochiamo con il valore calcolato dalla loro espressione, se sono funzioni le allochiamo con la push dell'indirizzo
				declCode, // generate code for local declarations (they use the new $fp!!!)
				//parametri li metto in ordine inverso perchè primo parametro ha offset 1, secondo 2 (?) ma le dichiarazioni le mettiamo nello stesso ordine in cui compaiono del codice
				//è fondamentantale settare fp all'inizio perchè dentro declcode, posso avere funzioni con paraemtri e roba locali a un certo punto può fare dei calcoli che usano var appena dichiarate e dove le trova? le trov anell'activation record in cui siamo, lui la x la raggiunge tramite il registro fp, il registro fp deve essere settato in modo che punti all'activation record in cui siamo (?),
				
				
				visit(n.exp), // generate code for function body expression
				
				//salvo il valore del corpo della funzione nel registro tmp
				"stm", // set $tm to popped value (function result)
				
				//inizio a rimuovere l'activation record
				//rimuovo le dichiarazioni
				popDecl, // remove local declarations from stack
				//rimuovo il return address: lo prendo e lo metto in ra così quando devo saltaare lo vado a prendere e per saltare indietro al chiamante
				"sra", // set $ra to popped value
				//rimuovo l'access link, allocato dal chiamante come ultima cosa 
				"pop", // remove Access Link from stack
				//il chiamante aveva allocato gli argomenti che ci aveva passatojk
				popParl, // remove parameters from stack
				//rimuovo il control link con fp, il control link mi serve per ripristinare il frame pointer alla posizione dle chiamante(Come se la chiamata a funzione non fosse avevanuta
				"sfp", // set $fp to popped value (Control Link)
				//recupero il valore della funzione (unica cosa che deve restare sullo stack
				"ltm", // load $tm value (function result)
				"lra", // load $ra value
				//salto al chiamante
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
		if (print) printNode(n,n.id);
		String argCode = null, getAR = null;
		for (int i=n.arglist.size()-1;i>=0;i--) argCode=
				nlJoin(argCode,visit(n.arglist.get(i)));
		//n.nl =nesting level a cui è fatta la chiamata
		//n.entry.nl= nesting level a cui è dichiarata la funzione 
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

	@Override
	public String visitNode(IdNode n) {
		if (print) printNode(n,n.id);	
		String getAR = null;
		for (int i = 0; i < n.nl - n.entry.nl; i++) getAR = nlJoin(getAR, "lw");
		/*	getAr codice che mi serve per raggiunger l'ar della dichiarazione; 
			iterando sulla differenza di nesting level ad ogni iterazione 
			aggiungo una lw alla stringa
		*/
	
		return nlJoin(
				"lfp",						//retrieve address of frame containing "id" declaration
				getAR, 					/*per raggiungere il frame corretto devo risalire la catena 
											degli access link dopo aver fatto lw punto al frame che mi 
											richiude sintatticamente ed in particolare punto al suo 
											access link
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