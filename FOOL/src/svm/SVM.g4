grammar SVM;
/*
 * When you use ANTLR you start by writing a grammar, 
 * a file with extension .g4 which contains the rules of the language that you are analyzing.
 *  You then use the antlr4 program to generate the files that your program will actually use, 
 * such as the lexer and the parser.

 
 */
@header {
import java.util.HashMap;
}

@lexer::members {
public int lexicalErrors=0;
}

@parser::members {
public int[] code = new int[ExecuteVM.CODESIZE];  /* code � un array di interi che conterr� il codice oggetto
 											* creato vuoto; con sta costante,
 											* idea tutte le volte che vedo un'istruzione vado a 
 											* popolare l'array code''
 											* ed incremento la i
 											* occhio: non stiamo eseguendo ma stiamo 
 											* trasformando il resto in numeri
 											*  */  
private int i = 0; /*non stiamo eseguendo ma trasformando il testo in numeri la i verr� incrementata */
					/* ad ogni token viene associato in maniera automatica da antlr 
					 * un numero(SVM.tokens)
					 */
private HashMap<String,Integer> labelDef = new HashMap<String,Integer>();	/* chiave id numerico
																			 * le chiavi push pop 
																			 */ 
private HashMap<Integer,String> labelRef = new HashMap<Integer,String>();	/* id numerico chiave
																			 * un numero per ogni token
																			 */
}


/*------------------------------------------------------------------
 * PARSER RULES
 *------------------------------------------------------------------*/
//assembly � il nostro S grande (variabile iniziale)
//instruction *EOF: insieme di istruzioni $
//S -> insieme di istruzioni $
assembly: instruction* EOF {for(Integer j: labelRef.keySet()) /*
 * recuperare il token che a chiave j
 */
							code[j]=labelDef.get(labelRef.get(j));/*
							 * dato l'id numerico recupero il simbolo ad esso asssociato e lo salvo nell'array contennte tutto il programma 
							 * questo � quello che vado a 
																* mettere come indirizzo nel buco*/
							}; 

instruction:
/*
 * la push mette sullo stack l'indirizzo della label
 * il token si chaima branch ma il lessema si chiama b
 * 
 * branch label: deve metter l'indirizzo effettivo: devon andare a capire quella lael a che indirizzo � 
 * 
 * COme gestire le label? se abbiamo lla branch a pippo e pippo due punti non � ancora stato raggiunto che facciio?
 * lascio il buco che riempiremo una volta trovato il pippo :
 * devo salavarmi da qualche parte che l� c'� un buco
 * 
 * tengo una lista di buchi 
 * 
 * labelref: lista di riferimneti irrisolti: li si ricorda che c'� un buco: mappa interi: posizione del buco in stringhe ossia la label (mapping indirizzo -etichetta)
 * 
 * 
 * labeldef: viene popolato qunado incontro label : associa stringhe a indirizzi: una volta incontrata la label si salva a che indirizzo � ; perch� mi serve anche questo perch� pi� avanti ci p�potrebbe essere una nuova branch a pippo 
 * aspetto di arrivare alla fine prima di riempire (utilizzer� un ciclo for per riempire tutti i buchi alla fine  
 *  1 ora e 24
 * 
 */
	PUSH n=INTEGER{	code[i++] = PUSH;	
        				code[i++] =	Integer.parseInt($n.text);}	/* Prendo il lessema che ha matchato con integer ossia il numero; 
    				 											* il lexer passa al parser non solo il nome del token ma come 
    				 											* parametro anche il lessama
    				 											* viene dato il numero che ha matchato in base alla regex degli integer
    				 											*/
			        											/*push INTEGER on the stack;
			        											n mi da la possibilit� di accedere al lessema che ha matchato con integer
			        				  							push � un valore numerico che possiamo mettere dentro un array di interi: 
			        											code parte  da 0 va avanti; code � l'array che conterr� la nostra roba
			        											NB ho bisogno di due caselle perch� mi serve l'argomento*/
	| PUSH l=LABEL{code[i++] = PUSH;			
		  			labelRef.put(i++, $l.text);}	/*push the location address pointed by 
													LABEL on the stack;
		  				 							* mettere sullo stack l'indirizzo a cui
		  				 							 � associato lab
		  				 							*/	     
	| POP		{code[i++] = POP;}					//pop the top of the stack ; non ha argomenti
	| ADD		{code[i++] = ADD;}					//replace the two values on top of the stack with their sum; ha argomenti implici perch� lavora con lo stack
	| SUB		{code[i++] = SUB;}					//pop the two values v1 and v2 (respectively) and push v2-v1;  ha argomenti implici perch� lavora con lo stack
	| MULT	{code[i++] = MULT;}					//replace the two values on top of the stack with their product;  ha argomenti implici perch� lavora con lo stack
	| DIV		{code[i++] = DIV;}					//pop the two values v1 and v2 (respectively) and push v2/v1;  ha argomenti implici perch� lavora con lo stack
	  
	  

	| STOREW	{code[i++] = STOREW;}				///pop two values: 
	  												//  the second one is written at the memory address pointed by the first one
	| LOADW  {code[i++] = LOADW;}   	/// read the content of the memory cell pointed by the top of the stack
	                					//  and replace the top of the stack with such value
	  
	  /*
	   * COme gestire le label : se abbiamo un jump pippo prima di pippo : lasciamo il buco finch� non incrontiamo pippo : 
	   * Quello che si fa � tenere una lista di buchi (labelRef): quando noi lasciamo un buco relativo a pippo; la label ref si ricorda che in quel punto c'� un buco quindi mappa interi (posizione dove c'� il buco in label: 
	   * LabelRef lista di riferimenti irrisolti di buchi in label a cui dovremmo associare un indirizzo quando le incrontreremo.
	   * LabelDEf: associa stringeh ad intdirizzi: associa indirizzi a pippo;
	   * perch� ci serve? PErch� quando incontro pippo: devo memorizzare il suo indirizzo perch� pi� aventi potrei trovare una branch con pippo : 
	   * Aspetto di arrivare alla fine per riempire con un ciclo for il labelRef con i labelDef.
	   * Label deve essere associata all'indirizzo dell'istruzione di i  
	  */
	| l=LABEL COL {labelDef.put($l.text,i);}  //LABEL points at the location of the subsequent instruction; 
	  											//non devo incrementare perch� essendo che postincremento i � gi� l'indirizzo dell'istruzione successiva
	| BRANCH l=LABEL {	code[i++] = BRANCH;			
	  						labelRef.put(i++, $l.text);}  //il nome della label che gli diamo (label1, label2)
	  						//jump at the instruction pointed by LABEL
	| BRANCHEQ l=LABEL {	code[i++] = BRANCHEQ;			
	  						labelRef.put(i++, $l.text);}    //pop two values and jump if they are equal
	| BRANCHLESSEQ l=LABEL {	code[i++] = BRANCHLESSEQ;			
	  						labelRef.put(i++, $l.text);} //pop two values and jump if the second one is less or equal to the first one
	  
	  
	  
	  
	| JS        {code[i++] = JS;}        ///pop one value from the stack:
	  		      							//  copy the instruction pointer in the RA register and jump to the popped value 
	  		       
	  /*in generale ho una load e una store per ogni registro */  
	| LOADRA      {code[i++] = LOADRA;}	///push in the stack the content of the RA register   
	| STORERA     {code[i++] = STORERA;}	///pop the top of the stack and copy it in the RA register     
	| LOADTM      {code[i++] = LOADTM;}	/*	push in the stack the content of the TM register; 
	  											carica il valore da TM e lo mette sullo stack;
	  											ha argomenti implici perch� lavora con lo stack;
	  										*/ 
	| STORETM     {code[i++] = STORETM;}	/*pop the top of the stack and copy it in the TM register, 
	  											ha argomenti implici perch� lavora con lo stack*/
	| LOADFP      {code[i++] = LOADFP;}	///push in the stack the content of the FP register   
	| STOREFP     {code[i++] = STOREFP;}	///pop the top of the stack and copy it in the FP register    
	| COPYFP      {code[i++] = COPYFP;}			///copy in the FP register the current stack pointer    
	| LOADHP      {code[i++] = LOADHP;}	///push in the stack the content of the HP register    
	| STOREHP     {code[i++] = STOREHP;}	///pop the top of the stack and copy it in the HP register  
	    
	| PRINT       {code[i++] = PRINT;}	//visualize the top of the stack without removing it, ha argomenti implici perch� lavora con lo stack
	| HALT        {code[i++] = HALT;}		//terminate the execution, ha argomenti implici perch� lavora con lo stack
	;
 	 
/*------------------------------------------------------------------
 * LEXER RULES
 * n token per ogni istruzione
 *------------------------------------------------------------------*/
//nomi token  nomi ??
PUSH		: 'push' ; 	
POP	 		: 'pop' ; 	
ADD	 		: 'add' ;  	
SUB	 		: 'sub' ;	
MULT	 	: 'mult' ;  	
DIV	 		: 'div' ;	
STOREW	 	: 'sw' ; 	
LOADW	 	: 'lw' ;	
BRANCH	 	: 'b' ;	
BRANCHEQ 	: 'beq' ;	
BRANCHLESSEQ: 'bleq' ;	
JS	 		: 'js' ;	
LOADRA	 	: 'lra' ;	
STORERA  	: 'sra' ;	 
LOADTM	 	: 'ltm' ;	
STORETM  	: 'stm' ;	
LOADFP	 	: 'lfp' ;	
STOREFP	 	: 'sfp' ;	
COPYFP   	: 'cfp' ;      
LOADHP	 	: 'lhp' ;	
STOREHP	 	: 'shp' ;	
PRINT	 	: 'print' ;	
HALT	 	: 'halt' ;	

COL	 		: ':' ;
LABEL	 	: ('a'..'z'|'A'..'Z')('a'..'z' | 'A'..'Z' | '0'..'9')* ;
INTEGER	 	: '0' | ('-')?(('1'..'9')('0'..'9')*) ;

WHITESP  	: (' '|'\t'|'\n'|'\r')+ -> channel(HIDDEN) ;

ERR	     	: . { System.out.println("Invalid char: "+ getText()); lexicalErrors++; } -> channel(HIDDEN); 
