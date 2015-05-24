package syntaxtree;

/**
 *
 * Represents a node in the syntax tree.
 * @author ken
 */
public abstract class SyntaxTreeNode
{
	/**
	 * Creates a String representation of this node and its children.
	 * @param level The level at which this node resides.
	 * @return 
	 */
	public String indentedToString(int level)
	{
		String answer = "";
		if(level > 0)
		{
			answer = "|-- ";
		}
		for(int indent = 1; indent < level; indent++)
		{
				answer += "--- ";
		}
		return answer;
	}
	
}
