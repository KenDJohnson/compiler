package codegenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author ken
 */
public class Compiler
{
	
	public static void main(String[] args) throws IOException
	{
        if(args.length < 1)
        {
            System.out.println("Usage: pascalc <filename>");
            System.exit(-1);
        }
        String inFile = args[0];
		Generator compiler = new Generator(inFile);
		String progname = compiler.name();
		String asm = compiler.generate();
		if(args.length == 2 && args[1].equals("-p"))
		{
			System.out.println(compiler.getTree());
		}
		progname = progname + ".asm";
		File mipsFile = new File(progname);
		BufferedWriter asmWriter = new BufferedWriter(
			new FileWriter(mipsFile.getAbsoluteFile()));
		System.out.println("Writing to " + mipsFile.getAbsolutePath());
		asmWriter.write(asm);
		asmWriter.close();
	}
}
