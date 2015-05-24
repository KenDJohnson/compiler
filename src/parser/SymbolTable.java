/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import scanner.*;

/**
 * The Table containing all the symbols contained in the program, categorized by
 * scope.
 *
 * @author ken
 */
public class SymbolTable
{

	/**
	 * Hashtable that contains all of the scopes, including those that are not
	 * currently in use.
	 */
	private Hashtable<String, Hashtable<String, Info>> tableIndex;

	/**
	 * The initial Hashtable for the global scope.
	 */
	private Hashtable<String, Info> globalScope;

	/**
	 * Stack of scopes, used to hold the names of the currently active stack.
	 */
	private Stack<String> scopes;

	/**
	 * Constructor: initializes values.
	 */
	public SymbolTable()
	{
		globalScope = new Hashtable<String, Info>();
		tableIndex = new Hashtable<String, Hashtable<String, Info>>();
		tableIndex.put("globalScope", globalScope);
		scopes = new Stack<String>();
		scopes.push("globalScope");
	}

	/**
	 * Checks all the scopes, starting at the highest level, to see if there
	 * element has been declared in the program.
	 *
	 * @param element
	 * @return
	 */
	public boolean exists(String element)
	{
		/* Need to implement for if I do function calls
		Enumeration<String> keys = tableIndex.keys();
		while (keys.hasMoreElements())
		{
			if (tableIndex.containsKey(element))
			{
				return true;
			}
			keys.nextElement();
		}

		return false;
		*/

		return globalScope.containsKey(element);
		
	}

	/**
	 * Checks if the scope already exists, if it does, add the already created
	 * Info object to it. If the scope does not exist, it creates a new scope
	 * and then adds the Info object.
	 *
	 * @param scopeName
	 * @param symbol
	 */
	public void add(String lexeme)
	{
		tableIndex.get(scopes.peek()).put(lexeme, new Info(lexeme));
	}

    /**
     * Adds a lexeme to the symbol table.
     * @param lexeme
     * @param kind 
     */
	public void add(String lexeme, Kind kind)
	{
		tableIndex.get(scopes.peek()).put(lexeme, new Info(lexeme, kind));
	}

    /**
     * Pushes a new scope on the the current stack.
     * @param scopeName 
     */
	public void pushScope(String scopeName)
	{
		Hashtable<String, Info> newScope = new Hashtable<String, Info>();
		scopes.push(scopeName);
		tableIndex.put(scopeName, newScope);
	}

	/**
	 * Pops the top scope off of the tables stack.
	 *
	 * @param scopeName
	 */
	public void popScope()
	{
		if (!scopes.isEmpty())
		{
			scopes.pop();
		}
	}

	/**
	 * Set the symbol defined by lexeme's kind to given kind.
	 *
	 * @param lexeme
	 * @param kind
	 */
	public void setKind(String lexeme, Kind kind)
	{
		if (tableIndex.get(scopes.peek()).containsKey(lexeme))
		{
			tableIndex.get(scopes.peek()).get(lexeme).kind = kind;
		}
		else if (tableIndex.get("globalScope").containsKey(lexeme))
		{
			tableIndex.get("globalScope").get(lexeme).kind = kind;
		}
		else
		{
			System.out.println("Failed adding kind " + kind + " to "
					+ lexeme);

		}
	}

	/**
	 * Get the symbol defined by lexeme's kind.
	 *
	 * @param lexeme
	 * @return
	 */
	public Kind getKind(String lexeme)
	{
		if (tableIndex.get(scopes.peek()).containsKey(lexeme))
		{
			return tableIndex.get(scopes.peek()).get(lexeme).kind;
		}
		else if (tableIndex.get("globalScope").containsKey(lexeme))
		{
			return tableIndex.get("globalScope").get(lexeme).kind;
		}
		return null;
	}

	/**
	 * Set the symbol defined by lexeme's kind to given kind.
	 *
	 * @param lexeme
	 * @param kind
	 */
	public void setType(String lexeme, Token type)
	{
		if (tableIndex.get(scopes.peek()).containsKey(lexeme))
		{
			tableIndex.get(scopes.peek()).get(lexeme).type = type;
		}
		else if (tableIndex.get("globalScope").containsKey(lexeme))
		{
			tableIndex.get("globalScope").get(lexeme).type = type;
		}
		else
		{
			System.out.println("Failed adding type " + type + " to "
					+ lexeme);

		}
	}

	/**
	 * Get the symbol defined by lexeme's kind.
	 *
	 * @param lexeme
	 * @return
	 */
	public Token getType(String lexeme)
	{
		if (tableIndex.get(scopes.peek()).containsKey(lexeme))
		{
			return tableIndex.get(scopes.peek()).get(lexeme).type;
		}
		else if (tableIndex.get("globalScope").containsKey(lexeme))
		{
			return tableIndex.get("globalScope").get(lexeme).type;
		}
		return null;
	}

	/**
	 * Set the start property of a given lexeme. Used for arrays.
	 * @param lexeme the string to which the start will be added.
	 * @param start the value that it will start at.
	 */
	public void setStart(String lexeme, int start)
	{
		if (tableIndex.get(scopes.peek()).containsKey(lexeme))
		{
			tableIndex.get(scopes.peek()).get(lexeme).startIndex = start;
		}
		else if (tableIndex.get("globalScope").containsKey(lexeme))
		{
			tableIndex.get("globalScope").get(lexeme).startIndex = start;
		}
		else
		{
			System.out.println("Failed adding start index " + start
					+ " to " + lexeme);
		}
	}

	/**
	 * Returns the end of a given lexeme.
	 * @param lexeme the string from which the end is returned.
	 * @return int end.
	 */
	public int getStart(String lexeme)
	{
		if (tableIndex.get(scopes.peek()).containsKey(lexeme))
		{
			return tableIndex.get(scopes.peek()).get(lexeme).startIndex;
		}
		else if (tableIndex.get("globalScope").containsKey(lexeme))
		{
			return tableIndex.get("globalScope").get(lexeme).startIndex;
		}
		// Should exit with error
		return 0;
	}

	/**
	 * Set the start property of a given lexeme. Used for arrays.
	 * @param lexeme the string to which the start will be added.
	 * @param start the value that it will start at.
	 */
	public void setEnd(String lexeme, int end)
	{
		if (tableIndex.get(scopes.peek()).containsKey(lexeme))
		{
			tableIndex.get(scopes.peek()).get(lexeme).endIndex = end;
		}
		else if (tableIndex.get("globalScope").containsKey(lexeme))
		{
			tableIndex.get("globalScope").get(lexeme).endIndex = end;
		}
		else
		{
			System.out.println("Failed adding end index " + end
					+ " to " + lexeme);
		}
	}

	/**
	 * Returns the end of a given lexeme.
	 * @param lexeme the string from which the end is returned.
	 * @return int end.
	 */
	public int getEnd(String lexeme)
	{
		if (tableIndex.get(scopes.peek()).containsKey(lexeme))
		{
			return tableIndex.get(scopes.peek()).get(lexeme).endIndex;
		}
		else if (tableIndex.get("globalScope").containsKey(lexeme))
		{
			return tableIndex.get("globalScope").get(lexeme).endIndex;
		}
		// Should exit with error
		return 0;
	}

	/**
	 * Set the return type of a function named lexeme.
	 * @param lexeme name of the function.
	 * @param rtype Token of the return type.
	 */
	public void setReturnType(String lexeme, Token rtype)
	{
		if (tableIndex.get(scopes.peek()).containsKey(lexeme))
		{
			tableIndex.get(scopes.peek()).get(lexeme).returnType = rtype;
		}
		else if (tableIndex.get("globalScope").containsKey(lexeme))
		{
			tableIndex.get("globalScope").get(lexeme).returnType = rtype;
		}
		else
		{
			System.out.println("Error setting return type " + rtype
					+ " for function " + lexeme);
		}
	}

	/**
	 * Get the return type of a function lexeme.
	 * @param lexeme the function from which to get the return type.
	 * @return Token containing the return type of function lexeme.
	 */
	public Token getReturnType(String lexeme)
	{
		if (tableIndex.get(scopes.peek()).containsKey(lexeme))
		{
			return tableIndex.get(scopes.peek()).get(lexeme).returnType;
		}
		else if (tableIndex.get("globalScope").containsKey(lexeme))
		{
			return tableIndex.get("globalScope").get(lexeme).returnType;
		}
		return null;
	}

	/**
	 * Add a parameter to a function or procedure lexeme.
	 * @param lexeme the funciton/procedure to which the parameter is added.
	 * @param type the parameter type to be added.
	 */
	public void addParamType(String lexeme, Token type)
	{
		if (tableIndex.get(scopes.peek()).containsKey(lexeme))
		{
			tableIndex.get(scopes.peek()).get(lexeme).paramTypes.add(type);
		}
		else if (tableIndex.get("globalScope").containsKey(lexeme))
		{
			tableIndex.get("globalScope").get(lexeme).paramTypes.add(type);
		}
		else
		{
			System.out.println("Error adding parameter type " + type
					+ " for function/procedure " + lexeme);
		}
	}

	/**
	 * Get the parameter types of a function or procedure named lexeme.
	 * @param lexeme name of the function or procedure.
	 * @return ArrayList<Token> of parameter types.
	 */
	public ArrayList<Token> getParamTypes(String lexeme)
	{
		if (tableIndex.get(scopes.peek()).containsKey(lexeme))
		{
			return tableIndex.get(scopes.peek()).get(lexeme).paramTypes;
		}
		else if (tableIndex.get("globalScope").containsKey(lexeme))
		{
			return tableIndex.get("globalScope").get(lexeme).paramTypes;
		}
		return null;
	}

	/**
	 * Set the integer value of a variable.
	 * @param lexeme variable to set.
	 * @param intval integer to set it to.
	 */
	public void setIntValue(String lexeme, int intval)
	{
		if (tableIndex.get(scopes.peek()).containsKey(lexeme))
		{
			tableIndex.get(scopes.peek()).get(lexeme).intValue = intval;
		}
		else if (tableIndex.get("globalScope").containsKey(lexeme))
		{
			tableIndex.get("globalScope").get(lexeme).intValue = intval;
		}
		else
		{
			System.out.println("Error adding intValue " + intval
					+ " for " + lexeme);
		}
	}

	/**
	 * Get the integer value of a variable.
	 * @param lexeme name of the variable.
	 * @return int value of the variable.
	 */
	public int getIntValue(String lexeme)
	{
		if (tableIndex.get(scopes.peek()).containsKey(lexeme))
		{
			return tableIndex.get(scopes.peek()).get(lexeme).intValue;
		}
		else if (tableIndex.get("globalScope").containsKey(lexeme))
		{
			return tableIndex.get("globalScope").get(lexeme).intValue;
		}
		return 0;
	}

	/**
	 * Set the integer value of a variable.
	 * @param lexeme variable to set.
	 * @param intval integer to set it to.
	 */
	public void setRealValue(String lexeme, double realval)
	{
		if (tableIndex.get(scopes.peek()).containsKey(lexeme))
		{
			tableIndex.get(scopes.peek()).get(lexeme).realValue = realval;
		}
		else if (tableIndex.get("globalScope").containsKey(lexeme))
		{
			tableIndex.get("globalScope").get(lexeme).realValue = realval;
		}
		else
		{
			System.out.println("Error adding realValue " + realval
					+ " for " + lexeme);
		}
	}

	/**
	 * Get the integer value of a variable.
	 * @param lexeme name of the variable.
	 * @return double value of the variable.
	 */
	public double getRealValue(String lexeme)
	{
		if (tableIndex.get(scopes.peek()).containsKey(lexeme))
		{
			return tableIndex.get(scopes.peek()).get(lexeme).realValue;
		}
		else if (tableIndex.get("globalScope").containsKey(lexeme))
		{
			return tableIndex.get("globalScope").get(lexeme).realValue;
		}
		return 0;
	}

	@Override
	public String toString()
	{
		Enumeration tableKeys = tableIndex.keys();
		String retval = "";
		while (tableKeys.hasMoreElements())
		{
			String currentKey = tableKeys.nextElement().toString();
			retval += currentKey;
			Hashtable<String, Info> currentTable = tableIndex.get(currentKey);
			Enumeration currentTableKeys = currentTable.elements();
			while (currentTableKeys.hasMoreElements())
			{
				String nestedKey = currentTableKeys.nextElement().toString();
				System.out.println(nestedKey);
				retval += "\n\t";
				retval += nestedKey;
			}
			retval += "\n";
		}
		return retval;
	}

	protected class Info
	{

		/**
		 * The string that is the ID of the symbol.
		 */
		protected String lexeme;
		/**
		 * The type (for variables): real, or integer.
		 */
		protected Token type;
		/**
		 * The kind of symbol (var, program, array, function, procedure).
		 */
		protected Kind kind;
		/**
		 * The starting index for arrays (not necessarily 0, but probably should
		 * be for simplicity's sake). I suppose it could be a 1 if you're a
		 * masochist. Or a mathematician.
		 */
		protected int startIndex;
		protected int endIndex;
		protected Token returnType;
		protected ArrayList<Token> paramTypes;
		protected int intValue;
		protected double realValue;

		public Info(String lexeme, Token type)
		{
			this.lexeme = lexeme;
			this.type = type;
			paramTypes = new ArrayList<Token>();
		}

		public Info(String lexeme)
		{
			this.lexeme = lexeme;
			paramTypes = new ArrayList<Token>();
		}

		public Info(String lexeme, Kind kind)
		{
			this.lexeme = lexeme;
			this.kind = kind;
			paramTypes = new ArrayList<Token>();
		}

		@Override
		public String toString()
		{
			return "Info{" + "lexeme=" + lexeme + ", type=" + type + ", kind="
					+ kind + ", startIndex=" + startIndex + ", endIndex=" + endIndex
					+ ", returnType=" + returnType + ", paramTypes=" + paramTypes
					+ '}';
		}

	}

}
