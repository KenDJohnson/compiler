/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syntaxtree;

/**
 *
 * @author ken
 */
public class ReadNode extends StatementNode
{

    /**
     * The variable to be written to by the read.
     */
	private VariableNode input;

    /**
     * Constructor.
     */
	public ReadNode()
	{
		input = new VariableNode();
	}

    /**
     * Get the variable to be stored in.
     * @return 
     */
	public VariableNode getInput()
	{
		return input;
	}

    /**
     * Set the variable to be stored in.
     * @param input 
     */
	public void setInput(VariableNode input)
	{
		this.input = input;
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

		answer += input.indentedToString(level+1);
		return answer;
	}
}
