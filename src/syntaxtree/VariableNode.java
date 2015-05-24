package syntaxtree;

import parser.Kind;
import parser.SymbolTable;
import scanner.Token;

/**
 *
 * @author ken
 */
public class VariableNode extends ExpressionNode
{
	/** The name of the variable. */
	private String name;

    /**
     * The type of token.
     */
	private Token type;

    /**
     * Empty Constructor.
     */
	public VariableNode()
	{
	}

    /**
     * Constructor
     * @param name
     * @param type 
     */
	public VariableNode(String name, Token type)
	{
		this.name = name;
		this.type = type;
	}

	
	 
	
    /**
     * Constructor.
     * @param name 
     */
	public VariableNode(String name)
	{
		this.name = name;
	}

    /**
     * Gets the name of the Var.
     * @return 
     */
	public String getName()
	{
		return name;
	}

    /**
     * Sets the name of the var.
     * @param name 
     */
	public void setName(String name)
	{
		this.name = name;
	}

    /**
     * Gets the type of the var.
     * @return 
     */
	public Token getType()
	{
		return type;
	}

    /**
     * Sets the type of the var.
     * @param type 
     */
	public void setType(Token type)
	{
		this.type = type;
	}

	
    /**
     * Returns true if the variable is real.
     * @param symbols
     * @return 
     */
	@Override
	public boolean isReal(SymbolTable symbols)
	{
		return type == Token.REAL;
		
	}

	
    /**
     * Used for printing the tree. Indents with --- per level of 
     * depth in the tree.
     * @param level
     * @return 
     */
	@Override
	public String indentedToString(int level)
	{
		String answer = super.indentedToString(level);
		answer += "varibale name: " + name + "\n";
		answer += super.indentedToString(level);
		answer += "type: " + type + "\n";
		return answer;
	}

}
