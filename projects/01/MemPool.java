/**
 * This class represents the memory pool and provides functions to insert,
 * remove, and retrieve records. It also keeps track of free blocks of space.
 * @author rcsvt Robert C. Senkbeil
 * @author avneeeet Avneet Singh
 */
public class MemPool {
    
    private LList<FreeSpace> freeBlocks;
    private byte[] byteArray;
    private int poolSize;
    
    /**
     * Creates a new instance of the Memory Pool with the specified number
     * of bytes allocated.
     * @param poolSize The number of bytes to be allocated for the memory pool
     */
    public MemPool(int poolSize) {
        // Allocate the space for the byte array
        byteArray = new byte[poolSize];
        this.poolSize = poolSize;
        
        // Create the free block representing the entire memory pool
        freeBlocks = new LList<FreeSpace>();
        freeBlocks.append(new FreeSpace(0, poolSize));
    }
    
    /**
     * Returns the size of the pool created.
     * @return The integer size
     */
    public int getPoolSize() {
        return this.poolSize;
    }
    
    /**
     * This inserts a new record into the memory pool.
     * @param space The byte array of the record to insert
     * @param size The size of the record to insert
     * @return The handle pointing to the position in the byte array
     */
    public int insert(byte[] space, int size) {
        // Locate slot using bestfit algorithm
        int startLocation = findOpenSlot(size);
        int totalSize = size + 1; // Factors in the size byte
        
        // Insert the information into the free space
        if (startLocation != -1) {
            // Move to the free block with the matching handle
            freeBlocks.moveToPos(findBestSpace(size));
            
            // Get space left over after new record inserted
            int leftoverSpace = freeBlocks.getValue().getSpace() - totalSize;
            
            // Remove the free space from the list
            freeBlocks.remove();
            
            // Insert new record at postion, the first byte indicates the size
            // of the record NOT including the size byte itself
            byteArray[startLocation] = (byte) size;
            for (int i = 0; i < size; ++i) {
                byteArray[startLocation + i + 1] = space[i];
            }
            
            // Check if need to create a new free space block to represent the
            // leftover space and insert it into the list if created
            if (leftoverSpace > 0) {
                FreeSpace remainingSpace = 
                        new FreeSpace(startLocation + totalSize, leftoverSpace);
                
                // Add to the list of free spaces and resort the list
                freeBlocks.append(remainingSpace);
                sortFreeSpace();
                mergeFreeSpace();
            }
        }
        
        // Return the handle of the new record (or -1 if not created)
        return startLocation;
    }
    
    /**
     * Removes the record associated with the provided handle
     * @param handle The integer position of the start of the record to remove
     */
    public void remove(int handle) {
        // Create a new free space block to fill in this space
        // Adds the single byte used to indicate size to the total bytes
        FreeSpace newSpace = 
                new FreeSpace(handle,
                (((int) byteArray[handle]) & 0xFF) + 1);
        
        // Clear bytes associated with the record
        // Adds the single byte used to indicate size to the total bytes
        clearBytes(
                handle, // Position
                (((int) byteArray[handle]) & 0xFF) + 1
        );

        // Insert the free space into the list of spaces, sort, and merge it
        freeBlocks.append(newSpace);
        sortFreeSpace();
        mergeFreeSpace();
    }
    
    /**
     * Returns the array of bytes representing the record pointed to by the
     * provided handle up to the number of bytes specified (no more than the
     * size of the record).
     * @param space The array to store the bytes
     * @param handle The handle pointing to the record
     * @param size The total bytes of the array provided
     */
    public int get(byte[] space, int handle, int size) {
        // Get unsigned byte size
        int recordSize = ((int) byteArray[handle]) & 0xFF;
        
        for (int i = handle + 1;
             i < handle + recordSize + 1 && i < handle + size + 1; ++i) {
            space[i - handle - 1] = byteArray[i];
        }
        
        return recordSize;
    }
    
    /**
     * Retrieves the starting byte position of a free space.
     * @param index The free space in the list whose location to retrieve
     * @return The location
     */
    public int getFreeSpaceLocation(int index) {
        // Check if the index is within the bounds of the array
        if (index < 0 || index > freeBlocks.length() - 1) {
            System.err.println("Free space index is out of bounds!");
            return -1;
        }
        
        freeBlocks.moveToPos(index);
        int location = freeBlocks.getValue().getHandlePosition();
        freeBlocks.moveToStart();
        
        return location;
    }
    
    /**
     * Retrieves the byte size of a free space.
     * @param index The free space in the list whose size to retrieve
     * @return The total bytes as an integer
     */
    public int getFreeSpaceSize(int index) {
        // Check if the index is within the bounds of the array
        if (index < 0 || index > freeBlocks.length() - 1) {
            System.err.println("Free space index is out of bounds!");
            return -1;
        }
        
        freeBlocks.moveToPos(index);
        int size = freeBlocks.getValue().getSpace();
        freeBlocks.moveToStart();
        
        return size;
    }
    
    /**
     * Returns the total number of free blocks (not the total free space
     * available in the memory pool).
     * @return The total number of free blocks
     */
    public int getTotalFreeBlocks() {
        return freeBlocks.length();
    }
    
    /**
     * Returns the index of best free space to place the
     * specified number of bytes.
     * @param space The number of bytes to potentially place
     * @return The location of the free space in the list of spaces
     */
    private int findBestSpace(int space) {
        int currentSpace = -1;
        int freeSize = -1;
        
        // Exit early if there is no free space
        if (freeBlocks.length() == 0) return -1;
        
        // Start at beginning to search for free block
        freeBlocks.moveToStart();
        freeSize = freeBlocks.getValue().getSpace() + 2; // Make one larger than
                                                         // the maximum size
        
        for (int i = 0; i < freeBlocks.length(); ++i) {
            // Check if there is enough space with this block (ignores blocks
            // of the same size as the current best block)
            if (freeBlocks.getValue().getSpace() >= space + 1 &&
                freeBlocks.getValue().getSpace() < freeSize) {
                currentSpace = i;
                freeSize = freeBlocks.getValue().getSpace();
            } else if (freeBlocks.getValue().getSpace() < space + 1) {
                break;
            }
            
            // Check next block
            freeBlocks.next();
        }
        
        // Reset location to beginning
        freeBlocks.moveToStart();
        
        // Return the location (or -1 if not found)
        return currentSpace;
    }
    
    /**
     * Returns an integer pointing to the available location in the byte array
     * to store the specified number of bytes plus the size byte.
     * Returns -1 if no location is available to use.
     * @param space The number of bytes to store
     * @return The Handle to store the bytes
     */
    public int findOpenSlot(int space) {
        int freeBlockIndex = findBestSpace(space);
        int byteIndex = -1;
        
        // Get starting location of best free space in the byte array
        if (freeBlockIndex != -1) {
            freeBlocks.moveToPos(freeBlockIndex);
            byteIndex = freeBlocks.getValue().getHandlePosition();
            freeBlocks.moveToStart();
        }
        
        // Returns the starting location of the block or -1 if there is no block
        return byteIndex;
    }
    
    /**
     * Merges all adjacent free space blocks.
     */
    private void mergeFreeSpace() {
        // Exit if there's one or zero free blocks
        if (freeBlocks.length() < 2) return;
        
        // Represents the current space comparing to all other spaces
        // and its position in the list
        FreeSpace currentSpace = null;
        int currentIndexPosition = 0;
        int currentPosition = 0;
        int otherPosition = 0;
        
        // Start at beginning of free block list
        freeBlocks.moveToStart();
        
        // Loop through and find the two adjacent free blocks
        while (freeBlocks.currPos() != freeBlocks.length()) {
            // Get the current free block and its position
            currentSpace = freeBlocks.getValue();
            currentPosition = freeBlocks.getValue().getHandlePosition();
            
            // Loop through and check all possible remaining free blocks
            // to see if they are connected
            for (int i = currentIndexPosition + 1; i < freeBlocks.length(); ++i) {
                // Move to next block
                freeBlocks.next();
                
                // Get the starting position of the other free block
                otherPosition = freeBlocks.getValue().getHandlePosition();
                
                // Compare free space to see if adjacent
                if (currentSpace.isAdjacent(freeBlocks.getValue())) {
                    // Get the combined size of the two free spaces
                    int totalSize = 
                            currentSpace.getSpace() + freeBlocks.getValue().getSpace();
                    
                    // Determine which block is the left of the two being merged
                    // and grab its position
                    int position = (currentPosition < otherPosition) ? 
                                    currentPosition : otherPosition;
                    
                    // Create a new free space at the start of the left smaller
                    // free space with the size of the two merged spaces
                    FreeSpace mergedBlock = 
                            new FreeSpace(position, totalSize);
                    
                    // Remove other space
                    freeBlocks.remove();
                    
                    // Remove current space
                    freeBlocks.moveToPos(currentIndexPosition);
                    freeBlocks.remove();
                    
                    // Append new free space to end of list
                    freeBlocks.append(mergedBlock);
                    
                    // Sort free block list and exit
                    sortFreeSpace();
                    
                    return;
                }
            }
            
            // Move to next free block in list
            freeBlocks.moveToPos(++currentIndexPosition);
        }
        
        // Move back to beginning of free space list
        freeBlocks.moveToStart();
    }
    
    /**
     * Sorts the list of free spaces in descending order of size and, in the
     * case of having the same size, in ascending order of handle position.
     */
    private void sortFreeSpace() {
        // Create an empty free space list to populate in decending order
        // of spaces
        LList<FreeSpace> newSpaceList = new LList<FreeSpace>();
        
        // Loop through and move free spaces from the old list to the new
        // list in decending order of sizes and, in the case of matching sizes,
        // ascending order of handles
        while (freeBlocks.length() != 0) {
            // Start at beginning of free block list
            freeBlocks.moveToStart();
            
            int largestHandle = freeBlocks.getValue().getHandlePosition();
            int largestSize = freeBlocks.getValue().getSpace();
            int largestSpacePos = 0;
            for (int i = 1; i < freeBlocks.length(); ++i) {
                freeBlocks.next();
                
                // New block is larger than current largest block
                if (freeBlocks.getValue().getSpace() > largestSize) {
                    largestHandle = freeBlocks.getValue().getHandlePosition();
                    largestSize = freeBlocks.getValue().getSpace();
                    largestSpacePos = freeBlocks.currPos();
                    
                // New block is equal in size, but has an earlier handle
                } else if (freeBlocks.getValue().getSpace() == largestSize) {
                    if (freeBlocks.getValue().getHandlePosition() < largestHandle) {
                        largestHandle = freeBlocks.getValue().getHandlePosition();
                        largestSize = freeBlocks.getValue().getSpace();
                        largestSpacePos = freeBlocks.currPos();
                    }
                }
            }
            
            // Move to next free space to insert
            freeBlocks.moveToPos(largestSpacePos);
            
            // Insert the free space into the new list and remove it from the
            // old list
            newSpaceList.append(freeBlocks.remove());
        }
        
        // Start new list at beginning
        newSpaceList.moveToStart();
        
        // Move new list of free spaces to free block list
        freeBlocks = newSpaceList;
    }
    
    /**
     * Zeroes all bytes specified in the byte array. Has bound
     * @param startingPosition The first byte to clear in the byte array
     * @param totalBytes The total number of bytes to clear in the byte array
     */
    private void clearBytes(int startingPosition, int totalBytes) {
        // Exit if not in bounds
        if (startingPosition < 0) return;
        
        // Loop through and clear bytes until total bytes are cleared or 
        // at the end of the byte array
        for (int i = startingPosition;
                 i < startingPosition + totalBytes && i < byteArray.length;
               ++i) {
            byteArray[i] = 0;
        }
    }
    
        /**
     * Represents a block of free space.
     */
    private class FreeSpace {
        
        private int handlePosition;
        private int space;
        
        /**
         * Creates a new FreeSpace information.
         * @param handlePosition The start of the free space
         * @param space The total amount of free space
         */
        public FreeSpace(int handlePosition, int space) {
            this.handlePosition = handlePosition;
            this.space = space;
        }

        /**
         * Returns the handle to the start of the free space.
         * @return The starting position as an integer
         */
        public int getHandlePosition() {
            return handlePosition;
        }

        /**
         * Returns the total space of this free space.
         * @return The space as an integer
         */
        public int getSpace() {
            return space;
        }
        
        /**
         * Checks to see if the other space is adjacent to this one.
         * @param otherSpace The other free space to compare
         * @return Whether or not the two spaces are adjacent
         */
        public boolean isAdjacent(FreeSpace otherSpace) {
            // Represents the end of the left free block
            int endOfSpace = 0;
            
            // Check if the other space is to the left or right of the
            // current space
            if (this.handlePosition < otherSpace.getHandlePosition()) { // Right
                endOfSpace = this.handlePosition + this.space - 1;
                return (endOfSpace == otherSpace.getHandlePosition() - 1);
            } else { // Left
                endOfSpace = otherSpace.handlePosition + otherSpace.space - 1;
                return (endOfSpace == this.getHandlePosition() - 1);
            }
        }
    }
}
