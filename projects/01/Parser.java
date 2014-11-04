
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


/**
 * The class that handles parsing the data from the files.
 * @author rcsvt Robert C. Senkbeil
 * @author avneeeet Avneet Singh
 */
public class Parser {
    
    // Acts like a public enum that identifies types of commands read
    // by the parser
    public static final int COMMAND_NONE        = 0;
    public static final int COMMAND_UNKNOWN     = 1;
    public static final int COMMAND_BLANK       = 2;
    public static final int COMMAND_INSERT      = 3;
    public static final int COMMAND_REMOVE      = 4;
    public static final int COMMAND_PRINT       = 5;
    public static final int COMMAND_PRINTALL    = 6;
    
    private Scanner scanner;
    
    /**
     * Initializes the internal classes used by the parser and opens the file
     * provided for parsing.
     * @param file The file to parse
     */
    public Parser(File file) throws FileNotFoundException {
        scanner = new Scanner(file);
    }
    
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
            for (int i = 0; i < splitLine[4].length(); ++i) {
                if ((splitLine[4].charAt(i) < 'A' || splitLine[4].charAt(i) > 'Z') &&
                    (splitLine[4].charAt(i) < 'a' || splitLine[4].charAt(i) > 'z') &&
                     splitLine[4].charAt(i) != '_') {
                    commandType = COMMAND_BLANK;
                    System.err.println("ERROR: '" + splitLine[4] + "' contains characters" +
                                       " different from letters or underscore!");
                    break;
                }
            }
            
        }
        if (splitLine[0].toLowerCase().equals("remove")) commandType = COMMAND_REMOVE;
        if (splitLine[0].toLowerCase().equals("print")) {
            // See if there are any arguments for print
            if (splitLine.length < 2) {
                commandType = COMMAND_PRINTALL;
            } else {
                commandType = COMMAND_PRINT;
            }
        }
        
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
