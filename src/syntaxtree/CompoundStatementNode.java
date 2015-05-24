package syntaxtree;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A node representing the Compound Statement. Parsed in compoundStatement, 
 * and contains an ArrayList of statements. 
 * @author ken
 */
public class CompoundStatementNode extends StatementNode
{
	/**
	 * List of statements that making up the body of the CompoundStatement.
	 */
	ArrayList<StatementNode> statements;

    /**
     * Constructor.
     * @param statements 
     */
	public CompoundStatementNode(ArrayList<StatementNode> statements)
	{
		this.statements = statements;
	}

    /**
     * Constructor.
     */
	public CompoundStatementNode()
	{
		statements = new ArrayList<StatementNode>();
	}

    /**
     * Get the list of statements.
     * @return 
     */
	public ArrayList<StatementNode> getStatements()
	{
		return statements;
	}
	
    /**
     * Add a statement to the list of statements.
     * @param newStmt 
     */
	public void addStatement(StatementNode newStmt)
	{
		statements.add(newStmt);
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
		answer += "Statements:\n";
		for(int i = 0; i < statements.size(); i++)
		{
			answer += statements.get(i).indentedToString(level+1);
			answer += "\n";
		}
		return answer;
	}
}
