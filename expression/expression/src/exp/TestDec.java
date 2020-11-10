package exp;
import java.io.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

//SimpleExpLexer
//SimpleExpParser
//CommonTokenStream
//lexicalErrors
//getNumberOfSyntaxErrors

public class TestDec {
    public static void main(String[] args) throws Exception {

        String fileName = "prova.txt";
     
        CharStream chars = CharStreams.fromFileName(fileName);
        ExpDecLexer lexer=new ExpDecLexer(chars); 		/*attacco il lexer alla stringa di carattere*/        
        CommonTokenStream tokens=new CommonTokenStream(lexer);	/*prende il flusso di token*/
        ExpDecParser parser= new ExpDecParser(tokens);								/*passo il flusso di token al parser*/
        ParseTree prog = parser.prog(); 

        System.out.println("You had: "+lexer.lexicalErrors+" lexical errors and "+
                parser.getNumberOfSyntaxErrors()+" syntax errors");

        if (lexer.lexicalErrors+parser.getNumberOfSyntaxErrors() > 0) System.exit(1);           
        
        System.out.println("Calculating expression");
        
        CalcSTVisitor visitor = new CalcSTVisitor();
        System.out.println("The result is: " + visitor.visit(prog));
    }
}
