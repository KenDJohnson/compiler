/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syntaxtree;

import java.util.ArrayList;

/**
 *
 * @author ken
 */
public class SubProgramDeclarationsNode extends SyntaxTreeNode
{
    /**
     * The list of functions and procedures.
     */
	ArrayList<SubProgramNode> procs;

    /**
     * Constructor.
     */
	public SubProgramDeclarationsNode()
	{
		procs = new ArrayList<SubProgramNode>();
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
		answer += "Functions/Procedures:\n";
		for(int i = 0; i < procs.size(); i++)
		{
			answer += procs.get(i).indentedToString(level+1);
		}
		return answer;
	}
}
