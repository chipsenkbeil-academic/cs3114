
import java.io.File;
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
 * Represents the main executing class of the program.
 * @author Robert Senkbeil (rcsvt)
 */
public class Bindisk {
    
    /**
     * Completed: 12/10/2011 (used 4 late days)
     * Compiler: Java 6 through Netbeans 7.0.1
     * Operating System: Debian 6 "Squeeze"
     * 
     * The main executing method run when the program starts.
     * Format is java bindisk <input-file> <buffer-pool> <block-size>
     * @param args The commandline arguments passed to the program
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        MemPool memPool = null;
        Executer executer = null;
        PRQuadTree<CityNode> quadTree = null;
        BST<String, CityNode> bsTree = null;
        BufferBridge bBridge = null;
        
        // Check for the proper number of arguments
        if (args.length != 3) {
            System.err.println("ERROR: Format should be\n'bindisk " +
                               "<input_file> <buffer_pool> <block_size>'");
            return;
        }
        
        // Delete the old p4bin.dat file
        File p4bin = new File("p4bin.dat");
        if (p4bin.exists()) p4bin.delete();
        
        // Parse the provided arguments
        String nameOfFile = args[0];
        bBridge = new BufferBridge(p4bin, Integer.parseInt(args[1]), Integer.parseInt(args[2]));
        
        // Create a new memory pool with the specified size
        memPool = new MemPool(bBridge, 0);
        
        // Create the quad tree to be used (boundaries are 0 to (2^14 = 16384) - 1)
        quadTree = new PRQuadTree<CityNode>(memPool, 0.0, 0.0, 16383.0, 16383.0);
        
        // Create the binary search tree
        bsTree = new BST<String, CityNode>(memPool);
        
        executer = new Executer(memPool, quadTree, bsTree);
        executer.executeCommands(nameOfFile);
    }
}
