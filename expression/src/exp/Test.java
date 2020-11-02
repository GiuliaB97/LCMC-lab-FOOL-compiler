package exp;
import java.io.*;
import org.antlr.v4.runtime.*;

//SimpleExpLexer
//SimpleExpParser
//CommonTokenStream
//lexicalErrors
//getNumberOfSyntaxErrors

public class Test {
    public static void main(String[] args) throws Exception {

        String fileName = "prova.txt";
     
        CharStream chars = CharStreams.fromFileName(fileName);
        SimpleExpLexer lexer=new SimpleExpLexer(chars); 		/*attacco il lexer alla stringa di carattere*/        
        CommonTokenStream tokens=new CommonTokenStream(lexer);	/*prende il flusso di token*/
        SimpleExpParser parser= new SimpleExpParser(tokens);								/*passo il flusso di token al parser*/
        
        parser.prog();/*per lanciare il parsing chiamo il metodo porg(), che rappresenta la variabile iniziale*/	
        
        System.out.println("\nYou had: " + 
        					lexer.lexicalErrors + " lexical errors "+
        					parser.getNumberOfSyntaxErrors() + "syntax errors");/*campo del lexer*/
    }
}
