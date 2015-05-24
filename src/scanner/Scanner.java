package scanner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.PushbackReader;
import java.io.RandomAccessFile;
import java.util.Hashtable;

/**
 * The scanner used to get tokens from the input file.
 *
 * @author ken
 *
 */
public class Scanner
{

	/**
	 * States of a pseudo-DFA transition table, used to scan through characters
	 * in a file, getting tokens to give to the parser. For more detail refer to
	 * the detailed DFA here.
	 */
	private static final int START_STATE = 0;
	private static final int IN_NUMBER = 1;
	private static final int NUMBER_OPTIONAL_FRACTION = 2;
	private static final int FRACTION_FIRST_DIGIT = 3;
	private static final int OPTIONAL_EXPONENT = 4;
	private static final int OPTIONAL_EXPONENT_SIGN = 5;
	private static final int EXPONENT_DIGITS = 6;
	private static final int IN_ID = 7;
	private static final int GREATER_THAN = 8;
	private static final int COLON = 9;
	private static final int LESS_THAN = 10;
	private static final int COMMENT = 11;
	private static final int ID_ACCEPTANCE = 50;
	private static final int NUMBER_ACCEPTANCE = 51;
	private static final int REAL_ACCEPTANCE = 56;
	private static final int SYMBOL_ACCEPTANCE_PUSHBACK = 52;
	private static final int SYMBOL_ACCEPTANCE = 53;
	private static final int COMMENT_ACCEPT = 54;
	private static final int ERROR = 55;

	/**
	 * False if the EOF was not found. Set to true, then EOF pushed back so that
	 * the last token can be scanned.
	 */
	private boolean foundEOF = false;

	/**
	 * Line counter.
	 */
	private int line;

	/**
	 * This is the transition table that will be used, major being the state,
	 * and minor being the next char.
	 */
	private int[][] transitionTable;

	/**
	 * Symbol table (extended from hashtable, used to lookup tokens, to give
	 * them a return value.
	 */
	private Hashtable lookupTable;

	/**
	 * Current token in the stream.
	 */
	private Token token;

	/**
	 * Attribute of current token.
	 */
	private Object attribute;

	private File input;

	/**
	 * PushbackReader used to push unwanted chars back in to the input stream,
	 * so that there can be a look ahead to determine if it is part of the
	 * current token.
	 */
	private PushbackReader inputReader;

	public Scanner(File input, Hashtable symbolTable)
	{
		this.input = input;
		this.lookupTable = symbolTable;
		try
		{
			this.inputReader = new PushbackReader(new FileReader(input));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		this.transitionTable = createTransitionTable();
		this.line = 1;
	}

	/**
	 *
	 * @return the attribute of the current token
	 */
	public Object getAttribute()
	{
		return this.attribute;
	}

	/**
	 *
	 * @return the current token
	 */
	public Token getToken()
	{
		return this.token;
	}

	/**
	 * Moves through the transition table and follow states in the DFA to
	 * recognize the next token. Updates the NextTokenReturnValue, and if a
	 * token is available it gives updates the current token's attribute and the
	 * current token's value.
	 *
	 * @return NextTokenReturnValue: availability of the next token.
	 */
	public NextTokenReturnValue nextToken()
	{
		int currentState = START_STATE;
		StringBuilder workingAttribute = new StringBuilder("");
		while (currentState < ID_ACCEPTANCE)
		{
			char currentChar = '\0';
			try
			{
				currentChar = (char) inputReader.read();
			}
			catch (Exception e)
			{
				System.out.println("Input Complete");
				return NextTokenReturnValue.INPUT_COMPLETE;
			}
			if (currentChar == 10)
			{
				line++;
			}
			// EOF
			if (currentChar == 65535)
			{
				if (!foundEOF)
				{
					currentChar = 32;
					foundEOF = true;
				}
				else
				{
					try
					{
						inputReader.unread(currentChar);
					}
					catch (IOException ioe)
					{
					}
					return NextTokenReturnValue.INPUT_COMPLETE;
				}
			}
			// NON-ACSII char
			if (currentChar > 127)
			{
				return NextTokenReturnValue.TOKEN_NOT_AVAILABLE;
			}
			int nextState = transitionTable[currentState][currentChar];

			switch (nextState)
			{
				case START_STATE:
					workingAttribute = new StringBuilder("");
					break;
				case IN_NUMBER:
				case NUMBER_OPTIONAL_FRACTION:
				case FRACTION_FIRST_DIGIT:
				case OPTIONAL_EXPONENT:
				case OPTIONAL_EXPONENT_SIGN:
				case EXPONENT_DIGITS:
				case IN_ID:
				case GREATER_THAN:
				case COLON:
				case LESS_THAN:
				case COMMENT:
					workingAttribute.append(currentChar);
					break;
				case ID_ACCEPTANCE:
					unread(currentChar);
					token = (Token) lookupTable.get(workingAttribute.toString());
					if (token == null)
					{
						token = Token.ID;
					}
					attribute = workingAttribute.toString();
					break;
				case NUMBER_ACCEPTANCE:

					if (currentChar == ')' || currentChar == '/' || currentChar == '*')
					{
						unread(currentChar);
					}
					token = Token.INTEGER;
					attribute = workingAttribute.toString();
					break;
				case SYMBOL_ACCEPTANCE_PUSHBACK:
					unread(currentChar);
					token = (Token) lookupTable.get(workingAttribute.toString());
					attribute = workingAttribute.toString();
					break;
				case SYMBOL_ACCEPTANCE:
					workingAttribute.append(currentChar);
					token = (Token) lookupTable.get(workingAttribute.toString());
					attribute = workingAttribute.toString();
					break;
				case COMMENT_ACCEPT:
					workingAttribute = new StringBuilder("");
					nextState = START_STATE;
					break;
				case ERROR:
					attribute = currentChar;

					return NextTokenReturnValue.TOKEN_NOT_AVAILABLE;
				case REAL_ACCEPTANCE:
					if (currentChar == ')' || currentChar == '/' || currentChar == '*')
					{
						unread(currentChar);
					}
					token = Token.REAL;
					attribute = workingAttribute.toString();
					break;
			}
			currentState = nextState;
		}

		return NextTokenReturnValue.TOKEN_AVAILABLE;
	}

	private void unread(char currentChar)
	{
		try
		{
			if (currentChar == 10)
			{
				line--;
			}
			inputReader.unread(currentChar);
		}
		catch (IOException ioe)
		{
		}
	}

	/**
	 * Gives the line number.
	 *
	 * @return current line number.
	 */
	public int getLine()
	{
		return line;
	}

	/**
	 *
	 * @return the transition table (for more info see DFA in documentation)
	 */
	private int[][] createTransitionTable()
	{
		return new int[][]
		{
			//State 0: Start Sate
			{
				55, 55, 55, 55, 55, 55, 55, 55, 55, 0, 0, 55, 55, 55, 55, 55, // 0x00 - 0x0f
				55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, // 0x10 - 0x1f
				0, 55, 55, 55, 55, 55, 55, 55, 53, 53, 53, 53, 53, 53, 53, 53, // 0x20 - 0x2f
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 9, 53, 10, 53, 8, 55, // 0x30 - 0x3f
				55, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, // 0x40 - 0x4f
				7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 53, 55, 53, 55, 55, // 0x50 - 0x5f
				55, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, // 0x60 - 0x6f
				7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 11, 55, 55, 55, 55, // 0x70 - 0x7f
			},
			//State 1: In Number
			{
				55, 55, 55, 55, 55, 55, 55, 55, 55, 51, 51, 55, 55, 55, 55, 55, // 0x00 - 0x0f
				55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, // 0x10 - 0x1f
				51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 2, 51, // 0x20 - 0x2f
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 51, 51, 51, 51, 51, 51, // 0x30 - 0x3f
				51, 51, 51, 51, 51, 4, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, // 0x40 - 0x4f
				51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, // 0x50 - 0x5f
				51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, // 0x60 - 0x6f
				51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 55, // 0x70 - 0x7f
			},
			//State 2: Optional Fraction
			{
				55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, // 0x00 - 0x0f
				55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, // 0x10 - 0x1f
				55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, // 0x20 - 0x2f
				3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 55, 55, 55, 55, 55, 55, // 0x30 - 0x3f
				55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, // 0x40 - 0x4f
				55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, // 0x50 - 0x5f
				55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, // 0x60 - 0x6f
				55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, // 0x70 - 0x7f
			},
			//State 3: Fraction First Digit
			{
				55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 56, 55, 55, 55, 55, 55, // 0x00 - 0x0f
				55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, // 0x10 - 0x1f
				56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, // 0x20 - 0x2f
				3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 56, 56, 56, 56, 56, 56, // 0x30 - 0x3f
				56, 56, 56, 56, 56, 4, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, // 0x40 - 0x4f
				56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, // 0x50 - 0x5f
				56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, // 0x60 - 0x6f
				56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 55, // 0x70 - 6x7f
			},
			//State 4: Optional Exponent
			{
				55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, // 0x00 - 0x0f
				55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, // 0x10 - 0x1f  
				55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 5, 55, 5, 55, 55, // 0x20 - 0x2f
				6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 55, 55, 55, 55, 55, // 0x30 - 0x3f
				55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, // 0x40 - 0x4f
				55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, // 0x50 - 0x6f
				55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, // 0x60 - 0x6f
				55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, // 0x70 - 0x7f
			},
			//State 5: Optional Exponent Sign
			{
				55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, // 0x00 - 0x0f
				55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, // 0x10 - 0x1f  
				55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, // 0x20 - 0x2f
				6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 55, 55, 55, 55, 55, // 0x30 - 0x3f
				55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, // 0x40 - 0x4f
				55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, // 0x50 - 0x6f
				55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, // 0x60 - 0x6f
				55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, // 0x70 - 0x7f
			},
			//State 6: Exponent Digits
			{
				55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 51, 55, 55, 55, 55, 55, // 0x00 - 0x0f
				55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, // 0x10 - 0x1f
				55, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, // 0x20 - 0x2f
				6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 56, 56, 56, 56, 56, 56, // 0x30 - 0x3f
				56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, // 0x40 - 0x4f
				56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, // 0x50 - 0x5f
				56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, // 0x60 - 0x6f
				56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 55, // 0x70 - 0x7f
			},
			//State 7: ID
			{
				55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 50, 55, 55, 55, 55, 55, // 0x00 - 0x0f
				55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, // 0x10 - 0x1f
				50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, // 0x20 - 0x2f
				7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 50, 50, 50, 50, 50, 50, // 0x30 - 0x3f
				50, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, // 0x40 - 0x4f
				7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 50, 50, 50, 50, 50, // 0x50 - 0x5f
				50, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, // 0x60 - 0x6f
				7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 50, 50, 50, 50, 55, // 0x70 - 0x7f
			},
			//State 8: Greater Than
			{
				55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 52, 55, 55, 55, 55, 55, // 0x00 - 0x0f
				55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, // 0x10 - 0x1f
				52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, // 0x20 - 0x2f
				52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 53, 52, 52, // 0x30 - 0x3f
				52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, // 0x40 - 0x4f
				52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, // 0x50 - 0x5f
				52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, // 0x60 - 0x6f
				52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 55, // 0x70 - 0x7f
			},
			//State 9: Colon
			{
				55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 52, 55, 55, 55, 55, 55, // 0x00 - 0x0f
				55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, // 0x10 - 0x1f
				52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, // 0x20 - 0x2f
				52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 53, 52, 52, // 0x30 - 0x3f
				52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, // 0x40 - 0x4f
				52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, // 0x50 - 0x5f
				52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, // 0x60 - 0x6f
				52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 55, // 0x70 - 0x7f
			},
			//State 10: Less Than
			{
				55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 52, 55, 55, 55, 55, 55, // 0x00 - 0x0f
				55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, // 0x10 - 0x1f
				52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, // 0x20 - 0x2f
				52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 53, 53, 52, // 0x30 - 0x3f
				52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, // 0x40 - 0x4f
				52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, // 0x50 - 0x5f
				52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, // 0x60 - 0x6f
				52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 55, // 0x70 - 0x7f
			},
			//State 11: Comment State
			{
				55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 52, 55, 55, 55, 55, 55, // 0x00 - 0x0f
				55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, // 0x10 - 0x1f
				11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, // 0x20 - 0x2f
				11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, // 0x30 - 0x3f
				11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, // 0x40 - 0x4f
				11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, // 0x50 - 0x5f
				11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, // 0x60 - 0x6f
				11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 55, 11, 54, 11, 55, // 0x70 - 0x7f
			},
		};
	}
}
