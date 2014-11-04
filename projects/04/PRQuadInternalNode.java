
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a QuadTree internal node that has children and no elements of
 * its own.
 * @author rcsvt Robert C. Senkbeil
 */
public class PRQuadInternalNode<T extends SerialNode> extends PRQuadBaseNode<T> {
    
    private static final int REGION_NORTHWEST = 1;
    private static final int REGION_NORTHEAST = 2;
    private static final int REGION_SOUTHWEST = 3;
    private static final int REGION_SOUTHEAST = 4;
    
    //private MemPool memPool;
    private int northWest, northEast, southWest, southEast;
    
    /*************************************************************************/
    /* CLASS CONSTRUCTORS                                                    */
    /*************************************************************************/
    
    /**
     * Creates a new instance of an internal node without any children.
     * @param memPool The memory pool associated with this node
     * @param handle The handle of this node
     */
    public PRQuadInternalNode(MemPool memPool, int handle) {
        setMemPool(memPool);
        setHandle(handle);
        this.northWest = -1;
        this.northEast = -1;
        this.southWest = -1;
        this.southEast = -1;
    }
    
    /*************************************************************************/
    /* CLASS METHODS                                                         */
    /*************************************************************************/

    /**
     * Retrieves the node located in the northwest region of this node.
     * @return The QuadTree node
     */
    public PRQuadBaseNode<T> getNorthWest() throws FileNotFoundException, IOException {
        return PRQuadTree.getNodeFromHandle(getMemPool(), northWest);
    }

    /**
     * Retrieves the node located in the northeast region of this node.
     * @return The QuadTree node
     */
    public PRQuadBaseNode<T> getNorthEast() throws FileNotFoundException, IOException {
        return PRQuadTree.getNodeFromHandle(getMemPool(), northEast);
    }

    /**
     * Retrieves the node located in the southwest region of this node.
     * @return The QuadTree node
     */
    public PRQuadBaseNode<T> getSouthWest() throws FileNotFoundException, IOException {
        return PRQuadTree.getNodeFromHandle(getMemPool(), southWest);
    }

    /**
     * Retrieves the node located in the southeast region of this node.
     * @return The QuadTree node
     */
    public PRQuadBaseNode<T> getSouthEast() throws FileNotFoundException, IOException {
        return PRQuadTree.getNodeFromHandle(getMemPool(), southEast);
    }
    
    /**
     * Returns the pointer to the northwest region node.
     * @return The integer pointer
     */
    public int getNorthWestPtr() {
        return this.northWest;
    }
    
    /**
     * Returns the pointer to the northeast region node.
     * @return The integer pointer
     */
    public int getNorthEastPtr() {
        return this.northEast;
    }
    
    /**
     * Returns the pointer to the southwest region node.
     * @return The integer pointer
     */
    public int getSouthWestPtr() {
        return this.southWest;
    }
    
    /**
     * Returns the pointer to the southeast region node.
     * @return The integer pointer
     */
    public int getSouthEastPtr() {
        return this.southEast;
    }
    
    /**
     * Sets the pointer to the node located in the northwest region of this node.
     * @param newNorthWestPtr The pointer to the node
     * @return The handle to the node
     */
    public final int setNorthWestPtr(int newNorthWestPtr) throws FileNotFoundException, IOException {
        this.northWest = newNorthWestPtr;
        
        // Update in file memory
        this.updateInMemory();
        
        return this.northWest;
    }
    
    /**
     * Sets the pointer to the node located in the northEast region of this node.
     * @param newnorthEastPtr The pointer to the node
     * @return The handle to the node
     */
    public final int setNorthEastPtr(int newNorthEastPtr) throws FileNotFoundException, IOException {
        this.northEast = newNorthEastPtr;
        
        // Update in file memory
        this.updateInMemory();
        
        return this.northEast;
    }
    
    /**
     * Sets the pointer to the node located in the southwest region of this node.
     * @param newSouthWestPtr The pointer to the node
     * @return The handle to the node
     */
    public final int setSouthWestPtr(int newSouthWestPtr) throws FileNotFoundException, IOException {
        this.southWest = newSouthWestPtr;
        
        // Update in file memory
        this.updateInMemory();
        
        return this.southWest;
    }
    
    /**
     * Sets the pointer to the node located in the southeast region of this node.
     * @param newSouthEastPtr The pointer to the node
     * @return The handle to the node
     */
    public final int setSouthEastPtr(int newSouthEastPtr) throws FileNotFoundException, IOException {
        this.southEast = newSouthEastPtr;
        
        // Update in file memory
        this.updateInMemory();
        
        return this.southEast;
    }

    /**
     * Sets the node located in the northwest region of this node.
     * @param newNorthWest The QuadTree node
     * @return The handle to the new node
     */
    public final int setNorthWest(PRQuadBaseNode<T> newNorthWest) throws FileNotFoundException, IOException {
        // Remove the old instance
        if (northWest != -1) getMemPool().remove(northWest);
        
        // Add the new instance
        byte[] temp = new byte[256];
        int tempSize;
        if (newNorthWest == null) {
            tempSize = (new PRQuadEmptyNode(getMemPool())).saveToBytes(temp);
        } else {
            tempSize = newNorthWest.saveToBytes(temp);
        }
        northWest = getMemPool().insert(temp, tempSize);
        
        return this.northWest;
    }

    /**
     * Sets the node located in the northeast region of this node.
     * @param newNorthEast The QuadTree node
     * @return The handle to the new node
     */
    public final int setNorthEast(PRQuadBaseNode<T> newNorthEast) throws FileNotFoundException, IOException {
        // Remove the old instance
        if (northEast != -1) getMemPool().remove(northEast);
        
        // Add the new instance
        byte[] temp = new byte[256];
        int tempSize;
        if (newNorthEast == null) {
            tempSize = (new PRQuadEmptyNode(getMemPool())).saveToBytes(temp);
        } else {
            tempSize = newNorthEast.saveToBytes(temp);
        }
        northEast = getMemPool().insert(temp, tempSize);
        
        return this.northEast;
    }

    /**
     * Sets the node located in the southwest region of this node.
     * @param newSouthWest The QuadTree node
     * @return The handle to the new node
     */
    public final int setSouthWest(PRQuadBaseNode<T> newSouthWest) throws FileNotFoundException, IOException {
        // Remove the old instance
        if (southWest != -1) getMemPool().remove(southWest);
        
        // Add the new instance
        byte[] temp = new byte[256];
        int tempSize;
        if (newSouthWest == null) {
            tempSize = (new PRQuadEmptyNode(getMemPool())).saveToBytes(temp);
        } else {
            tempSize = newSouthWest.saveToBytes(temp);
        }
        southWest = getMemPool().insert(temp, tempSize);
        
        return this.southWest;
    }

    /**
     * Sets the node located in the southeast region of this node.
     * @param newSouthEast The QuadTree node
     * @return The handle to the new node
     */
    public final int setSouthEast(PRQuadBaseNode<T> newSouthEast) throws FileNotFoundException, IOException {
        // Remove the old instance
        if (southEast != -1) getMemPool().remove(southEast);
        
        // Add the new instance
        byte[] temp = new byte[256];
        int tempSize;
        if (newSouthEast == null) {
            tempSize = (new PRQuadEmptyNode(getMemPool())).saveToBytes(temp);
        } else {
            tempSize = newSouthEast.saveToBytes(temp);
        }
        southEast = getMemPool().insert(temp, tempSize);
        
        return this.southEast;
    }
    
    /**
     * Retrieves the region that contains the pair of coordinates (returns
     * null if not in entire region).
     * @param xMin The minimum x coordinate in the region
     * @param yMin The minimum y coordinate in the region
     * @param xMax The maximum x coordinate in the region
     * @param yMax The maximum y coordinate in the region
     * @param x The x coordinate to look for
     * @param y The y coordinate to look for
     * @return The region containing the coordinates
     */
    public final PRQuadBaseNode<T> getRegion(
            double xMin, double yMin, double xMax, double yMax,
            double x, double y) throws FileNotFoundException, IOException {
        double xMiddle = (xMin + xMax) / 2.0;
        double yMiddle = (yMin + yMax) / 2.0;
        
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
     * -1 if not in entire region).
     * @param xMin The minimum x coordinate in the region
     * @param yMin The minimum y coordinate in the region
     * @param xMax The maximum x coordinate in the region
     * @param yMax The maximum y coordinate in the region
     * @param x The x coordinate to look for
     * @param y The y coordinate to look for
     * @param region The handle to the new region node
     * @return The handle of the region containing the coordinates
     */
    public final int setRegion(
            double xMin, double yMin, double xMax, double yMax,
            double x, double y, int region) throws FileNotFoundException, IOException {
        double xMiddle = (xMin + xMax) / 2;
        double yMiddle = (yMin + yMax) / 2;
        
        // Check for out of bounds
        if (x < xMin || x > xMax || y < yMin || y > yMax) return -1;
        
        // Northwest region
        int ptr = -1;
        if (x < xMiddle && y < yMiddle) {
            ptr = this.setNorthWestPtr(region);
            
        // Northeast region
        } else if (x >= xMiddle && y < yMiddle) {
            ptr = this.setNorthEastPtr(region);
            
        // Southwest region
        } else if (x < xMiddle && y >= yMiddle) {
            ptr = this.setSouthWestPtr(region);
            
        // Southeast region
        } else {
            ptr = this.setSouthEastPtr(region);
        }
        
        return ptr;
    }
    
    /**
     * Sets the region that contains the pair of coordinates (returns
     * -1 if not in entire region).
     * @param xMin The minimum x coordinate in the region
     * @param yMin The minimum y coordinate in the region
     * @param xMax The maximum x coordinate in the region
     * @param yMax The maximum y coordinate in the region
     * @param x The x coordinate to look for
     * @param y The y coordinate to look for
     * @param region The handle to the new region node
     * @return The handle of the region containing the coordinates
     */
    public final int createRegion(
            double xMin, double yMin, double xMax, double yMax,
            double x, double y, int region) throws FileNotFoundException, IOException {
        PRQuadBaseNode<T> regionNode = PRQuadTree.getNodeFromHandle(getMemPool(), region);
        double xMiddle = (xMin + xMax) / 2;
        double yMiddle = (yMin + yMax) / 2;
        
        // Check for out of bounds
        if (x < xMin || x > xMax || y < yMin || y > yMax) return -1;
        
        // Northwest region
        if (x < xMiddle && y < yMiddle) {
            return this.setNorthWest(regionNode);
            
        // Northeast region
        } else if (x >= xMiddle && y < yMiddle) {
            return this.setNorthEast(regionNode);
            
        // Southwest region
        } else if (x < xMiddle && y >= yMiddle) {
            return this.setSouthWest(regionNode);
            
        // Southeast region
        } else {
            return this.setSouthEast(regionNode);
        }
    }
    
    /**
     * Retrieves all regions that contain the pair of coordinates plus/minus the
     * radius provided.
     * @param xMin The minimum x coordinate in the region
     * @param yMin The minimum y coordinate in the region
     * @param xMax The maximum x coordinate in the region
     * @param yMax The maximum y coordinate in the region
     * @param x The x coordinate to look for
     * @param y The y coordinate to look for
     * @param radius The radius for variation
     * @return The regions containing the coordinates plus/minus the radius
     */
    public final List<PRQuadBaseNode<T>> getRegions(
            double xMin, double yMin, double xMax, double yMax,
            double x, double y, double radius) throws FileNotFoundException, IOException {
        double xMiddle = (xMin + xMax) / 2;
        double yMiddle = (yMin + yMax) / 2;
        
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
    private boolean intersectRegion(double xMin, double yMin, double xMax, double yMax, 
                                    double x, double y, double radius) {
        // Check if the point of the circle is inside the region itself
        if (x >= xMin && x <= xMax && y >= yMin && y <= yMax) return true;
        
        // Set coordinates of each point in the rectangular region
        double Ax = xMin, Ay = yMin, Bx = xMax, By = yMin,
               Cx = xMin, Cy = yMax, Dx = xMax, Dy = yMax;
        
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
    
    
    private int getRegionFlag(double xMin, double yMin, double xMax, double yMax, 
                              double x, double y) {
        double xMiddle = (xMin + xMax) / 2;
        double yMiddle = (yMin + yMax) / 2;
        
        // Check for out of bounds
        if (x < xMin || x > xMax || y < yMin || y > yMax) return -1;
        
        // Northwest region
        if (x < xMiddle && y < yMiddle) {
            return REGION_NORTHWEST;
            
        // Northeast region
        } else if (x >= xMiddle && y < yMiddle) {
            return REGION_NORTHEAST;
            
        // Southwest region
        } else if (x < xMiddle && y >= yMiddle) {
            return REGION_SOUTHWEST;
            
        // Southeast region
        } else {
            return REGION_SOUTHEAST;
        }
    }
    
    /**
     * Returns a list containing all handles to elements contained within this
     * internal node.
     * @param root The root node to start the search of elements
     * @return The list of handles to elements
     */
    private List<Integer> getElementList(PRQuadBaseNode<T> root) throws FileNotFoundException, IOException {
        LinkedList<Integer> newList = new LinkedList<Integer>();
        
        if (root.isLeaf()) {
            newList.addAll(((PRQuadLeafNode) root).getElementHandles());
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
    private boolean isEmpty() throws FileNotFoundException, IOException {
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
     * @param elementHandle The handle to the element to add
     * @return The node of the add call
     */
    @Override
    public int add(double xMin, double yMin, double xMax, double yMax,
                                 double x, double y, int elementHandle) {
        try {
            PRQuadBaseNode<T> regionNode = this.getRegion(xMin, yMin, xMax, yMax, x, y);
            double xMiddle = (xMin + xMax) / 2;
            double yMiddle = (yMin + yMax) / 2;
            if (this.getRegionFlag(xMin, yMin, xMax, yMax, x, y) == REGION_NORTHWEST) {
                this.setRegion(xMin, yMin, xMax, yMax, x, y, regionNode.add(xMin, yMin, xMiddle, yMiddle, x, y, elementHandle));
            } else if (this.getRegionFlag(xMin, yMin, xMax, yMax, x, y) == REGION_NORTHEAST) {
                this.setRegion(xMin, yMin, xMax, yMax, x, y, regionNode.add(xMiddle, yMin, xMax, yMiddle, x, y, elementHandle));
            } else if (this.getRegionFlag(xMin, yMin, xMax, yMax, x, y) == REGION_SOUTHWEST) {
                this.setRegion(xMin, yMin, xMax, yMax, x, y, regionNode.add(xMin, yMiddle, xMiddle, yMax, x, y, elementHandle));
            } else if (this.getRegionFlag(xMin, yMin, xMax, yMax, x, y) == REGION_SOUTHEAST) {
                this.setRegion(xMin, yMin, xMax, yMax, x, y, regionNode.add(xMiddle, yMiddle, xMax, yMax, x, y, elementHandle));
            } else {
                return -1;
            }
            
            // Update the information locally
            this.updateInMemory();
            
            // Return the handle to this internal node
            return this.getHandle();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PRQuadInternalNode.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        } catch (IOException ex) {
            Logger.getLogger(PRQuadInternalNode.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }
    
    /**
     * Removes the element from the node by trickling down to the region containing
     * the element and removing it. Returns the modified root node.
     * @param xMin The minimum x coordinate in the region
     * @param yMin The minimum y coordinate in the region
     * @param xMax The maximum x coordinate in the region
     * @param yMax The maximum y coordinate in the region
     * @param x The x coordinate of the element to remove
     * @param y The y coordinate of the element to remove
     * @return The root node of the removal
     */
    @Override
    public int remove(
            double xMin, double yMin, double xMax, double yMax,
            double x, double y) {
        try {
            PRQuadBaseNode<T> regionNode = this.getRegion(xMin, yMin, xMax, yMax, x, y);
            if (regionNode != null) {
                // Remove recursively and update the region where the element was removed
                double xMiddle = (xMin + xMax) / 2;
                double yMiddle = (yMin + yMax) / 2;
                if (this.getRegionFlag(xMin, yMin, xMax, yMax, x, y) == REGION_NORTHWEST) {
                    this.setRegion(xMin, yMin, xMax, yMax, x, y, regionNode.remove(xMin, yMin, xMiddle, yMiddle, x, y));
                } else if (this.getRegionFlag(xMin, yMin, xMax, yMax, x, y) == REGION_NORTHEAST) {
                    this.setRegion(xMin, yMin, xMax, yMax, x, y, regionNode.remove(xMiddle, yMin, xMax, yMiddle, x, y));
                } else if (this.getRegionFlag(xMin, yMin, xMax, yMax, x, y) == REGION_SOUTHWEST) {
                    this.setRegion(xMin, yMin, xMax, yMax, x, y, regionNode.remove(xMin, yMiddle, xMiddle, yMax, x, y));
                } else if (this.getRegionFlag(xMin, yMin, xMax, yMax, x, y) == REGION_SOUTHEAST) {
                    this.setRegion(xMin, yMin, xMax, yMax, x, y, regionNode.remove(xMiddle, yMiddle, xMax, yMax, x, y));
                }
                
                // Check if this internal region has too few elements to remain internal
                if (this.getTotalElements() <= PRQuadLeafNode.MAXIMUM_ELEMENTS) {
                    PRQuadLeafNode<T> newLeaf = new PRQuadLeafNode<T>(getMemPool(), -1);
                    List<Integer> elementHandles = getElementList(this);
                    
                    // Remove this internal node to free up memory to be used again
                    this.delete();
                    
                    // Add all internal elements to the new leaf
                    for (Integer e_Handle : elementHandles) {
                        SerialNode se = CityNode.create(getMemPool(), e_Handle);
                        newLeaf.add(xMin, yMin, xMax, yMax, se.x, se.y, e_Handle);
                    }
                    
                    // Store the leaf node
                    newLeaf.updateInMemory();
                    
                    return newLeaf.getHandle();
                } else {
                    return getHandle();
                }
            } else {
                return -1;
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PRQuadInternalNode.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        } catch (IOException ex) {
            Logger.getLogger(PRQuadInternalNode.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }
    
    /**
     * Returns the node containing the element at the specified location or null
     * if there is no region matching the coordinates.
     * @param xMin The minimum x coordinate in the region
     * @param yMin The minimum y coordinate in the region
     * @param xMax The maximum x coordinate in the region
     * @param yMax The maximum y coordinate in the region
     * @param x The x coordinate of the element
     * @param y The y coordinate of the element
     * @return The node containing the element
     */
    @Override
    public int contains(
            double xMin, double yMin, double xMax, double yMax,
            double x, double y) {
        try {
            PRQuadBaseNode<T> regionNode = this.getRegion(xMin, yMin, xMax, yMax, x, y);
            double xMiddle = (xMin + xMax) / 2;
            double yMiddle = (yMin + yMax) / 2;
            if (regionNode != null) {
                if (regionNode.getHandle() == getNorthWestPtr()) {
                    return regionNode.contains(xMin, yMin, xMiddle, yMiddle, x, y);
                } else if (regionNode.getHandle() == getNorthEastPtr()) {
                    return regionNode.contains(xMiddle, yMin, xMax, yMiddle, x, y);
                } else if (regionNode.getHandle() == getSouthWestPtr()) {
                    return regionNode.contains(xMin, yMiddle, xMiddle, yMax, x, y);
                } else if (regionNode.getHandle() == getSouthEastPtr()) {
                    return regionNode.contains(xMiddle, yMiddle, xMax, yMax, x, y);
                } else {
                    return -1;
                }
            } else {
                return -1;
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PRQuadInternalNode.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        } catch (IOException ex) {
            Logger.getLogger(PRQuadInternalNode.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }
    
    /**
     * Returns the total number of mapped elements contained in this internal node.
     * @return The integer number of elements
     */
    @Override
    public int getTotalElements() {
        try {
            int totalElements = 0;
            
            // Get the total elements from each region
            totalElements += this.getNorthWest().getTotalElements();
            totalElements += this.getNorthEast().getTotalElements();
            totalElements += this.getSouthWest().getTotalElements();
            totalElements += this.getSouthEast().getTotalElements();
            
            return totalElements;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PRQuadInternalNode.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        } catch (IOException ex) {
            Logger.getLogger(PRQuadInternalNode.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
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
        return ""; // Blank because wrapper is handled in main class
    }

    /**
     * Convert this internal node into a byte array with the type being the 
     * first byte and the contents being the remaining bytes.
     * @param byteArray The array to store the bytes into
     * @return The size of the contents returned
     */
    @Override
    public int saveToBytes(byte[] byteArray) {
        byte type = 3; // Type for internal node
        int size = 1 + 4 * 4;
        byteArray[0] = type;
        
        byteArray[1] = (byte) (northWest >> 24 & 0xFF);
        byteArray[2] = (byte) (northWest >> 16 & 0xFF);
        byteArray[3] = (byte) (northWest >> 8 & 0xFF);
        byteArray[4] = (byte) (northWest & 0xFF);
        
        byteArray[5] = (byte) (northEast >> 24 & 0xFF);
        byteArray[6] = (byte) (northEast >> 16 & 0xFF);
        byteArray[7] = (byte) (northEast >> 8 & 0xFF);
        byteArray[8] = (byte) (northEast & 0xFF);
        
        byteArray[9] = (byte) (southWest >> 24 & 0xFF);
        byteArray[10] = (byte) (southWest >> 16 & 0xFF);
        byteArray[11] = (byte) (southWest >> 8 & 0xFF);
        byteArray[12] = (byte) (southWest & 0xFF);
        
        byteArray[13] = (byte) (southEast >> 24 & 0xFF);
        byteArray[14] = (byte) (southEast >> 16 & 0xFF);
        byteArray[15] = (byte) (southEast >> 8 & 0xFF);
        byteArray[16] = (byte) (southEast & 0xFF);
        
        return size;
    }

    /**
     * Loads the contents of the byte array into this internal node.
     * @param byteArray The byte array to load from
     */
    @Override
    public void loadFromBytes(byte[] byteArray) {
        assert (byteArray[0] == 3) :
            "ERROR: Loaded type is not an internal node!";
        this.northWest = (((int) byteArray[1]) << 24 & 0xFF000000 |
                          ((int) byteArray[2]) << 16 & 0x00FF0000 |
                          ((int) byteArray[3]) << 8 & 0x0000FF00 |
                          ((int) byteArray[4]) & 0x000000FF);
        this.northEast = (((int) byteArray[5]) << 24 & 0xFF000000 |
                          ((int) byteArray[6]) << 16 & 0x00FF0000 |
                          ((int) byteArray[7]) << 8 & 0x0000FF00 |
                          ((int) byteArray[8]) & 0x000000FF);
        this.southWest = (((int) byteArray[9]) << 24 & 0xFF000000 |
                          ((int) byteArray[10]) << 16 & 0x00FF0000 |
                          ((int) byteArray[11]) << 8 & 0x0000FF00 |
                          ((int) byteArray[12]) & 0x000000FF);
        this.southEast = (((int) byteArray[13]) << 24 & 0xFF000000 |
                          ((int) byteArray[14]) << 16 & 0x00FF0000 |
                          ((int) byteArray[15]) << 8 & 0x0000FF00 |
                          ((int) byteArray[16]) & 0x000000FF);
    }

    /**
     * Deletes the internal node from memory.
     */
    @Override
    public void delete() {
        if (getHandle() == -1) return;
        try {
            // Remove the leaves contained in this internal node to be used again
            if (this.getNorthWestPtr() != -1) this.getNorthWest().delete();
            if (this.getNorthEastPtr() != -1) this.getNorthEast().delete();
            if (this.getSouthWestPtr() != -1) this.getSouthWest().delete();
            if (this.getSouthEastPtr() != -1) this.getSouthEast().delete();
            
            // Remove this internal node
            getMemPool().remove(getHandle());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PRQuadInternalNode.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PRQuadInternalNode.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
