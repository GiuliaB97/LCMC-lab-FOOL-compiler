package svm;

import org.antlr.v4.runtime.*;

public class ExecuteVM {
   
    public static final int CODESIZE = 10000;
    public static final int MEMSIZE = 10000;
    
    private int[] code;
    private int[] memory = new int[MEMSIZE]; // è lo stack
    
    private int ip = 0; /*	Instruction pointer: ci sta l'indirizzo dell'istruzione da eseguire; 
     					* 	è un po' come l' 'i' di prima ma sta volta eseguiamo;
     					* 	lui punta all'istruzione successiva e salto viene associato all'struzione a cui salto.*/
    private int sp = MEMSIZE; //sp: Stack pointer punta alla cima dello stack;
    private int tm; 		//istanzio il registro tm; per evidenziare il fatto che tm non ha un valore iniziale lo lascio vuoto anche se in java verrà automaticamente inizialiazzato a zero
    private int hp;
    private int fp =MEMSIZE;
    private int ra;
    
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
          case SVMParser.PUSH:  //push INTEGER on the stack
        	  push(code[ip++]);	//perchè ip era già stato post-incrementato
        	  break;
          case SVMParser.POP:	//pop the top of the stack 
        	  pop();			//la funzione tornerebbe il valore ma a me non interessa quindi lo ignoro buttadolo via.
        	  break;
          case SVMParser.ADD:	//replace the two values on top of the stack with their sum	
        	  					/*ADD fa due pop sullo stack e poi fa la loro somma*/
        	  v1=pop();
        	  v2=pop();
        	  push(v2+v1);
        	  break;
          case SVMParser.SUB:	//pop the two values v1 and v2 (respectively) and push v2-v1
	  							/*SUB fa due pop sullo stack e poi fa la loro differenza*/
        	  v1=pop();
        	  v2=pop();
        	  push(v2-v1);		//ATTENZIONE all'ordine!
        	  break;
          case SVMParser.MULT:	//replace the two values on top of the stack with their product	
        	  					/*MULT fa due pop sullo stack e poi fa il loro prodotto*/
        	  v1=pop();
        	  v2=pop();
        	  push(v2*v1);
        	  break;
          case SVMParser.DIV:	//pop the two values v1 and v2 (respectively) and push v2/v1	
        	  					/*DIV fa due pop sullo stack e poi fa il loro quoziente*/
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
        	  address = code[ip++];
              v1=pop();
              v2=pop();
              if (v2 <= v1) ip = address;
              break;

          case SVMParser.STOREW : //
            address = pop();
            memory[address] = pop();    
            break;
          case SVMParser.LOADW : // pop restituisce un numero accede all'indice dell'array memory c'è un valore
        	  					// la load prende il valore in memory e lo mette in cima allo stack
            push(memory[pop()]);
            break;
          case SVMParser.JS : //
              address = pop();
              ra = ip;
              ip = address;
              break;
           case SVMParser.STORERA : //
              ra=pop();
              break;
           case SVMParser.LOADRA : //
              push(ra);
              break;
           case SVMParser.STORETM : 
              tm=pop();
              break;
           case SVMParser.LOADTM : 
              push(tm);
              break;
           case SVMParser.LOADFP : //
              push(fp);
              break;
           case SVMParser.STOREFP : //
              fp=pop();
              break;
           case SVMParser.COPYFP : //
              fp=sp;
              break;
           case SVMParser.STOREHP : //
              hp=pop();
              break;
           case SVMParser.LOADHP : //
              push(hp);
              break;
        	  
        	  
          case SVMParser.PRINT:  //visualize the top of the stack without removing it 
        	  System.out.println((sp<MEMSIZE)?memory[sp]: "Empty stack!");/*memory di sp mi da il valore in cima allo stack senza toccarlo(non faccio né push né pop)
        	  									problema: se lo stack è vuoto?
        	  											sp=memsize ->Errpr: out of bound exception -aaaaaaaaa
        	  									risolvo con un bel ? :
        	  									*/
        	  break;
          case SVMParser.HALT: //terminate the execution
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