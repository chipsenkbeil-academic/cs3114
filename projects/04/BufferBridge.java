
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
    
    // Block size information
    private int blockSize;
    
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
    public BufferBridge(File file, int maxBuffers, int blockSize) throws FileNotFoundException {
        this.file = file;
        this.rfAccess = new RandomAccessFile(file, "rw");
        this.pool = new BufferPool(maxBuffers, blockSize);
        this.blockSize = blockSize;
        
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
     * Returns the file associated with this buffer bridge.
     * @return The file object
     */
    public File getFile() {
        return this.file;
    }
    
    /**
     * Retrieve the byte at the provided index.
     * @param index The index to find the byte
     * @return The byte retrieved
     */
    public byte getByte(int index) throws IOException {
        // Get the buffer with the byte
        BufferPool.Buffer buffer = this.getBuffer(index);
        int startPosition = (index) - buffer.getBytePosition();
        return buffer.getBytes()[startPosition];
    }
    
    /**
     * Retrieves the bytes starting at the provided index up the the end of the
     * size indicated by the first byte.
     * @param index The index of the start of the array to return
     * @return The size of the byte array returned (not including the size byte)
     */
    public int get(int index, byte[] byteArray) throws FileNotFoundException, IOException {
        // Get the buffer
        BufferPool.Buffer buffer = this.getBuffer(index);
        
        // Get the position relative to the buffer's byte array
        int startPosition = (index) - buffer.getBytePosition();
        int currentPosition = 0;
        
        // Get the total size of bytes to retrieve
        int totalBytes = ((int) buffer.getBytes()[startPosition]) & 0xFF;
        
        // Exit if the byte array is null
        if (byteArray == null) return totalBytes;
        
        // Check if need to get a new buffer
        if (startPosition + currentPosition >= buffer.getSize()) {
            buffer = this.getBuffer(index + currentPosition);
            startPosition = 0;
            currentPosition = 0;
        }
        
        // Retrieve bytes
        for (int i = 0; i < totalBytes + 1; ++i) {
            // Set the byte to return
            byteArray[i] = buffer.getBytes()[startPosition + currentPosition++];
            
            // Check if need to get a new buffer
            if (startPosition + currentPosition >= buffer.getSize()) {
                buffer = this.getBuffer(index + currentPosition);
                startPosition = 0;
                currentPosition = 0;
            }
        }
        
        return totalBytes;
    }
    
    /**
     * Sets the byte at the specified index.
     * @param index The index of the byte to set
     * @param value The value of the byte
     */
    public void writeByte(int index, byte value) throws IOException {
        // Get the first buffer
        BufferPool.Buffer buffer = this.getBuffer(index);
        
        // Get the position relative to the buffer's byte array
        int startPosition = (index) - buffer.getBytePosition();
        
        buffer.setByte(startPosition, value);
    }
    
    /**
     * Writes the set of bytes to the buffer pool at the starting index. Swaps
     * out buffers when needed.
     * @param index The starting index of the write to process
     * @param bArray The array of bytes
     * @param length The length of the array of bytes
     */
    public void write(int index, byte[] bArray, int length) throws IOException {
        // Get the first buffer
        BufferPool.Buffer buffer = this.getBuffer(index);
        
        // Get the position relative to the buffer's byte array
        int startPosition = (index) - buffer.getBytePosition();
        int currentPosition = 0;
        
        // Write each byte
        for (int i = 0; i < length; ++i) {
            // Set the new byte
            buffer.setByte(startPosition + currentPosition++, bArray[i]);
            
            // Check if need to get a new buffer
            if (startPosition + currentPosition >= buffer.getSize()) {
                buffer = this.getBuffer(index + currentPosition);
                startPosition = 0;
                currentPosition = 0;
            }
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
     * Opens the file for reading and writing associated with this buffer bridge.
     */
    public final void open() throws IOException {
        this.close();
        this.rfAccess = new RandomAccessFile(file, "rw");
    }
    
    /**
     * Flushes and closes the file associated with this buffer bridge.
     */
    public final void close() throws IOException {
        this.flush();
        this.rfAccess.close();
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
    
    /**
     * Returns the list of block IDs.
     * @return The String representing the list
     */
    public String getBlockIDs() {
        return pool.getIDList();
    }
    
    // =====================================================================
    // = PRIVATE METHODS                                                   =
    // =====================================================================
    
    /**
     * Gets the buffer associated with the provided index (now represented
     * by the exact byte index and not a multiple of SWAP_SIZE).
     * @param index The index of the buffer to retrieve
     * @return The buffer found
     */
    private BufferPool.Buffer getBuffer(int index) throws IOException {
        // Determine if the buffer pool needs to have the buffer added
        if (!this.pool.hasBufferAt(index)) {
            byte[] bArray = new byte[blockSize];
            int startBlock = (index) / blockSize;
            
            // Go to the block associated with the new buffer and read it
            this.rfAccess.seek(startBlock * blockSize);
            this.rfAccess.read(bArray);
            
            // Update counter for disk reads
            ++this.diskReads;
            
            // Store the binary array with the starting block position into the pool
            BufferPool.Buffer buffer = 
                    this.pool.insert(startBlock * blockSize, bArray);
            
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
        return this.pool.getBufferAt(index);
    }
}
