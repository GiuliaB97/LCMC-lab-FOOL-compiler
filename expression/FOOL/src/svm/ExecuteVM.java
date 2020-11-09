package svm;

import org.antlr.v4.runtime.*;

public class ExecuteVM {
    /*
     * Instruction pointer: ci sta l'indirizzo dell'istruzione da eseguire; 
     * 						è un po' come l' 'i' di prima ma sta volta eseguiamo;
     * 						lui punta all'istruzione successiva e salto viene associato all'struzione a cui salto.
     * 
     * sp: Stack pointer punta alla cima dello stack;
     *
     */
    public static final int CODESIZE = 10000;
    public static final int MEMSIZE = 10000;
    
    private int[] code;
    private int[] memory = new int[MEMSIZE];
    
    private int ip = 0;
    private int sp = MEMSIZE; //punta al top dello stack
    private int tm; //istanzio il registro tm; per evidenziare il fatto che tm non ha un valore iniziale lo lascio vuoto anche se in java verrà automaticamente inizialiazzato a zero
    
    
    public ExecuteVM(int[] code) {
      this.code = code;			// il code viene passato dall'assemblatore tramite costruttore
    }
    
    public void cpu() {			/*questo metodo lancia la cpu: solito ciclo fetch-execute
    							fetch prende la prossima istruzione da eseguire(prende il bytecode); post incremento ip; 
    							a meno di salti la cosa normale è passare all'istruzione successiva
    							switch implementare l'istruzione a seconda del case*/
    							
      while ( true ) {
        int bytecode = code[ip++]; // fetch
        int v1,v2;
        int address;
        switch ( bytecode ) {
          case SVMParser.PUSH:
        	  push(code[ip++]);	//perchè ip era già stato post-incrementato
        	  break;
          case SVMParser.POP:
        	  pop();			//la funzione tornerebbe il valore ma a me non interessa quindi lo ignoro buttadolo via.
        	  break;
          case SVMParser.ADD:	/*ADD fa due pop sullo stack e poi fa la loro somma*/
        	  v1=pop();
        	  v2=pop();
        	  push(v2+v1);
        	  break;
          case SVMParser.SUB:	/*SUB fa due pop sullo stack e poi fa la loro differenza*/
        	  v1=pop();
        	  v2=pop();
        	  push(v2-v1);		//ATTENZIONE all'ordine!
        	  break;
          case SVMParser.MULT:	/*MULT fa due pop sullo stack e poi fa il loro prodotto*/
        	  v1=pop();
        	  v2=pop();
        	  push(v2*v1);
        	  break;
          case SVMParser.DIV:	/*DIV fa due pop sullo stack e poi fa il loro quoziente*/
        	  v1=pop();
        	  v2=pop();
        	  push(v2+v1);
        	  break;
          case SVMParser.BRANCH:	/*BRANCH deve fare un salto incondizionato: 
          								deve prendere il numero dell'argomento e saltare a quell'indirizzo*/
        	  address=code[ip];
        	  ip=address;
        	  break;  
          case SVMParser.BRANCHEQ:	/*BRANCHEQ 
          								idea non salto sempre se i due valori che prendo dallo stack sono uguali salto 
          								se no ciccia e vado avanti
           							*/
        	  v1=pop();
        	  v2=pop();
        	  if(v2==v1) {
        		  address=code[ip];
        		  ip=address;
        	  }
        	  break; 
          case SVMParser.BRANCHLESSEQ:	/*BRANCHLESSEQ
											idea non salto sempre se i due valori che prendo dallo stack sono il secondo minore del primo 
											se no ciccia e vado avanti
										*/
				v1=pop();
				v2=pop();
				if(v2<=v1) {
				address=code[ip];
				ip=address;
				}
				break;
          case SVMParser.LOADTM:
        	  push(tm); 
        	  break;
          case SVMParser.STORETM:
        	  pop(); 
        	  break;
          case SVMParser.PRINT:
        	  System.out.println((sp<MEMSIZE)?memory[sp]: "Empty stack!");/*memory di sp mi da il valore in cima allo stack senza toccarlo(non faccio né push né pop)
        	  									problema: se lo stack è vuoto?
        	  											sp=memsize ->Errpr: out of bound exception -aaaaaaaaa
        	  									risolvo con un bel ? :
        	  									*/
        	  break;
          case SVMParser.HALT:
        	  return;
        }
      }
    } 
    
    private int pop() {
      return memory[sp++];
    }
    
    private void push(int v) {
      memory[--sp] = v; /*
      						se lo stack cresce verso il basso; 
      						se voglio pushare qualcosa prima decremento poi carico il valore (inizialmente punta a memsize); 
      						al primo caricaamneto punterà a memsize-1)
       					*/
    }
    
}