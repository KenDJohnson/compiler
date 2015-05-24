package syntaxtree;

import java.util.ArrayList;
import parser.SymbolTable;

/**
 *
 * @author ken
 */
public class WhileStatementNode extends StatementNode
{
	
    /**
     * The terminal condition for the loop.
     */
	private ExpressionNode condition;
	
    /**
     * The body of the loop.
     */
	private StatementNode statement;

    /**
     * Constructor.
     */
	public WhileStatementNode()
	{
		condition = null;
		statement = null;
	}

    /**
     * Get the condition.
     * @return 
     */
	public ExpressionNode getCondition()
	{
		return condition;
	}

    /**
     * Set the condition.
     * @param condition 
     */
	public void setCondition(ExpressionNode condition)
	{
		this.condition = condition;
	}

    /**
     * Get the statement.
     * @return 
     */
	public StatementNode getStatement()
	{
		return statement;
	}

    /**
     * Set the statement.
     * @param statement 
     */
	public void setStatement(StatementNode statement)
	{
		this.statement = statement;
	}


    /**
     * Check whether the statement is valid.
     * @param symbols
     * @return 
     */
	public boolean isValid(SymbolTable symbols)
	{
		if(condition instanceof OperationNode)
		{
			return ((OperationNode) condition).isValid(symbols);
		}
		return true;
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
		String answer 	=  super.indentedToString(level);
		answer 		+= "While Statement:\n";
		answer 		+=  super.indentedToString(level);
		answer 		+= "Conditions:\n";
		answer	   	+= condition.indentedToString(level+1);
		answer 		+=  super.indentedToString(level);
		answer 		+= "Statements:\n";
		answer 		+= statement.indentedToString(level+1);
		return answer;
	}

}