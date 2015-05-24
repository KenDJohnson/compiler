package scanner;

import java.util.Hashtable;

/**
 * LookupTable used to find the recognized symbols in the language.
 * Extended from hashtable, basically an explicitly defined hashtable
 * @author ken
 */
public class LookupTable extends Hashtable
{
    public LookupTable()
    {
        super();
        this.put("+", Token.PLUS);
        this.put("-", Token.MINUS);
        this.put("*", Token.MULTIPLY);
        this.put("/", Token.DIVIDE);
        this.put("=", Token.EQUALS);
        this.put("(", Token.LEFT_PARENTHESIS);
        this.put(")", Token.RIGHT_PARENTHESIS);
        this.put("[", Token.LEFT_SQUARE_BRACKET);
        this.put("]", Token.RIGHT_SQUARE_BRACKET);
        this.put(":=", Token.ASSIGN);
        this.put("E", Token.EXPONENT);
        this.put(">", Token.GREATER_THAN);
        this.put(">=", Token.GREATER_THAN_EQUAL);
        this.put("<", Token.LESS_THAN);
        this.put("<=", Token.LESS_THAN_EQUAL);
        this.put("<>", Token.LESS_THAN_GREATER_THAN);
        this.put(",", Token.COMMA);
        this.put(".", Token.PERIOD);
        this.put(";", Token.SEMICOLON);      
        this.put(":", Token.COLON);
        this.put("program", Token.PROGRAM);
        this.put("var", Token.VAR);
        this.put("array", Token.ARRAY);
        this.put("if", Token.IF);
        this.put("then", Token.THEN);
        this.put("else", Token.ELSE);
        this.put("do", Token.DO);
        this.put("while", Token.WHILE);
        this.put("procedure", Token.PROCEDURE);
        this.put("function", Token.FUNCTION);
        this.put("real", Token.REAL);
        this.put("integer", Token.INTEGER);
        this.put("begin", Token.BEGIN);
        this.put("end", Token.END);
        this.put("of", Token.OF);
        this.put("not", Token.NOT);
    }
}
