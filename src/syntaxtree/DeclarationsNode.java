package syntaxtree;

import java.util.ArrayList;

/**
 *
 * @author ken
 */
public class DeclarationsNode extends SyntaxTreeNode
{
	/** ArrayList of the variables. */
	private ArrayList<VariableNode>  vars;

    /**
     * Constructor.
     */
	public DeclarationsNode()
	{
		vars = new ArrayList<VariableNode>();
	}

    /**
     * Constructor.
     * @param vars 
     */
	public DeclarationsNode(ArrayList<VariableNode> vars)
	{
		this.vars = vars;
	}

    /**
     * Get the list of variables.
     * @return 
     */
	public ArrayList<VariableNode> getVars()
	{
		return vars;
	}
	
    /**
     * Add a variable to the list.
     * @param var 
     */
	public void addVar(VariableNode var)
	{
		vars.add(var);
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
		answer += "Variables:\n";
		for(int i = 0; i < vars.size(); i++)
		{
			answer += vars.get(i).indentedToString(level+1);
		}		
		return answer;
	}
	
}
