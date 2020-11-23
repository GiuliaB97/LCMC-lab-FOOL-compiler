grammar FOOL;
 
@lexer::members {
public int lexicalErrors=0;
}
   
/*------------------------------------------------------------------
 * PARSER RULES
 *------------------------------------------------------------------*/
  
   /*
    * ricorda le variabile puoi assegnarle solo nel momento in cui vengono dichiarate
    */
prog  : progbody EOF ;
     	
progbody : LET dec+ IN exp SEMIC  #letInProg 	/*let dichiarazioni locali in codice senza dichiarazioni
												* LET dec+: ho almeno una dichairazion eper via della chiusura positiva
												* se non ho dichiarazioni LET IN lo ometto completamente: scrivo direttamente il corpo della funzione
												*/
         | exp SEMIC              #noDecProg	/*ho un programma senza dichiarazioni*/
         ;

dec : VAR ID COLON type ASS exp SEMIC  #vardec									//dichiarazioni di variabili 	NB var è un token nuovo
    
    						//varName : int
    | FUN ID COLON type LPAR (ID COLON type (COMMA ID COLON type)* )? RPAR 		//dichiarazione di funzioni		NB fun è un token nuovo   																			
        	(LET dec+ IN)? exp SEMIC   #fundec
    ;																			//all'interno di fun: ho un nuovo scope
          																		/*
          																		 * //ATTENZIONE: posso avere scope annidati
																					int pippo (int x){  //nome funzione è a nesting level0: ambiente globale: questo si trova a un livello superiore; 
																					 						può essere utilizzato all'interno ma bisogna risalire di un livello
																						double y		//tutta sta roba è a nesting level 1: del corpo della funzione : sono cose dichiarate localmente 
																						print(x+y)
																					}
          																		 */
          																		 
exp     : exp TIMES exp #times
        | exp PLUS  exp #plus
        | exp EQ  exp   #eq 
        | LPAR exp RPAR #pars
    	| MINUS? NUM #integer
	    | TRUE #true     
	    | FALSE #false
	    | IF exp THEN CLPAR exp CRPAR ELSE CLPAR exp CRPAR  #if   
	    | PRINT LPAR exp RPAR #print
	    | ID #id									//se id compare in una expr allora è una variabile
	    | ID LPAR (exp (COMMA exp)* )? RPAR #call	//se id compare seguito da una parentesi allora ho una funzione. Sta roba complicata (exp (COMMA exp)* )?  serve per gestire i casi in cui ci siano o meno argomenti passati alla funzione
        ; 
             
type    : INT #intType
        | BOOL #boolType
 	    ;  
 	  		  
/*------------------------------------------------------------------
 * LEXER RULES
 *------------------------------------------------------------------*/

PLUS  	: '+' ;
MINUS	: '-' ; 
TIMES   : '*' ;
LPAR	: '(' ;
RPAR	: ')' ;
CLPAR	: '{' ;
CRPAR	: '}' ;
SEMIC 	: ';' ;
COLON   : ':' ; 
COMMA	: ',' ;
EQ	    : '==' ;	
ASS	    : '=' ;
TRUE	: 'true' ;
FALSE	: 'false' ;
IF	    : 'if' ;
THEN	: 'then';
ELSE	: 'else' ;
PRINT	: 'print' ;
LET     : 'let' ;	
IN      : 'in' ;	
VAR     : 'var' ;
FUN	    : 'fun' ;	  
INT	    : 'int' ;
BOOL	: 'bool' ;
NUM     : '0' | ('1'..'9')('0'..'9')* ; 

ID  	: ('a'..'z'|'A'..'Z')('a'..'z' | 'A'..'Z' | '0'..'9')* ;


WHITESP  : ( '\t' | ' ' | '\r' | '\n' )+    -> channel(HIDDEN) ;

COMMENT : '/*' (.)*? '*/' -> channel(HIDDEN) ;
 
 
ERR   	 : . { System.out.println("Invalid char: "+ getText() +"at line" + getLine()); lexicalErrors++; } -> channel(HIDDEN); 


