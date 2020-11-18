grammar FOOL;

@lexer::members {
int lexicalErrors=0;
}

 
/*------------------------------------------------------------------
 * PARSER RULES
 *------------------------------------------------------------------*/
 
prog    : exp SEMIC EOF ; //ogni istruzione viene terminata da ;
 //considero un'operazione a ogni livello per semplicità ; è simile a simpleexp
 //FOOL: linguaggio che creiamo: Functional Object Oriented Language
exp     : exp TIMES exp                 #highPriOp
        | exp PLUS exp                  #mediumPriOp
        | exp EQ exp                    #lowPriOp       //
        | LPAR exp RPAR                 #pars
    	| MINUS? NUM                    #integer        
	    | TRUE                          #true           //
	    | FALSE                         #false          //
	    //siamo in linguaggio funzionale sta roba ritorna un valore: nei linguaggi funzionali è implicito che ritorni un valore 
	    /*print( if (5+3)*-7 == 9  then { 8 } else { false }); Sta roba torno zero e come sideeffct stampa zero*/
	    | IF exp THEN CLPAR exp CRPAR 
	             ELSE CLPAR exp CRPAR   #if             //
	    | PRINT LPAR exp RPAR           #print          //
        ;  		
  		
/*------------------------------------------------------------------
 * LEXER RULES
 *------------------------------------------------------------------*/

SEMIC	: ';' ;
EQ	    : '==' ;
PLUS	: '+' ;
MINUS   : '-' ;
TIMES	: '*' ;
TRUE	: 'true' ;
FALSE	: 'false' ;
LPAR 	: '(' ;
RPAR	: ')' ;
CLPAR 	: '{' ;
CRPAR	: '}' ;
IF 	    : 'if' ;
THEN 	: 'then' ;
ELSE 	: 'else' ;
PRINT	: 'print' ; 
NUM     : '0' | ('1'..'9')('0'..'9')* ;
 
WHITESP : (' '|'\t'|'\n'|'\r')+ -> channel(HIDDEN) ;
COMMENT : '/*' .*? '*/' -> channel(HIDDEN) ; //non-greedy *

ERR	    : . { System.out.println("Invalid char: "+ getText()); lexicalErrors++; } -> channel(HIDDEN); 
