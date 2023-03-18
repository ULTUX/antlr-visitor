package pl.edu.pwr.lab;

import java.io.IOException;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class Main {

    public static void main(String[] args) throws IOException {
        // create a CharStream that reads from standard input
        CharStream input = CharStreams.fromStream(System.in);

        // create a lexer that feeds off of input CharStream
        calculatorLexer lexer = new calculatorLexer(input);

        // create a buffer of tokens pulled from the lexer
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // create a parser that feeds off the tokens buffer
        calculatorParser parser = new calculatorParser(tokens);

        // start parsing at the equation rule
        ParseTree tree = parser.equation();
        // System.out.println(tree.toStringTree());

        // create a visitor to traverse the parse tree
        CalculatorVisitorImpl visitor = new CalculatorVisitorImpl();
        System.out.println("The answer is: " + visitor.visit(tree));
    }
}