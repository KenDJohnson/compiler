package codegenerator;

import java.util.ArrayList;
import parser.Parser;
import parser.SymbolTable;
import scanner.Token;
import syntaxtree.*;

/**
 *
 * @author ken
 */
public class Generator
{

    /**
     * The root of the syntax tree. Used to walk the rest of the
     * tree and generate code.
     */
	private ProgramNode program;

    /**
     * The global StringBuilder that holds the generated code.
     */
	private StringBuilder asm;

    /**
     * The parser used to build the syntax tree.
     */
	private Parser parser;

    /**
     * Number of if branches needed.
     */
	private int numIf;
    

    /**
     * Number of while branches needed.
     */
	private int numWhile;

    /**
     * Number of branches needed.
     */
	private int numBranch;

    /**
     * A flag used to check if the code has been generated yet.
     */
	private boolean generated;

    /**
     * The symbols. This is used to do some type checking.
     */
	private SymbolTable symbols;

    /**
     * Constructor.
     * @param filename 
     */
	public Generator(String filename)
	{
		asm = new StringBuilder();
		parser = new Parser(filename);
		program = parser.program();
		symbols = parser.getSymbolTable();
		generated = false;
		numIf = 0;
		numWhile = 0;
		numBranch = 0;
	}

    /**
     * Gets the string representation of the tree. If the file
     * has not yet been parsed, returns a string stating that.
     * @return 
     */
	public String getTree()
	{
		if (generated)
		{
			return program.indentedToString(0);
		}
		return "File has not yet been parsed.";
	}

    /**
     * Get the name of the program.
     * @return 
     */
	public String name()
	{
		return program.getName();
	}

    /**
     * Generate the code. Calls data, and text to generate the respective 
     * sections of MIPS.
     * @return 
     */
	public String generate()
	{
		asm.append(data());
		asm.append(text());
		generated = true;
		return asm.toString();
	}

    /**
     * Generate the .text portion of the assembly code.
     * @return 
     */
	private String text()
	{
		StringBuilder dottext = new StringBuilder();
		dottext.append("\n.text\n");
		dottext.append(main());
		return dottext.toString();
	}

    /**
     * Generate the main compound statement code. Save $ra register,
     * evaluate the compound statement, then reload $ra and jr to it.
     * @return 
     */
	private String main()
	{
		StringBuilder mainSection = new StringBuilder();
		mainSection.append("main:\n ");
		mainSection.append("addi $sp, $sp, -4\n sw $ra, 0($sp)\n ");

		compoundStatement(program.getMain(), mainSection);

		mainSection.append("lw $ra, 0($sp)\n addi $sp, $sp, 4\n jr $ra\n ");

		return mainSection.toString();
	}

    /**
     * Generate the code for an if statement. Checks whether the condition is real or not,
     * then generates the code for evaluating the condition. The true code is
     * then written, then a label to false. Next is the false code, and an 
     * end label.
     * @param currentNode
     * @param assembly 
     */
	private void ifStatement(IfStatementNode currentNode, StringBuilder assembly)
	{

		ExpressionNode cond = currentNode.getCondition();
		StatementNode trueStmt = currentNode.getStatement();
		StatementNode falseStmt = currentNode.getElseStatement();

		if (cond.isReal(symbols))
		{
			if (cond instanceof syntaxtree.VariableNode)
			{
				assembly.append("lwc1 $f0\n mtc1 $zero, $f1\n ");
				assembly.append("c.eq.s $f0, $f1\n bclt if").append(numIf);
				assembly.append("false\n ");
			}
			else if (cond instanceof syntaxtree.ValueNode)
			{
				assembly.append("li.s $f0, ").append(((ValueNode) cond).getAttribute());
				assembly.append("\n mtc1 $zero, $f1\n ");
				assembly.append("c.eq.s $f0, $f1\n bclt if").append(numIf);
				assembly.append("false\n ");
			}
			else if (cond instanceof syntaxtree.OperationNode)
			{
				OperationNode opCond = (OperationNode) cond;
				left(opCond.getLeft(), 0, assembly, true);
				right(opCond.getRight(), 1, assembly, true);
				if (opCond.getOperation() == Token.LESS_THAN)
				{
					assembly.append("c.lt.s $f").append(0).append(", $f");
					assembly.append(1).append("\n bc1f if");
				}
				else if (opCond.getOperation() == Token.LESS_THAN_EQUAL)
				{
					assembly.append("c.le.s $f").append(0).append(", $f");
					assembly.append(1).append("\n bc1f if");
				}
				else if (opCond.getOperation() == Token.GREATER_THAN)
				{
					assembly.append("c.lt.s $f").append(1).append(", $f");
					assembly.append(0).append("\n bc1f if");

				}
				else if (opCond.getOperation() == Token.GREATER_THAN_EQUAL)
				{
					assembly.append("c.le.s $f").append(1).append(", $f");
					assembly.append(0).append("\n bc1f if");
				}
				else if (opCond.getOperation() == Token.LESS_THAN_GREATER_THAN)
				{
					assembly.append("c.eq.s $f").append(0).append(", $f");
					assembly.append(1).append("\n bc1t if");
				}
                else if (opCond.getOperation() == Token.EQUALS)
				{
					assembly.append("c.eq.s $f").append(0).append(", $f");
					assembly.append(1).append("\n bc1f if");
				}
			}
		}

		else
		{

			if (cond instanceof syntaxtree.VariableNode
					|| cond instanceof syntaxtree.ValueNode)
			{
				if (cond instanceof syntaxtree.VariableNode)
				{
					assembly.append("lw $t0, ");
					assembly.append(((VariableNode) cond).getName());
				}
				else
				{
					assembly.append("li $t0, ");
					assembly.append(((ValueNode) cond).getAttribute());
				}
				if (cond.isNot())
				{
					assembly.append("\n bne $t0, $zero, if");
				}
				else
				{
					assembly.append("\n beq $t0, $zero, if");
				}
			}
			if (cond instanceof syntaxtree.OperationNode)
			{
				evalOp((OperationNode) cond, 0, assembly, false);
				if (((OperationNode) cond).getLeft().isNot())
				{
					assembly.append("bne $t0, $zero, if");
				}
				else
				{
					assembly.append("beq $t0, $zero, if");
				}

			}

		}
		assembly.append(numIf).append("false\n ");
		if (trueStmt instanceof syntaxtree.AssignmentStatementNode)
		{
			assignment((AssignmentStatementNode) trueStmt, assembly);

		}
		else if (trueStmt instanceof syntaxtree.CompoundStatementNode)
		{
			compoundStatement(trueStmt, assembly);
		}
		else if (trueStmt instanceof syntaxtree.WriteNode)
		{
			write((WriteNode) trueStmt, assembly);
		}
		else if (trueStmt instanceof syntaxtree.ReadNode)
		{
			read((ReadNode) trueStmt, assembly);
		}
		else if (trueStmt instanceof syntaxtree.IfStatementNode)
		{
			ifStatement((IfStatementNode) trueStmt, assembly);
		}
		else if (trueStmt instanceof syntaxtree.WhileStatementNode)
		{
			whileStatement((WhileStatementNode) trueStmt, assembly);
		}
		assembly.append("j if");
		assembly.append(numIf);
		assembly.append("end\n ");
		assembly.append("if");
		assembly.append(numIf);
		assembly.append("false:\n ");
		if (falseStmt instanceof syntaxtree.AssignmentStatementNode)
		{
			assignment((AssignmentStatementNode) falseStmt, assembly);
		}
		else if (falseStmt instanceof syntaxtree.CompoundStatementNode)
		{
			compoundStatement(falseStmt, assembly);
		}
		else if (falseStmt instanceof syntaxtree.WriteNode)
		{
			write((WriteNode) falseStmt, assembly);
		}
		else if (falseStmt instanceof syntaxtree.ReadNode)
		{
			read((ReadNode) falseStmt, assembly);
		}
		else if (falseStmt instanceof syntaxtree.IfStatementNode)
		{
			ifStatement((IfStatementNode) falseStmt, assembly);
		}
		else if (falseStmt instanceof syntaxtree.WhileStatementNode)
		{
			whileStatement((WhileStatementNode) falseStmt, assembly);
		}
		assembly.append("if");
		assembly.append(numIf++);
		assembly.append("end:\n ");
	}

    /**
     * Generates code for a while statement. Checks whether the condition is real or not.
     * Next generates the code for evaluating the condition, and a jump to the
     * end if it is false. Then the body of the loop is generated, followed by a
     * jump to the beginning, and a label for the end.
     * @param statement
     * @param assembly 
     */
	private void whileStatement(WhileStatementNode statement, StringBuilder assembly)
	{
		ExpressionNode cond = statement.getCondition();
		StatementNode body = statement.getStatement();

		if (cond.isReal(symbols))
		{
			if (cond instanceof syntaxtree.VariableNode)
			{
				assembly.append("lwc1 $f0\n mtc1 $zero, $f1\n ");
				assembly.append("c.eq.s $f0, $f1\n bclt if").append(numIf);
				assembly.append("false\n ");
			}
			else if (cond instanceof syntaxtree.ValueNode)
			{
				assembly.append("li.s $f0, ").append(((ValueNode) cond).getAttribute());
				assembly.append("\n mtc1 $zero, $f1\n ");
				assembly.append("c.eq.s $f0, $f1\n bclt if").append(numIf);
				assembly.append("false\n ");
			}
			else if (cond instanceof syntaxtree.OperationNode)
			{
				OperationNode opCond = (OperationNode) cond;
				left(opCond.getLeft(), 0, assembly, true);
				right(opCond.getRight(), 1, assembly, true);
				if (opCond.getOperation() == Token.LESS_THAN)
				{
					assembly.append("c.lt.s $f").append(0).append(", $f");
					assembly.append(1).append("\n bc1f if");
				}
				else if (opCond.getOperation() == Token.LESS_THAN_EQUAL)
				{
					assembly.append("c.le.s $f").append(0).append(", $f");
					assembly.append(1).append("\n bc1f if");
				}
				else if (opCond.getOperation() == Token.GREATER_THAN)
				{
					assembly.append("c.lt.s $f").append(1).append(", $f");
					assembly.append(0).append("\n bc1f if");

				}
				else if (opCond.getOperation() == Token.GREATER_THAN_EQUAL)
				{
					assembly.append("c.le.s $f").append(1).append(", $f");
					assembly.append(0).append("\n bc1f if");
				}
				else if (opCond.getOperation() == Token.LESS_THAN_GREATER_THAN)
				{
					assembly.append("c.eq.s $f").append(0).append(", $f");
					assembly.append(1).append("\n bc1f if");
				}
				else
				{
					evalOp(opCond, 0, assembly, true);
					assembly.append("mtc1 $zero, $f1\n bclt if");
				}
			}
		}

		else
		{
			if (cond instanceof syntaxtree.ValueNode)
			{
				assembly.append("li $t7, ");
				assembly.append(((ValueNode) cond).getAttribute());
				assembly.append("\n while");
				assembly.append(numWhile);
				assembly.append("begin:\n ");
			}
			else if (cond instanceof syntaxtree.VariableNode)
			{
				assembly.append("while");
				assembly.append(numWhile);
				assembly.append("begin:\n ");
				assembly.append("lw, $t7, ");
				assembly.append(((VariableNode) cond).getName());
			}
			else if (cond instanceof syntaxtree.OperationNode)
			{
				assembly.append("while");
				assembly.append(numWhile);
				assembly.append("begin:\n ");
				evalOp((OperationNode) cond, 7, assembly, false);
			}

			if (cond.isNot())
			{
				assembly.append("bne $t7, $zero, while");
			}
			else
			{
				assembly.append("beq $t7, $zero, while");
			}
			assembly.append(numWhile);
			assembly.append("end\n ");

			if (body instanceof syntaxtree.AssignmentStatementNode)
			{
				assignment((AssignmentStatementNode) body, assembly);
			}
			else if (body instanceof syntaxtree.CompoundStatementNode)
			{
				compoundStatement((CompoundStatementNode) body, assembly);
			}
			else if (body instanceof syntaxtree.IfStatementNode)
			{
				ifStatement((IfStatementNode) body, assembly);
			}
			else if (body instanceof syntaxtree.WriteNode)
			{
				write((WriteNode) body, assembly);
			}
			else if (body instanceof syntaxtree.ReadNode)
			{
				read((ReadNode) body, assembly);
			}
			else if (body instanceof syntaxtree.WhileStatementNode)
			{
				whileStatement((WhileStatementNode) body, assembly);
			}
		}
		assembly.append("j while");
		assembly.append(numWhile);
		assembly.append("begin\n while");
		assembly.append(numWhile);
		assembly.append("end:\n ");
		numWhile++;
	}

    /**
     * Generates code for a compound statement. Iterates through each statement and
     * calls a function to generate the corresponding type of code.
     * @param trueStmt
     * @param assembly 
     */
	private void compoundStatement(StatementNode trueStmt, StringBuilder assembly)
	{
		ArrayList<StatementNode> statements = ((CompoundStatementNode) trueStmt).getStatements();
		for (StatementNode current : statements)
		{
			if (current instanceof syntaxtree.AssignmentStatementNode)
			{
				assignment((AssignmentStatementNode) current, assembly);
			}
			else if (current instanceof syntaxtree.ReadNode)
			{
				read((ReadNode) current, assembly);
			}
			else if (current instanceof syntaxtree.WriteNode)
			{
				write((WriteNode) current, assembly);
			}
			else if (current instanceof syntaxtree.IfStatementNode)
			{
				ifStatement((IfStatementNode) current, assembly);
			}
			else if (current instanceof syntaxtree.WhileStatementNode)
			{
				whileStatement((WhileStatementNode) current, assembly);
			}
		}
	}

    /**
     * Generates a write node. Determines the type of expression to be written
     * and loads it into the corresponding register, then gives the corresponding
     * code to the $v0 register, then syscalls.
     * @param currentNode
     * @param assembly 
     */
	private void write(WriteNode currentNode, StringBuilder assembly)
	{
		ExpressionNode out = currentNode.getOutput();
		if (out instanceof syntaxtree.ValueNode)
		{
			assembly.append("li $a0, ");
			assembly.append(((ValueNode) out).getAttribute());
			assembly.append("\n li $v0, 1\n syscall\n ");
		}
		else if (out instanceof syntaxtree.VariableNode)
		{
			if (((VariableNode) out).getType() == Token.INTEGER)
			{
				assembly.append("lw $a0, ");
				assembly.append(((VariableNode) out).getName());
				assembly.append("\n li $v0, 1\n syscall\n ");
			}
			else if (((VariableNode) out).getType() == Token.REAL)
			{
				assembly.append("lwc1 $f12, ");
				assembly.append(((VariableNode) out).getName());
				assembly.append("\n li $v0, 2\n syscall\n ");
			}
		}
		else if (out instanceof syntaxtree.OperationNode)
		{
			evalOp((OperationNode) out, 0, assembly, false);
			assembly.append("addi $a0, $t0, 0\n li $v0, 1\n syscall\n ");
		}
		assembly.append("li $v0, 4\n la $a0, newline\n syscall\n ");
	}

    /**
     * Generates code for an assignment. Calls right to generate the code
     * for the right hand of the assignment, then checks what kind of data is
     * stored and generates the corresponding store instruction.
     * @param currentNode
     * @param assembly 
     */
	private void assignment(AssignmentStatementNode currentNode, StringBuilder assembly)
	{
		ExpressionNode right = currentNode.getExpression();
		if(right.isReal(symbols))
		{
			right(right, 0, assembly, true);
			assembly.append("swc1 $f0, ").append(currentNode.getLvalue().getName());
		}
		else
		{
			right(right, 0, assembly, false);
			assembly.append("sw $t0, ").append(currentNode.getLvalue().getName());
		}
		assembly.append("\n ");


	}

    /**
     * Generates the right hand side of an expression. Checks what kind of
     * expression it is, then calls the corresponding function to generate
     * the code.
     * @param exp
     * @param reg
     * @param assembly
     * @param real 
     */
	private void right(ExpressionNode exp, int reg, StringBuilder assembly, boolean real)
	{
		if (exp instanceof ValueNode)
		{
			value((ValueNode) exp, reg, assembly, real);
		}
		else if (exp instanceof VariableNode)
		{
			variable((VariableNode) exp, reg, assembly, real);
		}
		else if (exp instanceof OperationNode)
		{
			evalOp((OperationNode) exp, reg, assembly, real);
		}
	}

    /**
     * Generates the left hand side of an expression. Checks what kind of
     * expression it is, then calls the corresponding function to generate
     * the code.
     * @param exp
     * @param reg
     * @param assembly
     * @param real 
     */
	private void left(ExpressionNode exp, int reg, StringBuilder assembly, boolean real)
	{
		if (exp instanceof ValueNode)
		{
			value((ValueNode) exp, reg, assembly, real);
		}
		else if (exp instanceof VariableNode)
		{
			variable((VariableNode) exp, reg, assembly, real);
		}
		else if (exp instanceof OperationNode)
		{
			evalOp((OperationNode) exp, reg, assembly, real);
		}
	}

    /**
     * Generates the code for a value node. Checks type and sign to ensure
     * proper instruction and value.
     * @param val
     * @param reg
     * @param assembly
     * @param real 
     */
	private void value(ValueNode val, int reg, StringBuilder assembly, boolean real)
	{
		if (!real)
		{
			assembly.append("li $t").append(reg).append(", ");
            if(val.getSign() == Token.MINUS)
            {
                assembly.append("-");
            }
			assembly.append(val.getAttribute()).append("\n ");
		}
		else
		{
			assembly.append("li.s $f").append(reg).append(", ");
		    if(val.getSign() == Token.MINUS)
            {
                assembly.append("-");
            }	
            assembly.append(val.getAttribute());
			if(!val.isReal(symbols))
			{
				assembly.append(".0");
			}
			assembly.append("\n ");
		}
	}

    /**
     * Generates the code for loading a variable. Checks for the type of variable,
     * and the sign of the variable. Uses the not+1 trick to convert sign for 
     * integers, and the neg instruction for reals.
     * @param var
     * @param reg
     * @param assembly
     * @param real 
     */
	private void variable(VariableNode var, int reg, StringBuilder assembly, boolean real)
	{
		if (!real)
		{
			assembly.append("lw $t").append(reg).append(", ");
		    assembly.append(var.getName()).append("\n ");
            if(var.getSign() == Token.MINUS)
            {
                assembly.append("not $t").append(reg).append(", $t").append(reg);
                assembly.append("\n addi $t").append(reg).append(", $t");
                assembly.append(reg).append(", 1\n ");
            }   
		}
		else 
		{
			assembly.append("lwc1 $f").append(reg).append(", ");
    		assembly.append(var.getName()).append("\n ");
            if(var.getSign() == Token.MINUS)
            {
                assembly.append("neg $f").append(reg).append(", $f").append(reg);
                assembly.append("\n ");
            }   
		}
	}

    /**
     * Generates the last part of an operation. Given the operation, a result
     * register, and the register numbers of two operands, this method performs
     * the operation.
     * @param operation
     * @param result
     * @param operand1
     * @param operand2
     * @param assembly 
     */
	private void writeOp(Token operation, int result, int operand1, int operand2, StringBuilder assembly)
	{

		if (operation == Token.MULTIPLY)
		{
			assembly.append("mult $t").append(operand1).append(", $t");
			assembly.append(operand2).append("\n mflo $t").append(result);
		}
		else if (operation == Token.DIVIDE)
		{
			assembly.append("div $t").append(operand1).append(", $t");
			assembly.append(operand2).append("\n mflo $t").append(result);
		}
		else if (operation == Token.PLUS)
		{
			assembly.append("add $t").append(result).append(", $t");
			assembly.append(operand1).append(", $t").append(operand2);
		}
		else if (operation == Token.MINUS)
		{
			assembly.append("sub $t").append(result).append(", $t");
			assembly.append(operand1).append(", $t").append(operand2);
		}
		else if (operation == Token.LESS_THAN)
		{
			assembly.append("slt $t").append(result).append(", $t");
			assembly.append(operand1).append(", $t").append(operand2);
		}
		else if (operation == Token.LESS_THAN_EQUAL)
		{
            assembly.append("sle $t").append(result).append(", $t");
            assembly.append(operand1).append(", $t").append(operand2);
		}
		else if (operation == Token.GREATER_THAN)
		{
			assembly.append("slt $t").append(result).append(", $t");
			assembly.append(operand2).append(", $t").append(operand1);
		}
		else if (operation == Token.GREATER_THAN_EQUAL)
		{
            assembly.append("sge $t").append(result).append(", $t");
            assembly.append(operand1).append(", $t").append(operand2);
		}
		else if (operation == Token.LESS_THAN_GREATER_THAN)
		{
            assembly.append("sne $t").append(result).append(", $t");
            assembly.append(operand1).append(", $t").append(operand2);
		}
        else if(operation == Token.EQUALS)
        {
            assembly.append("seq $t").append(result).append(", $t");
            assembly.append(operand1).append(", $t").append(operand2);
        }
		assembly.append("\n ");
	}

    /**
     * Generates the last part of a real operation. Given the operation, a result
     * register, and the register numbers of two operands, this method performs
     * the operation.
     * @param op
     * @param result
     * @param operand1
     * @param operand2
     * @param assembly 
     */
	private void writeRealOp(OperationNode op, int result, int operand1, int operand2, StringBuilder assembly)
	{
		Token operation = op.getOperation();
/*
		if (!op.getLeft().isReal(symbols))
		{
			assembly.append("mtc1 $t").append(operand1).append(", $f");
			assembly.append(operand1).append("\n ");
		}

		if (!op.getRight().isReal(symbols))
		{
			assembly.append("mtc1 $t").append(operand2).append(", $f");
			assembly.append(operand2).append("\n ");
		}
		*/

		if (operation == Token.MULTIPLY)
		{
			assembly.append("mul.s $f").append(operand1).append(", $f");
			assembly.append(operand1).append(", $f");
			assembly.append(operand2);
		}
		else if (operation == Token.DIVIDE)
		{
			assembly.append("div.s $f").append(operand1).append(", $f");
			assembly.append(operand1).append(", $f");
			assembly.append(operand2);
		}
		else if (operation == Token.PLUS)
		{
			assembly.append("add.s $f").append(result).append(", $f");
			assembly.append(operand1).append(", $f").append(operand2);
		}
		else if (operation == Token.MINUS)
		{
			assembly.append("sub.s $f").append(result).append(", $f");
			assembly.append(operand1).append(", $f").append(operand2);
		}
		else if (operation == Token.LESS_THAN)
		{
            assembly.append("mct1 $zero, $f16\n ");
            assembly.append("addi $t9, $zero, 1\n mct1 $t9, $f15\n ");
			assembly.append("c.lt.s $f").append(operand1).append(", $f");
			assembly.append(operand2).append("\n bc1f branch").append(numBranch);
            assembly.append("\n add.s  $f").append(result).append(", $f16, $f15");
            assembly.append("\n j branch").append(numBranch).append("end\n ");
            assembly.append("branch").append(numBranch).append(":\n ");
            assembly.append("mct1 $zero, $f").append(result).append("\n ");
            assembly.append("branch").append(numBranch++).append(":");

		}
		else if (operation == Token.LESS_THAN_EQUAL)
		{
            assembly.append("mct1 $zero, $f16\n ");
            assembly.append("addi $t9, $zero, 1\n mct1 $t9, $f15\n ");
			assembly.append("c.le.s $f").append(operand1).append(", $f");
			assembly.append(operand2).append("\n bc1f branch").append(numBranch);
            assembly.append("\n add.s  $f").append(result).append(", $f16, $f15");
            assembly.append("\n j branch").append(numBranch).append("end\n ");
            assembly.append("branch").append(numBranch).append(":\n ");
            assembly.append("mct1 $zero, $f").append(result).append("\n ");
            assembly.append("branch").append(numBranch++).append(":");
		}
		else if (operation == Token.GREATER_THAN)
		{
            assembly.append("mct1 $zero, $f16\n ");
            assembly.append("addi $t9, $zero, 1\n mct1 $t9, $f15\n ");
			assembly.append("c.lt.s $f").append(operand2).append(", $f");
			assembly.append(operand1).append("\n bc1f branch").append(numBranch);
            assembly.append("\n add.s  $f").append(result).append(", $f16, $f15");
            assembly.append("\n j branch").append(numBranch).append("end\n ");
            assembly.append("branch").append(numBranch).append(":\n ");
            assembly.append("mct1 $zero, $f").append(result).append("\n ");
            assembly.append("branch").append(numBranch++).append(":");
		}
		else if (operation == Token.GREATER_THAN_EQUAL)
		{
            assembly.append("mct1 $zero, $f16\n ");
            assembly.append("addi $t9, $zero, 1\n mct1 $t9, $f15\n ");
			assembly.append("c.le.s $f").append(operand2).append(", $f");
			assembly.append(operand1).append("\n bc1f branch").append(numBranch);
            assembly.append("\n add.s  $f").append(result).append(", $f16, $f15");
            assembly.append("\n j branch").append(numBranch).append("end\n ");
            assembly.append("branch").append(numBranch).append(":\n ");
            assembly.append("mct1 $zero, $f").append(result).append("\n ");
            assembly.append("branch").append(numBranch++).append(":");
		}
		else if (operation == Token.LESS_THAN_GREATER_THAN)
		{
            assembly.append("mct1 $zero, $f16\n ");
            assembly.append("addi $t9, $zero, 1\n mct1 $t9, $f15\n ");
			assembly.append("c.eq.s $f").append(operand1).append(", $f");
			assembly.append(operand2).append("\n bc1t branch").append(numBranch);
            assembly.append("\n add.s  $f").append(result).append(", $f16, $f15");
            assembly.append("\n j branch").append(numBranch).append("end\n ");
            assembly.append("branch").append(numBranch).append(":\n ");
            assembly.append("mct1 $zero, $f").append(result).append("\n ");
            assembly.append("branch").append(numBranch++).append(":");
		}
        else if(operation == Token.EQUALS)
        {
            assembly.append("mct1 $zero, $f16\n ");
            assembly.append("addi $t9, $zero, 1\n mct1 $t9, $f15\n ");
			assembly.append("c.eq.s $f").append(operand1).append(", $f");
			assembly.append(operand2).append("\n bc1f branch").append(numBranch);
            assembly.append("\n add.s  $f").append(result).append(", $f16, $f15");
            assembly.append("\n j branch").append(numBranch).append("end\n ");
            assembly.append("branch").append(numBranch).append(":\n ");
            assembly.append("mct1 $zero, $f").append(result).append("\n ");
            assembly.append("branch").append(numBranch++).append(":");
        }
		assembly.append("\n ");
	}

	/**
	 * Evaluates an operation, and leaves the result in $t0. Currently only
	 * supports one level of depth, but this needs to be increased.
	 *
	 * @param op
	 * @param reg
	 * @return
	 */
	private void evalOp(OperationNode op, int reg, StringBuilder operation, boolean real)
	{
		left(op.getLeft(), reg, operation, real);
		right(op.getRight(), reg + 1, operation, real);
		if (op.isReal(symbols) || real)
		{
			writeRealOp(op, reg, reg, reg + 1, operation);
		}
		else
		{
			writeOp(op.getOperation(), reg, reg, reg + 1, operation);
		}
	}

    /**
     * Generate the .data section. These are the variable declarations,
     * everything is default set to 0. There is also a newline, used for
     * write.
     * @return 
     */
	private String data()
	{
		StringBuilder dotdata = new StringBuilder();
		ArrayList<VariableNode> vars = program.getVariables().getVars();
		dotdata.append(".data\n");
		for (VariableNode var : vars)
		{
			dotdata.append(var.getName());
			dotdata.append(": ");
			if (var.getType() == Token.INTEGER)
			{
				dotdata.append(".word 0");
			}
			else if (var.getType() == Token.REAL)
			{
				dotdata.append(".float 0.0");
			}
			dotdata.append("\n");
		}
		dotdata.append("newline: .asciiz \"\\n\"\n");
		return dotdata.toString();
	}

    /**
     * Generates the code for a read statement. Determines what data type
     * is being read, and performs the corresponding syscall, then copies the
     * result to the given variable.
     * @param readNode
     * @param assembly 
     */
	private void read(ReadNode readNode, StringBuilder assembly)
	{
		if (readNode.getInput().isReal(symbols))
		{
			assembly.append("addi $v0, $zero, 6\n syscall\n swcl $f0, ");
			assembly.append(readNode.getInput().getName()).append("\n ");
			return;
		}
		assembly.append("addi $v0, $zero, 5\n syscall\n addi $t0, $a0, 0\n ");
		assembly.append("sw $t0, ").append(readNode.getInput());
	}

}
