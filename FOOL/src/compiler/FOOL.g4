grammar FOOL;
 
@lexer::members {
public int lexicalErrors=0;
}
   
/*------------------------------------------------------------------
 * PARSER RULES
 *------------------------------------------------------------------*/
  
prog  : progbody EOF ;
     
progbody : LET dec+ IN exp SEMIC  #letInProg	/*let ha almeno una dichiarazione (che può essere:
												* una funzione (vardec), o una variabile (vardec)
												*/	
/*
 *  Dichiarazioni come nel linguaggio funzionale ML (Meta Language)
	-------------
	in C/java
	{
	int X = 5;
	int y = 6;
	
	codice senza dichiarazioni
	}
	------------------
	let
	
	int X = 5;
	int y = 6;
	
	in
	
	codice senza dichiarazioni
	;
	-----------------------
	//ATTENZIONE: posso avere scope annidati
	* //nesting level 0(ambiente globale): nome funzione
	int pippo (int x){  
		//nesting level1: dichiarazioni + chiamate a funzione
		double y	 
		print(x+y)
		//nb : g qui è visto ma è dichiarato un livello sopra
	}
 */						
         | exp SEMIC              #noDecProg
         ;
 
dec : VAR ID COLON type ASS exp SEMIC  #vardec
    | FUN ID COLON type LPAR (ID COLON type (COMMA ID COLON type)* )? RPAR 
        	(LET dec+ IN)? exp SEMIC   #fundec
    ;
     /*
      *ha una ricorsione a sinistra che viene trasformata in ricorsione a dx??
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
	    | ID #id
	    | ID LPAR (exp (COMMA exp)* )? RPAR #call
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

COMMENT : '/*' .*? '*/' -> channel(HIDDEN) ;
 
ERR   	 : . { System.out.println("Invalid char "+getText()+" at line "+getLine()); lexicalErrors++; } -> channel(HIDDEN); 


