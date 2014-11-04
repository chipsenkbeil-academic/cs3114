
/**
 * Represents a city that contains a location and name.
 * @author rcsvt Robert C. Senkbeil
 */
public class CityNode {
    
    private int x, y;
    private String name;
    
    /**
     * Creates a new instance of the CityNode with the provided data.
     * @param x The x coordinate of the city
     * @param y The y coordinate of the city
     * @param name The name of the city
     */
    public CityNode(int x, int y, String name) {
        this.x = x;
        this.y = y;
        this.name = name;
    }

    /**
     * Retrieves the x coordinate of the city.
     * @return The integer coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Retrieves the y coordinate of the city.
     * @return The integer coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Retrieves the name of the city.
     * @return The String name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the x coordinate of the city.
     * @param x The new coordinate
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Sets the y coordinate of the city
     * @param y The new coordinate
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Sets the name of the city
     * @param name The new name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Determines if a given CityNode is equal to this CityNode.
     * @param node The node to compare
     * @return Whether or not they are equal
     */
    public boolean equals(CityNode node) {
        return (this.getX() == node.getX() &&
                this.getY() == node.getY() &&
                this.getName().equals(node.getName()));
    }
    
    /**
     * Returns the name of the city.
     * @return The name of the city
     */
    @Override
    public String toString() {
        return this.getName();
    }
}
