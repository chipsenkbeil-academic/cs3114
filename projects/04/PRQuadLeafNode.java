
import java.awt.Point;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a QuadTree leaf node, which is a node that does not contain
 * any children, but does have elements in it.
 * @author rcsvt Robert C. Senkbeil
 */
public class PRQuadLeafNode<T extends SerialNode> extends PRQuadBaseNode<T> {
    
    /**
     * Maximum number of elements before the leaf node splits into pieces.
     */
    public static int MAXIMUM_ELEMENTS = 3;
    
    private List<Integer> elementHandles;
    
    /*************************************************************************/
    /* CLASS CONSTRUCTORS                                                    */
    /*************************************************************************/
    
    /**
     * Creates a new instance of a QuadTree leaf node with no elements.
     * @param memPool The memory pool associated with this node
     * @param handle The handle of this node
     */
    public PRQuadLeafNode(MemPool memPool, int handle) {
        elementHandles = new LinkedList<Integer>();
        setMemPool(memPool);
        setHandle(handle);
    }
    
    /*************************************************************************/
    /* CLASS METHODS                                                         */
    /*************************************************************************/
    
    /**
     * Returns the handle to the value of the element located at the specified 
     * location or -1 if no element exists at that location.
     * @param x The x coordinate to look for
     * @param y The y coordinate to look for
     * @return The element value
     */
    public int getElementHandleAt(double x, double y) throws FileNotFoundException, IOException {
        byte[] eBytes = new byte[256];
        for (int e_Handle : elementHandles) {
            SerialNode se = CityNode.create(getMemPool(), e_Handle);
            if (se.x == x && se.y == y) {
                return e_Handle;
            }
        }
        return -1;
    }
    
    /**
     * Returns the list of handles to elements contained in this leaf.
     * @return The list of handles to elements
     */
    public List<Integer> getElementHandles() throws FileNotFoundException, IOException {
        return elementHandles;
    }
    
    /*************************************************************************/
    /* INHERITED METHODS                                                     */
    /*************************************************************************/
    
    /**
     * Adds the element to the leaf node and returns the success. If the number
     * of elements would exceed the maximum, the element is not added.
     * @param xMin The minimum x coordinate in the region
     * @param yMin The minimum y coordinate in the region
     * @param xMax The maximum x coordinate in the region
     * @param yMax The maximum y coordinate in the region
     * @param x The x coordinate of the element
     * @param y The y coordinate of the element
     * @param elementHandle The handle to the element to add
     * @return The handle to the node affected
     */
    @Override
    public int add(double xMin, double yMin, double xMax, double yMax,
                   double x, double y, int elementHandle) {
        
        // Exit if the maximum number of elements has been reached
        if (elementHandles.size() >= MAXIMUM_ELEMENTS) {
            PRQuadInternalNode<T> newInternal = 
                    new PRQuadInternalNode<T>(getMemPool(), -1);
            
            // Delete this leaf node to allow any new leaves to take its place
            this.delete();
            
            // Add the old leaf elements
            for (Integer e_Handle : elementHandles) {
                try {
                    SerialNode se = CityNode.create(getMemPool(), e_Handle);
                    newInternal.add(xMin, yMin, xMax, yMax, se.x, se.y, e_Handle);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(PRQuadLeafNode.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(PRQuadLeafNode.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            // Add the new element
            newInternal.add(xMin, yMin, xMax, yMax, x, y, elementHandle);
            try {
                /// Add the internal node into memory
                newInternal.updateInMemory();
            } catch (IOException ex) {
                Logger.getLogger(PRQuadLeafNode.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            // Return the new internal node with the elements added
            return newInternal.getHandle();
            
        } else {
            // Add the handle to the list
            elementHandles.add(elementHandle);
            
            try {
                // Write this leaf node
                this.updateInMemory();
            } catch (IOException ex) {
                Logger.getLogger(PRQuadLeafNode.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            return getHandle();
        }
    }
    
    /**
     * Removes all elements found with the provided coordinates and returns the
     * leaf node.
     * @param xMin The minimum x coordinate in the region
     * @param yMin The minimum y coordinate in the region
     * @param xMax The maximum x coordinate in the region
     * @param yMax The maximum y coordinate in the region
     * @param x The x coordinate of the element
     * @param y The y coordinate of the element
     * @return The handle to the node affected
     */
    @Override
    public int remove(double xMin, double yMin, double xMax, double yMax,
                                    double x, double y) {
        List<Integer> handlesToRemove = new LinkedList<Integer>();
        for (Integer e_Handle : elementHandles) {
            try {
                SerialNode se = CityNode.create(getMemPool(), e_Handle);
                if (se.x == x && se.y == y) {
                    // Mark for removal
                    handlesToRemove.add(e_Handle);
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(PRQuadLeafNode.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(PRQuadLeafNode.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        // Remove all handles used
        for (Integer e_Handle : handlesToRemove) {
            elementHandles.remove(e_Handle);
        }
        
        // Store the leaf node
        try {
            this.updateInMemory();
        } catch (IOException ex) {
            Logger.getLogger(PRQuadLeafNode.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Check if this leaf should now be a flyweight
        if (elementHandles.isEmpty()) {
            this.delete();
            return -1;
        } else {
            return getHandle();
        }
    }
    
    /**
     * Returns the node containing an element with the coordinates provided or
     * null if no element with those coordinates exists.
     * @param xMin The minimum x coordinate in the region
     * @param yMin The minimum y coordinate in the region
     * @param xMax The maximum x coordinate in the region
     * @param yMax The maximum y coordinate in the region
     * @param x The x coordinate to look for
     * @param y The y coordinate to look for
     * @return The node containing the element with the provided coordinates
     */
    @Override
    public int contains(
            double xMin, double yMin, double xMax, double yMax, 
            double x, double y) {
        for (Integer e_Handle : elementHandles) {
            try {
                SerialNode se = CityNode.create(getMemPool(), e_Handle);
                if (se.x == x && se.y == y) {
                    return getHandle();
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(PRQuadLeafNode.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(PRQuadLeafNode.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return -1;
    }

    /**
     * Returns true as this is a leaf node.
     * @return True
     */
    @Override
    public boolean isLeaf() {
        return true;
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
     * Returns false as this is not the flyweight node.
     * @return False
     */
    @Override
    public boolean isFlyweight() {
        return false;
    }
    
    /**
     * Returns the total number of elements in this leaf.
     * @return The integer number of elements in the node
     */
    @Override
    public int getTotalElements() {
        return elementHandles.size();
    }
    
    /**
     * Returns the string representation of this leaf node by printing out each
     * data element.
     * @return The leaf node in String form
     */
    @Override
    public String toString() {
        String returnString = "";
        for (Integer e_Handle : elementHandles) {
            try {
                returnString += CityNode.create(getMemPool(), e_Handle) + ":";
            } catch (FileNotFoundException ex) {
                Logger.getLogger(PRQuadLeafNode.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(PRQuadLeafNode.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        returnString += "|";
        return returnString;
    }

    /**
     * Converts this leaf node into a byte array with the type being the first 
     * byte and the remaining bytes being the contents of this leaf node.
     * @return The converted byte array
     */
    @Override
    public int saveToBytes(byte[] byteArray) {
        byte type = 2; // Leaf is type 2
        byte size = (byte) (1 + 1 + MAXIMUM_ELEMENTS * 4);
        byteArray[0] = type;
        byteArray[1] = (byte) elementHandles.size();
        int i = 0;
        for (; i < MAXIMUM_ELEMENTS * 4 && i < elementHandles.size() * 4; i += 4) {
            byteArray[i + 2] = (byte) (elementHandles.get(i / 4) >> 24 & 0xFF);
            byteArray[i + 3] = (byte) (elementHandles.get(i / 4) >> 16 & 0xFF);
            byteArray[i + 4] = (byte) (elementHandles.get(i / 4) >> 8 & 0xFF);
            byteArray[i + 5] = (byte) (elementHandles.get(i / 4) & 0xFF);
        }
        for (; i < MAXIMUM_ELEMENTS * 4; i += 4) {
            byteArray[i + 2] = (byte) (-1 >> 24 & 0xFF);
            byteArray[i + 3] = (byte) (-1 >> 16 & 0xFF);
            byteArray[i + 4] = (byte) (-1 >> 8 & 0xFF);
            byteArray[i + 5] = (byte) (-1 & 0xFF);
        }
        return size;
    }

    /**
     * Loads the contents of the byte array into this leaf node.
     * @param byteArray The byte array to load from
     */
    @Override
    public void loadFromBytes(byte[] byteArray) {
        assert (byteArray[0] == 2) :
            "ERROR: Loaded type is not a leaf node!";
        elementHandles.clear();
        
        // Ignore elements 0 and 1
        // 0 = Type
        // 1 = Number of elements
        
        // Load all elements
        for (int i = 0; i < MAXIMUM_ELEMENTS * 4; i += 4) {
            elementHandles.add(((int) byteArray[i + 2]) << 24 & 0xFF000000 |
                               ((int) byteArray[i + 3]) << 16 & 0x00FF0000 |
                               ((int) byteArray[i + 4]) << 8 & 0x0000FF00 |
                               ((int) byteArray[i + 5]) & 0x000000FF);
        }
        
        // Remove any invalid handles
        while (elementHandles.remove(new Integer(-1))) { /* Do nothing */ };
    }

    /**
     * Deletes the leaf node from memory.
     */
    @Override
    public void delete() {
        if (getHandle() == -1) return;
        try {
            getMemPool().remove(getHandle());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PRQuadLeafNode.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PRQuadLeafNode.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
