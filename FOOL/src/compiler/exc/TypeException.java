package compiler.exc;

import compiler.lib.*;
/*
 * chi è che sa che cos'è che causa l'eccezione?
 * chi lancia l'eccezione
 * 
 * da dove tiro fuori il  numero di linea ?
 * dal nodo node-> se non c'è sei stupida  e non glielo hai messo!
 * 
 * la classe conta anche il numero di errori dii tipo
 * 
 *  la stringa che viene passata viene passata insieme al numero di linea nel campo text
 *  
 *  chi lo stampa?
 *  chi cattura l'ecceziione
 *  
 *  Necessario: dotare i nodi del numero di linea (che sta nella classe abstract node
 *  ma chi è ch elo mette ? astgeneration: in alcuni li abbiamo messi nello scorso lab 
 *  negli altri lo abbiamo messo in questi (sono nodi che nella symbol table non potevano dare errori ma ora sì
 */
public class TypeException extends Exception {

	private static final long serialVersionUID = 1L;

	public String text;

	public TypeException(String t, int line) {
		FOOLlib.typeErrors++;
		text = t + " at line "+ line;
	}

}
