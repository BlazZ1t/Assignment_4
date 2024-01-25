/**
 * Board entity abstract class, containing entity position
 */
abstract class BoardEntity {
    /**
     * The Entity position.
     */
    protected EntityPosition entityPosition;

    /**
     * Gets entity position.
     *
     * @return the entity position
     */
    public EntityPosition getEntityPosition() {
        return entityPosition;
    }
}