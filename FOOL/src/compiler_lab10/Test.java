package compiler_lab10;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import compiler.FOOLLexer;
import compiler.FOOLParser;
import compiler.exc.IncomplException;
import compiler.exc.TypeException;
import compiler.lib.*;
/*
 * Reminder: se voglio la stampa del syntax tree devo passare true al costruttore
 * perchè è una funzione di debug
 * 
 * Ora quando chiamo la funzione:
 * 	per ogni uso di identificatore e chiamata di funzione ho un altro info : il tipo che vviene visualizzato come figlio
 * 
 * 
 * TO DO SCRIVI NELLE NOTE SPIEGAZIONE DELLA STAMPA: FOTO FOOL FOTO STAMPA ENRICHED
 * ArrowTipe 
 * tipo par 1 e 2 ...
 * e tipo di ritorno
 * info prese dalla dichiaraione della var
 * If			
      Call: f
 *  STentry: nestlev 0
        STentry: type
          ArrowType
            IntType
            IntType
            ->BoolType
 */
public class Test {
    public static void main(String[] args) throws Exception {
   			
    	String fileName = "prova.fool";

    	CharStream chars = CharStreams.fromFileName(fileName);
    	FOOLLexer lexer = new FOOLLexer(chars);
    	CommonTokenStream tokens = new CommonTokenStream(lexer);
    	FOOLParser parser = new FOOLParser(tokens);

    	System.out.println("Generating ST via lexer and parser.");
    	ParseTree st = parser.prog();
    	System.out.println("You had "+lexer.lexicalErrors+" lexical errors and "+
    		parser.getNumberOfSyntaxErrors()+" syntax errors.\n");

    	System.out.println("Generating AST.");
    	ASTGenerationSTVisitor visitor = new ASTGenerationSTVisitor(); // use true to visualize the ST
    	Node ast = visitor.visit(st);
    	System.out.println("");

    	System.out.println("Enriching AST via symbol table.");
    	SymbolTableASTVisitor symtableVisitor = new SymbolTableASTVisitor();
    	symtableVisitor.visit(ast);
    	System.out.println("You had "+symtableVisitor.stErrors+" symbol table errors.\n");

    	System.out.println("Visualizing Enriched AST.");
    	new PrintEASTVisitor().visit(ast);
    	System.out.println("");

    	System.out.println("Checking Types.");
    	//se l'errore ce l'ho sul main program body devo catturrarlo dla di qui
    	try {
    		TypeCheckASTVisitor typeCheckVisitor = new TypeCheckASTVisitor();
    		TypeNode mainType = typeCheckVisitor.visit(ast);
    		System.out.print("Type of main program expression is: ");
    		new PrintEASTVisitor().visit(mainType);
    	} catch (TypeException e) {
    		System.out.println("Type checking error in main program expression: "+e.text);   
    	} catch (IncomplException e) {    		
    		System.out.println("Could not determine main program expression type due to errors detected before type checking.");
    	}
    	
    	//SE ARRIVO QUI PER IL MOMENTO STAMPO SEMPRE 0 perchè se ci fossero eccezioni il programma si ssarebbe interrotto
    	System.out.println("You had "+FOOLlib.typeErrors+" type checking errors.\n");

    	int frontEndErrors = lexer.lexicalErrors+parser.getNumberOfSyntaxErrors()+symtableVisitor.stErrors+FOOLlib.typeErrors;
    	System.out.println("You had a total of "+frontEndErrors+" front-end errors.\n");
    	//fine fase di front end
    }
}