grammar SVM;

@header {
import java.util.HashMap;
}

@lexer::members {
int lexicalErrors=0;
}

@parser::members {
int[] code = new int[ExecuteVM.CODESIZE];    
private int i = 0; /*non stiamo eseguendo ma trasformando il testo in numeri la i verrà incrementata */
					/* ad ogni token viene associato in maniera automatica da antlr un numero(SVM.tokens) */
private HashMap<String,Integer> labelDef = new HashMap<String,Integer>();
private HashMap<Integer,String> labelRef = new HashMap<Integer,String>();
}


/*------------------------------------------------------------------
 * PARSER RULES
 *------------------------------------------------------------------*/

assembly: instruction* EOF {for(Integer j: labelRef.keySet())
							code[j]=labelDef.get(labelRef.get(j));/*questo è quello che vado a mettere come indirizzo nel buco*/
							}; 

instruction:
        PUSH n=INTEGER{	code[i++] = PUSH;	
        				code[i++] =	Integer.parseInt($n.text);}	
        											/*push INTEGER on the stack;
        											n mi da la possibilità di accedere al lessema che ha matchato con integer
        				  							push è un valore numerico che possiamo mettere dentro un array di interi: 
        											code parte  da 0 va avanti; code è l'array che conterrà la nostra roba
        											NB ho bisogno di due caselle perchè mi serve l'argomento*/
	  | PUSH l=LABEL{code[i++] = PUSH;			
		  			labelRef.put(i++, $l.text);}	/*push the location address pointed by LABEL on the stack;
		  				 							* mettere sullo stack l'indirizzo a cui è associato lab
		  				 							*/	     
	  | POP		{code[i++] = POP;}					//pop the top of the stack ; non ha argomenti
	  | ADD		{code[i++] = ADD;}					//replace the two values on top of the stack with their sum; ha argomenti implici perchè lavora con lo stack
	  | SUB		{code[i++] = SUB;}					//pop the two values v1 and v2 (respectively) and push v2-v1;  ha argomenti implici perchè lavora con lo stack
	  | MULT	{code[i++] = MULT;}					//replace the two values on top of the stack with their product;  ha argomenti implici perchè lavora con lo stack
	  | DIV		{code[i++] = DIV;}					//pop the two values v1 and v2 (respectively) and push v2/v1;  ha argomenti implici perchè lavora con lo stack
	  
	  
	  | STOREW	{code[i++] = STORERW;}				///pop two values: 
	  												//  the second one is written at the memory address pointed by the first one
	  | LOADW  {code[i++] = LOADW;}   				///read the content of the memory cell pointed by the top of the stack
	                								//  and replace the top of the stack with such value
	  
	  /*
	   * COme gestire le label : se abbiamo un jump pippo prima di pippo : lasciamo il buco finchè non incrontiamo pippo : 
	   * Quello che si fa è tenere una lista di buchi (labelRef): quando noi lasciamo un buco relativo a pippo; la label ref si ricorda che in quel punto c'è un buco quindi mappa interi (posizione dove c'è il buco in label: 
	   * LabelRef lista di riferimenti irrisolti di buchi in label a cui dovremmo associare un indirizzo quando le incrontreremo.
	   * LabelDEf: associa stringeh ad intdirizzi: associa indirizzi a pippo;
	   * perchè ci serve? PErchè quando incontro pippo: devo memorizzare il suo indirizzo perchè più aventi potrei trovare una branch con pippo : 
	   * Aspetto di arrivare alla fine per riempire con un ciclo for il labelRef con i labelDef.
	   * Label deve essere associata all'indirizzo dell'istruzione di i  
	  */
	  | l=LABEL COL {labelDef.put($l.text,i);}  //LABEL points at the location of the subsequent instruction; 
	  											//non devo incrementare perchè essendo che postincremento i è già l'indirizzo dell'istruzione successiva
	  | BRANCH l=LABEL {	code[i++] = BRANCH;			
	  						labelRef.put(i++, $l.text);}  //jump at the instruction pointed by LABEL
	  | BRANCHEQ l=LABEL {	code[i++] = BRANCHEQ;			
	  						labelRef.put(i++, $l.text);}    //pop two values and jump if they are equal
	  | BRANCHLESSEQ l=LABEL {	code[i++] = BRANCHLESSEQ;			
	  						labelRef.put(i++, $l.text);} //pop two values and jump if the second one is less or equal to the first one
	  
	  
	  
	  
	  | JS        {code[i++] = POP;}        ///pop one value from the stack:
	  		      							//  copy the instruction pointer in the RA register and jump to the popped value 
	  		       
	  /*in generale ho una load e una store per ogni registro */  
	  | LOADRA      {code[i++] = LOADRA;}	///push in the stack the content of the RA register   
	  | STORERA     {code[i++] = STORERA;}	///pop the top of the stack and copy it in the RA register     
	  | LOADTM      {code[i++] = LOADTM;}	/*	push in the stack the content of the TM register; 
	  											carica il valore da TM e lo mette sullo stack;
	  											ha argomenti implici perchè lavora con lo stack;
	  										*/ 
	  | STORETM     {code[i++] = STORETM;}	/*pop the top of the stack and copy it in the TM register, 
	  											ha argomenti implici perchè lavora con lo stack*/
	  | LOADFP      {code[i++] = LOADFP;}	///push in the stack the content of the FP register   
	  | STOREFP     {code[i++] = STOREFP;}	///pop the top of the stack and copy it in the FP register    
	  | COPYFP      						///copy in the FP register the currest stack pointer    
	  | LOADHP      {code[i++] = LOADHP;}	///push in the stack the content of the HP register    
	  | STOREHP     {code[i++] = STOREHP;}	///pop the top of the stack and copy it in the HP register  
	    
	  | PRINT       {code[i++] = PRINT;}	//visualize the top of the stack without removing it, ha argomenti implici perchè lavora con lo stack
	  | HALT        {code[i++] = HALT;}		//terminate the execution, ha argomenti implici perchè lavora con lo stack
	  ;
 	 
/*------------------------------------------------------------------
 * LEXER RULES
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
