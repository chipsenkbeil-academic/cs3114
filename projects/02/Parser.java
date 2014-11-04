
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


/**
 * The class that handles parsing the data from the files.
 * @author rcsvt Robert C. Senkbeil
 */
public class Parser {
    
    // Acts like a public enum that identifies types of commands read
    // by the parser
    public static final int COMMAND_NONE        = 0;
    public static final int COMMAND_UNKNOWN     = 1;
    public static final int COMMAND_BLANK       = 2;
    public static final int COMMAND_ERROR       = 3;
    public static final int COMMAND_INSERT      = 4;
    public static final int COMMAND_REMOVE_XY   = 5;
    public static final int COMMAND_REMOVE_NAME = 6;
    public static final int COMMAND_FIND        = 7;
    public static final int COMMAND_SEARCH      = 8;
    public static final int COMMAND_DEBUG       = 9;
    public static final int COMMAND_MAKENULL    = 10;
    
    private Scanner scanner;
    
    /*************************************************************************/
    /* CLASS CONSTRUCTORS                                                    */
    /*************************************************************************/
    
    /**
     * Initializes the internal classes used by the parser and opens the file
     * provided for parsing.
     * @param file The file to parse
     */
    public Parser(File file) throws FileNotFoundException {
        scanner = new Scanner(file);
    }
    
    /*************************************************************************/
    /* CLASS METHODS                                                         */
    /*************************************************************************/
    
    /**
     * Retrieves the next command and packs the arguments into the provided
     * String array.
     * @param args The array to store arguments of the command
     * @return The type of command found
     */
    public int getNextCommand(String[] args) {
        if (!scanner.hasNextLine()) return COMMAND_NONE;
        
        // Get the individual chunks and ignore all whitespace
        String line = scanner.nextLine().trim();
        if (line.isEmpty()) return COMMAND_BLANK;
        String[] splitLine = line.split("\\s+");
        
        // Determine type of command
        int commandType = COMMAND_UNKNOWN;
        if (splitLine[0].toLowerCase().equals("insert")) {
            commandType = COMMAND_INSERT;
            
            // Check to make sure that the name is only letters and underscore
            for (int i = 0; i < splitLine[3].length(); ++i) {
                if ((splitLine[3].charAt(i) < 'A' || splitLine[3].charAt(i) > 'Z') &&
                    (splitLine[3].charAt(i) < 'a' || splitLine[3].charAt(i) > 'z') &&
                     splitLine[3].charAt(i) != '_') {
                    commandType = COMMAND_ERROR;
                    args[0] = splitLine[0].toUpperCase();
                    for (int n = 1; n < splitLine.length; ++n)
                        args[0] += " " + splitLine[n];
                    args[1] = "ERROR: '" + splitLine[3] +
                              "' contains characters different from " +
                              "letters or underscore!";
                    return commandType;
                }
            }
            
        }
        if (splitLine[0].toLowerCase().equals("remove")) {
            // If one argument, assume name
            if (splitLine.length == 2) {
                commandType = COMMAND_REMOVE_NAME;
                
                // Check to make sure that the name is only letters and underscore
                for (int i = 0; i < splitLine[1].length(); ++i) {
                    if ((splitLine[1].charAt(i) < 'A' || splitLine[1].charAt(i) > 'Z') &&
                        (splitLine[1].charAt(i) < 'a' || splitLine[1].charAt(i) > 'z') &&
                         splitLine[1].charAt(i) != '_') {
                        commandType = COMMAND_ERROR;
                        args[0] = splitLine[0].toUpperCase();
                        for (int n = 1; n < splitLine.length; ++n)
                            args[0] += " " + splitLine[n];
                        args[1] = "ERROR: '" + splitLine[3] +
                                  "' contains characters different from " +
                                  "letters or underscore!";
                        return commandType;
                    }
                }
            } else if (splitLine.length == 3) {
                commandType = COMMAND_REMOVE_XY;
            }
        }
        if (splitLine[0].toLowerCase().equals("find")) {
            commandType = COMMAND_FIND;
            
            // Check to make sure that the name is only letters and underscore
            for (int i = 0; i < splitLine[1].length(); ++i) {
                if ((splitLine[1].charAt(i) < 'A' || splitLine[1].charAt(i) > 'Z') &&
                    (splitLine[1].charAt(i) < 'a' || splitLine[1].charAt(i) > 'z') &&
                     splitLine[1].charAt(i) != '_') {
                    commandType = COMMAND_ERROR;
                    args[0] = splitLine[0].toUpperCase();
                    for (int n = 1; n < splitLine.length; ++n)
                        args[0] += " " + splitLine[n];
                    args[1] = "ERROR: '" + splitLine[3] +
                              "' contains characters different from " +
                              "letters or underscore!";
                    return commandType;
                }
            }
        }
        if (splitLine[0].toLowerCase().equals("search")) commandType = COMMAND_SEARCH;
        if (splitLine[0].toLowerCase().equals("debug")) commandType = COMMAND_DEBUG;
        if (splitLine[0].toLowerCase().equals("makenull")) commandType = COMMAND_MAKENULL;
        
        // Exit if there are no extra arguments
        if (splitLine.length - 1 < 1) return commandType;
        
        // Set all other chunks as arguments for the command
        for (int i = 1; i < splitLine.length; ++i) {
            args[i - 1] = splitLine[i];
        }
        
        // Return the type of the command
        return commandType;
    }
}
