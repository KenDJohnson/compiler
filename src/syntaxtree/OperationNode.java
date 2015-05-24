package syntaxtree;

import parser.SymbolTable;
import scanner.Token;

/**
 *
 * @author ken
 */
public class OperationNode extends ExpressionNode
{
    /**
     * The operation of the node.
     */
	private Token operation;
    /**
     * Left hand side of the operation.
     */
	private ExpressionNode left;
    /**
     * Right hand side of the operation.
     */
	private ExpressionNode right;

    /**
     * Constructor.
     */
	public OperationNode()
	{
		left = null;
		right = null; 
	}
	
    /**
     * Constructor.
     * @param operation
     * @param left
     * @param right 
     */
	public OperationNode(Token operation, ExpressionNode left, ExpressionNode right)
	{
		this.operation = operation;
		this.left = left;
		this.right = right;
	}

    /***
     * Get the operation.
     * @return 
     */
	public Token getOperation()
	{
		return operation;
	}

    /**
     * Set the operation.
     * @param operation 
     */
	public void setOperation(Token operation)
	{
		this.operation = operation;
	}

    /**
     * Get the left node.
     * @return 
     */
	public ExpressionNode getLeft()
	{
		return left;
	}

    /**
     * Set the left node.
     * @param left 
     */
	public void setLeft(ExpressionNode left)
	{
		this.left = left;
	}

    /**
     * Add to the left side of the node. If the left node is null,
     * add it there, otherwise recursively call on that left node. 
     * @param left 
     */
	public void addToLeft(ExpressionNode left)
	{
		if(this.left != null)
		{
			try{
				((OperationNode) this.left).getLeft();
			} catch(java.lang.ClassCastException e)
			{
				System.out.println("Unable to add to the "
					+ "left recursively, left node is not"
					+ " an OperationNode");
				System.exit(-1);
			}

			((OperationNode)this.left).addToLeft(left);
		}
		else
		{
			setLeft(left);
		}
	}

    /**
     * Get the right node.
     * @return 
     */
	public ExpressionNode getRight()
	{
		return right;
	}

    /**
     * Set the right node.
     * @param right 
     */
	public void setRight(ExpressionNode right)
	{
		this.right = right;
	}

    /**
     * Check whether the statement is valid. Statement is valid
     * if a real is not being added or compared to an integer.
     * @param symbols
     * @return 
     */
	public boolean isValid(SymbolTable symbols)
	{
		return 	( right.isReal(symbols) &&  left.isReal(symbols)) ||
				(!right.isReal(symbols) && !left.isReal(symbols));
	}

    /**
     * Check whether the operation results in a real value.
     * @param symbols
     * @return 
     */
	@Override
	public boolean isReal(SymbolTable symbols)
	{
		return right.isReal(symbols) || left.isReal(symbols);
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
		answer += "Operation: " + operation + "\n"; 
		answer += left.indentedToString(level+1);
		answer += right.indentedToString(level+1);
		return answer;
	}


}
