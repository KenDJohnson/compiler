package syntaxtree;

import java.util.ArrayList;
import scanner.Token;

/**
 *
 * @author ken
 */
public class SubProgramNode extends SyntaxTreeNode
{
    /**
     * Arguments to the function.
     */
    private ArrayList<Token> args;

    /**
     * Variables in the funciton.
     */
    private DeclarationsNode declarations;

    /**
     * Funcitons in the function.
     */
    private SubProgramDeclarationsNode subDeclarations;
    
    /**
     * Body of the function.
     */
    private CompoundStatementNode body;

    /**
     * Constructor.
     */
    public SubProgramNode()
    {
    }

    /**
     * Get the arguments to the function.
     * @return 
     */
    public ArrayList<Token> getArgs()
    {
        return args;
    }

    /**
     * Set the arguments to the function.
     * @param args 
     */
    public void setArgs(ArrayList<Token> args)
    {
        this.args = args;
    }

    /**
     * 
     * @return 
     */
    public DeclarationsNode getDeclarations()
    {
        return declarations;
    }

    /**
     * 
     * @param declarations 
     */
    public void setDeclarations(DeclarationsNode declarations)
    {
        this.declarations = declarations;
    }

   /**
    * Get the functions.
    * @return 
    */
    public SubProgramDeclarationsNode getSubDeclarations()
    {
        return subDeclarations;
    }

    /**
     * Set the functions.
     * @param subDeclarations 
     */
    public void setSubDeclarations(SubProgramDeclarationsNode subDeclarations)
    {
        this.subDeclarations = subDeclarations;
    }

    /**
     * Get the body.
     * @return 
     */
    public CompoundStatementNode getBody()
    {
        return body;
    }

    /**
     * Set the body of the func/proc.
     * @param body 
     */
    public void setBody(CompoundStatementNode body)
    {
        this.body = body;
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
