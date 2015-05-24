package recognizer;
import scanner.LookupTable;
import scanner.NextTokenReturnValue;
import scanner.Scanner;
import scanner.Token;
import java.io.File;
import java.util.Hashtable;

/**
 * A yes/no parser that will recognize the validity of the syntax in the
 * language. Uses a scanner to get the tokens in a file and then recognize
 * whether or not it is a valid simple-pascal program.
 * @author ken
 */
public class Recognizer
{
    /** The scanner that will be used to recognize tokens in the input file. */
    private Scanner scanner;
    
    /** The currentToken that will be used for lookahead. */
    private Token currentToken;
    
    /** Token not available error code (2). */
    private static final int TOKEN_NOT_AVAILABLE_ERROR = 2;
    
    /** Token mismatch error code (3). */
    private static final int TOKEN_MISMATCH = 3;
    
    /** Error found after program (4). */
    private static final int AFTER_PROGRAM = 4;
    
    
    /** Program token not found (5). */
    private static final int PROGRAM_NOT_FOUND = 5;
    
    /** Keyword mismatch error (6). */
    private static final int KEYWORD_MISMATCH = 6;
    
    /** Datatype is not REAL or INTEGER (7). */
    private static final int UNRECOGNIZED_DATA_TYPE = 7;
    
    /** EOF expected but not found. */
    private static final int EXPECTED_EOF = 8;
    
    /** Holds the attribute of the current token. */
    private Object currentAttribute;
    
    /**
     * Create a recognizer to yes/no parse the file.
     * @param filename Name of the input file
     */
    public Recognizer(String filename)
    {
        File inputFile = new File(filename);
        Hashtable symbols = new LookupTable();
        scanner = new Scanner(inputFile, symbols);
        scanner.nextToken();
        currentToken = scanner.getToken();
        currentAttribute = scanner.getAttribute();
    }
    
    /**
     * Matches the current token to the program token, and then calls the
     * rest of the methods for program body.
     * Matches the program token, program_id, semi-colon. Then calls
     * declarations, subprogram_declarations, and compound_statement, then
     * matches the period that should be the end of the program.
     */
    public void program()
    {
        match(Token.PROGRAM);
        match(Token.ID);
        match(Token.SEMICOLON);
        declarations();
        subprogramDeclarations();
        compoundStatement();
        match(Token.PERIOD);
        checkInput();
    }
    
    /**
     * Program declarations are handled here. Can be lambda, otherwise a list of
     * declarations. 
     */
    private void declarations()
    {
        while(currentToken == Token.VAR)
        {
            match(Token.VAR);
            identifierList();
            match(Token.COLON);
            type();
            match(Token.SEMICOLON);
            declarations();
        }
    }
    
    /**
     * List of identifies. This can be one or more IDs separated by a comma.
     * Variable declarations.
     */
    private void identifierList()
    {
        while(currentToken == Token.ID)
        {
            match(Token.ID);
            if(currentToken == Token.COMMA)
            {
                match(Token.COMMA); 
            }
            else
                break;
        }
    }
    
    
    /**
     * Variable types. Can be either standard type (real or integer), or an 
     * array of standard type ( array[ num : num ] of standard type)
     */
    private void type()
    {
        if(currentToken == Token.ARRAY)
        {
            match(Token.ARRAY);
            match(Token.LEFT_SQUARE_BRACKET);
            match(Token.NUMBER);
            match(Token.COLON);
            match(Token.NUMBER);
            match(Token.RIGHT_SQUARE_BRACKET);
            match(Token.OF);
        }
        standardType();
    }
    
    /**
     * Standard type. Can be "integer" or "real"
     */
    private void standardType()
    {
        if(currentToken == Token.REAL)
        {
            match(Token.REAL);
        }
        else if(currentToken == Token.INTEGER)
        {
            match(Token.INTEGER);
        }
        else
            error(UNRECOGNIZED_DATA_TYPE, null);
    }
    
    /**
     * Subprogram Declarations. Can be lambda.
     * Yet to be implemented
     */
    private void subprogramDeclarations()
    {
        if(currentToken ==  Token.FUNCTION  || currentToken == Token.PROCEDURE)
        {
            subprogramDeclaration();
            match(Token.SEMICOLON);
            subprogramDeclarations();
        }
    }
    
    /**
     * 
     */
    private void subprogramDeclaration()
    {
        subprogramHead();
        declarations();
        subprogramDeclarations();
        compoundStatement();
    }
    
    /**
     * The signature of the method/function. Consists of function id: arguments
     * standard type; or procedure id: arguments
     */
    private void subprogramHead()
    {
        if(currentToken == Token.FUNCTION)
        {
            match(Token.FUNCTION);
            match(Token.ID);
            arguments();
            match(Token.COLON);
            standardType();
            match(Token.SEMICOLON);
        }
        else if(currentToken == Token.PROCEDURE)
        {
            match(Token.PROCEDURE);
            match(Token.ID);
            arguments();
            match(Token.SEMICOLON);
        }
    }
    
    /**
     * Parses the arguments of a function, or a procedure.
     * ( parameters )
     */
    private void arguments()
    {
        if(currentToken == Token.LEFT_PARENTHESIS)
        {
            match(Token.LEFT_PARENTHESIS);
            parameterList();
            match(Token.RIGHT_PARENTHESIS);
        }
    }
    /**
     * List of the parameters for a function/procedure arguments.
     * identifier list : type
     * or
     * identifier list : type; parameter list;
     */
    private void parameterList()
    {
        identifierList();
        match(Token.COLON);
        type();
        if(currentToken == Token.SEMICOLON)
        {
            parameterList();
        }
    }
    /**
     * Compound statements. Must start with "begin", and end with "end" and 
     * has optional statements in the middle. This is the "main"
     */
    private void compoundStatement()
    {
        match(Token.BEGIN);
        optionalStatements();
        match(Token.END);
    }
    
    /**
     * The optional statements that make up the pacsal program.
     * Checks to see if statement() will be called through statementList(),
     * otherwise lambda.
     */
    private void optionalStatements()
    {
        if(currentToken == Token.ID || currentToken == Token.BEGIN ||
           currentToken == Token.IF || currentToken == Token.WHILE )
        {
            statementList();
        }
    }
    
    /**
     * List of optional statements. Can be lambda, or a number of statements.
     */
    private void statementList()
    {
        statement();
        if(currentToken == Token.SEMICOLON)
        {
            match(Token.SEMICOLON);
            statementList();
        }
    }
    
    /**
     * A single statement.
     * variable := expression
     * procedure statement
     * compound statement
     * if expression then statement else statement
     * while expression do statement
     * 
     * read/write to do later.
     */
    private void statement()
    {
        if(currentToken == Token.IF)
        {
            match(Token.IF);
            expression();
            match(Token.THEN);
            statement();
            match(Token.ELSE);
            statement();
        }
        else if(currentToken == Token.WHILE)
        {
            match(Token.WHILE);
            expression();
            match(Token.DO);
            statement();
        }
        else if(currentToken == Token.BEGIN)
        {
            compoundStatement();
        }
        else if(currentToken == Token.ID)
        {
            match(Token.ID);
            if(currentToken == Token.LEFT_SQUARE_BRACKET)
            {
                match(Token.LEFT_SQUARE_BRACKET);
                expression();
                match(Token.RIGHT_SQUARE_BRACKET);
            }
            else if(currentToken == Token.ASSIGN)
            {
                match(Token.ASSIGN);
                expression();
            }
            else if(currentToken == Token.LEFT_PARENTHESIS)
            {
                match(Token.LEFT_PARENTHESIS);
                expressionList();
                match(Token.RIGHT_PARENTHESIS);
            }
        }
        
    }
    
    /**
     * Expression can be either simpleExpression, or simpleExpression RELOP
     * simpleExpression.
     */
    private void expression()
    {
        simpleExpression();
        if(currentToken == Token.LESS_THAN)
        {
            match(Token.LESS_THAN);
            simpleExpression();
        }
        else if(currentToken == Token.LESS_THAN_EQUAL)
        {
            match(Token.LESS_THAN_EQUAL);
            simpleExpression();
        }
        else if(currentToken == Token.GREATER_THAN)
        {
            match(Token.GREATER_THAN);
            simpleExpression();
        }
        else if(currentToken == Token.GREATER_THAN_EQUAL)
        {
            match(Token.GREATER_THAN_EQUAL);
            simpleExpression();
        }
        else if(currentToken == Token.LESS_THAN_GREATER_THAN)
        {
            match(Token.LESS_THAN_GREATER_THAN);
            simpleExpression();
        }
        else if(currentToken == Token.EQUALS)
        {
            match(Token.EQUALS);
            simpleExpression();
        }
    }
    
    /**
     * A simple expression that consists of a term and a simple part following 
     * an optional sign.
     */
    private void simpleExpression()
    {
        sign();
        term();
        simplePart();

    }
    
    
    private void sign()
    {
        if(currentToken == Token.PLUS)
        {
            match(Token.PLUS);
        }
        else if(currentToken == Token.MINUS)
        {
            match(Token.MINUS);
        }
    }
    
    /**
     * A single term. Consists of a factor, followed by the termPart.
     */
    private void term()
    {
        factor();
        termPart();
    }
    
    /**
     * The factor portion of an expression.
     * ID
     * ID [ expression ]
     * ID ( expressionList )
     * NUMBER
     * ( expression )
     * NOT factor
     */
    private void factor()
    {
        if(currentToken == Token.ID)
        {
            match(Token.ID);
            if(currentToken == Token.LEFT_SQUARE_BRACKET)
            {
                match(Token.LEFT_SQUARE_BRACKET);
                expression();
                match(Token.RIGHT_SQUARE_BRACKET);
            }
            else if(currentToken == Token.LEFT_PARENTHESIS)
            {
                match(Token.LEFT_PARENTHESIS);
                expressionList();
                match(Token.RIGHT_PARENTHESIS);
            }
        }
        else if(currentToken == Token.NUMBER)
        {
            match(Token.NUMBER);
        }
        else if(currentToken == Token.LEFT_PARENTHESIS)
        {
            match(Token.LEFT_PARENTHESIS);
            expression();
            match(Token.RIGHT_PARENTHESIS);
        }
        else if(currentToken == Token.NOT)
        {
            match(Token.NOT);
            factor();
        }
    }
    
    /**
     * The simple part of an expression. Consists of lambda, or an addop 
     * followed by a term, and another simplePart.
     */
    private void simplePart()
    {
        if(currentToken == Token.PLUS)
        {
            match(Token.PLUS);
            term();
            simplePart();
        }
        else if(currentToken == Token.MINUS)
        {
            match(Token.MINUS);
            term();
            simplePart();
        }
    }
    
    /**
     * The term part of an expression. Consists of a lambda, or a mulop, 
     * followed by factor, followed by another term part.
     */
    private void termPart()
    {
        if(currentToken == Token.MULTIPLY)
        {
            match(Token.MULTIPLY);
            factor();
            termPart();
        }
        else if(currentToken == Token.DIVIDE)
        {
            match(Token.DIVIDE);
            factor();
            termPart();
        }
    }
    
    /**
     * A list of expressions. One or more comma separated expressions.
     */
    private void expressionList()
    {
        expression();
        if(currentToken == Token.COMMA)
        {
            expressionList();
        }
    }
    
    
    /**
     * Matches the current token the the expected token to be matched.
     * If there is a match, match gets the next token return value. If the return
     * value indicates there is another token, match updates the currentToken
     * and currentAttribute values.
     * @param matchToken The expected token.
     */
    private void match(Token matchToken)
    {
        if(currentToken == matchToken)
        {
            NextTokenReturnValue retval = scanner.nextToken();
            switch(retval)
            {
                case TOKEN_AVAILABLE:
                    currentToken = scanner.getToken();
                    currentAttribute = scanner.getAttribute();
                    break;
                case TOKEN_NOT_AVAILABLE:
                    error(TOKEN_NOT_AVAILABLE_ERROR, matchToken);
                    break;
                case INPUT_COMPLETE:
                    System.out.println("Parsed successfully!");
                    break;
            }
        }
        else
        {
            error(TOKEN_MISMATCH, matchToken);
        }
    }
    
    private void checkInput()
    {
        if(scanner.nextToken() != NextTokenReturnValue.INPUT_COMPLETE)
        {
            error(EXPECTED_EOF, null);
        }
    }
    
    /**
     * Handles errors in the parsing.
     * @param errorCode 
     */
    public void error(int errorCode, Token expected)
    {
        switch(errorCode)
        {
            case TOKEN_NOT_AVAILABLE_ERROR:
                // Implement verbose debugging later.
                System.out.print("Error, invalid token found on line ");
                break;
            case TOKEN_MISMATCH:
                // Implement more verbose debugging.
                System.out.print("Error, token mismatch on token "
                        + currentToken + " : " + scanner.getAttribute() + 
                        " expected " + expected + " on line ");
                break;
            case AFTER_PROGRAM:
                // Better debugging (line found error on?)
                System.out.println("Error found after the program match"
                        + ". Check the first line of the program.");
                break;
            case PROGRAM_NOT_FOUND:
                System.out.println("\"Program\" not found at beginning of "
                        + "file.");
                break;
            case KEYWORD_MISMATCH:
                System.out.print("Keyword mismatch on: " 
                        + currentAttribute + " found on line ");
                break;
            case UNRECOGNIZED_DATA_TYPE:
                System.out.println("Unrecognized data type: " 
                        + currentAttribute + " should be real, or integer");
                break;
            case EXPECTED_EOF:
                System.out.print("EOF expected, but not found, found: " +
                    currentToken + ", with value: " + currentAttribute + 
                        " on line ");
                System.exit(EXPECTED_EOF);
                break;
        }
        System.out.print(scanner.getLine() + "\n");
        System.exit(errorCode);            
    }
}