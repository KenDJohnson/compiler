package syntaxtree;

import syntaxtree.DeclarationsNode;

/**
 * A node representing the Program node of the pascal program.
 * @author ken
 */
public class ProgramNode extends SyntaxTreeNode
{
	/** The name of the program. */
	private String name;	

	/** The variables in the program. */
	private DeclarationsNode variables;

	/** The main function of the program. I.e. the main compound statement in 
	 * the program.
	 */
	private CompoundStatementNode main;

	/** The functions in the program. */
	private SubProgramDeclarationsNode functions;

    /**
     * Constructor.
     */
	public ProgramNode()
	{
		this.name = "";
		this.variables 	= new DeclarationsNode();
		this.functions 	= new SubProgramDeclarationsNode();
		this.main 	= new CompoundStatementNode();
	}


	
    /**
     * Constructor.
     * @param name
     * @param variables
     * @param main
     * @param functions 
     */
	public ProgramNode(String name, DeclarationsNode variables, CompoundStatementNode main, SubProgramDeclarationsNode functions)

	{
		this.name = name;
		this.variables = variables;
		this.main = main;
		this.functions = functions;
	}

    /**
     * Get the name of the program.
     * @return 
     */
	public String getName()
	{
		return name;
	}

    /**
     * Set the name of the program.
     * @param name 
     */
	public void setName(String name)
	{
		this.name = name;
	}

    /**
     * Get the declared variables.
     * @return 
     */
	public DeclarationsNode getVariables()
	{
		return variables;
	}

    /***
     * Set the declared variables.
     * @param variables 
     */
	public void setVariables(DeclarationsNode variables)
	{
		this.variables = variables;
	}

    /**
     * Get the main compound statement.
     * @return 
     */
	public CompoundStatementNode getMain()
	{
		return main;
	}

    /**
     * Set the main compound statement.
     * @param main 
     */
	public void setMain(CompoundStatementNode main)
	{
		this.main = main;
	}

    /**
     * Get the function and procedure declarations.
     *  Never used.
     * @return 
     */
	public SubProgramDeclarationsNode getFunctions()
	{
		return functions;
	}

    /**
     * Set the function and procedure declarations.
     * Never used.
     * @param functions 
     */
	public void setFunctions(SubProgramDeclarationsNode functions)
	{
		this.functions = functions;
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
		answer += "Name: " + this.name + "\n";
		answer += "Declarations:\n";
		answer += variables.indentedToString(level+1);
		answer += "Main:\n";
		answer += main.indentedToString(level+1);
		answer += "Functions:\n";
		answer += functions.indentedToString(level+1);
		return answer;
	}
}
