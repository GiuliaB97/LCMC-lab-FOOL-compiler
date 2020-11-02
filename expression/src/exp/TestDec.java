package exp;
import java.io.*;
import org.antlr.v4.runtime.*;

//SimpleExpLexer
//SimpleExpParser
//CommonTokenStream
//lexicalErrors
//getNumberOfSyntaxErrors

public class TestDec {
    public static void main(String[] args) throws Exception {

        String fileName = "provaDec.txt";
     
        CharStream chars = CharStreams.fromFileName(fileName);
        ExpDecLexer lexer=new ExpDecLexer(chars); 		/*attacco il lexer alla stringa di carattere*/        
        CommonTokenStream tokens=new CommonTokenStream(lexer);	/*prende il flusso di token*/
        ExpDecParser parser= new ExpDecParser(tokens);								/*passo il flusso di token al parser*/
        
        parser.prog();/*per lanciare il parsing chiamo il metodo porg(), che rappresenta la variabile iniziale*/	
        
        System.out.println("\n You had: " + 
        					lexer.lexicalErrors + " lexical errors "+
        					parser.getNumberOfSyntaxErrors() + " syntax errors ");/*campo del lexer*/
    }
}
