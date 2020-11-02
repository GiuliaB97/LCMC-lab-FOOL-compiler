package exp;
import java.io.*;
import org.antlr.v4.runtime.*;

//SimpleExpLexer
//SimpleExpParser
//CommonTokenStream
//lexicalErrors
//getNumberOfSyntaxErrors

public class TestSimpleDec {
    public static void main(String[] args) throws Exception {

        String fileName = "prova.txt";
     
        CharStream chars = CharStreams.fromFileName(fileName);
        SimpleExpDecLexer lexer=new SimpleExpDecLexer(chars); 		/*attacco il lexer alla stringa di carattere*/        
        CommonTokenStream tokens=new CommonTokenStream(lexer);	/*prende il flusso di token*/
        SimpleExpDecParser parser= new SimpleExpDecParser(tokens);								/*passo il flusso di token al parser*/
        
        parser.prog();/*per lanciare il parsing chiamo il metodo porg(), che rappresenta la variabile iniziale*/	
        
        System.out.println("\n You had: " + 
        					lexer.lexicalErrors + " lexical errors "+
        					parser.getNumberOfSyntaxErrors() + " syntax errors ");/*campo del lexer*/
    }
}
