 
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Utilizes a parse to read a file and execute various commands.
 * @author rcsvt Robert C. Senkbeil
 * @author avneeeet Avneet Singh
 */
public class Executer {
    
    private RecordHandler recordHandler;
    
    /**
     * Creates a new instance of the Executer class and links it with the
     * associated record handler.
     * @param recordHandler The record handler to link with this executer
     */
    public Executer(RecordHandler recordHandler) {
        this.recordHandler = recordHandler;
    }
    
    /**
     * Executes commands from the file specified.
     * @param fileName The name of the file containing commands
     */
    public void executeCommands(String fileName) throws FileNotFoundException, IOException {
        Parser parser = new Parser(new File(fileName));
        
        String[] args = new String[4];
        int commandType = Parser.COMMAND_NONE;
        do {
            commandType = parser.getNextCommand(args);
            switch (commandType) {
                case Parser.COMMAND_NONE:
                    continue;
                    
                case Parser.COMMAND_UNKNOWN:
                    System.err.println("ERROR: Unknown command!");
                    break;
                    
                case Parser.COMMAND_BLANK:
                    continue;
                    
                case Parser.COMMAND_INSERT:
                    recordHandler.insert(
                            Integer.parseInt(args[0]),
                            Integer.parseInt(args[1]),
                            Integer.parseInt(args[2]),
                            args[3]);
                    break;
                    
                case Parser.COMMAND_REMOVE:
                    recordHandler.remove(Integer.parseInt(args[0]));
                    break;
                    
                case Parser.COMMAND_PRINT:
                    recordHandler.print(
                            Integer.parseInt(args[0]),
                            System.out);
                    break;
                    
                case Parser.COMMAND_PRINTALL:
                    recordHandler.print(System.out);
                    break;
            }
        } while (commandType != Parser.COMMAND_NONE);
    }
    
}
