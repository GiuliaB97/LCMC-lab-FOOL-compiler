package compiler_lab9;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import compiler.FOOLLexer;
import compiler.FOOLParser;
import compiler.lib.*;

public class Test {
	
    public static void main(String[] args) throws Exception {
   			
        String fileName = "prova.fool";

        CharStream chars = CharStreams.fromFileName(fileName);	//prendo lo stream di caratteri dal di input
        FOOLLexer lexer = new FOOLLexer(chars);					//creo+ inizializzo il lexer con lo stream di char
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        FOOLParser parser = new FOOLParser(tokens);
        
        ParseTree st = parser.prog();
        
        System.out.println("You had: "+lexer.lexicalErrors+" lexical errors and "+
                parser.getNumberOfSyntaxErrors()+" syntax errors.");

        System.out.println("Generating AST.");
        
        ASTGenerationSTVisitor visitor = new ASTGenerationSTVisitor(true);//qui vengono generate le entry
        Node ast = visitor.visit(st); //generazione AST 

    	System.out.println("Visualizing AST...");
    	new PrintEASTVisitor().visit(ast);
        
        System.out.println("Enriching AST.");
        
        SymbolTableASTVisitor symtableVisitor = new SymbolTableASTVisitor();
        symtableVisitor.visit(ast);

        System.out.println("You had: "+symtableVisitor.stErrors+" symbol table errors.\n");
        
        System.out.println("Visualizing Enriched AST...");
        new PrintEASTVisitor().visit(ast);    

    }

}
