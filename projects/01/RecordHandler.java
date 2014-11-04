
import java.io.IOException;
import java.io.OutputStream;

/**
 * The class that handles the list of records pointing to used space.
 * @author rcsvt Robert C. Senkbeil
 * @author avneeeet Avneet Singh
 */
public class RecordHandler {
    private Handle[] handles;
    private MemPool memPool;
    
    private static final int INTEGER_SIZE = 4;
    
    /**
     * Creates a list of record handles to be associated with the memory pool
     * and connects a memory pool to this handler.
     * @param memPool The memory pool to be associated with this handler
     * @param recordSize The number of records in the record array
     */
    public RecordHandler(MemPool memPool, int recordSize) {
        // Update the reference to the memory pool to be associated with this
        // record handler
        this.memPool = memPool;
        
        // Set all handles to null (represents empty)
        handles = new Handle[recordSize];
        for (int i = 0; i < recordSize; ++i) {
            handles[i] = null;
        }
    }
    
    /**
     * Creates a new record with the provided information, removes any existing
     * record in the provided index, and inserts the new record in that index.
     * @param recordPosition The index to insert the record handle
     * @param x The x location of the city
     * @param y The y location of the city
     * @param name The name of the city
     */
    public void insert(int recordPosition, int x, int y, String name) {
        // Check if the record position is within the bounds of the array
        if (recordPosition < 0 || recordPosition > handles.length - 1) {
            System.err.println("Record position is out of bounds!");
            return;
        }
        
        // Remove the old record, if there is one, and add back the free space
        // of the record
        if (handles[recordPosition] != null) remove(recordPosition);
        
        // Determine the total size of the record to store
        // 4 bytes for each integer
        // X bytes for the string (without termination)
        int recordSize = 2 * INTEGER_SIZE + name.length();
        
        // Search through free spaces for an open slot using bestfit
        int slotLocation = memPool.findOpenSlot(recordSize);
        
        // Return an error if no location available
        if (slotLocation == -1) {
            System.err.println("No free space large enough for record!");
            return;
        }
        
        // Convert record to byte array
        byte[] byteArray = new byte[recordSize];
        recordToByteArray(byteArray, recordSize, x, y, name);
        
        // Add record to memory pool
        int iHandle = memPool.insert(byteArray, recordSize);
        handles[recordPosition] = new Handle(iHandle);
    }
    
    /**
     * Removes the record in the associated recordPosition index.
     * @param recordPosition The index in the record array of the record to remove
     */
    public void remove(int recordPosition) {
        // Check if the record position is within the bounds of the array
        if (recordPosition < 0 || recordPosition > handles.length - 1) {
            System.err.println("Record position is out of bounds!");
            return;
        }
        
        // Remove byte information from memory pool if it exists
        if (handles[recordPosition] != null) {
            memPool.remove(handles[recordPosition].getPosition());
        }
        
        // Remove the record handle
        handles[recordPosition] = null;
    }
    
    /**
     * Prints the information of the record specified by the
     * recordPosition position into the provided string.
     * @param recordPosition The index of the record to print
     * @param oStream The stream to print the record information
     */
    public void print(int recordPosition, OutputStream oStream) throws IOException {
        // Check if the record position is within the bounds of the array
        if (recordPosition < 0 || recordPosition > handles.length - 1) {
            System.err.println("Record position is out of bounds!");
            return;
        }
        
        // Exit if the stream provided is null
        if (oStream == null) return;
        
        // Print out a quick message indicating there is no record here
        if (handles[recordPosition] == null) {
            oStream.write(("Record(" + recordPosition + "): " +
                           "No record found!\n").getBytes());
            return;
        }
        
        // Retrieve the record
        byte[] byteArray = new byte[256];
        int recordSize = memPool.get(byteArray, handles[recordPosition].getPosition(), 256);
        
        // Convert the record to the record information
        int[] loc = new int[2];
        String[] name = new String[1];
        byteArrayToRecord(byteArray, recordSize, loc, name);
        
        // Check that the sizes of the arrays are correct
        if (loc.length < 2 || name.length < 1) return;
        
        // Print out the information about the record
        String message = "Record(" + recordPosition + "): " +
                         "(" + loc[0] + ", " + loc[1] + ") '" + name[0] + "'" +
                         " starting at " + handles[recordPosition].getPosition() +
                         " with a size of "+ recordSize + "\n";
        oStream.write(message.getBytes());
    }
    
    /**
     * Prints all of the records' information in order of the handle list.
     * @param oStream The stream to print the record information 
     */
    public void print(OutputStream oStream) throws IOException {
        // Exit if provided a null stream
        if (oStream == null) return;
        
        // Print Full Dump Indication
        oStream.write("===============\n".getBytes());
        oStream.write("= Full Dump   =\n".getBytes());
        oStream.write("===============\n".getBytes());
        
        // Print the used spaces
        for (int i = 0 ; i < handles.length; ++i) {
            print(i, oStream);
        }
        
        // Print the free spaces
        String message;
        for (int i = 0; i < memPool.getTotalFreeBlocks(); ++i) {
            message = "Free_Space(" + i + "): " +
                       "Starts at " + memPool.getFreeSpaceLocation(i) +
                       " with " + memPool.getFreeSpaceSize(i) + " bytes\n";
            oStream.write(message.getBytes());
        }
        
        // Close in the information so it is readable
        oStream.write("===============\n".getBytes());
    }
    
    /**
     * Converts the record parameters to a byte array.
     * @param byteArray The array to store the converted record into
     * @param size The maximum number of bytes to convert
     * @param x The x location of the city
     * @param y The y location of the city
     * @param name The name of the city
     */
    private void recordToByteArray(
            byte[] byteArray, int size, int x, int y, String name) {
        
        // Convert the x value to four bytes
        if (size >= 4) {
            byteArray[0] = (byte) (x >> 24 & 0xFF);
            byteArray[1] = (byte) (x >> 16 & 0xFF);
            byteArray[2] = (byte) (x >> 8 & 0xFF);
            byteArray[3] = (byte) (x & 0xFF);
        }
        
        // Convert the y value to four bytes
        if (size >= 8) {
            byteArray[4] = (byte) (y >> 24 & 0xFF);
            byteArray[5] = (byte) (y >> 16 & 0xFF);
            byteArray[6] = (byte) (y >> 8 & 0xFF);
            byteArray[7] = (byte) (y & 0xFF);
        }
        
        // Convert the name to a byte array
        for (int i = 0; i < name.length() && (i + 8) < size; ++i) {
            byteArray[8 + i] = name.getBytes()[i];
        }
    }
    
    /**
     * Converts a byte array to record values.
     * Requires an integer array of at least 2 elements and a string array of
     * at least 1 element!
     * @param byteArray The byte array to convert
     * @param size The size of the byte array to convert
     * @param locs The integer array to store the x and y values
     * @param name The string array to store the name
     */
    private void byteArrayToRecord(
            byte[] byteArray, int size, int[] locs, String[] name) {
        
        // Exit if not enough elements allowed for referencing
        if (locs.length < 2 || name.length < 1) return;
        
        // Retrieve the x value from the array
        locs[0] = ((int) byteArray[0]) << 24 & 0xFF000000 |
                  ((int) byteArray[1]) << 16 & 0x00FF0000 |
                  ((int) byteArray[2]) << 8 & 0x0000FF00 |
                  ((int) byteArray[3]) & 0x000000FF;
        
        // Retrieve the y value from the array
        locs[1] = ((int) byteArray[4]) << 24 & 0xFF000000 |
                  ((int) byteArray[5]) << 16 & 0x00FF0000 |
                  ((int) byteArray[6]) << 8 & 0x0000FF00 |
                  ((int) byteArray[7]) & 0x000000FF;
        
        // Retrieve the name of the city
        name[0] = new String(byteArray, 8, size - 8);
    }
    
    /* CONVERTING FROM BYTE ARRAY TO INTEGER
     * ((int) bytes[position]) << 24 & 0xFF000000
     * ((int) bytes[position + 1]) << 16 & 0x00FF0000
     * ((int) bytes[position + 2]) << 8 & 0x0000FF00
     * ((int) bytes[position + 3]) & 0x000000FF
     */
}
