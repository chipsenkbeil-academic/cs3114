
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Represents a QuadTree that is able to insert, remove, and search for elements.
 * @author rcsvt Robert C. Senkbeil
 */
public class PRQuadTree<T extends SerialNode> {
    
    private static final byte TYPE_EMPTY = 1;
    private static final byte TYPE_LEAF = 2;
    private static final byte TYPE_INTERNAL = 3;
    
    private MemPool memPool;
    private int root;
    //private PRQuadBaseNode root;
    public double minimumXBound, minimumYBound, maximumXBound, maximumYBound;
    
    /*************************************************************************/
    /* CLASS CONSTRUCTORS                                                    */
    /*************************************************************************/
    
    /**
     * Creates a new instance of the QuadTree with the provided region bounds.
     * @param memPool The memory pool associated with this quad tree
     * @param x The starting x location of the QuadTree region
     * @param y The starting y location of the QuadTree region
     * @param width The width of the QuadTree region
     * @param height The height of the QuadTree region
     */
    public PRQuadTree(MemPool memPool,
            double x, double y, double width, double height) {
        // Set the memory pool
        this.memPool = memPool;
        
        // Set the bounds of the tree
        this.minimumXBound = x;
        this.minimumYBound = y;
        this.maximumXBound = x + width;
        this.maximumYBound = y + height;
        
        // Set root to an empty node, or the flyweight
        root = -1;
        //root = new PRQuadEmptyNode(memPool);
    }
    
    /*************************************************************************/
    /* CLASS METHODS                                                         */
    /*************************************************************************/
    
    /**
     * Inserts an element into the location specified.
     * @param x The x location to insert the element
     * @param y The y location to insert the element
     * @param elementHandle The handle to the element to insert
     * @return Whether or not the element was successfully inserted
     */
    public boolean insert(double x, double y, int elementHandle) throws FileNotFoundException, IOException {
        PRQuadBaseNode rootNode = getNodeFromHandle(memPool, root);
        
        root = rootNode.add(minimumXBound, minimumYBound, 
                                maximumXBound, maximumYBound, 
                                x, y, elementHandle);
        
        rootNode = getNodeFromHandle(memPool, root);
        
        return (rootNode.contains(
                minimumXBound, minimumYBound, 
                maximumXBound, maximumYBound, 
                x, y) != -1);
    }
    
    /**
     * Find and stores all elements within the provided range given by the
     * coordinates and radius into a list and returns the total number of
     * nodes looked at during the search.
     * @param x The x location of the search
     * @param y The y location of the search
     * @param radius The radius around the x & y coordinates to search
     * @param handles The list of integers to store the handles to found elements
     * @return The total number of nodes looked at during the search
     */
    public int search(double x, double y, double radius,
                      List<Integer> handles) throws FileNotFoundException, IOException {
        return search(getNodeFromHandle(memPool, root),
                        minimumXBound, minimumYBound, 
                        maximumXBound, maximumYBound, 
                        x, y, radius, handles);
    }
    
    /**
     * Find and stores all elements within the provided range given by the
     * coordinates and radius into a list and returns the total number of
     * nodes looked at during the search.
     * @param root The root node to start the search
     * @param xMin The minimum x coordinate in the region
     * @param yMin The minimum y coordinate in the region
     * @param xMax The maximum x coordinate in the region
     * @param yMax The maximum y coordinate in the region
     * @param x The x location of the search
     * @param y The y location of the search
     * @param radius The radius around the x & y coordinates to search
     * @param handles The list of integers to store the handles of found elements
     * @return The total number of nodes looked at during the search
     */
    private int search(PRQuadBaseNode<T> root, double xMin, double yMin, double xMax, double yMax,
                        double x, double y, double radius,
                       List<Integer> handles) throws FileNotFoundException, IOException {
        // Check to see if the root is null
        if (root == null) return 0;
        
        // Search based on type of node
        int totalNodesChecked = 0;
        if (root.isLeaf()) {
            for (Integer e_Handle : ((PRQuadLeafNode<T>) root).getElementHandles()) {
                SerialNode se = CityNode.create(memPool, e_Handle);
                if (radius*radius >= ((se.x - x)*(se.x - x) + (se.y - y)*(se.y - y))) {
                    handles.add(e_Handle);
                }
            }
        } else if (root.isFlyweight()) {
            // Do nothing
        } else {
            List<PRQuadBaseNode<T>> regions =
                    ((PRQuadInternalNode<T>) root).getRegions(
                        xMin, yMin, 
                        xMax, yMax, 
                        x, y, radius);
            
            double xMiddle = (xMin + xMax) / 2;
            double yMiddle = (yMin + yMax) / 2;
            int nw = ((PRQuadInternalNode<T>) root).getNorthWestPtr();
            int ne = ((PRQuadInternalNode<T>) root).getNorthEastPtr();
            int sw = ((PRQuadInternalNode<T>) root).getSouthWestPtr();
            int se = ((PRQuadInternalNode<T>) root).getSouthEastPtr();
            
            
            // Search each applicable region for coordinates
            for (PRQuadBaseNode<T> region : regions) {
                if (region.getHandle() == nw) {
                    totalNodesChecked += search(region, 
                                                xMin, yMin, xMiddle, yMiddle,
                                                x, y, radius, handles);
                } else if (region.getHandle() == ne) {
                    totalNodesChecked += search(region, 
                                                xMiddle, yMin, xMax, yMiddle,
                                                x, y, radius, handles);
                } else if (region.getHandle() == sw) {
                    totalNodesChecked += search(region, 
                                                xMin, yMiddle, xMiddle, yMax,
                                                x, y, radius, handles);
                } else if (region.getHandle() == se) {
                    totalNodesChecked += search(region, 
                                                xMiddle, yMiddle, xMax, yMax,
                                                x, y, radius, handles);
                } else {
                    // Do nothing
                }
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
     * @param element The element to store the element removed into
     * @return The pointer to the element removed
     */
    public int remove(double x, double y, T element) throws FileNotFoundException, IOException {
        PRQuadBaseNode rootNode = getNodeFromHandle(memPool, root);
        int nodeWithElement = rootNode.contains(
                                minimumXBound, minimumYBound, 
                                maximumXBound, maximumYBound, 
                                x, y);
        
        // Check if exists, if not, exit with empty handle
        if (nodeWithElement == -1) return -1;
        
        // Get the element from the node that contains it
        int elementRemoved =
                ((PRQuadLeafNode<T>) getNodeFromHandle(memPool, rootNode.contains(
                        minimumXBound, minimumYBound, 
                        maximumXBound, maximumYBound, x, y))).getElementHandleAt(x, y);
        element = loadFromHandle(elementRemoved);
        
        // Remove the element from the node and return the element value
        root = rootNode.remove(minimumXBound, minimumYBound, 
                               maximumXBound, maximumYBound, 
                               x, y
        );
        return elementRemoved;
    }
    
    /**
     * Returns whether or not the element with the specified coordinates is
     * in the quad tree.
     * @param x The x coordinate to look for
     * @param y The y coordinate to look for
     * @return Whether or not the element with the coordinates exists
     */
    public boolean contains(double x, double y) throws FileNotFoundException, IOException {
        return (getNodeFromHandle(memPool, root).contains(
                        minimumXBound, minimumYBound, 
                        maximumXBound, maximumYBound, x, y) != -1);
    }
    
    /**
     * Returns the element with the matching coordinates or null if it does not
     * exist.
     * @param x The x coordinate to look for
     * @param y The y coordinate to look for
     * @return The element found
     */
    public T get(double x, double y) throws FileNotFoundException, IOException {
        PRQuadLeafNode<T> foundNode = 
                (PRQuadLeafNode<T>) getNodeFromHandle(memPool, 
                        getNodeFromHandle(memPool, root).contains(
                        minimumXBound, minimumYBound, 
                        maximumXBound, maximumYBound, x, y));
        return (foundNode != null) ? loadFromHandle(foundNode.getElementHandleAt(x, y)) : null;
    }
    
    /**
     * Prints out information about the entire tree.
     */
    public void printAll() throws IOException {
        print(getNodeFromHandle(memPool, root), System.out);
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
            oStream.write("(".getBytes());
            print(((PRQuadInternalNode) qRoot).getNorthWest(), oStream);
            print(((PRQuadInternalNode) qRoot).getNorthEast(), oStream);
            print(((PRQuadInternalNode) qRoot).getSouthWest(), oStream);
            print(((PRQuadInternalNode) qRoot).getSouthEast(), oStream);
            oStream.write(")".getBytes());
        }
    }
    
    /**
     * Returns the quad node found at the specified handle.
     * @param handle The handle used to find the quad node
     * @return The quad node object
     */
    public static PRQuadBaseNode getNodeFromHandle(MemPool memPool, int handle) throws FileNotFoundException, IOException {
        if (handle == -1) return new PRQuadEmptyNode(memPool);
        byte[] temp = new byte[256];
        int tempSize = memPool.get(temp, handle, 256);
        if (tempSize > 0) {
            if (temp[0] == TYPE_LEAF) {
                PRQuadLeafNode leafNode = new PRQuadLeafNode(memPool, handle);
                leafNode.loadFromBytes(temp);
                return leafNode;
            } else if (temp[0] == TYPE_INTERNAL) {
                PRQuadInternalNode internalNode = new PRQuadInternalNode(memPool, handle);
                internalNode.loadFromBytes(temp);
                return internalNode;
            } else {
                return new PRQuadEmptyNode(memPool);
            }
        } else {
            return new PRQuadEmptyNode(memPool);
        }
    }
    
    /**
     * Loads the CityNode from the provided handle.
     * @param handle 
     */
    private T loadFromHandle(int handle) throws FileNotFoundException, IOException {
        return (T) CityNode.create(memPool, handle);
    }
}
