package scanner;

import java.io.File;
import java.util.Hashtable;

/**
 * File to test the scanner. Takes in a file and gets each token through the 
 * end of the file.
 * @author ken
 */
public class ScannerTest
{
    public static void main(String[] args)
    {
        if(args.length < 1)
            System.out.println("Scanner needs a file\nUsage:\nScanner <filename>");
        File input = new File(args[0]);
        Hashtable symbols = new LookupTable();
        Scanner fileScan = new Scanner(input, symbols);
        NextTokenReturnValue retVal;
        while((retVal = fileScan.nextToken()) != NextTokenReturnValue.INPUT_COMPLETE)
        {
            System.out.println("The return value is: " + retVal);
            if(retVal == NextTokenReturnValue.TOKEN_AVAILABLE)
            {
                Token nextT = fileScan.getToken();
                Object attr = fileScan.getAttribute();
                System.out.println("Token: " + nextT + "\tAttribue: " + attr);
            }
            else
            {
                System.out.println("Ya Dun Goofed.\n" + fileScan.getAttribute());
            }
        }
    }
}
