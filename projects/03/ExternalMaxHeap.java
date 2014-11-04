
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a maximum heap. This class nearly identical to Dr. Shaffer's max
 * heap from his provided source code, except that it was rewritten to gain
 * more practice and a better understanding of maximum heaps.
 * @author rcsvt (Robert C. Senkbeil)
 */
public final class ExternalMaxHeap {
    private BufferBridge bridge; // The bridge connecting the heap to the buffer pool
    private int elementsInHeap; // The total number of elements in the heap,
                                // equivalent to heap.length
    private int maxSize; // The maximum allowed size for the heap
    
    // =====================================================================
    // = CONSTRUCTORS                                                      =
    // =====================================================================
    
    /**
     * Creates a new maximum heap with the provided array and parameters.
     * @param bridge The bridge to be used to provide information to the heap
     * @param elementsInHeap The total number of elements in the heap
     * @param maxSize The maximum supported number of elements in the heap
     */
    public ExternalMaxHeap(BufferBridge bridge, int elementsInHeap, int maxSize) throws FileNotFoundException, IOException {
        this.bridge = bridge;
        this.elementsInHeap = elementsInHeap;
        this.maxSize = maxSize;
        this.buildHeap();
    }
    
    // =====================================================================
    // = PUBLIC METHODS                                                    =
    // =====================================================================
    
    /**
     * Returns the size of the maximum heap.
     * @return The current size
     */
    public int getSize() {
        return this.elementsInHeap;
    }
    
    /**
     * Checks whether or not the provided index points to a leaf in the heap.
     * @param index The index of the node to check
     * @return Whether or not the index points to a leaf
     */
    public boolean isLeaf(int index) {
        // Heaps are balanced with the later half of the elements being leaves
        return (index >= (this.elementsInHeap / 2)) && // Check if is in leaf range
               (index < this.elementsInHeap); // Make sure not over highest value
    }
    
    /**
     * Determines the index of the left child of the node whose index is provided.
     * @param index The node whose left child's index to determine
     * @return The index of the left child
     */
    public int getLeftChildIndex(int index) {
        // Check that it is possible for a left child to exist
        assert (index < (this.elementsInHeap / 2)) : 
                "No left child available!";
        
        // Return the index of the left child
        return (2 * index + 1); // Determined from way heap is stored in array
    }
    
    /**
     * Determines the index of the right child of the node whose index is provided.
     * @param index The node whose right child's index to determine
     * @return The index of the right child
     */
    public int getRightChildIndex(int index) {
        // Check that it is possible for a right child to exist
        assert (index < ((this.elementsInHeap - 1) / 2)) : 
                "No right child available!";
        
        // Return the index of the right child
        return (2 * index + 2); // Determined from way heap is stored in array
    }
    
    /**
     * Determines the index of the parent of the node whose index is provided.
     * @param index The node whose parent's index to determine
     * @return The index of the parent
     */
    public int getParentIndex(int index) {
        // Check that it is possible for a parent to exist
        assert (index > 0) :
                "No parent available!";
        
        // Return the index of the parent
        return ((index - 1) / 2); // Determined from way heap is stored in array
    }
    
    /**
     * Inserts a new value into the maximum heap (does not build the heap).
     * @param value The value to insert into the heap
     */
    public void insert(int value) throws FileNotFoundException, IOException {
        // Prevent insertion if the heap is full
        assert this.elementsInHeap < this.maxSize :
                "Heap is full!";
        
        // Set the index of this element to the last location in the heap
        // and also increment the size of the heap to represent the new size
        int index = this.elementsInHeap++;
        
        // Continuously bubble the new value up until its parent has a value
        // larger than it (to meet the requirements of a maximum heap)
        while ((index != 0) && 
               (this.bridge.compareTo(index, this.getParentIndex(index)) > 0)) {
            this.bridge.swap(index, this.getParentIndex(index)); // Bubble the value up
            index = this.getParentIndex(index); // Replace the value index with
                                                // its new location
        }
    }
    
    /**
     * Builds the heap based on the values it contains.
     */
    public void buildHeap() throws FileNotFoundException, IOException {
        // Sift down all non-leaf nodes starting from the bottom nodes and
        // moving up to the root node
        for (int i = ((this.elementsInHeap - 1) / 2); i >= 0; --i) {
            this.siftDown(i);
        }
    }
    
    /**
     * Removes the maximum value from the heap and returns it.
     * @return The maximum value in the heap
     */
    public int removeMax() throws FileNotFoundException, IOException {
        // Check that the heap is not empty
        assert this.elementsInHeap > 0 :
                "Unable to remove maximum value from empty heap!";
        
        // Swap the maximum value and the last value as well as decrement size
        this.bridge.swap(0, --this.elementsInHeap);
        
        // Place the new root in the correct location (if the heap is not empty)
        if (this.elementsInHeap > 0) {
            this.siftDown(0);
        }
        
        // Return the element removed
        int removedElement = this.bridge.get(this.elementsInHeap);
        return removedElement;
    }
    
    /**
     * Removes the value at the specified index from the heap and returns it.
     * @param index The index of the value to remove
     * @return The value removed
     */
    public int remove(int index) throws FileNotFoundException, IOException {
        // Check that the index fits in the allowed range of values
        assert (index >= 0 && index < this.elementsInHeap) :
                "Invalid index provided in remove function!";
        
        // Check if the last value is being removed, which is a special case
        if (index == (this.elementsInHeap - 1)) {
            --this.elementsInHeap;
        } else {
            // Move the value to be removed to the end of the heap as well as
            // decrement the size of the heap
            this.bridge.swap(index, --this.elementsInHeap);
            
            // Check if the value should be bubbled up
            while ((index > 0) && 
                   (this.bridge.compareTo(index, this.getParentIndex(index)) > 0)) {
                this.bridge.swap(index, this.getParentIndex(index));
                index = this.getParentIndex(index);
            }
            
            // Check if the value should be sifted down
            if (this.elementsInHeap != 0) this.siftDown(index);
        }
        
        // Return the element removed
        int elementRemoved = this.bridge.get(this.elementsInHeap);
        return elementRemoved;
    }
    
    /**
     * Returns the heap array in string form.
     * @return The string array of heap values
     */
    @Override
    public String toString() {
        java.lang.StringBuilder sb = new java.lang.StringBuilder();
        for (int i = 0; i < this.elementsInHeap; ++i) {
            try {
                sb.append(this.bridge.get(i));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ExternalMaxHeap.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ExternalMaxHeap.class.getName()).log(Level.SEVERE, null, ex);
            }
            sb.append(' ');
        }
        return sb.toString();
    }
    
    // =====================================================================
    // = PRIVATE METHODS                                                   =
    // =====================================================================
    
    /**
     * Sifts the value at the specified index down based on the max-heap
     * structure.
     * @param index The index of the value to sift down
     */
    private void siftDown(int index) throws FileNotFoundException, IOException {
        // Check that the index fits in the allowed range of values
        assert (index >= 0 && index < this.elementsInHeap) :
                "Invalid index provided in siftDown function!";
        
        // Continue sifting down until the targeted position is a leaf node
        int iChild = -1;
        while (!this.isLeaf(index)) {
            iChild = this.getLeftChildIndex(index);
            
            // Check if the value in the right child is larger than the left
            if ((iChild < (this.elementsInHeap - 1)) &&
                (this.bridge.compareTo(iChild, iChild + 1) < 0)) // Left < right?
                ++iChild; // Make the child index point to the right child
            
            // Check if the value at the current index is the maximum value,
            // if it is then the sifting process is finished
            if (this.bridge.compareTo(index, iChild) >= 0) return;
            
            // Swap the value at the current index and the largest child
            this.bridge.swap(index, iChild);
            
            // Set index to point to new location
            index = iChild;
        }
    }
    
    // =====================================================================
    // = STATIC METHODS                                                    =
    // =====================================================================
    
    /**
     * Sorts an array using a max heap removal system used from the book
     * "Data Structures & Algorithm Analysis in Java" by Clifford Shaffer.
     * @param f The file to sort
     * @param maxBuffers The total number of buffers allowed in the sort
     * @param state The file to write statistics to
     */
    static void sort(File f, int maxBuffers, File stat) throws FileNotFoundException, IOException {
        long maxSortTime = System.currentTimeMillis();
        long fSize = f.length();
        ExternalMaxHeap maxHeap = 
                new ExternalMaxHeap(
                        new BufferBridge(f, maxBuffers), 
                        (int) (fSize / 4), 
                        (int) (fSize / 4)
                );
        
        // Continuously remove the maximum value from the max heap (the root),
        // which places it at the end of the array
        // E.G. A max heap of n elements becomes a max heap of n-1 elements with
        // the nth element in the array being the previous maximum element
        // This continues as n-1 becomes n-2 with the n-1 element in the array
        // being the maximum element of the n-1 max heap
        for (int i = 0; i < (fSize / 4); ++i) {
            maxHeap.removeMax();
        }
        
        // Flush to make sure all information has been written
        maxHeap.bridge.flush();
        
        // Update the time
        maxSortTime = System.currentTimeMillis() - maxSortTime;
        
        // Print out the keys and values of each block
        java.io.FileInputStream fis = new java.io.FileInputStream(f);
        int count = 0;
        byte[] bytes = new byte[4096];
        while (fis.read(bytes) != -1) {
            // Add a new line if necessary
            if (count++ % 8 == 0) System.out.println();
            int key = ((int) bytes[0]) << 8 & 0x0000FF00 |
                      ((int) bytes[1] & 0x000000FF);
            int value = ((int) bytes[2]) << 8 & 0x0000FF00 |
                        ((int) bytes[3] & 0x000000FF);
            System.out.print(key + "\t" + value + "\t");
        }
        System.out.println();
        
        // Write statistics
        java.io.FileWriter fw = new java.io.FileWriter(stat, true);
        fw.append("File: " + f.getName() + "\n");
        fw.append("Cache Hits: " + maxHeap.bridge.getCacheHits() + "\n");
        fw.append("Cache Misses: " + maxHeap.bridge.getCacheMisses() + "\n");
        fw.append("Disk Reads: " + maxHeap.bridge.getDiskReads() + "\n");
        fw.append("Disk Writes: " + maxHeap.bridge.getDiskWrites() + "\n");
        fw.append("Max Time: " + maxSortTime + " ms\n\n");
        fw.close();
    }
}
