/**
 * Represents a single memory handle.
 * @author rcsvt Robert C. Senkbeil
 * @author avneeeet Avneet Singh
 */
public class Handle {
    
    private int position;
    
    /**
     * Creates a new handle that points to the specified position.
     * @param position The new integer position
     */
    public Handle(int position) {
        this.position = position;
    }

    /**
     * Returns the position this handle points to.
     * @return The integer position
     */
    public int getPosition() {
        return position;
    }
    
    /* CONVERTING FROM BYTE ARRAY TO INTEGER
     * ((int) bytes[position]) << 24 & 0xFF000000
     * ((int) bytes[position + 1]) << 16 & 0x00FF0000
     * ((int) bytes[position + 2]) << 8 & 0x0000FF00
     * ((int) bytes[position + 3]) & 0x000000FF
     */
}
