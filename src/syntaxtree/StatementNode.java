package syntaxtree;

/**
 *
 * @author ken
 */
public  abstract class StatementNode extends SyntaxTreeNode
{
	
    /**
     * Constructor.
     */
	public StatementNode()
	{
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
		return answer;
	}
}

