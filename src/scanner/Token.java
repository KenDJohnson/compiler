package scanner;

/**
 *
 * @author ken
 * An enum for the actual tokens (returned from getToken())
 * Possbile values: PLUS, MINUS, MULTIPLY, DIVIDE, ASSIGN, LEFT_PARENTHESIS, 
 * RIGHT_PARENTHESIS, LEFT_SQUARE_BRACKET, RIGHT_SQUARE_BRACKET, EXPONENT, 
 * GREATER_THAN, LESS_THAN, GREATER_THAN_EQUAL, LESS_THAN_EQUAL, 
 * LESS_THAN_GREATER_THAN, EQUALS, COMMA, PERIOD, SEMICOLON, ID, NUMBER, 
 * COMMENT, COLON, PROGRAM, ARRAY, VAR, INTEGER, REAL, FUNCTION, PROCEDURE, 
 * BEGIN, END, IF, THEN, ELSE, WHILE, DO, OF, NOT
 */
public enum Token
{
    PLUS, MINUS, MULTIPLY, DIVIDE, ASSIGN, LEFT_PARENTHESIS, RIGHT_PARENTHESIS,
    LEFT_SQUARE_BRACKET, RIGHT_SQUARE_BRACKET, EXPONENT, GREATER_THAN,
    LESS_THAN, GREATER_THAN_EQUAL, LESS_THAN_EQUAL, LESS_THAN_GREATER_THAN, 
    EQUALS, COMMA, PERIOD, SEMICOLON, ID, NUMBER, COMMENT, COLON,PROGRAM, ARRAY, 
    VAR, INTEGER, REAL, FUNCTION, PROCEDURE, BEGIN, END, IF,THEN, ELSE, WHILE,
    DO, OF, NOT,
}