
import java.awt.Point;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a QuadTree leaf node, which is a node that does not contain
 * any children, but does have elements in it.
 * @author rcsvt Robert C. Senkbeil
 */
public class PRQuadLeafNode<T> implements PRQuadBaseNode<T> {
    
    /**
     * Maximum number of elements before the leaf node splits into pieces.
     */
    public static int MAXIMUM_ELEMENTS = 3;
    
    private List<MappedElement<T>> elements;
    
    /*************************************************************************/
    /* CLASS CONSTRUCTORS                                                    */
    /*************************************************************************/
    
    /**
     * Creates a new instance of a QuadTree leaf node with no elements.
     */
    public PRQuadLeafNode() {
        elements = new LinkedList<MappedElement<T>>();
    }
    
    /*************************************************************************/
    /* CLASS METHODS                                                         */
    /*************************************************************************/
    
    /**
     * Returns the value of the element located at the specified location or
     * null if no element exists at that location.
     * @param x The x coordinate to look for
     * @param y The y coordinate to look for
     * @return The element value
     */
    public T getElementAt(int x, int y) {
        for (MappedElement<T> mE : elements) {
            if (mE.x == x && mE.y == y) {
                return mE.getValue();
            }
        }
        return null;
    }
    
    /**
     * Returns the list of mapped elements contained in this leaf.
     * @return The list of mapped elements
     */
    public List<MappedElement<T>> getMappedElements() {
        return elements;
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
     * @param element The element to add
     * @return Whether or not the element was added
     */
    @Override
    public PRQuadBaseNode<T> add(int xMin, int yMin, int xMax, int yMax,
                                 int x, int y, T element) {
        MappedElement<T> mElement = new MappedElement<T>(x, y, element);
        
        // Exit if the maximum number of elements has been reached
        if (elements.size() >= MAXIMUM_ELEMENTS) {
            PRQuadInternalNode<T> newInternal = 
                    new PRQuadInternalNode<T>(xMin, yMin, xMax, yMax);
            
            // Add the old leaf elements
            for (MappedElement<T> mE : elements) {
                newInternal.add(xMin, yMin, xMax, yMax, mE.x, mE.y, mE.getValue());
            }
            
            // Add the new element
            newInternal.add(xMin, yMin, xMax, yMax, x, y, element);
            
            // Return the new internal node with the elements added
            return newInternal;
            
        } else {
            // Add the element to the leaf
            elements.add(mElement);
            return this;
        }
    }
    
    /**
     * Removes all elements found with the provided coordinates and returns the
     * leaf node.
     * @param x The x coordinate of the element
     * @param y The y coordinate of the element
     * @return Whether or not any elements were removed
     */
    @Override
    public PRQuadBaseNode<T> remove(int x, int y) {
        LinkedList<MappedElement<T>> elementsToRemove =
                new LinkedList<MappedElement<T>>();
        
        // Get a list of elements to remove (not removed in this search to
        // prevent errors with modifications during looping)
        for (MappedElement<T> mE : elements) {
            if (mE.x == x && mE.y == y) {
                elementsToRemove.add(mE);
            }
        }
        
        // Remove all elements found to remove
        elements.removeAll(elementsToRemove);
        
        // Check if this leaf should now be a flyweight
        if (elements.isEmpty()) {
            return PRQuadEmptyNode.getInstance();
        } else {
            return this;
        }
    }
    
    /**
     * Returns the node containing an element with the coordinates provided or
     * null if no element with those coordinates exists.
     * @param x The x coordinate to look for
     * @param y The y coordinate to look for
     * @return The node containing the element with the provided coordinates
     */
    @Override
    public PRQuadBaseNode<T> contains(int x, int y) {
        for (MappedElement<T> mE : elements) {
            if (mE.x == x && mE.y == y) {
                return this;
            }
        }
        return null;
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
        return elements.size();
    }
    
    /**
     * Returns the string representation of this leaf node by printing out each
     * data element.
     * @return The leaf node in String form
     */
    @Override
    public String toString() {
        String returnString = "";
        Iterator<MappedElement<T>> it = elements.iterator();
        while (it.hasNext()) {
            returnString += it.next();
        }
        returnString += "|";
        return returnString;
    }
    
    /*************************************************************************/
    /* INTERNAL CLASSES                                                      */
    /*************************************************************************/
    
    /**
     * Represents an element that has been mapped to a specific point.
     * @param <T> The type of the element mapped
     */
    public class MappedElement<T> extends Point {
        
        private T element;
        
        /**
         * Creates a new mapped element at the specified point.
         * @param x The x coordinate of the location
         * @param y The y coordinate of the location
         * @param element The element to be associated with the location
         */
        public MappedElement(int x, int y, T element) {
            this.element = element;
            this.setLocation(x, y);
        }
        
        /**
         * Returns the value of this mapped element.
         * @return The element represented by the mapped element class
         */
        public T getValue() {
            return element;
        }
        
        /**
         * Sets the value of this mapped element.
         * @param element The new value of the element
         */
        public void setValue(T element) {
            this.element = element;
        }
        
        /**
         * Determines whether this mapped element is within range of a specific
         * pair of coordinates and a given radius.
         * @param x The x coordinate of the point
         * @param y The y coordinate of the point
         * @param radius The radius to check around the point
         * @return Whether or not the location is within range of this element
         */
        public boolean isWithinRange(int x, int y, int radius) {
            return (((int) this.distance(x, y)) <= radius);
        }
        
        
        /**
         * Returns the contents of this mapped element as a string.
         * @return The String representation of the mapped element
         */
        @Override
        public String toString() {
            return this.x + "," + this.y + "," + this.element.toString();
        }
    }
    
}
