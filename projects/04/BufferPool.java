
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/**
 * Represents the buffer pool that contains all of the buffers of a specified
 * block size.
 * @author rcsvt (Robert C. Senkbeil)
 */
public class BufferPool {
    
    private java.util.LinkedList<Buffer> buffers;
    private int maxBuffers;
    private int bufferSize;
    
    // =====================================================================
    // = CONSTRUCTORS                                                      =
    // =====================================================================
    
    /**
     * Creates a new buffer pool with the specified number of buffers.
     * @param maxBuffers The total number of buffers to hold
     * @param bufferSize The size of all buffers
     */
    public BufferPool(int maxBuffers, int bufferSize) {
        this.buffers = new java.util.LinkedList<Buffer>();
        this.maxBuffers = maxBuffers;
        this.bufferSize = bufferSize;
    }
    
    // =====================================================================
    // = PUBLIC METHODS                                                    =
    // =====================================================================
    
    /**
     * Inserts a buffer that has a byte position specified and contains the
     * byte array provided. If the maximum number of buffers has been reached,
     * the last buffer in the list is removed and returned, else null is returned.
     * @param firstBytePosition The location of the first byte
     * @param byteArray The array of bytes
     * @return The buffer removed (if limit has been achieved)
     */
    public Buffer insert(int firstBytePosition, byte[] byteArray) {
        Buffer removedBuffer = null;
        
        // Check if the limit has been exceeded
        if (this.buffers.size() >= this.maxBuffers) {
            // Set the removed buffer to a copy of the last buffer
            removedBuffer = this.buffers.getLast().makeCopy();
            
            // Move the least recently used to the front and wipe it
            Buffer newBuffer = this.buffers.getLast();
            newBuffer.setBytePosition(firstBytePosition);
            newBuffer.setBytes(byteArray);
            newBuffer.makeClean();
            this.buffers.addFirst(newBuffer);
            this.buffers.removeLast();
        } else {
            // Add the buffer to the list after dynamically allocating it
            this.buffers.addFirst(new Buffer(firstBytePosition, byteArray, bufferSize));
        }
        
        // Return the removed buffer
        return removedBuffer;
    }
    
    /**
     * Determines if this pool contains a buffer with the provided index.
     * @param index The index to be contained in a buffer
     * @return Whether or not a buffer exists
     */
    public boolean hasBufferAt(int index) {
        Buffer currentBuffer = null;
        java.util.ListIterator<Buffer> it = this.buffers.listIterator();
        while (it.hasNext()) {
            currentBuffer = it.next();
            int startPosition = currentBuffer.getBytePosition();
            int buffSize = currentBuffer.getSize();
            if (index >= startPosition && index < startPosition + buffSize) {
                this.buffers.remove(currentBuffer);
                this.buffers.addFirst(currentBuffer);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns the buffer that contains the provided index or null if no buffer
     * exists.
     * @param index The index to be contained in a buffer
     * @return The buffer found
     */
    public Buffer getBufferAt(int index) {
        Buffer currentBuffer = null;
        java.util.ListIterator<Buffer> it = this.buffers.listIterator();
        while (it.hasNext()) {
            currentBuffer = it.next();
            int startPosition = currentBuffer.getBytePosition();
            int buffSize = currentBuffer.getSize();
            if (index >= startPosition && index < startPosition + buffSize) {
                this.buffers.remove(currentBuffer);
                this.buffers.addFirst(currentBuffer);
                return currentBuffer;
            }
        }
        return null;
    }
    
    /**
     * Removes all buffers in the pool and returns them as a list.
     * @return The array of buffers
     */
    public List<Buffer> flush() {
        List<Buffer> bList = new LinkedList<Buffer>();
        for (int i = 0; i < this.buffers.size(); ++i) {
            bList.add(this.buffers.get(i));
        }
        this.buffers.clear();
        return bList;
    }
    
    /**
     * Returns a String list of buffers.
     * @return The String list of buffers
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.buffers.size(); ++i) {
            sb.append(this.buffers.get(i).toString());
        }
        return sb.toString();
    }
    
    /**
     * Returns the list of buffer IDs.
     * @return The String representing the list
     */
    public String getIDList() {
        String idList = "";
        Iterator<Buffer> it = buffers.iterator();
        while (it.hasNext()) {
            Buffer currentBuffer = it.next();
            idList += (currentBuffer.getBytePosition() / bufferSize) + " ";
        }
        return idList;
    }
    
    // =====================================================================
    // = PRIVATE METHODS                                                   =
    // =====================================================================
    
    
    // =====================================================================
    // = INNER CLASSES                                                     =
    // =====================================================================
    
    /**
     * A public buffer class used by the buffer pool.
     */
    public class Buffer {
        private boolean dirty;
        private int firstBytePosition;
        private byte[] bytes;
        
        /**
         * Creates a new buffer containing the provided information.
         * @param byteArray The array of bytes to store
         * @param totalBytes The total bytes from the array provided to store
         * @param size The size of the buffer
         */
        public Buffer(int firstBytePosition, byte[] byteArray, int size) {
            this.bytes = new byte[size];
            for (int i = 0; i < byteArray.length; ++i) {
                this.bytes[i] = byteArray[i];
            }
            this.firstBytePosition = firstBytePosition;
            this.dirty = false;
        }
        
        /**
         * Sets the buffer modified flag to true.
         */
        public void makeDirty() {
            this.dirty = true;
        }
        
        /**
         * Sets the buffer modified flag to false.
         */
        public void makeClean() {
            this.dirty = false;
        }
        
        /**
         * Returns the buffer modified flag.
         * @return Whether or not the buffer is dirty
         */
        public boolean isDirty() {
            return this.dirty;
        }
        
        /**
         * Returns the byte position of this buffer.
         * @return The starting byte position of this buffer
         */
        public int getBytePosition() {
            return this.firstBytePosition;
        }
        
        /**
         * Sets the byte position of this buffer.
         * @param firstBytePosition The new byte position
         */
        public void setBytePosition(int firstBytePosition) {
            this.firstBytePosition = firstBytePosition;
        }
        
        /**
         * Returns the bytes represented by the buffer.
         * @return The array of bytes
         */
        public byte[] getBytes() {
            return this.bytes;
        }
        
        /**
         * Sets the bytes to the byte array provided.
         * @param byteArray The new array of bytes
         */
        public void setBytes(byte[] byteArray) {
            this.bytes = new byte[byteArray.length];
            for (int i = 0; i < byteArray.length; ++i) {
                this.bytes[i] = byteArray[i];
            }
        }
        
        /**
         * Returns the size of the buffer.
         * @return The integer size
         */
        public int getSize() {
            return this.bytes.length;
        }
        
        /**
         * Sets the byte at the specified index within the buffer.
         * @param index The index of the byte to set
         * @param value The new value for the byte in the buffer
         */
        public void setByte(int index, byte value) {
            this.bytes[index] = value;
            this.makeDirty();
        }
        
        /**
         * Swaps the bytes at the provided indices with the provided sizes.
         * E.G. With a size of 3 and indices 0 and 4, range 0 through 2 move to
         * range 4 through 6 and range 4 through 6 move to 0 through 2.
         * @param index1 The index of the first range to swap
         * @param index2 The index of the second range to swap
         * @param size The size of the range to swap
         */
        public void swapBytes(int index1, int index2, int size) {
            // Swap all bytes
            for (int i = 0; i < size; ++i) {
                byte temp = this.bytes[index1 + i];
                this.bytes[index1 + i] = this.bytes[index2 + i];
                this.bytes[index2 + i] = temp;
            }
            
            // Make dirty now
            this.makeDirty();
        }
        
        /**
         * Returns a deep copy of this buffer.
         * @return The copy of this buffer
         */
        public Buffer makeCopy() {
            Buffer copy = new Buffer(this.firstBytePosition, this.bytes, bufferSize);
            if (this.dirty) copy.makeDirty();
            return copy;
        }
        
        /**
         * Returns the byte position of the buffer in a string format.
         * @return The String byte position
         */
        @Override
        public String toString() {
            return "{" + this.getBytePosition() + "}";
        }
    }
}
