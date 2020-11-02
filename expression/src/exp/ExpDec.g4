grammar ExpDec; //quando creo un g4 devo mettere la dichiarazione grammar con lo stesso nome del file

/*lo mette nel corpo della classe che crea: diventa un campo*/	
@lexer::members{
int	lexicalErrors=0; 
}

// PARSER RULES: grammatica del linguaggio
prog: exp EOF {System.out.println("Parsing finished!"); };/*prog variabile iniziale, EOF guarda di essere arrivato in fondo alla stringa*/

exp:  exp (TIMES|DIV) exp|  exp (PLUS | MINUS) exp |NUM |LPAR exp RPAR; 
//<assoc=right> : per ridefinire l'associatività che di default sarebbe a sx così prende solo gli alberi che sono associativi a dx sia per il * che per il +

// LEXER RULES: una regex per ogni lessema del linguaggio: sono in ordine di priorità dall'alto verso il basso perchè sono antlr

/* Per il fatto di avere il file g4 ha già generato il compilatore nella directory target: 
 * crea una copia della struttura fdi pkg del src dove c'è il .g4 genera il compilatore
 */
 
PLUS    : '+';
MINUS	: '-';
DIV		: '/';
TIMES   : '*';
LPAR    : '(';
RPAR    : ')';
NUM     : '0'|('1'..'9') ('0'..'9')*;
WHITESP : (' ' | '\t'| '\n'| '\r')+ -> channel(HIDDEN);/*->channel(HIDDEN): non lo passa al parser
														 * In antlr 
														 * | : + nelle nostre regex
														 * + : equivale alla nostra chiusura positiva
														 * . : un qualsiasi carattere
														 */ 
 /* Se primo su salva a quest punto genera il lexer*/
  
ERR: . {System.out.println("Invalid char: " + getText()); lexicalErrors++; }->channel(HIDDEN); /*Essendo a priorità minore matcha 
																							   * qualsiasi cosa non sia stata matchata prima
																							   * gli posso associare un'azione {} in cui inserisco un pezzo di codice java
																							   * getText recupera il lessema che ha matchato
																							   */
COMMENT : '/*' '.*?' '/*'-> channel(HIDDEN);/*'/*' .* '/*': problema usiamo la regola di maximal match: 
 *  									quando matcho un commento rischio di matchare l'intero
 										programma. 
 										Nel caso dei commenti dle C dobbiamo dire al lexer di 
 										NON applicare la regola di maximal match(quella di default)
 										La stella deve scegliere se considerarla un interazione 
 										del punto o la fine del commento.
 										La stella di Kleeene è greedy: applico regola del maximal match
 										antlr mette a disposizione una versione della stella di Kleene non greedy
 										a ogni lessema tenta di uscire: appena ci riesce esce.
 										'*?': stella di Kleene non greedy (disabilita la regola di maximal match*/																				   