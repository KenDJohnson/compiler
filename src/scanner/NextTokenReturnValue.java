package scanner;

/**
 *
 * @author ken
 * An enum for the return value of a nextToken() call to the scanner.
 * Possible values: TOKEN_AVAILABLE, TOKEN_NOT_AVAILABLE, INPUT_COMPLETE
 */
public enum NextTokenReturnValue
{
    TOKEN_AVAILABLE,
    TOKEN_NOT_AVAILABLE,
    INPUT_COMPLETE,
}
