
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

/**
 * Represents a generic node in a QuadTree.
 * @author rcsvt Robert C. Senkbeil
 */
public abstract class PRQuadBaseNode<T> {
    
    /*************************************************************************/
    /* CLASS VARIABLES                                                       */
    /*************************************************************************/
    private MemPool memPool;
    private int handle;
    
    /*************************************************************************/
    /* CLASS METHODS                                                         */
    /*************************************************************************/
    
    /**
     * Sets the handle of this node.
     * @param handle The new handle
     */
    public final void setHandle(int handle) {
        this.handle = handle;
    }
    
    /**
     * Returns the handle of this node.
     * @return The integer handle
     */
    public final int getHandle() {
        return this.handle;
    }
    
    /**
     * Sets the memory pool used by this node.
     * @param memPool The new memory pool
     */
    public final void setMemPool(MemPool memPool) {
        this.memPool = memPool;
    }
    
    /**
     * Returns the memory pool used by this node.
     * @return The memory pool
     */
    public final MemPool getMemPool() {
        return this.memPool;
    }
    
    /**
     * Stores the current contents of this node into memory (updates the handle).
     */
    public final void storeInMemory() throws IOException {
        byte[] temp = new byte[256];
        int tempSize = saveToBytes(temp);
        setHandle(this.memPool.insert(temp, tempSize));
    }
    
    /**
     * Loads the contents of the node in memory into this node (uses the set handle).
     */
    public final void loadFromMemory() throws IOException {
        assert (this.handle != -1) : 
                "ERROR: Invalid handle for loading from memory!";
        byte[] temp = new byte[256];
        int tempSize = this.memPool.get(temp, handle, 256);
        loadFromBytes(Arrays.copyOf(temp, tempSize));
    }
    
    /**
     * Updates the contents of this node in memory.
     */
    public final void updateInMemory() throws FileNotFoundException, IOException {
        // Remove the node from memory if it was already in memory
        if (this.handle != -1) {
            getMemPool().remove(this.handle);
        }
        
        // Add (or readd) the node into memory
        this.storeInMemory();
    }
    
    /*************************************************************************/
    /* CLASS ABSTRACT METHODS                                                */
    /*************************************************************************/
    
    /**
     * Adds an element to the node.
     * @param xMin The minimum x coordinate in the region
     * @param yMin The minimum y coordinate in the region
     * @param xMax The maximum x coordinate in the region
     * @param yMax The maximum y coordinate in the region
     * @param x The x coordinate of the element to add
     * @param y The y coordinate of the element to add
     * @param elementHandle The handle to the element to add
     * @return The handle to the node of the add call
     */
    public abstract int add(double xMin, double yMin, double xMax, double yMax,
                                 double x, double y, int elementHandle);
    
    /**
     * Removes the element from the node and returns the node.
     * @param xMin The minimum x coordinate in the region
     * @param yMin The minimum y coordinate in the region
     * @param xMax The maximum x coordinate in the region
     * @param yMax The maximum y coordinate in the region
     * @param x The x coordinate of the element to remove
     * @param y The y coordinate of the element to remove
     * @return The handle to the node of the removal call
     */
    public abstract int remove(
            double xMin, double yMin, double xMax, double yMax,
            double x, double y);
    
    /**
     * Returns the node containing the element at the specified location or
     * null if the location is not found.
     * @param xMin The minimum x coordinate in the region
     * @param yMin The minimum y coordinate in the region
     * @param xMax The maximum x coordinate in the region
     * @param yMax The maximum y coordinate in the region
     * @param x The x coordinate of the element
     * @param y The y coordinate of the element
     * @return The handle to the node containing the element
     */
    public abstract int contains(
            double xMin, double yMin, double xMax, double yMax,
            double x, double y);
    
    /**
     * Returns the total number of elements in this node.
     * @return The integer number of elements
     */
    public abstract int getTotalElements();
    
    /**
     * Returns whether or not the node is a leaf.
     * @return The true/false value
     */
    public abstract boolean isLeaf();
    
    /**
     * Returns whether or not the node is internal.
     * @return The true/false value
     */
    public abstract boolean isInternal();
    
    /**
     * Returns whether or not the node is the flyweight.
     * @return The true/false value
     */
    public abstract boolean isFlyweight();
    
    /**
     * Prints information about this node into a String that is returned.
     * @return The information about this node
     */
    @Override
    public abstract String toString();
    
    /**
     * Converts this node into a byte form (with the size as the first byte).
     * @param byteArray The array to store the bytes into
     * @return The byte array representing this node
     */
    public abstract int saveToBytes(byte[] byteArray);
    
    /**
     * Loads this node from an array of bytes (with the first byte being the size).
     * @param byteArray The byte array representing this node
     */
    public abstract void loadFromBytes(byte[] byteArray);
    
    /**
     * Deletes this node from memory.
     */
    public abstract void delete();
    
}
