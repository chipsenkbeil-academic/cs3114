
/**
 * Represents the flyweight object that is used for all nodes that do not
 * contain children or elements.
 * @author rcsvt Robert C. Senkbeil
 */
public class PRQuadEmptyNode<T> implements PRQuadBaseNode<T> {
    
    /**
     * Creates a single instance of the empty node that is accessed using
     * a getter (represents the only empty node as a flyweight).
     */
    private static PRQuadEmptyNode emptyNode = new PRQuadEmptyNode();
    
    /*************************************************************************/
    /* CLASS CONSTRUCTORS                                                    */
    /*************************************************************************/
    
    /**
     * A private constructor to prevent normal instantiation of the empty node.
     */
    private PRQuadEmptyNode() {
        // Do nothing
    }
    
    /*************************************************************************/
    /* CLASS METHODS                                                         */
    /*************************************************************************/
    
    /**
     * Returns the reference to the only allowed instance of the flyweight
     * empty node of the QuadTree.
     * @return The empty node of the QuadTree
     */
    public static PRQuadBaseNode getInstance() {
        return (PRQuadBaseNode) emptyNode;
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
     * @param element The element to add
     * @return The node of the add call
     */
    @Override
    public PRQuadBaseNode add(int xMin, int yMin, int xMax, int yMax,
                              int x, int y, T element) {
        PRQuadLeafNode<T> leafNode = new PRQuadLeafNode<T>();
        leafNode.add(xMin, yMin, xMax, yMax, x, y, element);
        return leafNode;
    }
    
    /**
     * Removes the element (attempts to) from the node and returns the node accessed.
     * @param x The x coordinate of the element to remove
     * @param y The y coordinate of the element to remove
     * @return The node of the removal call
     */
    @Override
    public PRQuadBaseNode remove(int x, int y) {
        return this;
    }
    
    /**
     * Returns the node containing the element at the specified location; however,
     * returns null as there is no element in the flyweight.
     * @param x The x coordinate of the element
     * @param y The y coordinate of the element
     * @return The node containing the element
     */
    @Override
    public PRQuadBaseNode contains(int x, int y) {
        return null;
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
        return "E";
    }
    
}
