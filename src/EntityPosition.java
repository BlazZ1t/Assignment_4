/**
 * Class for entity positions
 */
class EntityPosition {
    private int x;
    private int y;

    /**
     * Constructor method
     */
    public EntityPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public int getX() {
        return this.x;
    }
    public int getY() {
        return this.y;
    }

    /**
     * Gets string value of the position
     * @return the string
     */
    public String getString() {
        return this.x + " " + this.y;
    }
}