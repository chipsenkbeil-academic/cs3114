
import java.io.FileNotFoundException;
import java.io.IOException;

// On my honor:
//
// - I have not used source code obtained from another student,
// or any other unauthorized source, either modified or
// unmodified.
//
// - All source code and documentation used in my program is
// either my original work, or was derived by me from the
// source code published in the textbook for this course.
//
// - I have not discussed coding details about this project with
// anyone other than my partner (in the case of a joint
// submission), instructor, ACM/UPE tutors or the TAs assigned
// to this course. I understand that I may discuss the concepts
// of this program with other students, and that another student
// may help me debug my program so long as neither of us writes
// anything during the discussion or modifies any computer file
// during the discussion. I have violated neither the spirit nor
// letter of this restriction.

/**
 * The main executing class.
 * @author rcsvt Robert C. Senkbeil
 * @author avneeeet Avneet Singh
 * @compiler javac 
 * @operating_system Windows 7 and Debian  
 */
public class memman {
    
    /**
     * The main executing method run when the program starts.
     * Format is java memman <poolSize> <numOfRecs> <nameOfFile>
     * @param args The commandline arguments passed to the program
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        MemPool memPool = null;
        RecordHandler recordHandler = null;
        Executer executer = null;
        
        // Check for the proper number of arguments
        if (args.length != 3) {
            System.err.println("ERROR: Format should be\n'memman " +
                               "<pool_size> <record_count> <file_name>'");
            return;
        }
        
        // Parse the provided arguments
        int poolSize = Integer.parseInt(args[0]);
        int numOfRecs = Integer.parseInt(args[1]);
        String nameOfFile = args[2];
        
        // Create a new memory pool with the specified size
        memPool = new MemPool(poolSize);
        
        // Create the record handler and link it to the memory pool
        recordHandler = new RecordHandler(memPool, numOfRecs);
        
        // Set up the executer to work with the created record handler
        executer = new Executer(recordHandler);
        
        // Execute the inputted file
        executer.executeCommands(nameOfFile);
    }
}
