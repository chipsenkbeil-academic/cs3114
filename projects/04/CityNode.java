
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Represents a city that contains a location and name.
 * @author rcsvt Robert C. Senkbeil
 */
public class CityNode extends SerialNode {
    
    private int namePtr;
    
    /**
     * Creates a new instance of the CityNode with the provided data.
     * @param memPool The memory pool associated with this city node
     * @param x The x coordinate of the city
     * @param y The y coordinate of the city
     * @param namePtr The pointer to the name of the city
     */
    public CityNode(MemPool memPool, int x, int y, int namePtr) {
        setMemPool(memPool);
        setHandle(-1);
        setX_Int(x);
        setY_Int(y);
        this.namePtr = namePtr;
    }

    /**
     * Retrieves the pointer to the name of the city.
     * @return The integer pointer
     */
    public int getNamePtr() {
        return namePtr;
    }
    
    /**
     * Retrieves the name of the city based on the name pointer.
     * @return The String name
     */
    public String getName() {
        if (namePtr == -1) return "";
        byte[] temp = new byte[256];
        try {
            int tempSize = getMemPool().get(temp, namePtr, 256);
            return new String(Arrays.copyOf(temp, tempSize));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CityNode.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        } catch (IOException ex) {
            Logger.getLogger(CityNode.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }

    

    /**
     * Sets the pointer to the name of the city
     * @param namePtr The new pointer to the name
     */
    public void setNamePtr(int namePtr) {
        this.namePtr = namePtr;
    }
    
    /**
     * Determines if a given CityNode is equal to this CityNode.
     * @param node The node to compare
     * @return Whether or not they are equal
     */
    public boolean equals(CityNode node) {
        return (this.getX() == node.getX() &&
                this.getY() == node.getY() &&
                this.getNamePtr() == node.getNamePtr());
    }
    
    /**
     * Returns the name of the city.
     * @return The name of the city
     */
    @Override
    public String toString() {
        return getX_Int() + "," + getY_Int() + "," + this.getName();
    }
    
    /**
     * Converts this city node into a byte array with the location being 8 bytes
     * and the name being variable-length.
     * @return The size of the byte array
     */
    @Override
    public int saveToBytes(byte[] byteArray) {
        int size = 4 + 4 + 4;
        // Convert the x value to four bytes
        byteArray[0] = (byte) (getX_Int() >> 24 & 0xFF);
        byteArray[1] = (byte) (getX_Int() >> 16 & 0xFF);
        byteArray[2] = (byte) (getX_Int() >> 8 & 0xFF);
        byteArray[3] = (byte) (getX_Int() & 0xFF);
        
        // Convert the y value to four bytes
        byteArray[4] = (byte) (getY_Int() >> 24 & 0xFF);
        byteArray[5] = (byte) (getY_Int() >> 16 & 0xFF);
        byteArray[6] = (byte) (getY_Int() >> 8 & 0xFF);
        byteArray[7] = (byte) (getY_Int() & 0xFF);
        
        // Convert the name pointer to a byte array
        byteArray[8] = (byte) (getNamePtr() >> 24 & 0xFF);
        byteArray[9] = (byte) (getNamePtr() >> 16 & 0xFF);
        byteArray[10] = (byte) (getNamePtr() >> 8 & 0xFF);
        byteArray[11] = (byte) (getNamePtr() & 0xFF);
        
        return size;
    }

    /**
     * Loads the contents of the byte array into this city node.
     * @param byteArray The byte array to load from
     * @param size The total number of bytes to load
     */
    @Override
    public void loadFromBytes(byte[] byteArray) {
        // Retrieve the x value from the array
        setX_Int( 
            ((int) byteArray[0]) << 24 & 0xFF000000 |
            ((int) byteArray[1]) << 16 & 0x00FF0000 |
            ((int) byteArray[2]) << 8 & 0x0000FF00 |
            ((int) byteArray[3]) & 0x000000FF
        );
        
        // Retrieve the y value from the array
        setY_Int(
            ((int) byteArray[4]) << 24 & 0xFF000000 |
            ((int) byteArray[5]) << 16 & 0x00FF0000 |
            ((int) byteArray[6]) << 8 & 0x0000FF00 |
            ((int) byteArray[7]) & 0x000000FF
        );
        
        // Retrieve the pointer to the name of the city
        setNamePtr( 
            ((int) byteArray[8]) << 24 & 0xFF000000 |
            ((int) byteArray[9]) << 16 & 0x00FF0000 |
            ((int) byteArray[10]) << 8 & 0x0000FF00 |
            ((int) byteArray[11]) & 0x000000FF
        );
    }
    
    /**
     * Removes this city node and the name associated with it from memory.
     * @param memPool The memory pool associated with this city handle
     */
    @Override
    public void delete() {
        try {
            getMemPool().remove(getNamePtr());
            getMemPool().remove(getHandle());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CityNode.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CityNode.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Creates a new city node from the handle provided.
     * @param memPool The byte array to retrieve the city node contents
     * @param handle The handle to the city node
     * @return The new city node
     */
    public static CityNode create(MemPool memPool, int handle) throws FileNotFoundException, IOException {
        CityNode newNode = new CityNode(memPool, 0, 0, -1);
        newNode.setHandle(handle);
        newNode.loadFromMemory();
        return newNode;
    }
}
