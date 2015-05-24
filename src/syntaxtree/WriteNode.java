package syntaxtree;

/**
 *
 * @author ken
 */
public class WriteNode extends StatementNode
{
    /**
     * The expression that will be written.
     */
	private ExpressionNode output;

    /**
     * Constructor.
     */
	public WriteNode()
	{
		output = null;
	}


    /**
     * Get the expression to be written.
     * @return 
     */
	public ExpressionNode getOutput()
	{
		return output;
	}

    /**
     * Set the expression to be written.
     * @param output 
     */
	public void setOutput(ExpressionNode output)
	{
		this.output = output;
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
		answer += "WriteNode:\n";
		answer += output.indentedToString(level+1);
		return answer;
	}
}
