
import java.awt.Point;
import java.io.IOException;
import java.util.Arrays;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Represents a generic node that can be converted to and from bytes.
 * @author rcsvt
 */
public abstract class SerialNode extends Point.Double {
    
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
     * Retrieves the x coordinate of the city.
     * @return The integer coordinate
     */
    public final int getX_Int() {
        return (int) x;
    }

    /**
     * Retrieves the y coordinate of the city.
     * @return The integer coordinate
     */
    public final int getY_Int() {
        return (int) y;
    }
    
    /**
     * Sets the x coordinate of the city.
     * @param x The new coordinate
     */
    public final void setX_Int(int x) {
        this.x = x;
    }

    /**
     * Sets the y coordinate of the city
     * @param y The new coordinate
     */
    public final void setY_Int(int y) {
        this.y = y;
    }
    
    /**
     * Determines whether this mapped element is within range of a specific
     * pair of coordinates and a given radius.
     * @param x The x coordinate of the point
     * @param y The y coordinate of the point
     * @param radius The radius to check around the point
     * @return Whether or not the location is within range of this element
     */
    public final boolean isWithinRange(double x, double y, double radius) {
        return (((int) this.distance(x, y)) <= radius);
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
    
    /*************************************************************************/
    /* CLASS ABSTRACT METHODS                                                */
    /*************************************************************************/
    
    public abstract int saveToBytes(byte[] byteArray);
    public abstract void loadFromBytes(byte[] byteArray);
    public abstract void delete();
}
