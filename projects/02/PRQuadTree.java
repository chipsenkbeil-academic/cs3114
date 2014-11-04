
import java.io.IOException;
import java.util.List;

/**
 * Represents a QuadTree that is able to insert, remove, and search for elements.
 * @author rcsvt Robert C. Senkbeil
 */
public class PRQuadTree<T> {
    
    private PRQuadBaseNode root;
    public int minimumXBound, minimumYBound, maximumXBound, maximumYBound;
    
    /*************************************************************************/
    /* CLASS CONSTRUCTORS                                                    */
    /*************************************************************************/
    
    /**
     * Creates a new instance of the QuadTree with the provided region bounds.
     * @param x The starting x location of the QuadTree region
     * @param y The starting y location of the QuadTree region
     * @param width The width of the QuadTree region
     * @param height The height of the QuadTree region
     */
    public PRQuadTree(int x, int y, int width, int height) {
        // Set the bounds of the tree
        this.minimumXBound = x;
        this.minimumYBound = y;
        this.maximumXBound = x + width;
        this.maximumYBound = y + height;
        
        // Set root to an empty node, or the flyweight
        root = PRQuadEmptyNode.getInstance();
    }
    
    /*************************************************************************/
    /* CLASS METHODS                                                         */
    /*************************************************************************/
    
    /**
     * Inserts an element into the location specified.
     * @param x The x location to insert the element
     * @param y The y location to insert the element
     * @param element The element to insert
     * @return Whether or not the element was successfully inserted
     */
    public boolean insert(int x, int y, T element) {
        /*return insert(root, minimumXBound, minimumYBound, 
                      maximumXBound, maximumYBound, x, y, element);*/
        root = root.add(minimumXBound, minimumYBound, 
                        maximumXBound, maximumYBound, 
                        x, y, element);
        return (root.contains(x, y) != null);
    }
    
    /**
     * Find and stores all elements within the provided range given by the
     * coordinates and radius into a list and returns the total number of
     * nodes looked at during the search.
     * @param x The x location of the search
     * @param y The y location of the search
     * @param radius The radius around the x & y coordinates to search
     * @param elements The list of elements to store found elements
     * @return The total number of nodes looked at during the search
     */
    public int search(int x, int y, int radius,
                      List<PRQuadLeafNode<T>.MappedElement<T>> elements) {
        return search(root, x, y, radius, elements);
    }
    
    /**
     * Find and stores all elements within the provided range given by the
     * coordinates and radius into a list and returns the total number of
     * nodes looked at during the search.
     * @param root The root node to start the search
     * @param x The x location of the search
     * @param y The y location of the search
     * @param radius The radius around the x & y coordinates to search
     * @param elements The list of elements to store found elements
     * @return The total number of nodes looked at during the search
     */
    private int search(PRQuadBaseNode<T> root, int x, int y, int radius,
                       List<PRQuadLeafNode<T>.MappedElement<T>> elements) {
        // Check to see if the root is null
        if (root == null) return 0;
        
        // Search based on type of node
        int totalNodesChecked = 0;
        if (root.isLeaf()) {
            for (PRQuadLeafNode<T>.MappedElement<T> mE :
                 ((PRQuadLeafNode<T>) root).getMappedElements()) {
                if (Math.abs(mE.x - x) <= radius && Math.abs(mE.y - y) <= radius) {
                    elements.add(mE);
                }
            }
        } else if (root.isFlyweight()) {
            // Do nothing
        } else {
            List<PRQuadBaseNode<T>> regions =
                    ((PRQuadInternalNode<T>) root).getRegions(x, y, radius);
            
            // Search each applicable region for coordinates
            for (PRQuadBaseNode<T> region : regions) {
                totalNodesChecked += search(region, x, y, radius, elements);
            }
        }
        
        // Return the total nodes checked + 1 to include the root node
        return ++totalNodesChecked;
    }
    
    /**
     * Removes the element at the specified location and returns it.
     * Returns null if no element with the matching coordinates is found.
     * @param x The x location in the QuadTree
     * @param y The y location in the QuadTree
     * @return The element removed
     */
    public T remove(int x, int y) {
        PRQuadLeafNode<T> nodeWithElement = (PRQuadLeafNode<T>) root.contains(x, y);
        
        // Check if exists, if not, exit with null
        if (nodeWithElement == null) return null;
        
        // Get the element from the node that contains it
        T elementRemoved = ((PRQuadLeafNode<T>) root.contains(x, y)).getElementAt(x, y);
        
        // Remove the element from the node and return the element value
        root = root.remove(x, y);
        return elementRemoved;
    }
    
    /**
     * Returns whether or not the element with the specified coordinates is
     * in the quad tree.
     * @param x The x coordinate to look for
     * @param y The y coordinate to look for
     * @return Whether or not the element with the coordinates exists
     */
    public boolean contains(int x, int y) {
        return (root.contains(x, y) != null);
    }
    
    /**
     * Returns the element with the matching coordinates or null if it does not
     * exist.
     * @param x The x coordinate to look for
     * @param y The y coordinate to look for
     * @return The element found
     */
    public T get(int x, int y) {
        PRQuadLeafNode<T> foundNode = (PRQuadLeafNode<T>) root.contains(x, y);
        return (foundNode != null) ? foundNode.getElementAt(x, y) : null;
    }
    
    /**
     * Prints out information about the entire tree.
     */
    public void printAll() throws IOException {
        print(root, System.out);
    }
    
    /**
     * Prints information about nodes in pre-order traversal.
     * @param qRoot The root node to start with
     * @param oStream The stream to print to
     * @throws java.io.IOException The errors that can be thrown
     */
    private void print(PRQuadBaseNode qRoot, java.io.OutputStream oStream) throws java.io.IOException {
        // Preorder is root, northwest, northeast, southwest, southeast
        oStream.write(qRoot.toString().getBytes());
        
        // Check if it is an internal node that has children
        if (qRoot.isInternal()) {
            print(((PRQuadInternalNode) qRoot).getNorthWest(), oStream);
            print(((PRQuadInternalNode) qRoot).getNorthEast(), oStream);
            print(((PRQuadInternalNode) qRoot).getSouthWest(), oStream);
            print(((PRQuadInternalNode) qRoot).getSouthEast(), oStream);
        }
    }
}
