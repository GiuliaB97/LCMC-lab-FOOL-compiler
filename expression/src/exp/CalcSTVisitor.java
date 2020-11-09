package exp;

import org.antlr.v4.runtime.tree.*;
import exp.ExpDecParser.*;

//voglio fare un visitor dve la visita di ogni nodo torna un intero
//prog come unico figlio a exp: di che tipo è exp? Dipende da che produzione applico
public class CalcSTVisitor extends ExpDecBaseVisitor<Integer> {
	String indent;
  @Override
	public Integer visit(ParseTree x) {
	  String temp=indent;
	  indent =(indent==null)?"": indent+" ";
	  int res=super.visit(x);
	  indent=temp;
	  return res;
	}
	
	@Override
	public Integer visitProg(ProgContext ctx) {//quando definisco un metodo di visita qui devo definire la classe del nodo che visito
		
		System.out.println(indent + "prog");//essite un oggetto dell'albero di classe effwettiva prog context e questo ha un unica produzione exp
		return visit( ctx.exp() ); /*chiamo visit sul mio figlio exp; 
									visit metodo che mi permette di visitare 
									i suoi figli (sono dei sottoalberi); 
									avendo un'unica variabile mi basta scrivere exp
									*/
		//non esiste una ExpContext dovrò calcolare i 4 casi
		//quando chiamo visit lui fa un indagine sul tipo effettivo e chiama il metodo giusto (guardando di che var è il context che gli passo).
	}
	

	public Integer visitExpProd1(ExpProd1Context ctx) { //PRODUZIONE 1: PRODOTTO
		boolean times=ctx.TIMES()!=null;
		System.out.println(indent+"exp: prod1 with "+ (times?"TIMES": "DIV"));
		if (times)
			return visit( ctx.exp(0)) * visit( ctx.exp(0) ); //in questo caso ho due figli exp: posso distinguerli indicando un paramentro 
															// 0: primo, 1: secondo
		else //ctx.DIV !=null
			return visit( ctx.exp(0)) * visit( ctx.exp(0) );
	}
	public Integer visitExpProd2(ExpProd2Context ctx) { //PRODUZIONE 2: SOMMA
		boolean plus=ctx.PLUS()!=null;
		System.out.println(indent+"exp: prod1 with "+ (plus?"PLUS": "MINUS"));
		if (plus)
			return visit( ctx.exp(0)) * visit( ctx.exp(0) ); //in questo caso ho due figli exp: posso distinguerli indicando un paramentro 
															// 0: primo, 1: secondo
		else //ctx.MINUS() !=null
			return visit( ctx.exp(0)) * visit( ctx.exp(0) );
	}
	
	public Integer visitExpProd3(ExpProd3Context ctx) { //PRODUZIONE 2: SOMMA
		System.out.println(indent+"exp: prod2 with LPAR RPAR");
		return visit( ctx.exp()); //in questo caso ho due figli exp: posso distinguerli indicando un paramentro 
														// 0: primo, 1: secondo
	}
	
	public Integer visitExpProd4(ExpProd4Context ctx) {
		
		int v=Integer.parseInt(ctx.NUM().getText());//recupero il lessema che è un numero
		boolean minus=ctx.MINUS()!=null; //capisco se c'è il meno
		int res=minus? -v: v;
		System.out.println(indent+"exp: prod4 with "+ (minus?"MINUS ": "")+"NUM "+ res);
		return (Integer) res; //in questo caso ho due figli exp: posso distinguerli indicando un paramentro 
														// 0: primo, 1: secondo
	}
}
