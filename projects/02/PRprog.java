
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
 */
public class PRprog {
    /**
     * Completed: 10/10/2011
     * Compiler: Java 6 through Netbeans 7.0.1
     * Operating System: Debian 6 "Squeeze"
     * 
     * The main executing method run when the program starts.
     * Format is java PRprog <file_name>
     * @param args The commandline arguments passed to the program
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        PRQuadTree<CityNode> quadTree = null;
        BST<String, CityNode> bsTree = null;
        Executer executer = null;
        
        // Check for the proper number of arguments
        if (args.length != 1) {
            System.err.println("ERROR: Format should be\n'PRprog <file_name>'");
            return;
        }
        
        // Parse the provided arguments
        String nameOfFile = args[0];
        
        // Create the quad tree to be used (boundaries are 0 to (2^14 = 16384) - 1)
        quadTree = new PRQuadTree<CityNode>(0, 0, 16383, 16383);
        
        // Create the binary search tree
        bsTree = new BST<String, CityNode>();
        
        // Set up the executer to work with the created record handler
        executer = new Executer(quadTree, bsTree);
        
        // Execute the inputted file
        executer.executeCommands(nameOfFile);
    }
}
