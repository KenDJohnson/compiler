package syntaxtree;

import parser.SymbolTable;
import scanner.Token;

/**
 * The class that handles assignment of variables. Parsed from var ASSIGNOP
 * expression.
 * @author ken
 */
public class AssignmentStatementNode extends StatementNode
{
	/**
	 * Left variable to which the expression will be assigned.
	 */
	private VariableNode lvalue;

	/**
	 * The expression that is assigned in to the given variable.
	 */
	private ExpressionNode expression;

	/**
	 * Constructor.
	 */
	public AssignmentStatementNode()
	{
		this.lvalue = null;
		this.expression = null;
	}

	/**
	 * Constructor.
	 * @param lvalue
	 * @param expression 
	 */
	public AssignmentStatementNode(VariableNode lvalue, ExpressionNode expression)
	{
		this.lvalue = lvalue;
		this.expression = expression;
	}

    /**
     * Get the value being assigned to.
     * @return 
     */
	public VariableNode getLvalue()
	{
		return lvalue;
	}

    /**
     * Set the value being assigned to.
     * @param lvalue 
     */
	public void setLvalue(VariableNode lvalue)
	{
		this.lvalue = lvalue;
	}

    /**
     * Set the type.
     * @param type 
     */
	public void setType(Token type)
	{
		this.lvalue.setType(type);
	}

    /**
     * Get the type.
     * @return 
     */
	public Token getType()
	{
		return this.lvalue.getType();
	}

    /**
     * Get the expression.
     * @return 
     */
	public ExpressionNode getExpression()
	{
		return expression;
	}

    /**
     * Set the expression
     * @param expression 
     */
	public void setExpression(ExpressionNode expression)
	{
		this.expression = expression;
	}


    /**
     * Check whether the assignment is valid. Assignment is valid if
     * an integer is not attempted to assigned a real.
     * @param symbols
     * @return 
     */
	public boolean isValid(SymbolTable symbols)
	{
		if(symbols.getType(lvalue.getName()) == Token.INTEGER)
		{
			return !expression.isReal(symbols);
		}
		else
		{
			return true;
		}

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
		answer += "Assignment node: \n";
		answer += lvalue.indentedToString(level+1);
		answer += expression.indentedToString(level+1);
		return answer;
	}
}
