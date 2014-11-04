/**
 * Represents a generic node in a QuadTree.
 * @author rcsvt Robert C. Senkbeil
 */
public interface PRQuadBaseNode<T> {
    
    /*************************************************************************/
    /* CLASS METHODS                                                         */
    /*************************************************************************/
    
    /**
     * Adds an element to the node.
     * @param xMin The minimum x coordinate in the region
     * @param yMin The minimum y coordinate in the region
     * @param xMax The maximum x coordinate in the region
     * @param yMax The maximum y coordinate in the region
     * @param x The x coordinate of the element to add
     * @param y The y coordinate of the element to add
     * @param element The element to add
     * @return The node of the add call
     */
    public PRQuadBaseNode<T> add(int xMin, int yMin, int xMax, int yMax,
                                 int x, int y, T element);
    
    /**
     * Removes the element from the node and returns the node.
     * @param x The x coordinate of the element to remove
     * @param y The y coordinate of the element to remove
     * @return The node of the removal call
     */
    public PRQuadBaseNode<T> remove(int x, int y);
    
    /**
     * Returns the node containing the element at the specified location or
     * null if the location is not found.
     * @param x The x coordinate of the element
     * @param y The y coordinate of the element
     * @return The node containing the element
     */
    public PRQuadBaseNode<T> contains(int x, int y);
    
    /**
     * Returns the total number of elements in this node.
     * @return The integer number of elements
     */
    public int getTotalElements();
    
    /**
     * Returns whether or not the node is a leaf.
     * @return The true/false value
     */
    public boolean isLeaf();
    
    /**
     * Returns whether or not the node is internal.
     * @return The true/false value
     */
    public boolean isInternal();
    
    /**
     * Returns whether or not the node is the flyweight.
     * @return The true/false value
     */
    public boolean isFlyweight();
    
    /**
     * Prints information about this node into a String that is returned.
     * @return The information about this node
     */
    @Override
    public String toString();
    
}
