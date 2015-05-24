package parser;

import recognizer.Recognizer;
import syntaxtree.ProgramNode;

/**
 *
 * @author ken
 */
public class ParserTest
{
    public static void main(String[] args)
    {
        String[] testFiles = {
        "docs/testing/declarations.txt", 
		"docs/testing/array_declarations.txt", 
        "docs/testing/bigfile.txt", "docs/testing/two_ids_in_spd_cs.txt"};
        
		Parser parser = new Parser("docs/testing/declarations.txt");
		ProgramNode prog = parser.program();
		String tree = prog.indentedToString(0);
		System.out.println(tree);
		System.exit(0);
   /*     for(int i = 0; i < testFiles.length; i++)
        {
			System.out.println(testFiles[i]);
            Parser parser = new Parser(testFiles[i]);
            System.out.println("Parsing " + testFiles[i] + "...");
            parser.program();
        }
       */ 
    }
}
