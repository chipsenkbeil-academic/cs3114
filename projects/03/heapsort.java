
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
 * The main executing class of the project.
 * @author rcsvt (Robert C. Senkbeil)
 */
public class heapsort {
    
    /**
     * Completed: 11/02/2011
     * Compiler: Java 6 through Netbeans 7.0.1
     * Operating System: Debian 6 "Squeeze"
     * 
     * The main executing method run when the program starts.
     * Format is java heapsort <data-file-name> <numb-buffers> <stat-file-name>
     * @param args The commandline arguments passed to the program
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        // Check for the proper number of arguments
        if (args.length != 3) {
            System.err.println("ERROR: Format should be\n" + 
                                "'heapsort <data-file-name> <numb-buffers> " +
                                "<stat-file-name>'");
            return;
        }
        
        // Sort the file
        ExternalMaxHeap.sort(
                new File(args[0]), 
                Integer.parseInt(args[1]), 
                new File(args[2])
        );
    }
}
