grammar SimpleExp; //quando creo un g4 devo mettere la dichiarazione grammar con lo stesso nome del file

/*lo mette nel corpo della classe che crea: diventa un campo*/	
@lexer::members{
int	lexicalErrors=0; 
}

// PARSER RULES: grammatica del linguaggio
prog: exp EOF {System.out.println("Parsing finished!"); };/*prog variabile iniziale, EOF guarda di essere arrivato in fondo alla stringa*/

exp: term exp1; 							/*E -> T E'-> diventa : */
exp1: PLUS term exp1| 	/*epsilon*/; 		/* +T E'|epsilon con la notazione EBNF posso liberarmi di E' utilizzzando la stella di Kleene*/


term: value term1;							/* T-> V T'*/ 
term1: TIMES value term1| 	/*epsilon */;	/*epsilon (non metto nulla) */ /* *V T'|epsilon */

value: NUM |LPAR exp RPAR;					
// LEXER RULES: una regex per ogni lessema del linguaggio: sono in ordine di priorit� dall'alto verso il basso perch� sono antlr

/* Per il fatto di avere il file g4 ha gi� generato il compilatore nella directory target: 
 * crea una copia della struttura fdi pkg del src dove c'� il .g4 genera il compilatore
 */
 
PLUS    : '+';
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
  
ERR: . {System.out.println("Invalid char: " + getText()); lexicalErrors++; }->channel(HIDDEN); /*Essendo a priorit� minore matcha 
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
 										La stella di Kleeene � greedy: applico regola del maximal match
 										antlr mette a disposizione una versione della stella di Kleene non greedy
 										a ogni lessema tenta di uscire: appena ci riesce esce.
 										'*?': stella di Kleene non greedy (disabilita la regola di maximal match*/																				   