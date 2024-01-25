import java.util.HashMap;
import java.util.Map;

/**
 * Board class
 */
class Board {
    private int size;
    private Map<String, BoardEntity> boardData = new HashMap<>();

    /**
     * Board constructor
     * @param size the size
     */
    public Board(int size) {
        this.size = size;
    }

    /**
     * Add entity.
     *
     * @param entity the entity
     */
    public void addEntity(BoardEntity entity) {
        boardData.put(entity.getEntityPosition().getString(), entity);
    }

    /**
     * Get entity board entity.
     *
     * @param position the position
     * @return the board entity
     */
    public BoardEntity getEntity(EntityPosition position) {
        return boardData.get(position.getString());
    }

    /**
     * Get insect direction
     * @param insect insect
     */
    public Direction getDirection(Insect insect) {
        //Some code
        return null;
    }

    /**
     * Get sum of food points in direction
     * @param insect insect
     */
    public int getDirectionSum(Insect insect) {
        //Some code
        return 0;
    }
    public Map<String, BoardEntity> getBoardData() {
        return boardData;
    }
}