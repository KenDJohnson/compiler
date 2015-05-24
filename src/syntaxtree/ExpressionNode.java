package syntaxtree;

import parser.SymbolTable;
import scanner.Token;

/**
 * Abstract class from which all Expressions are extended.
 * @author ken
 */
public abstract class ExpressionNode extends SyntaxTreeNode
{
	
    /**
     * A flag for if the expression has been "notted".
     */
    private boolean not = false;

    /**
     * The sign of the expression. Default PLUS.
     */
    private Token sign = Token.PLUS; 

    /**
     * Get the sign.
     * @return 
     */
    public Token getSign()
    {
        return sign;
    }

    /**
     * Set the sign.
     * @param sign 
     */
    public void setSign(Token sign)
    {
        this.sign = sign;
    }

    /**
     * Returns true if the expression has been "notted".
     * @return 
     */
    public boolean isNot()
    {
        return not;
    }

    /**
     * Sets the negation of the expression.
     * @param not 
     */
    public void setNot(boolean not)
    {
        this.not = not;
    }

    /**
     * Abstract method to determine if the expression is real.
     * @param symbols
     * @return 
     */
	abstract public boolean isReal(SymbolTable symbols);

    
    /**
     * Used for printing the tree. Indents with --- per level of 
     * depth in the tree.
     * @param level
     * @return 
     */
	@Override
	public String indentedToString(int level)
	{
		String answer  =  super.indentedToString(level);
        answer        +=  "Not: " + not + "\n";
        answer        +=  super.indentedToString(level);
        answer        +=  "Sign: " + sign + "\n";
        answer        +=  super.indentedToString(level);
		return answer;
	}
}
