 
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Utilizes a parse to read a file and execute various commands.
 * @author rcsvt Robert C. Senkbeil
 */
public class Executer {
    
    private MemPool memPool;
    private PRQuadTree<CityNode> quadTree;
    private BST<String, CityNode> bsTree;
    
    /*************************************************************************/
    /* CLASS CONSTRUCTORS                                                    */
    /*************************************************************************/
    
    /**
     * Creates a new instance of the Executer class and links it with the
     * associated quadtree and binary search tree.
     * @param memPool The memory pool associated with this executer
     * @param quadTree The quadtree to link with this executer
     * @param bsTree The binary search tree to link with this executer
     */
    public Executer(MemPool memPool, PRQuadTree quadTree, BST bsTree) {
        this.memPool = memPool;
        this.quadTree = quadTree;
        this.bsTree = bsTree;
    }
    
    /*************************************************************************/
    /* CLASS METHODS                                                         */
    /*************************************************************************/
    
    /**
     * Executes commands from the file specified.
     * @param fileName The name of the file containing commands
     */
    public void executeCommands(String fileName) throws FileNotFoundException, IOException {
        Parser parser = new Parser(new File(fileName));
        
        String[] args = new String[3];
        int x, y, radius, commandsExecuted = 0;
        int commandType = Parser.COMMAND_NONE;
        do {
            // Parse the next command and process it
            commandType = parser.getNextCommand(args);
            if (commandType != Parser.COMMAND_NONE && commandType != Parser.COMMAND_UNKNOWN &&
                commandType != Parser.COMMAND_BLANK) System.out.print("Command " + ++commandsExecuted + ": ");
            switch (commandType) {
                case Parser.COMMAND_NONE:
                    continue;
                    
                case Parser.COMMAND_UNKNOWN:
                    System.err.println("ERROR: Unknown command!");
                    break;
                    
                case Parser.COMMAND_BLANK:
                    continue;
                    
                case Parser.COMMAND_ERROR:
                    System.out.println(args[0]);
                    System.err.println(args[1]);
                    break;
                    
                case Parser.COMMAND_INSERT:
                    System.out.println("INSERT " + args[0] + " " +
                                       args[1] + " " + args[2]);
                    
                    // Check for potential errors
                    x = Integer.parseInt(args[0]);
                    y = Integer.parseInt(args[1]);
                    if (x < quadTree.minimumXBound || x > quadTree.maximumXBound) {
                        System.err.println(">> Insert rejected, bad X coordinate");
                        break;
                    }
                    if (y < quadTree.minimumYBound || y > quadTree.maximumYBound) {
                        System.err.println(">> Insert rejected, bad Y coordinate");
                        break;
                    }
                    if (quadTree.contains(x, y)) {
                        System.err.println(">> Insert rejected, " + 
                                "coordinates duplicate an existing city record");
                        break;
                    }
                    
                    // Add the name of the city to the pool
                    int namePtr = memPool.insert(args[2].getBytes(), 
                                                 args[2].getBytes().length);
                    
                    byte[] t = new byte[256];
                    int ts = memPool.get(t, namePtr, 256);
                    
                    
                    // Create the city node and add it to the memory pool
                    CityNode cn = new CityNode(memPool,
                                                Integer.parseInt(args[0]),
                                                Integer.parseInt(args[1]),
                                                namePtr);
                    cn.storeInMemory();
                    
                    // Insert into the QuadTree and Binary Search Tree
                    boolean s = quadTree.insert(Integer.parseInt(args[0]),
                                    Integer.parseInt(args[1]),
                                    cn.getHandle());
                    bsTree.insert(namePtr, cn.getHandle());
                    System.out.println(">> Insertion operation successful");
                    break;
                    
                case Parser.COMMAND_REMOVE_XY:
                    System.out.println("REMOVE " + args[0] + " " + args[1]);
                    x = Integer.parseInt(args[0]);
                    y = Integer.parseInt(args[1]);
                    
                    // Check if x is within bounds
                    if (x < 0 || x >= 16384) {
                        System.err.println(">> Removal failed: bad X coordinate");
                        break;
                    }
                    
                    // Check if y is within bounds
                    if (y < 0 || y >= 16384) {
                        System.err.println(">> Removal failed: bad Y coordinate");
                        break;
                    }
                    
                    if (quadTree.contains(
                            Integer.parseInt(args[0]), Integer.parseInt(args[1])
                       )) {
                        CityNode nodeToRemove = null;
                        int nodeToRemoveHandle = quadTree.remove(
                                                    Integer.parseInt(args[0]),
                                                    Integer.parseInt(args[1]),
                                                    nodeToRemove);
                        nodeToRemove = CityNode.create(memPool, nodeToRemoveHandle);
                        
                        // Remove and readd nodes until the matching one is found
                        CityNode removedNode = null;
                        do {
                            if (removedNode != null)
                                bsTree.insert(removedNode.getNamePtr(), nodeToRemoveHandle);
                            removedNode = bsTree.remove(nodeToRemove.getName());
                        } while (!nodeToRemove.equals(removedNode));
                        System.out.println(">> Removed " + args[0] + ", " +
                                           args[1] + ", " + nodeToRemove.getName());
                        nodeToRemove.delete();
                    } else {
                        System.out.println(">> No record exists at " +
                                           args[0] + ", " + args[1]);
                    }
                    break;
                    
                case Parser.COMMAND_REMOVE_NAME:
                    System.out.println("REMOVE " + args[0]);
                    if (bsTree.find(args[0]) != null) {
                        int rHandle = -1;
                        CityNode rCity = bsTree.remove(args[0]);
                        CityNode blank = null;
                        rHandle = quadTree.remove(rCity.getX_Int(), rCity.getY_Int(), blank);
                        System.out.println(">> Removed " + rCity.getX_Int() + ", " +
                                           rCity.getY_Int() + ", " + rCity.getName());
                        rCity.delete();
                    } else {
                        System.out.println(">> No record exists with name " +
                                           args[0]);
                    }
                    break;
                    
                case Parser.COMMAND_FIND:
                    System.out.println("FIND " + args[0]);
                    List<CityNode> cities = bsTree.findAll(args[0]);
                    System.out.println(">> City record(s) found:");
                    for (CityNode c : cities) {
                        System.out.println(">> " + c.getX_Int() + ", " + c.getY_Int() +
                                           ", " + c.getName());
                    }
                    if (cities.isEmpty()) System.out.println(">> No record exists");
                    break;
                    
                case Parser.COMMAND_SEARCH:
                    System.out.println("SEARCH " + args[0] + " " +
                                       args[1] + " " + args[2]);
                    
                    // Assert the parameters are okay
                    x = Integer.parseInt(args[0]);
                    y = Integer.parseInt(args[1]);
                    radius = Integer.parseInt(args[2]);
                    
                    // Check if x is within bounds
                    if (Math.abs(x) >= 16384) {
                        System.err.println(">> Search failed: bad X coordinate");
                        break;
                    }
                    
                    // Check if y is within bounds
                    if (Math.abs(y) >= 16384) {
                        System.err.println(">> Search failed: bad Y coordinate");
                        break;
                    }
                    
                    // Check if radius is within bounds
                    if (radius < 0 || radius >= 16384) {
                        System.err.println(">> Search failed: bad Radius value");
                        break;
                    }
                    
                    LinkedList<Integer> elements = new LinkedList<Integer>();
                    int nodesSearched = quadTree.search(Integer.parseInt(args[0]),
                                                        Integer.parseInt(args[1]),
                                                        Integer.parseInt(args[2]),
                                                        elements);
                    // Print out a generic message
                    System.out.println(">> City record(s) found:");
                    
                    // Check to see if any elements were found
                    if (elements.isEmpty()) {
                        System.out.println(">> No such record");
                    } else {
                        for (Integer e_Handle : elements) {
                            System.out.println(">> " + CityNode.create(memPool, e_Handle).toString());
                        }
                    }
                    
                    // Print out the nodes traversed
                    System.out.println(">> " + nodesSearched + " nodes searched");
                    break;
                    
                case Parser.COMMAND_DEBUG:
                    System.out.println("DEBUG");
                    System.out.print(">> ");
                    memPool.printBlockIDs(System.out);
                    System.out.print(">> ");
                    memPool.printFreeBlocks(System.out);
                    System.out.print(">> ");
                    quadTree.printAll();
                    System.out.println();
                    break;
                    
                case Parser.COMMAND_MAKENULL:
                    System.out.println("MAKENULL");
                    
                    // Flush the memory pool to remove any existing buffers
                    memPool.flush();
                    
                    // Close the file being used by the bridge
                    memPool.getBridge().close();
                    
                    // Wipe the file used externally
                    memPool.getBridge().getFile().delete();
                    memPool.getBridge().getFile().createNewFile();
                    
                    // Open the file for access by the bridge
                    memPool.getBridge().open();
                    
                    // Throw out any leftover nodes in the quad tree and binary tree
                    quadTree = new PRQuadTree(memPool, 0, 0, 16383, 16383);
                    bsTree = new BST<String, CityNode>(memPool);
                    
                    // Update status
                    System.out.println(">> Makenull operation successful");
                    break;
                    
                default:
                    // Do nothing
            }
            
            // Add a new line to make things more readable
            System.out.println();
        } while (commandType != Parser.COMMAND_NONE);
        
        // Flushes the stream
        memPool.flush();
    }
    
}
