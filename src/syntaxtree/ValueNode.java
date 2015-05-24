package syntaxtree;

import parser.SymbolTable;
import scanner.Token;

/**
 *
 * @author ken
 */
public class ValueNode extends ExpressionNode
{
    /**
     * The string representation of the value.
     */
	private String attribute;

    /**
     * The type of value. Real or integer,
     */
    private Token type;

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
		answer += "attribute: " + attribute + "\n";
		answer += super.indentedToString(level);
		answer += "type: " + type + "\n";
		return answer;
	}

    /**
     * Get the type of the value.
     * @return 
     */
    public Token getType()
    {
        return type;
    }

    /**
     * Set the type of the value.
     * @param type 
     */
    public void setType(Token type)
    {
        this.type = type;
    }


    /**
     * Returns true if the value is real.
     * @param symbols
     * @return 
     */
	public boolean isReal(SymbolTable symbols)
	{
		return type == Token.REAL;
	}
    
    /**
     * Get the string representation of the value.
     * @return 
     */
	public String getAttribute()
	{
		return attribute;
	}
	
    /**
     * Set the string representation of the value.
     * @param value 
     */
	public void setAttribute(String value)
	{
		this.attribute = value;
	}
}
