
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Represents the flyweight object that is used for all nodes that do not
 * contain children or elements.
 * @author rcsvt Robert C. Senkbeil
 */
public class PRQuadEmptyNode<T extends SerialNode> extends PRQuadBaseNode<T> {
    
    /**
     * Creates a single instance of the empty node that is accessed using
     * a getter (represents the only empty node as a flyweight).
     */
    
    /*************************************************************************/
    /* CLASS CONSTRUCTORS                                                    */
    /*************************************************************************/
    
    /**
     * A private constructor to prevent normal instantiation of the empty node.
     * @param memPool The memory pool associated with this empty node
     */
    public PRQuadEmptyNode(MemPool memPool) {
        setMemPool(memPool);
        setHandle(-1); // All empty nodes have a handle of -1
    }
    
    /*************************************************************************/
    /* CLASS METHODS                                                         */
    /*************************************************************************/
    
    /**
     * Returns the reference to the only allowed instance of the flyweight
     * empty node of the QuadTree.
     * @return The empty node of the QuadTree
     */
    public PRQuadBaseNode getInstance() {
        return (PRQuadBaseNode) new PRQuadEmptyNode(getMemPool());
    }
    
    /*************************************************************************/
    /* INHERITED METHODS                                                     */
    /*************************************************************************/
    
    /**
     * Adds an element to the node by creating a leaf node and replacing itself
     * with the leaf node.
     * @param xMin The minimum x coordinate in the region
     * @param yMin The minimum y coordinate in the region
     * @param xMax The maximum x coordinate in the region
     * @param yMax The maximum y coordinate in the region
     * @param x The x coordinate of the element to add
     * @param y The y coordinate of the element to add
     * @param elementHandle The handle to the element to add
     * @return The node of the add call
     */
    @Override
    public int add(double xMin, double yMin, double xMax, double yMax,
                   double x, double y, int elementHandle) {
        PRQuadLeafNode leafNode = new PRQuadLeafNode(getMemPool(), -1);
        return leafNode.add(xMin, yMin, xMax, yMax, x, y, elementHandle);
    }
    
    /**
     * Removes the element (attempts to) from the node and returns the node accessed.
     * @param x The x coordinate of the element to remove
     * @param y The y coordinate of the element to remove
     * @return The node of the removal call
     */
    @Override
    public int remove(
            double xMin, double yMin, double xMax, double yMax,
            double x, double y) {
        return -1;
    }
    
    /**
     * Returns the node containing the element at the specified location; however,
     * returns null as there is no element in the flyweight.
     * @param x The x coordinate of the element
     * @param y The y coordinate of the element
     * @return The node containing the element
     */
    @Override
    public int contains(
            double xMin, double yMin, double xMax, double yMax,
            double x, double y) {
        return -1;
    }
    
    /**
     * Returns zero as there are no elements in the flyweight.
     * @return The integer number of elements in the node
     */
    @Override
    public int getTotalElements() {
        return 0;
    }

    /**
     * Returns false as this is not a leaf node.
     * @return False
     */
    @Override
    public boolean isLeaf() {
        return false;
    }

    /**
     * Returns false as this is not an internal node.
     * @return False
     */
    @Override
    public boolean isInternal() {
        return false;
    }

    /**
     * Returns true as this is the flyweight node.
     * @return True
     */
    @Override
    public boolean isFlyweight() {
        return true;
    }
    
    /**
     * Prints information about this node and returns it as a String.
     * @return The information about the node
     */
    @Override
    public String toString() {
        return "*";
    }

    /**
     * Converts this empty node into a byte array with the size being the first
     * byte and the type being the second byte
     * @return The size of the byte array
     */
    @Override
    public int saveToBytes(byte[] byteArray) {
        byte type = 1; // Empty node type
        byteArray[0] = type;
        return 1;
    }

    /**
     * Loads the contents of the byte array into this empty node (does nothing);
     * @param byteArray The byte array to load from
     */
    @Override
    public void loadFromBytes(byte[] byteArray) {
        assert (byteArray[0] == 1) : 
                "ERROR: Loaded non-empty node!";
    }

    /**
     * Deletes the empty node from memory.
     */
    @Override
    public void delete() {
        // Exit if nothing to delete
        if (getHandle() == -1) return;
        
        // Attempt to delete
        try {
            getMemPool().remove(getHandle());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PRQuadEmptyNode.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PRQuadEmptyNode.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
