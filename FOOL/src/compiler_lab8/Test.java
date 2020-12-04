package compiler_lab8;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import compiler.FOOLLexer;
import compiler.FOOLParser;
import compiler.lib.*;

public class Test {
    public static void main(String[] args) throws Exception {
    			
        String fileName = "prova.fool";

        CharStream chars = CharStreams.fromFileName(fileName);
        FOOLLexer lexer = new FOOLLexer(chars);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        FOOLParser parser = new FOOLParser(tokens);
                
        ParseTree st = parser.prog();

        System.out.println("You had: "+lexer.lexicalErrors+" lexical errors and "+
                parser.getNumberOfSyntaxErrors()+" syntax errors");

        if (lexer.lexicalErrors+parser.getNumberOfSyntaxErrors() > 0) System.exit(1);   
        
        System.out.println("Generating AST");
        
        ASTGenerationSTVisitor astGenVisitor = new ASTGenerationSTVisitor();		//tutta sta sbatta è perchè io non voglio più l'ST ma l'ABS: 
        																			// quindi voglio una versione compatta dove decido cosa ignorare.
        Node ast = astGenVisitor.visit(st);											//per generarlo devo visitare il parse tree
        
        System.out.println("Visualizing AST...");
        
    	new PrintASTVisitor().visit(ast);											//boisito il tree

        System.out.println("Calculating program value...");        
        System.out.println("Program value is: "+new CalcASTVisitor().visit(ast));
            
    }
}

