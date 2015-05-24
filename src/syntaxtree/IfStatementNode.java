package syntaxtree;

import java.util.ArrayList;
import parser.SymbolTable;

/**
 * IfStatementNode, representing an if statement in the grammar. The node 
 * consists of an ExpressionNode, which is the condition for the if statement,
 * and a StatementNode which is the statement in the body.
 * @author ken
 */
public class IfStatementNode extends StatementNode 
{
	/** The conditions for the if statement. */
	private ExpressionNode condition;
	
	/** The statement that is executed if condition is true. */
	private StatementNode statement;

	/** The statement that is executed if condition is false. */
	private StatementNode elseStatement;

    /**
     * Constructor.
     */
	public IfStatementNode()
	{
		condition     = null;
		statement     = null;
		elseStatement = null;
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
     * Get the true body.
     * @return 
     */
	public StatementNode getStatement()
	{
		return statement;
	}

    /**
     * Set the true body.
     * @param statement 
     */
	public void setStatement(StatementNode statement)
	{
		this.statement = statement;
	}

    /**
     * Get the false body.
     * @return 
     */
	public StatementNode getElseStatement()
	{
		return elseStatement;
	}

    /**
     * Set the false body.
     * @param elseStatement 
     */
	public void setElseStatement(StatementNode elseStatement)
	{
		this.elseStatement = elseStatement;
	}
	

    /**
     * Check whether the condition is valid.
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
		String answer = super.indentedToString(level);

		answer  += "If Statement:\n";
		answer  += super.indentedToString(level);
		answer 	+= "Conditions:\n";
		answer  += condition.indentedToString(level+1);
		answer  += super.indentedToString(level);
		answer 	+= "Statements:\n";
		answer  += statement.indentedToString(level+1);
		answer  += super.indentedToString(level);
		answer  += "ElseStatements:\n";
		answer  += elseStatement.indentedToString(level+1);
		return     answer;
	}
}

