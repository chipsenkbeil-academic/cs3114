
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

/**
 * Represents a class that bridges the connection between the external max heap
 * and the buffer pool containing information. It provides routines to read
 * information from a file and compare indices (built to use a two-byte value
 * for comparisons and move a four-byte value).
 * @author rcsvt (Robert C. Senkbeil)
 */
public class BufferBridge {
    
    // Constants for processing information
    private static final int BLOCK_SIZE = 4096;
    private static final int COMPARE_SIZE = 2;
    private static final int SWAP_SIZE = 4;
    
    // File and buffer references
    private File file;
    private RandomAccessFile rfAccess;
    public BufferPool pool;
    
    // Statistical information
    private int cacheHits;
    private int cacheMisses;
    private int diskReads;
    private int diskWrites;
    
    // =====================================================================
    // = CONSTRUCTORS                                                      =
    // =====================================================================
    
    /**
     * Creates a new instance of the buffer bridge class that targets the
     * specified file.
     * @param file The file to access
     * @param maxBuffers The total number of buffers supported
     */
    public BufferBridge(File file, int maxBuffers) throws FileNotFoundException {
        this.file = file;
        this.rfAccess = new RandomAccessFile(file, "rw");
        this.pool = new BufferPool(maxBuffers);
        
        // Initialize statistics
        this.cacheHits = 0;
        this.cacheMisses = 0;
        this.diskReads = 0;
        this.diskWrites = 0;
    }
    
    // =====================================================================
    // = PUBLIC METHODS                                                    =
    // =====================================================================
    
    /**
     * Gets the value at the specified index (index jumps by 4 bytes).
     * @param index The index of the key to retrieve
     * @return The key value
     */
    public int get(int index) throws FileNotFoundException, IOException {
        // Get the buffer
        BufferPool.Buffer buffer = this.getBuffer(index);
        
        // Grab the bytes from the buffer
        byte[] bArray = buffer.getBytes();
        
        // Get the position relative to the buffer's byte array
        int startPosition = (index * SWAP_SIZE) - buffer.getBytePosition();
        
        // Determine the key to return and return it
        int key = ((int) bArray[startPosition]) << 8 & 0x0000FF00 |
                  ((int) bArray[startPosition + 1] & 0x000000FF);
        return key;
    }
    
    /**
     * Compares the values at the two indices. Returns -1 if the value at index1
     * is less than the value at index2. Returns 0 if the values are equal.
     * Returns 1 if the value at index1 is greater than the value at index2. 
     * @param index1 The index of the first value
     * @param index2 The index of the second value
     * @return The result of the comparison
     */
    public int compareTo(int index1, int index2) throws FileNotFoundException, IOException {
        int v1 = get(index1), v2 = get(index2);
        if (v1 > v2) {
            return 1;
        } else if (v1 == v2) {
            return 0;
        } else {
            return -1;
        }
    }
    
    /**
     * Swaps the values at the specified indices.
     * @param index1 The first index
     * @param index2 The second index
     */
    public void swap(int index1, int index2) throws IOException {
        // Get the first buffer
        BufferPool.Buffer firstBuffer = this.getBuffer(index1);
        
        // Store the byte array
        int firstBufferStart = (index1 * SWAP_SIZE) - firstBuffer.getBytePosition();
        byte[] firstBytes = firstBuffer.getBytes();
        byte[] temp1 = new byte[SWAP_SIZE];
        for (int i = 0; i < SWAP_SIZE; ++i) {
            temp1[i] = firstBytes[firstBufferStart + i];
        }
        
        // Swap the bytes with the second buffer
        BufferPool.Buffer secondBuffer = this.getBuffer(index2);
        int secondBufferStart = (index2 * SWAP_SIZE) - secondBuffer.getBytePosition();
        byte[] secondBytes = secondBuffer.getBytes();
        byte[] temp2 = new byte[SWAP_SIZE];
        for (int i = 0; i < SWAP_SIZE; ++i) {
            // Store temporarily the second buffer's byte
            temp2[i] = secondBytes[secondBufferStart + i];
            
            // Set the new value for the second buffer's byte
            secondBuffer.setByte(secondBufferStart + i, temp1[i]);
        }
        
        // Swap the bytes with the first buffer
        firstBuffer = this.getBuffer(index1);
        firstBufferStart = (index1 * SWAP_SIZE) - firstBuffer.getBytePosition();
        temp1 = new byte[SWAP_SIZE];
        for (int i = 0; i < SWAP_SIZE; ++i) {
            firstBuffer.setByte(firstBufferStart + i, temp2[i]);
        }
    }
    
    /**
     * Flushes the buffer pool and writes all data.
     */
    public void flush() throws IOException {
        List<BufferPool.Buffer> buffers = this.pool.flush();
        BufferPool.Buffer currentBuffer = null;
        for (int i = 0; i < buffers.size(); ++i) {
            currentBuffer = buffers.get(i);
            
            // Exit if the block does not need to be written
            if (!currentBuffer.isDirty()) continue;
            
            // Get the bytes and write them
            byte[] bytes = currentBuffer.getBytes();
            this.rfAccess.seek(currentBuffer.getBytePosition());
            this.rfAccess.write(bytes);
            
            // Update counter for disk writes
            ++this.diskWrites;
        }
    }

    /**
     * Returns the number of times the buffer bridge has been able to find the
     * requested data within the buffer pool.
     * @return The integer count
     */
    public int getCacheHits() {
        return cacheHits;
    }

    /**
     * Returns the number of times the buffer bridge has been unable to find the
     * requested data within the buffer pool.
     * @return The integer count
     */
    public int getCacheMisses() {
        return cacheMisses;
    }

    /**
     * Returns the number of times the buffer bridge has had to read from the disk.
     * @return The integer count
     */
    public int getDiskReads() {
        return diskReads;
    }

    /**
     * Returns the number of times the buffer bridge has had to write to the disk.
     * @return The integer count
     */
    public int getDiskWrites() {
        return diskWrites;
    }
    
    // =====================================================================
    // = PRIVATE METHODS                                                   =
    // =====================================================================
    
    /**
     * Gets the buffer associated with the provided index.
     * @param index The index of the buffer to retrieve
     * @return The buffer found
     */
    private BufferPool.Buffer getBuffer(int index) throws IOException {
        // Determine if the buffer pool needs to have the buffer added
        if (!this.pool.hasBufferAt(index * SWAP_SIZE)) {
            byte[] bArray = new byte[BLOCK_SIZE];
            int startBlock = (index * SWAP_SIZE) / BLOCK_SIZE;
            
            // Go to the block associated with the new buffer and read it
            this.rfAccess.seek(startBlock * BLOCK_SIZE);
            this.rfAccess.read(bArray);
            
            // Update counter for disk reads
            ++this.diskReads;
            
            // Store the binary array with the starting block position into the pool
            BufferPool.Buffer buffer = 
                    this.pool.insert(startBlock * BLOCK_SIZE, bArray);
            
            // Write the buffer if there is a buffer being removed and it has been modified
            if (buffer != null && buffer.isDirty()) {
                this.rfAccess.seek(buffer.getBytePosition());
                this.rfAccess.write(buffer.getBytes());
                
                // Update counter for disk writes
                ++this.diskWrites;
            }
            
            // Update counter for cache misses
            ++this.cacheMisses;
        } else {
            // Update counter for cache hits
            ++this.cacheHits;
        }
        
        // Return the buffer associated with this index
        return this.pool.getBufferAt(index * SWAP_SIZE);
    }
}
