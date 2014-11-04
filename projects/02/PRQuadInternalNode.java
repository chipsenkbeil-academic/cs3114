
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a QuadTree internal node that has children and no elements of
 * its own.
 * @author rcsvt Robert C. Senkbeil
 */
public class PRQuadInternalNode<T> implements PRQuadBaseNode<T> {
    
    private PRQuadBaseNode<T> northWest, northEast, southWest, southEast;
    private int xMin, yMin, xMax, yMax;
    
    /*************************************************************************/
    /* CLASS CONSTRUCTORS                                                    */
    /*************************************************************************/
    
    /**
     * Creates a new instance of an internal node without any children.
     * @param xMin The minimum x coordinate in the region
     * @param yMin The minimum y coordinate in the region
     * @param xMax The maximum x coordinate in the region
     * @param yMax The maximum y coordinate in the region
     */
    public PRQuadInternalNode(int xMin, int yMin, int xMax, int yMax) {
        this(xMin, yMin, xMax, yMax, null, null, null, null);
    }

    /**
     * Creates a new instance of an internal QuadTree node.
     * @param xMin The minimum x coordinate in the region
     * @param yMin The minimum y coordinate in the region
     * @param xMax The maximum x coordinate in the region
     * @param yMax The maximum y coordinate in the region
     * @param northWest The node in the northwest region of this node
     * @param northEast The node in the northeast region of this node
     * @param southWest The node in the southwest region of this node
     * @param southEast The node in the southeast region of this node
     */
    public PRQuadInternalNode(
            int xMin, int yMin, int xMax, int yMax,
            PRQuadBaseNode<T> northWest, PRQuadBaseNode<T> northEast,
            PRQuadBaseNode<T> southWest, PRQuadBaseNode<T> southEast) {
        this.xMin = xMin;
        this.yMin = yMin;
        this.xMax = xMax;
        this.yMax = yMax;
        setNorthWest(northWest);
        setNorthEast(northEast);
        setSouthWest(southWest);
        setSouthEast(southEast);
    }
    
    /*************************************************************************/
    /* CLASS METHODS                                                         */
    /*************************************************************************/

    /**
     * Retrieves the node located in the northwest region of this node.
     * @return The QuadTree node
     */
    public PRQuadBaseNode<T> getNorthWest() {
        return northWest;
    }

    /**
     * Retrieves the node located in the northeast region of this node.
     * @return The QuadTree node
     */
    public PRQuadBaseNode<T> getNorthEast() {
        return northEast;
    }

    /**
     * Retrieves the node located in the southwest region of this node.
     * @return The QuadTree node
     */
    public PRQuadBaseNode<T> getSouthWest() {
        return southWest;
    }

    /**
     * Retrieves the node located in the southeast region of this node.
     * @return The QuadTree node
     */
    public PRQuadBaseNode<T> getSouthEast() {
        return southEast;
    }

    /**
     * Sets the node located in the northwest region of this node.
     * @param northWest The QuadTree node
     * @return The northwest node
     */
    public final PRQuadBaseNode<T> setNorthWest(PRQuadBaseNode<T> northWest) {
        if (northWest == null) {
            this.northWest = PRQuadEmptyNode.getInstance();
        } else {
            this.northWest = northWest;
        }
        return this.northWest;
    }

    /**
     * Sets the node located in the northeast region of this node.
     * @param northEast The QuadTree node
     * @return The northeast node
     */
    public final PRQuadBaseNode<T> setNorthEast(PRQuadBaseNode<T> northEast) {
        if (northEast == null) {
            this.northEast = PRQuadEmptyNode.getInstance();
        } else {
            this.northEast = northEast;
        }
        return this.northEast;
    }

    /**
     * Sets the node located in the southwest region of this node.
     * @param southWest The QuadTree node
     * @return The southwest node
     */
    public final PRQuadBaseNode<T> setSouthWest(PRQuadBaseNode<T> southWest) {
        if (southWest == null) {
            this.southWest = PRQuadEmptyNode.getInstance();
        } else {
            this.southWest = southWest;
        }
        return this.southWest;
    }

    /**
     * Sets the node located in the southeast region of this node.
     * @param southEast The QuadTree node
     * @return The southeast node
     */
    public final PRQuadBaseNode<T> setSouthEast(PRQuadBaseNode<T> southEast) {
        if (southEast == null) {
            this.southEast = PRQuadEmptyNode.getInstance();
        } else {
            this.southEast = southEast;
        }
        return this.southEast;
    }
    
    /**
     * Retrieves the region that contains the pair of coordinates (returns
     * null if not in entire region).
     * @param x The x coordinate to look for
     * @param y The y coordinate to look for
     * @return The region containing the coordinates
     */
    public final PRQuadBaseNode<T> getRegion(int x, int y) {
        int xMiddle = (xMin + xMax) / 2;
        int yMiddle = (yMin + yMax) / 2;
        
        // Check for out of bounds
        if (x < xMin || x > xMax || y < yMin || y > yMax) return null;
        
        // Northwest region
        if (x < xMiddle && y < yMiddle) {
            return this.getNorthWest();
            
        // Northeast region
        } else if (x >= xMiddle && y < yMiddle) {
            return this.getNorthEast();
            
        // Southwest region
        } else if (x < xMiddle && y >= yMiddle) {
            return this.getSouthWest();
            
        // Southeast region
        } else {
            return this.getSouthEast();
        }
    }
    
    /**
     * Sets the region that contains the pair of coordinates (returns
     * null if not in entire region).
     * @param x The x coordinate to look for
     * @param y The y coordinate to look for
     * @param region The new region node
     * @return The region containing the coordinates
     */
    public final PRQuadBaseNode<T> setRegion(int x, int y, PRQuadBaseNode<T> region) {
        int xMiddle = (xMin + xMax) / 2;
        int yMiddle = (yMin + yMax) / 2;
        
        // Check for out of bounds
        if (x < xMin || x > xMax || y < yMin || y > yMax) return null;
        
        // Check for null and set to flyweight
        if (region == null) region = PRQuadEmptyNode.getInstance();
        
        // Northwest region
        if (x < xMiddle && y < yMiddle) {
            return this.setNorthWest(region);
            
        // Northeast region
        } else if (x >= xMiddle && y < yMiddle) {
            return this.setNorthEast(region);
            
        // Southwest region
        } else if (x < xMiddle && y >= yMiddle) {
            return this.setSouthWest(region);
            
        // Southeast region
        } else {
            return this.setSouthEast(region);
        }
    }
    
    /**
     * Retrieves all regions that contain the pair of coordinates plus/minus the
     * radius provided.
     * @param x The x coordinate to look for
     * @param y The y coordinate to look for
     * @param radius The radius for variation
     * @return The regions containing the coordinates plus/minus the radius
     */
    public final List<PRQuadBaseNode<T>> getRegions(int x, int y, int radius) {
        int xMiddle = (xMin + xMax) / 2;
        int yMiddle = (yMin + yMax) / 2;
        
        // Create a linked list to store the nodes
        LinkedList<PRQuadBaseNode<T>> regionList =
                new LinkedList<PRQuadBaseNode<T>>();
        
        // Check for out of bounds
        //if (x < xMin || x > xMax || y < yMin || y > yMax) return regionList;
        
        // Northwest region
        if (intersectRegion(xMin, yMin, xMiddle, yMiddle, x, y, radius)) {
            regionList.add(this.getNorthWest());
        }
        
        // Northeast region
        if (intersectRegion(xMiddle, yMin, xMax, yMiddle, x, y, radius)) {
            regionList.add(this.getNorthEast());
        }
        
        // Southwest region
        if (intersectRegion(xMin, yMiddle, xMiddle, yMax, x, y, radius)) {
            regionList.add(this.getSouthWest());
        }
        
        // Southeast region
        if (intersectRegion(xMiddle, yMiddle, xMax, yMax, x, y, radius)) {
            regionList.add(this.getSouthEast());
        }
        
        // Return the newly-constructed list
        return regionList;
    }
    
    /*********************************************************************/
    /* Lines of the rectangle:                                           */
    /*                                                                   */
    /*    A                    B                                         */
    /* (x1,y1)--------------(x2,y1)                                      */
    /* |                          |                                      */
    /* |                          |                                      */
    /* |                          |                                      */
    /* (x1,y2)--------------(x2,y2)                                      */
    /*    C                    D                                         */
    /*                                                                   */
    /* x1 = xMin                                                         */
    /* x2 = xMax                                                         */
    /* y1 = yMin                                                         */
    /* y2 = yMax                                                         */
    /*********************************************************************/

    /*********************************************************************/
    /* Distance from point (x0,y0) to line (x1,y1)(x2,y2):               */
    /*                                                                   */
    /* ||(x2-x1)(y1-y0)-(x1-x0)(y2-y1)||  <-- Absolute value             */
    /* ---------------------------------  <-- Division bar               */
    /*   [(x2-x1)^2 + (y2-y1)^2]^(1/2)                                   */
    /*********************************************************************/

    //int distanceToLine = (int) (
    //                        Math.abs((x2 - x1)*(y1 - y0) - (x1 - x0)*(y2 - y1)) / 
    //                        Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2))
    //                   );
    
    /**
     * Determines whether or not a point with a given radius intersects a region.
     * @param xMin The minimum x coordinate in the region
     * @param yMin The minimum y coordinate in the region
     * @param xMax The maximum x coordinate in the region
     * @param yMax The maximum y coordinate in the region
     * @param x The x coordinate of the point to check
     * @param y The y coordinate of the point to check
     * @param radius The radius around the point to use for intersection
     * @return Whether or not there is an intersection
     */
    private boolean intersectRegion(int xMin, int yMin, int xMax, int yMax, 
                                    int x, int y, int radius) {
        // Check if the point of the circle is inside the region itself
        if (x >= xMin && x <= xMax && y >= yMin && y <= yMax) return true;
        
        // Set coordinates of each point in the rectangular region
        int Ax = xMin, Ay = yMin, Bx = xMax, By = yMin,
            Cx = xMin, Cy = yMax, Dx = xMax, Dy = yMax;
        
        // NEED TO FIX CHECK FOR POINT ON THE INFINITE LINE
        
        // Get the distance from the center point to each line of the region
        // Assumes infinite line segment; so, check for points matching the
        // horizontal or vertical positions
        double uAB = 0, uAC = 0, uBD = 0, uCD = 0;
        int distanceToAB = 0, distanceToAC = 0, distanceToBD = 0, distanceToCD = 0;
        
        // Determine if the point is between the two points that make up each line
        // u = [(x3-x1)(x2-x1)+(y3-y1)(y2-y1)] / ||(P2 - P1)||^2
        // If u < 0, closest distance is to P1
        // If u > 1, closest distance is to P2
        // If 0 < u < 1, closest distance can be found using point-to-line distance
        uAB = ((x - Ax)*(Bx - Ax) + (y - Ay)*(By - Ay)) /
              Math.sqrt(Math.pow((Bx - Ax), 2) + Math.pow((By - Ay), 2));
        uAC = ((x - Ax)*(Cx - Ax) + (y - Ay)*(Cy - Ay)) /
              Math.sqrt(Math.pow((Cx - Ax), 2) + Math.pow((Cy - Ay), 2));
        uBD = ((x - Bx)*(Dx - Bx) + (y - By)*(Dy - By)) /
              Math.sqrt(Math.pow((Dx - Bx), 2) + Math.pow((Dy - By), 2));
        uCD = ((x - Cx)*(Dx - Cx) + (y - Cy)*(Dy - Cy)) /
              Math.sqrt(Math.pow((Dx - Cx), 2) + Math.pow((Dy - Cy), 2));
        
        // Check line AB
        if (uAB < 0) {
            // Use distance to A
            distanceToAB = (int) (Math.sqrt(Math.pow((x - Ax), 2) + Math.pow((y - Ay), 2)));
        } else if (uAB > 1) {
            // Use distance to B
            distanceToAB = (int) (Math.sqrt(Math.pow((x - Bx), 2) + Math.pow((y - By), 2)));
        } else {
            distanceToAB = (int) (
                                Math.abs((Bx - Ax)*(Ay - y) - (Ax - x)*(By - Ay)) / 
                                Math.sqrt(Math.pow(Bx - Ax, 2) + Math.pow(By - Ay, 2))
                           );
        }
        
        // Check line AC
        if (uAC < 0) {
            // Use distance to A
            distanceToAC = (int) (Math.sqrt(Math.pow((x - Ax), 2) + Math.pow((y - Ay), 2)));
        } else if (uAC > 1) {
            // Use distance to C
            distanceToAC = (int) (Math.sqrt(Math.pow((x - Cx), 2) + Math.pow((y - Cy), 2)));
        } else {
            distanceToAC = (int) (
                                Math.abs((Cx - Ax)*(Ay - y) - (Ax - x)*(Cy - Ay)) / 
                                Math.sqrt(Math.pow(Cx - Ax, 2) + Math.pow(Cy - Ay, 2))
                           );
        }
        
        // Check line BD
        if (uBD < 0) {
            // Use distance to B
            distanceToBD = (int) (Math.sqrt(Math.pow((x - Bx), 2) + Math.pow((y - By), 2)));
        } else if (uBD > 1) {
            // Use distance to D
            distanceToBD = (int) (Math.sqrt(Math.pow((x - Dx), 2) + Math.pow((y - Dy), 2)));
        } else {
            distanceToBD = (int) (
                                Math.abs((Dx - Bx)*(By - y) - (Bx - x)*(Dy - By)) / 
                                Math.sqrt(Math.pow(Dx - Bx, 2) + Math.pow(Dy - By, 2))
                           );
        }
        
        // Check line CD
        if (uCD < 0) {
            // Use distance to C
            distanceToCD = (int) (Math.sqrt(Math.pow((x - Cx), 2) + Math.pow((y - Cy), 2)));
        } else if (uCD > 1) {
            // Use distance to D
            distanceToCD = (int) (Math.sqrt(Math.pow((x - Dx), 2) + Math.pow((y - Dy), 2)));
        } else {
            distanceToCD = (int) (
                                Math.abs((Dx - Cx)*(Cy - y) - (Cx - x)*(Dy - Cy)) / 
                                Math.sqrt(Math.pow(Dx - Cx, 2) + Math.pow(Dy - Cy, 2))
                           );
        }
        
        // Determine if any point in the circle falls within the region given
        return (distanceToAB <= radius || distanceToAC <= radius ||
                distanceToBD <= radius || distanceToCD <= radius);
    }
    
    /**
     * Returns a list containing all mapped elements contained within this
     * internal node.
     * @param root The root node to start the search of elements
     * @return The list of mapped elements
     */
    private List<PRQuadLeafNode.MappedElement> getElementList(PRQuadBaseNode<T> root) {
        LinkedList<PRQuadLeafNode.MappedElement> newList =
                new LinkedList<PRQuadLeafNode.MappedElement>();
        
        if (root.isLeaf()) {
            newList.addAll(((PRQuadLeafNode) root).getMappedElements());
        } else if (root.isInternal()) {
            newList.addAll(getElementList(((PRQuadInternalNode) root).getNorthWest()));
            newList.addAll(getElementList(((PRQuadInternalNode) root).getNorthEast()));
            newList.addAll(getElementList(((PRQuadInternalNode) root).getSouthWest()));
            newList.addAll(getElementList(((PRQuadInternalNode) root).getSouthEast()));
        } else {
            // Do nothing in the case of a flyweight as it is empty
        }
        
        return newList;
    }
    
    /**
     * Returns whether or not this internal node is completely empty (only flyweights).
     * @return Whether nor not the internal node is empty
     */
    private boolean isEmpty() {
        return (this.getNorthWest().isFlyweight() &&
                this.getNorthEast().isFlyweight() &&
                this.getSouthWest().isFlyweight() &&
                this.getSouthEast().isFlyweight());
    }
    
    /*************************************************************************/
    /* INHERITED METHODS                                                     */
    /*************************************************************************/
    
    /**
     * Adds an element to the node by trickling down to the region containing
     * the coordinates of the element and adding it to that region. Returns the
     * root node of the addition or null if there was no success addition.
     * @param xMin The minimum x coordinate in the region
     * @param yMin The minimum y coordinate in the region
     * @param xMax The maximum x coordinate in the region
     * @param yMax The maximum y coordinate in the region
     * @param x The x coordinate of the element to add
     * @param y The y coordinate of the element to add
     * @param element The element to add
     * @return The node of the add call
     */
    @Override
    public PRQuadBaseNode<T> add(int xMin, int yMin, int xMax, int yMax,
                                 int x, int y, T element) {
        PRQuadBaseNode<T> regionNode = this.getRegion(x, y);
        int xMiddle = (xMin + xMax) / 2;
        int yMiddle = (yMin + yMax) / 2;
        if (regionNode == this.getNorthWest()) {
            this.setRegion(x, y, regionNode.add(xMin, yMin, xMiddle, yMiddle, x, y, element));
            return this;
        } else if (regionNode == this.getNorthEast()) {
            this.setRegion(x, y, regionNode.add(xMiddle, yMin, xMax, yMiddle, x, y, element));
            return this;
        } else if (regionNode == this.getSouthWest()) {
            this.setRegion(x, y, regionNode.add(xMin, yMiddle, xMiddle, yMax, x, y, element));
            return this;
        } if (regionNode == this.getSouthEast()) {
            this.setRegion(x, y, regionNode.add(xMiddle, yMiddle, xMax, yMax, x, y, element));
            return this;
        } else {
            return null;
        }
    }
    
    /**
     * Removes the element from the node by trickling down to the region containing
     * the element and removing it. Returns the modified root node.
     * @param x The x coordinate of the element to remove
     * @param y The y coordinate of the element to remove
     * @return The root node of the removal
     */
    @Override
    public PRQuadBaseNode<T> remove(int x, int y) {
        PRQuadBaseNode<T> regionNode = this.getRegion(x, y);
        if (regionNode != null) {
            // Remove recursively and update the region where the element was removed
            this.setRegion(x, y, regionNode.remove(x, y));
            
            // Check if this internal region has too few elements to remain internal
            if (this.getTotalElements() <= PRQuadLeafNode.MAXIMUM_ELEMENTS) {
                PRQuadLeafNode<T> newLeaf = new PRQuadLeafNode<T>();
                
                // Add all internal elements to the new leaf
                for (Object o : getElementList(this)) {
                    PRQuadLeafNode.MappedElement mE = (PRQuadLeafNode.MappedElement) o;
                    newLeaf.add(xMin, yMin, xMax, yMax, mE.x, mE.y, (T) mE.getValue());
                }
                
                return newLeaf;
            } else {
                return this;
            }
        } else {
            return null;
        }
    }
    
    /**
     * Returns the node containing the element at the specified location or null
     * if there is no region matching the coordinates.
     * @param x The x coordinate of the element
     * @param y The y coordinate of the element
     * @return The node containing the element
     */
    @Override
    public PRQuadBaseNode<T> contains(int x, int y) {
        PRQuadBaseNode<T> regionNode = this.getRegion(x, y);
        if (regionNode != null) {
            return regionNode.contains(x, y);
        } else {
            return null;
        }
    }
    
    /**
     * Returns the total number of mapped elements contained in this internal node.
     * @return The integer number of elements
     */
    @Override
    public int getTotalElements() {
        int totalElements = 0;
        
        // Get the total elements from each region
        totalElements += this.getNorthWest().getTotalElements();
        totalElements += this.getNorthEast().getTotalElements();
        totalElements += this.getSouthWest().getTotalElements();
        totalElements += this.getSouthEast().getTotalElements();
        
        return totalElements;
    }

    /**
     * Returns false as this is not a leaf node.
     * @return False
     */
    @Override
    public boolean isLeaf() {
        return false;
    }

    /**
     * Returns true as this is an internal node.
     * @return True
     */
    @Override
    public boolean isInternal() {
        return true;
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
     * Prints information about this node and returns it as a String.
     * @return The information about the node
     */
    @Override
    public String toString() {
        return "I";
    }
    
}
