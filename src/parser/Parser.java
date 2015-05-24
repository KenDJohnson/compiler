package parser;

import scanner.LookupTable;
import scanner.NextTokenReturnValue;
import scanner.Scanner;
import scanner.Token;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Stack;
import syntaxtree.*;

/**
 * A parser that will create a syntax tree from lexical token. Uses a scanner to
 * get the tokens in a file and then checks syntax according to the grammar of
 * the language, and builds a tree representation of the program. The parser
 * also does type checking, and ensures that variables being used have been
 * declared.
 *
 * @author ken
 */
public class Parser
{

	/**
	 * The scanner that will be used to recognize tokens in the input file.
	 */
	private Scanner scanner;

	/**
	 * Stack of Info items, that can be used to hold multiple symbols that are
	 * declared in a list.
	 */
	private Stack<String> currentSymbols;

	/**
	 * Stack of Strings, used for function and procedure arguments, so they can
	 * be added in their own scope, after the function/procedure is added to the
	 * current scope.
	 */
	private Stack<String> argStack;

	/**
	 * Hashtable that will contain the lexeme for the arg as well as its type so
	 * that the arguments can be added to the func/proc scope.
	 */
	private Hashtable<String, Token> argTable;

	/**
	 *
	 * Backstack of Info items.
	 */
	private Stack<String> symbolsBackstack;

	/**
	 * String to hold the name of the current scope.
	 */
	private static Stack<String> currentScope;

	/**
	 * The currentToken that will be used for lookahead.
	 */
	private Token currentToken;

	/**
	 * Token not available error code (2).
	 */
	private static final int TOKEN_NOT_AVAILABLE_ERROR = 2;

	/**
	 * Token mismatch error code (3).
	 */
	private static final int TOKEN_MISMATCH = 3;

	/**
	 * Error found after program (4).
	 */
	private static final int AFTER_PROGRAM = 4;

	/**
	 * Program token not found (5).
	 */
	private static final int PROGRAM_NOT_FOUND = 5;

	/**
	 * Keyword mismatch error (6).
	 */
	private static final int KEYWORD_MISMATCH = 6;

	/**
	 * Datatype is not REAL or INTEGER (7).
	 */
	private static final int UNRECOGNIZED_DATA_TYPE = 7;

	/**
	 * EOF expected but not found.
	 */
	private static final int EXPECTED_EOF = 8;

	/**
	 * Semicolon found after last statement in a compound statement.
	 */
	private static final int COMPOUND_STMT_SEMICOLON = 9;

	/**
	 * Attempt to use variable without declaring variable first.
	 */
	private static final int VARIABLE_NOT_DEC = 10;


	/**
	 * Attempt was made to assign a real in to an int.
	 */
	private static final int ASSIGN_REAL_TO_INT = 11;
	
	/**
	 * Attempt was made to compare real and int.
	 */
	private static final int REAL_INT_COMPARISON = 12;
	
	/**
	 * Holds the attribute of the current token.
	 */
	private Object currentAttribute;

	/**
	 * SymbolTable that is used for the declared variables, arrays, functions,
	 * and procedures. Currently only one scope is used, although multiple could
	 * be supported. This may be implemented in later versions.
	 */
	private SymbolTable symbolTable;

	public SymbolTable getSymbolTable()
	{
		return symbolTable;
	}


	

	/**
	 * Create a parser to parse the file and create a syntax tree.
	 *
	 * @param filename Name of the input file
	 */
	public Parser(String filename)
	{
		File inputFile = new File(filename);
		Hashtable symbols = new LookupTable();
		scanner = new Scanner(inputFile, symbols);
		scanner.nextToken();
		currentToken = scanner.getToken();
		currentAttribute = scanner.getAttribute();
		currentScope = new Stack<String>();
		symbolTable = new SymbolTable();
		currentScope.push("globalScope");
		currentSymbols = new Stack<String>();
		symbolsBackstack = new Stack<String>();
		argStack = new Stack<String>();
		argTable = new Hashtable<String, Token>();
	}

	/**
	 * Matches the current token to the program token, and then calls the rest
	 * of the methods for program body, building the program node, which is the
	 * root of the entire tree. Matches the program token, program_id,
	 * semi-colon. Then calls declarations, subprogram_declarations, and
	 * compound_statement, then matches the period that should be the end of the
	 * program, and adds all of these to the node.
	 *
	 * @return ProgramNode that is the root of the tree representing the
	 * program.
	 */
	public ProgramNode program()
	{
		ProgramNode thisProg = new ProgramNode();
		if (currentToken == Token.PROGRAM)
		{
			match(Token.PROGRAM);
		}
		if (currentToken == Token.ID)
		{
			symbolTable.add(scanner.getAttribute().toString(),
							Kind.PROGRAM);
			thisProg.setName(scanner.getAttribute().toString());
			match(Token.ID);
		}
		match(Token.SEMICOLON);
		thisProg.setVariables(declarations());
		subprogramDeclarations();
		thisProg.setMain(compoundStatement());
		match(Token.PERIOD);
		checkInput();
		return thisProg;
	}

	/**
	 * Declarations are handled here. Can be lambda, otherwise a list of
	 * declarations. This is used for variable, function, array, and procedure
	 * declarations. Iteratively continues to add vars to the DeclarationsNode
	 * until there are no more.
	 *
	 * @return DeclarationsNode containing a node with all the declarations made
	 * here.
	 */
	private DeclarationsNode declarations()
	{
		DeclarationsNode decs = new DeclarationsNode();
		while (currentToken == Token.VAR)

		{
			match(Token.VAR);
			ArrayList<String> ids = identifierList();
			match(Token.COLON);
			Token idType = type();
			match(Token.SEMICOLON);
			for (int i = 0; i < ids.size(); i++)
			{
				decs.addVar(new VariableNode(ids.get(i), idType));
			}
		}
		return decs;
	}

	/**
	 * List of identifies. This can be one or more IDs separated by a comma.
	 * Variable declarations.
	 */
	private ArrayList<String> identifierList()
	{
		ArrayList<String> ids = new ArrayList<String>();

		while (currentToken == Token.ID)
		{
			currentSymbols.push(scanner.getAttribute().toString());
			symbolTable.add(scanner.getAttribute().toString());
			ids.add(scanner.getAttribute().toString());
			match(Token.ID);
			if (currentToken == Token.COMMA)
			{
				match(Token.COMMA);
			}
			else
			{
				break;
			}
		}
		return ids;
	}

	private void argIdentifierList()
	{
		while (currentToken == Token.ID)
		{
			argStack.push(scanner.getAttribute().toString());
			match(Token.ID);
			if (currentToken == Token.COMMA)
			{
				match(Token.COMMA);
			}
			else
			{
				break;
			}
		}
	}

	/**
	 * Variable types. Can be either standard type (real or integer), or an
	 * array of standard type ( array[ num : num ] of standard type)
	 *
	 * @return Token of the type of current element being parsed.
	 */
	private Token type()
	{
		if (currentToken == Token.ARRAY)
		{
			match(Token.ARRAY);

			match(Token.LEFT_SQUARE_BRACKET);
			if (currentToken == Token.INTEGER)
			{
				while (!currentSymbols.isEmpty())
				{
					symbolTable.setStart(currentSymbols.peek(),
										 new Integer(
												 scanner.getAttribute().toString()).intValue());
					symbolTable.setKind(currentSymbols.peek(), Kind.ARRAY);
					symbolsBackstack.push(currentSymbols.pop());
				}
				while (!symbolsBackstack.isEmpty())
				{
					currentSymbols.push(symbolsBackstack.pop());
				}
				match(Token.INTEGER);
			}
			match(Token.COLON);
			if (currentToken == Token.INTEGER)
			{
				while (!currentSymbols.isEmpty())
				{
					symbolTable.setEnd(currentSymbols.peek(),
									   new Integer(scanner.getAttribute().toString()).intValue());
					symbolsBackstack.push(currentSymbols.pop());
				}
				while (!symbolsBackstack.isEmpty())
				{
					currentSymbols.push(symbolsBackstack.pop());
				}
				match(Token.INTEGER);
			}
			match(Token.RIGHT_SQUARE_BRACKET);
			match(Token.OF);
//			return Token.ARRAY;
		}
		else
		{
			while (!currentSymbols.isEmpty())
			{
				symbolTable.setKind(currentSymbols.peek(), Kind.VAR);
				symbolsBackstack.push(currentSymbols.pop());
			}
			while (!symbolsBackstack.isEmpty())
			{
				currentSymbols.push(symbolsBackstack.pop());
			}
		}
		return standardType();
	}

	/**
	 * Used for the types of arguments. Not currently implemented. May implement
	 * later.
	 *
	 * @param name
	 */
	private void argType(String name)
	{
		if (currentToken == Token.ARRAY)
		{
			/* 		THIS DOES NOT WORK YET
			 NEED TO FIGURE OUT HOW TO PASS ARRAYS LATER 
			
			 match(Token.ARRAY);
			 match(Token.LEFT_SQUARE_BRACKET);
			 if (currentToken == Token.INTEGER)
			 {
			 while (!currentSymbols.isEmpty())
			 {
			 symbolTable.setStart(currentSymbols.peek(),
			 new Integer(
			 scanner.getAttribute().toString()).intValue());
			 symbolTable.setKind(currentSymbols.peek(), Kind.ARRAY);
			 symbolsBackstack.push(currentSymbols.pop());
			 }
			 while (!symbolsBackstack.isEmpty())
			 {
			 currentSymbols.push(symbolsBackstack.pop());
			 }
			 match(Token.INTEGER);
			 }
			 match(Token.COLON);
			 if (currentToken == Token.INTEGER)
			 {
			 while (!currentSymbols.isEmpty())
			 {
			 symbolTable.setEnd(currentSymbols.peek(),
			 new Integer(scanner.getAttribute().toString()).intValue());
			 symbolsBackstack.push(currentSymbols.pop());
			 }
			 while (!symbolsBackstack.isEmpty())
			 {
			 currentSymbols.push(symbolsBackstack.pop());
			 }
			 match(Token.INTEGER);
			 }
			 match(Token.RIGHT_SQUARE_BRACKET);
			 match(Token.OF);
			 */
		}
		argStandardType(name);
	}

	/**
	 * Standard type. Can be "integer" or "real"
	 *
	 * @return Token of the type of variable. Should be INTEGER or REAL.
	 */
	private Token standardType()
	{
		Token stdType = null;
		if (currentToken == Token.REAL || currentToken == Token.INTEGER)
		{
			stdType = currentToken;
			if (symbolTable.getKind(currentSymbols.peek()) == Kind.FUNCTION)
			{
				while (!currentSymbols.isEmpty())
				{
					symbolTable.setReturnType(currentSymbols.pop(), currentToken);
				}
			}
			else
			{
				while (!currentSymbols.isEmpty())
				{
					symbolTable.setType(currentSymbols.peek(),
										stdType);
					symbolsBackstack.push(currentSymbols.pop());
				}
				while (!symbolsBackstack.isEmpty())
				{
					currentSymbols.push(symbolsBackstack.pop());
				}
			}
			match(currentToken);
		}
        currentSymbols.clear();
		return stdType;
	}

	/**
	 * Not currently used, but may be implemented further for function/
	 * procedure.
	 *
	 * @param name
	 */
	private void argStandardType(String name)
	{
		if (currentToken == Token.INTEGER || currentToken == Token.REAL)
		{
			Stack<String> argBack = new Stack<String>();
			while (!argStack.isEmpty())
			{
				symbolTable.addParamType(name, currentToken);
				argTable.put(argStack.peek(), currentToken);
				argBack.push(argStack.pop());
			}
			while (!argBack.isEmpty())
			{
				argStack.push(argBack.pop());
			}
			match(currentToken);
		}
	}

	/**
	 * Subprogram Declarations. Declarations of functions and procedures.
	 * Currently parses, but does not create a syntax tree node. This may be
	 * implemented if I choose to support functions calls/ procedures.
	 *
	 * @return SubProgramDeclarationsNode that should contain the function /
	 * procedure declarations. Only returns a skeleton of this node now.
	 */
	private SubProgramDeclarationsNode subprogramDeclarations()
	{
		SubProgramDeclarationsNode funcs = new SubProgramDeclarationsNode();
		while (currentToken == Token.FUNCTION || currentToken == Token.PROCEDURE)
		{
			subprogramDeclaration();
			match(Token.SEMICOLON);
			subprogramDeclarations();
		}
		return funcs;
	}

	/**
	 * A single subprogram declaration. Called from subprogramDeclarations. This
	 * currently parses, but does not produce nodes. If support is made it will
	 * create and return a SubProgramNode.
	 */
	private void subprogramDeclaration()
	{
		subprogramHead();

		declarations();
		subprogramDeclarations();
		compoundStatement();
		symbolTable.popScope();
	}

	/**
	 * The signature of the method/function. Consists of function id: arguments
	 * standard type; or procedure id: arguments. No tree building support yet.
	 */
	private void subprogramHead()
	{
		if (currentToken == Token.FUNCTION)
		{
			match(Token.FUNCTION);
			String functionName = scanner.getAttribute().toString();
			currentSymbols.push(functionName);
			symbolTable.add(functionName, Kind.FUNCTION);
			match(Token.ID);
			arguments(functionName);
			match(Token.COLON);
			standardType();
			match(Token.SEMICOLON);
			symbolTable.pushScope(functionName);
			while (!argStack.isEmpty())
			{
				System.out.println("Adding to next scope");
				String current = argStack.pop();
				symbolTable.add(current, Kind.VAR);
				symbolTable.setType(current, argTable.get(current));
			}
		}
		else if (currentToken == Token.PROCEDURE)
		{
			match(Token.PROCEDURE);
			String procedureName = scanner.getAttribute().toString();
			symbolTable.add(procedureName, Kind.PROCEDURE);
			match(Token.ID);
			arguments(procedureName);
			match(Token.SEMICOLON);
			symbolTable.pushScope(procedureName);
			while (!argStack.isEmpty())
			{
				System.out.println("Adding to next scope");
				String current = argStack.pop();
				symbolTable.add(current, Kind.VAR);
				symbolTable.setType(current, argTable.get(current));
			}
		}

	}

	/**
	 * Parses the arguments of a function, or a procedure. ( parameters )
	 * Parses, but does not create tree nodes yet.
	 */
	private void arguments(String name)
	{
		if (currentToken == Token.LEFT_PARENTHESIS)
		{
			match(Token.LEFT_PARENTHESIS);
			parameterList(name);
			match(Token.RIGHT_PARENTHESIS);
		}

	}

	/**
	 * List of the parameters for a function/procedure arguments. identifier
	 * list : type or identifier list : type; parameter list; No tree node
	 * creation.
	 */
	private void parameterList(String name)
	{
		argIdentifierList();
		match(Token.COLON);
		argType(name);

		if (currentToken == Token.SEMICOLON)
		{
			parameterList(name);
		}
	}

	/**
	 * Compound statements. Must start with "begin", and end with "end" and has
	 * optional statements in the middle. This is the body of a function /
	 * procedure / "main".
	 *
	 * @return CompoundStatementNode containing an ArrayList of StatementNodes
	 * in the compound statement.
	 */
	private CompoundStatementNode compoundStatement()
	{
		CompoundStatementNode cpd = new CompoundStatementNode();
		match(Token.BEGIN);
		ArrayList<StatementNode> optStmts;
		optStmts = optionalStatements();
		while (!optStmts.isEmpty())
		{
			cpd.addStatement(optStmts.remove(0));
		}
		match(Token.END);
		return cpd;
	}

	/**
	 * The optional statements that make up the compound statement. Checks to
	 * see if statement() will be called through statementList(), otherwise
	 * lambda.
	 *
	 * @return ArrayList<StatementNode> of all the statements.
	 */
	private ArrayList<StatementNode> optionalStatements()
	{
		ArrayList<StatementNode> optStmts = new ArrayList<StatementNode>();
		while (currentToken == Token.ID || currentToken == Token.BEGIN
			   || currentToken == Token.IF || currentToken == Token.WHILE)

		{
			optStmts.addAll(statementList());
		}
		return optStmts;
	}

	/**
	 * List of optional statements. Can be lambda, or a number of statements
	 * each of which ends with a semicolon, except the last one.
	 *
	 * @return ArrayList<StatementNode> containing all the statements.
	 */
	private ArrayList<StatementNode> statementList()
	{
		ArrayList<StatementNode> stmt = new ArrayList<StatementNode>();
		stmt.add(statement());
		while (currentToken == Token.SEMICOLON)
		{
			match(Token.SEMICOLON);
			if (currentToken == Token.END)
			{
				error(COMPOUND_STMT_SEMICOLON, Token.ID);
			}
			stmt.add(statement());
		}
		return stmt;
	}

	/**
	 * A single statement. variable := expression procedure statement compound
	 * statement if expression then statement else statement while expression do
	 * statement. Currently parses and creates tree nodes for assignment
	 * statements, and read/write. Needs to be implemented for if and while, and
	 * possibly procedure / function.
	 *
	 * @return StatementNode containing a single statement.
	 */
	private StatementNode statement()
	{
		StatementNode stmt = null;
		if (currentToken == Token.IF)
		{
			stmt = ifStatement();
		}
		else if (currentToken == Token.WHILE)
		{
			stmt = whileStatement();
		}
		else if (currentToken == Token.BEGIN)
		{
			stmt = compoundStatement();
		}
		else if (currentToken == Token.ID)
		{
			String tokenAtt = scanner.getAttribute().toString();
			if (symbolTable.exists(tokenAtt))
			{
				Kind idKind = symbolTable.getKind(tokenAtt);
				match(Token.ID);
				if (idKind == Kind.ARRAY)
				{
					match(Token.LEFT_SQUARE_BRACKET);
					expression();
					match(Token.RIGHT_SQUARE_BRACKET);
				}
				else if (idKind == Kind.VAR)
				{
					stmt = new AssignmentStatementNode();
					VariableNode varNode = new VariableNode(tokenAtt);
					((AssignmentStatementNode) stmt).setLvalue(varNode);
					((AssignmentStatementNode) stmt).setType(
							symbolTable.getType(tokenAtt));
					match(Token.ASSIGN);
					((AssignmentStatementNode) stmt).setExpression(expression());
					if(!((AssignmentStatementNode)stmt).isValid(symbolTable))
					{
						error(ASSIGN_REAL_TO_INT, Token.ID);
					}
				}
				else if (idKind == Kind.PROCEDURE
						 || idKind == Kind.FUNCTION)
				{
					match(Token.LEFT_PARENTHESIS);
					expressionList();
					match(Token.RIGHT_PARENTHESIS);
				}
			}
			else if (tokenAtt.equals("read"))
			{
				stmt = new ReadNode();
				match(Token.ID);
				match(Token.LEFT_PARENTHESIS);
				VariableNode readNode = new VariableNode();
				readNode.setName(scanner.getAttribute().toString());
				readNode.setType(symbolTable.getType(
						scanner.getAttribute().toString()));
				((ReadNode) stmt).setInput(null);
				match(Token.RIGHT_PARENTHESIS);
			}
			else if (tokenAtt.equals("write"))
			{
				stmt = new WriteNode();
				match(Token.ID);
				match(Token.LEFT_PARENTHESIS);
				((WriteNode) stmt).setOutput(expression());
				match(Token.RIGHT_PARENTHESIS);
			}
			else
			{
				error(VARIABLE_NOT_DEC, currentToken);
			}
		}
		return stmt;
	}

	/**
	 * Parses an if statement. Does not create a node yet, this is next to be
	 * implemented.
	 *
	 * @return IfStatementNode that will contain the conditions, as well as the
	 * statements.
	 */
	private IfStatementNode ifStatement()
	{
		IfStatementNode ifN = new IfStatementNode();
		match(Token.IF);
		ifN.setCondition(expression());
		if(!ifN.isValid(symbolTable))
		{
			error(REAL_INT_COMPARISON, currentToken);
		}
		match(Token.THEN);
		ifN.setStatement(statement());
		match(Token.ELSE);
		ifN.setElseStatement(statement());

		return ifN;
	}

	/**
	 * Parses a while statement. Does not create a node yet.
	 *
	 * @return WhileStatementNode containing conditions and the statements.
	 */
	private WhileStatementNode whileStatement()
	{
		WhileStatementNode whileN = new WhileStatementNode();

		match(Token.WHILE);
		whileN.setCondition(expression());
		if(!whileN.isValid(symbolTable))
		{
			error(REAL_INT_COMPARISON, currentToken);
		}
		match(Token.DO);
		whileN.setStatement(statement());

		return whileN;
	}

	/**
	 * Expression can be either simpleExpression, or simpleExpression RELOP
	 * simpleExpression.
	 *
	 * @return ExpressionNode with the expression.
	 */
	private ExpressionNode expression()
	{
		ExpressionNode exp;
		exp = simpleExpression();
		if (currentToken == Token.LESS_THAN
			|| currentToken == Token.LESS_THAN_EQUAL
			|| currentToken == Token.GREATER_THAN
			|| currentToken == Token.GREATER_THAN_EQUAL
			|| currentToken == Token.LESS_THAN_GREATER_THAN
			|| currentToken == Token.EQUALS)
		{
			ExpressionNode tmp = exp;
			exp = new OperationNode();
			((OperationNode) exp).setOperation(currentToken);
			match(currentToken);
			ExpressionNode right = simpleExpression();
			((OperationNode) exp).setLeft(tmp);
			((OperationNode) exp).setRight(right);
		}
		return exp;
	}

	/**
	 * A simple expression that consists of a term and a simple part following
	 * an optional sign.
	 *
	 * @return ExpressionNode containing the simple expression, which could be
	 * operation, variable, or value.
	 */
	private ExpressionNode simpleExpression()
	{
		Token expSign = sign();
		ExpressionNode trmPart = term();
        if(expSign != null)
        {
            trmPart.setSign(expSign);
        }
        else
        {
            trmPart.setSign(Token.PLUS);
        }
		ExpressionNode smpPart = simplePart();
		if (smpPart == null)
		{
			return trmPart;
		}
		if (smpPart instanceof syntaxtree.OperationNode)
		{
			((OperationNode) smpPart).addToLeft(trmPart);
		}
		return smpPart;
	}

	/**
	 * The sign of a term.
	 *
	 * @return Token PLUS or MINUS for sign.
	 */
	private Token sign()
	{
		Token sign = null;
		if (currentToken == Token.PLUS)
		{
			match(Token.PLUS);
			sign = Token.PLUS;
		}
		else if (currentToken == Token.MINUS)
		{
			match(Token.MINUS);
			sign = Token.MINUS;
		}
		return sign;
	}

	/**
	 * A single term. Consists of a factor, followed by the termPart.
	 *
	 * @return ExpressionNode VariableNode/OperationNode/ValueNode.
	 */
	private ExpressionNode term()
	{
		ExpressionNode fac = factor();
		ExpressionNode trmPrt = termPart();
		if (trmPrt == null || ((OperationNode) trmPrt).getOperation() == null)
		{
			return fac;
		}
		((OperationNode) trmPrt).addToLeft(fac);
		return trmPrt;
	}

	/**
	 * The factor portion of an expression. ID ID [ expression ] ID (
	 * expressionList ) NUMBER ( expression ) NOT factor. Currently only creates
	 * a tree for variable or values. Need to implement either arrays, or
	 * function/procedures here. FIRST implement NOT and parenthesis.
	 *
	 * @return ExpressionNode
	 */
	private ExpressionNode factor()
	{
		ExpressionNode fac = null;
		if (currentToken == Token.ID)
		{
			if (symbolTable.exists(scanner.getAttribute().toString()))
			{

				String att = scanner.getAttribute().toString();
				match(Token.ID);
				if (currentToken == Token.LEFT_SQUARE_BRACKET)
				{
					match(Token.LEFT_SQUARE_BRACKET);
					expression();
					match(Token.RIGHT_SQUARE_BRACKET);
				}
				else if (currentToken == Token.LEFT_PARENTHESIS)
				{
					match(Token.LEFT_PARENTHESIS);
					expressionList();
					match(Token.RIGHT_PARENTHESIS);
				}
				else
				{
					fac = new VariableNode();
					((VariableNode) fac).setName(att);
					((VariableNode) fac).setType(symbolTable.getType(att));
				}
			}
			else
			{
				error(VARIABLE_NOT_DEC, currentToken);
			}
		}
		else if (currentToken == Token.INTEGER)
		{
			fac = new ValueNode();
			((ValueNode) fac).setAttribute(scanner.getAttribute().toString());
            ((ValueNode) fac).setType(currentToken);
			match(Token.INTEGER);
		}
		else if (currentToken == Token.REAL)
		{
			fac = new ValueNode();
			((ValueNode) fac).setAttribute(currentAttribute.toString());
			((ValueNode) fac).setType(currentToken);
			match(Token.REAL);
		}
		else if (currentToken == Token.LEFT_PARENTHESIS)
		{
			match(Token.LEFT_PARENTHESIS);
			fac = expression();
			match(Token.RIGHT_PARENTHESIS);
		}
		else if (currentToken == Token.NOT)
		{
			match(Token.NOT);
			fac = factor();
			fac.setNot(true);
		}
		return fac;
	}

	/**
	 * The simple part of an expression. Consists of lambda, or an addop
	 * followed by a term, and another simplePart.
	 *
	 * @return ExpressionNode containing the simple part of an expression.
	 */
	private ExpressionNode simplePart()
	{
		ExpressionNode simple = new OperationNode();
		ExpressionNode termPrt = new OperationNode();
		if (currentToken == Token.PLUS || currentToken == Token.MINUS)
		{
			((OperationNode) simple).setOperation(currentToken);
			match(currentToken);
			termPrt = term();
			((OperationNode) simple).setRight(termPrt);
			ExpressionNode simpPart = simplePart();
			if (simpPart == null)
			{
				return simple;
			}
			ExpressionNode tmp = simpPart;
			((OperationNode) simpPart).addToLeft(simple);
			return simpPart;
		}
		return null;
	}

	/**
	 * The term part of an expression. Consists of a lambda, or a mulop,
	 * followed by factor, followed by another term part.
	 *
	 * @return ExpressionNode with the term part.
	 */
	private ExpressionNode termPart()
	{
		ExpressionNode trm = new OperationNode();
		ExpressionNode nextTerm = new OperationNode();
		if (currentToken == Token.DIVIDE
			|| currentToken == Token.MULTIPLY)
		{
			((OperationNode) trm).setOperation(currentToken);
			match(currentToken);
			((OperationNode) trm).setRight(factor());
			nextTerm = termPart();
			if (nextTerm == null || ((OperationNode) nextTerm).getOperation() == null)
			{
				return trm;
			}
			((OperationNode) nextTerm).addToLeft(trm);

		}
		return trm;
	}

	/**
	 * A list of expressions. One or more comma separated expressions.
	 *
	 * @return ArrayList<ExpressionNode> containing all the expressions in the
	 * expression list.
	 */
	private ArrayList<ExpressionNode> expressionList()
	{
		ArrayList<ExpressionNode> expressions = new ArrayList<ExpressionNode>();
		expressions.add(expression());
		if (currentToken == Token.COMMA)
		{
			expressions.addAll(expressionList());
		}
		return expressions;
	}

	/**
	 * Matches the current token the the expected token to be matched. If there
	 * is a match, match gets the next token return value. If the return value
	 * indicates there is another token, match updates the currentToken and
	 * currentAttribute values.
	 *
	 * @param matchToken The expected token.
	 */
	private void match(Token matchToken)
	{
		if (currentToken == matchToken)
		{
			NextTokenReturnValue retval = scanner.nextToken();
			switch (retval)
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

	/**
	 * Ensures that the file was ended properly.
	 */
	private void checkInput()
	{
		if (scanner.nextToken() != NextTokenReturnValue.INPUT_COMPLETE)
		{
			error(EXPECTED_EOF, null);
		}
	}

	/**
	 * Handles errors in the parsing.
	 *
	 * @param errorCode
	 * @param expected
	 */
	public void error(int errorCode, Token expected)
	{
		switch (errorCode)
		{
			case TOKEN_NOT_AVAILABLE_ERROR:
				// Implement verbose debugging later.
				System.out.print("Error, invalid token found on line ");
				break;
			case TOKEN_MISMATCH:
				// Implement more verbose debugging.
				System.out.print("Error, token mismatch on token "
								 + currentToken + " : " + scanner.getAttribute()
								 + " expected " + expected + " on line ");
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
				System.out.print("EOF expected, but not found, found: "
								 + currentToken + ", with value: " + currentAttribute
								 + " on line ");
				System.exit(EXPECTED_EOF);
				break;
			case COMPOUND_STMT_SEMICOLON:
				System.out.print("END token found after statement"
								 + " caused by extra semicolon. Unexpected "
								 + "END on line ");
				break;
			case VARIABLE_NOT_DEC:
				System.out.print("Variable "
								 + scanner.getAttribute().toString()
								 + " used before" + " declaration, on line ");
				break;
			case ASSIGN_REAL_TO_INT:
				System.out.print("An attempt was made to assign a real number"
						+ " in to an integer variable on line ");
				break;
			case REAL_INT_COMPARISON:
				System.out.print("An attempt was madde to compare a real number "
						+ "with an integer on line ");

		}
		System.out.print(scanner.getLine() + "\n");
		System.exit(errorCode);
	}
}
