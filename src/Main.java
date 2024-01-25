import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Main class for execution of the program
 */
public class Main {
    //Arrays and HashMaps with different data on Board Entities
    private static ArrayList<EntityPosition> existentPositions = new ArrayList<>();
    private static ArrayList<String> existentInsects = new ArrayList<>();
    private static ArrayList<Insect> insects = new ArrayList<>();
    private static Map<String, InsectColor> insectsPositionAndColor = new HashMap<>();
    private static Map<String, Integer> foodPositions = new HashMap<>();
    private static Board gameBoard;
    //Constant variables for boundaries check
    public static final int BOARD_LOWER_BOUNDARY = 4;
    public static final int BOARD_UPPER_BOUNDARY = 1000;
    public static final int INSECT_OR_FOOD_POINTS_LOWER_BOUNDARY = 1;
    public static final int INSECT_UPPER_BOUNDARY = 16;
    public static final int FOOD_POINTS_UPPER_BOUNDARY = 200;

    /**
     * Gets insects position and color.
     *
     * @return the insects position and color
     */
    public static Map<String, InsectColor> getInsectsPositionAndColor() {
        return insectsPositionAndColor;
    }

    /**
     * Gets food positions.
     *
     * @return the food positions
     */
    public static Map<String, Integer> getFoodPositions() {
        return foodPositions;
    }

    /**
     * Execution method
     */
    public static void main(String[] args) throws IOException {
        File inputFile = new File("input.txt");
        File outputFile = new File("output.txt");
        FileInputStream inputStream = new FileInputStream(inputFile);
        PrintStream outputStream = new PrintStream(outputFile);
        Scanner input = new Scanner(inputStream);
        System.setOut(outputStream);

        //Scan board
        int boardSize = input.nextInt();
        if (boardSize < BOARD_LOWER_BOUNDARY | boardSize > BOARD_UPPER_BOUNDARY) {
            System.out.println(new InvalidBoardSizeException().getMessage());
            outputStream.close();
            System.exit(0);
        }
        gameBoard = new Board(boardSize);
        //Scan number of insects
        int numberOfInsects = input.nextInt();
        if (numberOfInsects < INSECT_OR_FOOD_POINTS_LOWER_BOUNDARY | numberOfInsects > INSECT_UPPER_BOUNDARY) {
            System.out.println(new InvalidNumberOfInsectsException().getMessage());
            outputStream.close();
            System.exit(0);
        }
        //Scan number of food points
        int numberOfFoodPoints = input.nextInt();
        if (numberOfFoodPoints < INSECT_OR_FOOD_POINTS_LOWER_BOUNDARY
                | numberOfFoodPoints > FOOD_POINTS_UPPER_BOUNDARY) {
            System.out.println(new InvalidNumberOfFoodPointsException().getMessage());
            outputStream.close();
            System.exit(0);
        }
        //Check if coordinates of insects are out of boundaries, if insects duplicate and if they are at the same pos
        for (int i = 0; i < numberOfInsects; i++) {
            String insectColorString = input.next();
            String insectType = input.next();
            int xCoordinate = input.nextInt();
            int yCoordinate = input.nextInt();
            if (xCoordinate > boardSize | yCoordinate > boardSize | xCoordinate < 1 | yCoordinate < 1) {
                System.out.println(new InvalidEntityPositionException().getMessage());
                outputStream.close();
                System.exit(0);
            }
            InsectColor insectColor = InsectColor.NO_COLOR;
            insectColor = insectColor.toColor(insectColorString);
            if (existentInsects.contains(insectColorString + " " + insectType)) {
                System.out.println(new DuplicateInsectException().getMessage());
                outputStream.close();
                System.exit(0);
            } else {
                existentInsects.add(insectColorString + " " + insectType);
            }
            for (EntityPosition existentPosition : existentPositions) {
                if (xCoordinate == existentPosition.getX() & yCoordinate == existentPosition.getY()) {
                    System.out.println(new TwoEntitiesOnSamePositionException().getMessage());
                    outputStream.close();
                    System.exit(0);
                }
            }
            //Adding insects in arrays
            insectsPositionAndColor.put(new EntityPosition(xCoordinate, yCoordinate).getString(), insectColor);
            existentPositions.add(new EntityPosition(xCoordinate, yCoordinate));
            //Adding insects into a boardData HashMap
            switch (insectType) {
                case "Ant":
                    gameBoard.addEntity(new Ant(new EntityPosition(xCoordinate, yCoordinate), insectColor));
                    insects.add(new Ant(new EntityPosition(xCoordinate, yCoordinate), insectColor));
                    break;
                case "Spider":
                    insects.add(new Spider(new EntityPosition(xCoordinate, yCoordinate), insectColor));
                    gameBoard.addEntity(new Spider(new EntityPosition(xCoordinate, yCoordinate), insectColor));
                    break;
                case "Butterfly":
                    gameBoard.addEntity(new Butterfly(new EntityPosition(xCoordinate, yCoordinate), insectColor));
                    insects.add(new Butterfly(new EntityPosition(xCoordinate, yCoordinate), insectColor));
                    break;
                case "Grasshopper":
                    gameBoard.addEntity(new Grasshopper(new EntityPosition(xCoordinate, yCoordinate), insectColor));
                    insects.add(new Grasshopper(new EntityPosition(xCoordinate, yCoordinate), insectColor));
                    break;
                default:
                    System.out.println(new InvalidInsectTypeException().getMessage());
                    outputStream.close();
                    System.exit(0);
            }
        }
        //Checking if food points coordinates are out of the board, if it's placed on an existent position and if its
        //value is out of boundaries
        for (int i = 0; i < numberOfFoodPoints; i++) {
            int foodAmount = input.nextInt();
            int xCoordinate = input.nextInt();
            int yCoordinate = input.nextInt();
            if (xCoordinate > boardSize | yCoordinate > boardSize | xCoordinate < 1 | yCoordinate < 1) {
                System.out.println(new InvalidEntityPositionException().getMessage());
                outputStream.close();
                System.exit(0);
            }
            for (EntityPosition existentPosition : existentPositions) {
                if (xCoordinate == existentPosition.getX() & yCoordinate == existentPosition.getY()) {
                    System.out.println(new TwoEntitiesOnSamePositionException().getMessage());
                    outputStream.close();
                    System.exit(0);
                }
            }
            existentPositions.add(new EntityPosition(xCoordinate, yCoordinate));
            if (foodAmount < 1) {
                System.out.println(new InvalidNumberOfFoodPointsException().getMessage());
                outputStream.close();
                System.exit(0);
            }
            //Adding food positions
            gameBoard.addEntity(new FoodPoint(new EntityPosition(xCoordinate, yCoordinate), foodAmount));
            foodPositions.put(new EntityPosition(xCoordinate, yCoordinate).getString(), foodAmount);
        }
        //Moving insects
        Map<String, BoardEntity> boardData = gameBoard.getBoardData();
        for (int i = 0; i < numberOfInsects; i++) {
            System.out.println(existentInsects.get(0) + " "
                     + insects.get(0).getBestDirection(boardData, boardSize).getTextRepresentation()
                     + " " + insects.get(0).travelDirection(boardData, boardSize));
            existentInsects.remove(0);
            insectsPositionAndColor.remove(insects.get(0).getEntityPosition().getString());
            insects.remove(0);
        }
        outputStream.close();
    }
}







/**
 * Class for food points
 */
class FoodPoint extends BoardEntity {
    protected int value;
    /**
     * Constructor for a food point
     * @param position position
     * @param value value
     */
    public FoodPoint(EntityPosition position, int value) {
        this.value = value;
        this.entityPosition = position;
    }
}

/**
 * Enumeration for insect colors
 */
enum InsectColor {
    RED,
    GREEN,
    BLUE,
    YELLOW,
    /**
     * Color for initializing color variable throughout the code
     */
    NO_COLOR;

    /**
     * Method for creating enum value from text
     * @param s text
     * @return insect color
     */
    public static InsectColor toColor(String s) {
        switch (s) {
            case "Red":
                return RED;
            case "Green":
                return GREEN;
            case "Blue":
                return BLUE;
            case "Yellow":
                return YELLOW;
            default:
                System.out.println(new InvalidInsectColorException().getMessage());
                System.exit(0);
                return null;
        }
    }
}

/**
 * Class for creating insects, that contains insect color variable
 */
abstract class Insect extends BoardEntity {
    protected InsectColor color;
    /**
     * Constructor for an insect
     */
    public Insect(EntityPosition position, InsectColor color) {
        this.entityPosition = position;
        this.color = color;
    }

    /**
     * Method for returning best direction from possible
     * @param boardData board data
     * @param boardSize board size
     * @return best direction for moving
     */
    public Direction getBestDirection(Map<String, BoardEntity> boardData, int boardSize) {
        return null;
    }

    /**
     * Method simulating movement of an insect
     * @return amount of food eaten while going, also deletes entities from arrays
     */
    public int travelDirection(Map<String, BoardEntity> boardData, int boardSize) {
        return 0;
    }
}

/**
 * Grasshopper class
 */
class Grasshopper extends Insect {

    /**
     * Constructor for a grasshopper
     * @param position entity position
     * @param color insect color
     */
    public Grasshopper(EntityPosition position, InsectColor color) {
        super(position, color);
    }

    @Override
    public Direction getBestDirection(Map<String, BoardEntity> boardData, int boardSize) {
        int northTravel = 0;
        int eastTravel = 0;
        int southTravel = 0;
        int westTravel = 0;
        Direction direction = Direction.N;
        int maxValue = 0;
        for (int i = entityPosition.getX(); i >= 1; i = i - 2) {
            if (Main.getFoodPositions().containsKey(new EntityPosition(i, entityPosition.getY()).getString())
                & !Main.getInsectsPositionAndColor().containsKey(i + " " + entityPosition.getY())) {
                northTravel += Main.getFoodPositions().get(new EntityPosition(i, entityPosition.getY()).getString());
            }
        }
        for (int i = entityPosition.getY(); i <= boardSize; i = i + 2) {
            if (Main.getFoodPositions().containsKey(new EntityPosition(entityPosition.getX(), i).getString())
                    & !Main.getInsectsPositionAndColor().containsKey(entityPosition.getX() + " " + i)) {
                eastTravel += Main.getFoodPositions().get(entityPosition.getX() + " " + i);
            }
        }
        for (int i = entityPosition.getX(); i <= boardSize; i = i + 2) {
            if (Main.getFoodPositions().containsKey(new EntityPosition(i, entityPosition.getY()).getString())
                    & !Main.getInsectsPositionAndColor().containsKey(i + " " + entityPosition.getY())) {
                southTravel += Main.getFoodPositions().get(new EntityPosition(i, entityPosition.getY()).getString());
            }
        }
        for (int i = entityPosition.getY(); i >= 1; i = i - 2) {
            if (Main.getFoodPositions().containsKey(new EntityPosition(entityPosition.getX(), i).getString())
                    & !Main.getInsectsPositionAndColor().containsKey(entityPosition.getX() + " " + i)) {
                westTravel += Main.getFoodPositions().get(new EntityPosition(entityPosition.getX(), i).getString());
            }
        }
        if (northTravel > maxValue) {
            maxValue = northTravel;
        }
        if (eastTravel > maxValue) {
            maxValue = eastTravel;
            direction = Direction.E;
        }
        if (southTravel > maxValue) {
            maxValue = southTravel;
            direction = Direction.S;
        }
        if (westTravel > maxValue) {
            direction = Direction.W;
        }
        return direction;
    }
    @Override
    public int travelDirection(Map<String, BoardEntity> boardData, int boardSize) {
        Direction direction = getBestDirection(boardData, boardSize);
        int collectedFoodPoints = 0;
        switch (direction) {
            case E:
                for (int i = entityPosition.getY() + 2; i <= boardSize; i = i + 2) {
                    if (!color.equals(Main.getInsectsPositionAndColor().get(new EntityPosition(
                            entityPosition.getX(), i).getString()))
                            & Main.getInsectsPositionAndColor().containsKey(entityPosition.getX() + " " + i)) {
                        Main.getInsectsPositionAndColor().remove(entityPosition.getString());
                        boardData.remove(entityPosition.getString());
                        return collectedFoodPoints;
                    }
                    if (Main.getFoodPositions().containsKey(new EntityPosition(entityPosition.getX(), i).getString())) {
                        collectedFoodPoints += Main.getFoodPositions().get(new EntityPosition(
                                entityPosition.getX(), i).getString());
                        boardData.remove(new EntityPosition(entityPosition.getX(), i).getString());
                        Main.getFoodPositions().remove(entityPosition.getX() + " " + i);
                    }
                }
                Main.getInsectsPositionAndColor().remove(entityPosition.getString());
                boardData.remove(entityPosition.getString());
                return collectedFoodPoints;
            case S:
                for (int i = entityPosition.getX() + 2; i <= boardSize; i = i + 2) {
                    if (!color.equals(Main.getInsectsPositionAndColor().get(new EntityPosition(
                            i, entityPosition.getY()).getString()))
                            & Main.getInsectsPositionAndColor().containsKey(i + " " + entityPosition.getY())) {
                        Main.getInsectsPositionAndColor().remove(entityPosition.getString());
                        boardData.remove(entityPosition.getString());
                        return collectedFoodPoints;
                    }
                    if (Main.getFoodPositions().containsKey(new EntityPosition(i, entityPosition.getY()).getString())) {
                    collectedFoodPoints += Main.getFoodPositions().get(new EntityPosition(
                            i, entityPosition.getY()).getString());
                    boardData.remove(new EntityPosition(i, entityPosition.getY()).getString());
                    Main.getFoodPositions().remove(i + " " + entityPosition.getY());
                }
            }
                boardData.remove(entityPosition.getString());
                return collectedFoodPoints;
            case W:
                for (int i = entityPosition.getY() - 2; i >= 1; i = i - 2)  {
                    if (!color.equals(Main.getInsectsPositionAndColor().get(new EntityPosition(
                            entityPosition.getX(), i).getString()))
                            & Main.getInsectsPositionAndColor().containsKey(entityPosition.getX() + " " + i)) {
                        Main.getInsectsPositionAndColor().remove(entityPosition.getString());
                        boardData.remove(entityPosition.getString());
                        return collectedFoodPoints;
                    }
                    if (Main.getFoodPositions().containsKey(new EntityPosition(entityPosition.getX(), i).getString())) {
                        collectedFoodPoints += Main.getFoodPositions().get(new EntityPosition(
                                entityPosition.getX(), i).getString());
                        boardData.remove(new EntityPosition(entityPosition.getX(), i).getString());
                        Main.getFoodPositions().remove(entityPosition.getX() + " " + i);
                    }
                }
                Main.getInsectsPositionAndColor().remove(entityPosition.getString());
                boardData.remove(entityPosition.getString());
                return collectedFoodPoints;
            default:
                for (int i = entityPosition.getX() - 2; i >= 1; i = i - 2) {
                    if (!color.equals(Main.getInsectsPositionAndColor().get(new EntityPosition(
                            i, entityPosition.getY()).getString()))
                            & Main.getInsectsPositionAndColor().containsKey(i + " " + entityPosition.getY())) {
                        Main.getInsectsPositionAndColor().remove(entityPosition.getString());
                        boardData.remove(entityPosition.getString());
                        return collectedFoodPoints;
                    }
                    if (Main.getFoodPositions().containsKey(new EntityPosition(i, entityPosition.getY()).getString())) {
                        collectedFoodPoints += Main.getFoodPositions().get(new EntityPosition(
                                i, entityPosition.getY()).getString());
                        boardData.remove(new EntityPosition(i, entityPosition.getY()).getString());
                        Main.getFoodPositions().remove(i + " " + entityPosition.getY());
                    }
                }
                Main.getInsectsPositionAndColor().remove(entityPosition.getString());
                boardData.remove(entityPosition.getString());
                return collectedFoodPoints;
        }
    }
}


/**
 * Interface for orthogonal movement
 */
interface OrthogonalMoving {
    /**
     * Gets orthogonal direction travel value.
     * @param dir direction
     * @param entityPosition entity position
     * @param boardData board data
     * @param boardSize board size
     * @return orthogonal direction travel value
     */
    int getOrthogonalDirectionVisualValue(Direction dir, EntityPosition entityPosition, Map<String, BoardEntity>
            boardData, int boardSize);

    /**
     * Method for simulating orthogonal movement
     * @param dir direction
     * @param entityPosition entity position
     * @param boardData board data
     * @param boardSize board size
     * @return number of food points eaten
     */
    int travelOrthogonally(Direction dir, EntityPosition entityPosition, Map<String, BoardEntity>
            boardData, int boardSize);
}

/**
 * Interface for diagonal movement
 */
interface DiagonalMoving {
    /**
     * Method for simulating diagonal movement
     * @param dir direction
     * @param entityPosition entity position
     * @param boardData board data
     * @param boardSize board size
     * @return the diagonal direction movement value
     */
    int getDiagonalDirectionVisualValue(Direction dir, EntityPosition entityPosition, Map<String, BoardEntity>
            boardData, int boardSize);

    /**
     * Method for simulating diagonal movement
     * @param dir direction
     * @param entityPosition entity position
     * @param boardData board data
     * @param boardSize board size
     * @return number of food points eaten
     */
    int travelDiagonally(Direction dir, EntityPosition entityPosition, Map<String, BoardEntity>
            boardData, int boardSize);
}

/**
 * Butterfly class
 */
class Butterfly extends Insect implements OrthogonalMoving {


    /**
     * Constructor for a butterfly
     * @param position position
     * @param color color
     */
    public Butterfly(EntityPosition position, InsectColor color) {
        super(position, color);
    }

    @Override
    public Direction getBestDirection(Map<String, BoardEntity> boardData, int boardSize) {
        ArrayList<Integer> directions = new ArrayList<>();
        int northDirection = 0;
        int eastDirection = 0;
        int southDirection = 0;
        int westDirection = 0;
        Direction direction = Direction.N;
        int maxValue = 0;
        northDirection = getOrthogonalDirectionVisualValue(Direction.N, entityPosition, boardData, boardSize);
        eastDirection = getOrthogonalDirectionVisualValue(Direction.E, entityPosition, boardData, boardSize);
        southDirection = getOrthogonalDirectionVisualValue(Direction.S, entityPosition, boardData, boardSize);
        westDirection = getOrthogonalDirectionVisualValue(Direction.W, entityPosition, boardData, boardSize);
        if (northDirection > maxValue) {
            maxValue = northDirection;
        }
        if (eastDirection > maxValue) {
            maxValue = eastDirection;
            direction = Direction.E;
        }
        if (southDirection > maxValue) {
            maxValue = southDirection;
            direction = Direction.S;
        }
        if (westDirection > maxValue) {
            direction = Direction.W;
        }
        return direction;


    }

    @Override
    public int travelDirection(Map<String, BoardEntity> boardData, int boardSize) {
        Direction direction = getBestDirection(boardData, boardSize);
        return travelOrthogonally(direction, entityPosition, boardData, boardSize);
    }

    @Override
    public int getOrthogonalDirectionVisualValue(Direction dir, EntityPosition entityPosition,
                                                 Map<String, BoardEntity> boardData, int boardSize) {
        int directionVisualValue = 0;
        switch (dir) {
            case E:
                for (int i = entityPosition.getY(); i <= boardSize; i++) {
                    if (Main.getFoodPositions().containsKey(new EntityPosition(entityPosition.getX(), i).getString())
                            & !Main.getInsectsPositionAndColor().containsKey(entityPosition.getX() + " " + i)) {
                        directionVisualValue += Main.getFoodPositions().get(entityPosition.getX() + " " + i);
                    }
                }
                return directionVisualValue;
            case S:
                for (int i = entityPosition.getX(); i <= boardSize; i++) {
                    if (Main.getFoodPositions().containsKey(new EntityPosition(i, entityPosition.getY()).getString())
                            & !Main.getInsectsPositionAndColor().containsKey(i + " " + entityPosition.getY())) {
                        directionVisualValue += Main.getFoodPositions().get(i + " " + entityPosition.getY());
                    }
                }
                return directionVisualValue;
            case W:
                for (int i = entityPosition.getY(); i >= 1; i--) {
                    if (Main.getFoodPositions().containsKey(new EntityPosition(entityPosition.getX(), i).getString())
                            & !Main.getInsectsPositionAndColor().containsKey(entityPosition.getX() + " " + i)) {
                        directionVisualValue += Main.getFoodPositions().get(entityPosition.getX() + " " + i);
                    }
                }
                return directionVisualValue;
            default:
                for (int i = entityPosition.getX(); i >= 1; i--) {
                    if (Main.getFoodPositions().containsKey(new EntityPosition(i, entityPosition.getY()).getString())
                            & !Main.getInsectsPositionAndColor().containsKey(i + " " + entityPosition.getY())) {
                        directionVisualValue += Main.getFoodPositions().get(i + " " + entityPosition.getY());
                    }
                }
                return directionVisualValue;
        }
    }

    @Override
    public int travelOrthogonally(Direction dir, EntityPosition entityPosition,
                                  Map<String, BoardEntity> boardData, int boardSize) {
        int collectedFoodPoints = 0;
        switch (dir) {
            case E:
                for (int i = entityPosition.getY() + 1; i <= boardSize; i++) {
                    InsectColor insectColor = Main.getInsectsPositionAndColor().get(entityPosition.getX() + " " + i);
                    if (!color.equals(Main.getInsectsPositionAndColor().get(new EntityPosition(
                            entityPosition.getX(), i).getString()))
                            & Main.getInsectsPositionAndColor().containsKey(entityPosition.getX() + " " + i)) {
                        Main.getInsectsPositionAndColor().remove(entityPosition.getString());
                        boardData.remove(entityPosition.getString());
                        return collectedFoodPoints;
                    }
                    if (Main.getFoodPositions().containsKey(new EntityPosition(entityPosition.getX(), i).getString())) {
                        collectedFoodPoints += Main.getFoodPositions().get(new EntityPosition(
                                entityPosition.getX(), i).getString());
                        boardData.remove(new EntityPosition(entityPosition.getX(), i).getString());
                        Main.getFoodPositions().remove(entityPosition.getX() + " " + i);
                    }
                }
                Main.getInsectsPositionAndColor().remove(entityPosition.getString());
                boardData.remove(entityPosition.getString());
                return collectedFoodPoints;
            case S:
                for (int i = entityPosition.getX() + 1; i <= boardSize; i++) {
                    if (!color.equals(Main.getInsectsPositionAndColor().get(new EntityPosition(
                            i, entityPosition.getY()).getString()))
                            & Main.getInsectsPositionAndColor().containsKey(i + " " + entityPosition.getY())) {
                        Main.getInsectsPositionAndColor().remove(entityPosition.getString());
                        boardData.remove(entityPosition.getString());
                        return collectedFoodPoints;
                    }
                    if (Main.getFoodPositions().containsKey(new EntityPosition(i, entityPosition.getY()).getString())) {
                        collectedFoodPoints += Main.getFoodPositions().get(new EntityPosition(
                                i, entityPosition.getY()).getString());
                        boardData.remove(new EntityPosition(i, entityPosition.getY()).getString());
                        Main.getFoodPositions().remove(i + " " + entityPosition.getY());
                    }
                }
                boardData.remove(entityPosition.getString());
                return collectedFoodPoints;
            case W:
                for (int i = entityPosition.getY() - 1; i >= 1; i--)  {
                    if (!color.equals(Main.getInsectsPositionAndColor().get(new EntityPosition(
                            entityPosition.getX(), i).getString()))
                            & Main.getInsectsPositionAndColor().containsKey(entityPosition.getX() + " " + i)) {
                        Main.getInsectsPositionAndColor().remove(entityPosition.getString());
                        boardData.remove(entityPosition.getString());
                        return collectedFoodPoints;
                    }
                    if (Main.getFoodPositions().containsKey(new EntityPosition(entityPosition.getX(), i).getString())) {
                        collectedFoodPoints += Main.getFoodPositions().get(new EntityPosition(
                                entityPosition.getX(), i).getString());
                        boardData.remove(new EntityPosition(entityPosition.getX(), i).getString());
                        Main.getFoodPositions().remove(entityPosition.getX() + " " + i);
                    }
                }
                Main.getInsectsPositionAndColor().remove(entityPosition.getString());
                boardData.remove(entityPosition.getString());
                return collectedFoodPoints;
            default:
                for (int i = entityPosition.getX() - 1; i >= 1; i--) {
                    if (!color.equals(Main.getInsectsPositionAndColor().get(new EntityPosition(
                            i, entityPosition.getY()).getString()))
                            & Main.getInsectsPositionAndColor().containsKey(i + " " + entityPosition.getY())) {
                        Main.getInsectsPositionAndColor().remove(entityPosition.getString());
                        boardData.remove(entityPosition.getString());
                        return collectedFoodPoints;
                    }
                    if (Main.getFoodPositions().containsKey(new EntityPosition(i, entityPosition.getY()).getString())) {
                        collectedFoodPoints += Main.getFoodPositions().get(new EntityPosition(
                                i, entityPosition.getY()).getString());
                        boardData.remove(new EntityPosition(i, entityPosition.getY()).getString());
                        Main.getFoodPositions().remove(i + " " + entityPosition.getY());
                    }
                }
                Main.getInsectsPositionAndColor().remove(entityPosition.getString());
                boardData.remove(entityPosition.getString());
                return collectedFoodPoints;
        }
    }
}

/**
 * Class for an ant
 */
class Ant extends Insect implements OrthogonalMoving, DiagonalMoving {

    /**
     * Constructor for an ant
     * @param position position
     * @param color color
     */
    public Ant(EntityPosition position, InsectColor color) {
        super(position, color);
    }


    @Override
    public Direction getBestDirection(Map<String, BoardEntity> boardData, int boardSize) {
        int directionN = getOrthogonalDirectionVisualValue(Direction.N, entityPosition, boardData, boardSize);
        int directionE = getOrthogonalDirectionVisualValue(Direction.E, entityPosition, boardData, boardSize);
        int directionS = getOrthogonalDirectionVisualValue(Direction.S, entityPosition, boardData, boardSize);
        int directionW = getOrthogonalDirectionVisualValue(Direction.W, entityPosition, boardData, boardSize);
        int directionNE = getDiagonalDirectionVisualValue(Direction.NE, entityPosition, boardData, boardSize);
        int directionSE = getDiagonalDirectionVisualValue(Direction.SE, entityPosition, boardData, boardSize);
        int directionSW = getDiagonalDirectionVisualValue(Direction.SW, entityPosition, boardData, boardSize);
        int directionNW = getDiagonalDirectionVisualValue(Direction.NW, entityPosition, boardData, boardSize);
        int maxValue = 0;

        Direction direction = Direction.N;
        if (directionN > maxValue) {
            maxValue = directionN;
        }
        if (directionE > maxValue) {
            maxValue = directionE;
            direction = Direction.E;
        }
        if (directionS > maxValue) {
            maxValue = directionS;
            direction = Direction.S;
        }
        if (directionW > maxValue) {
            maxValue = directionW;
            direction = Direction.W;
        }
        if (directionNE > maxValue) {
            maxValue = directionNE;
            direction = Direction.NE;
        }
        if (directionSE > maxValue) {
            maxValue = directionSE;
            direction = Direction.SE;
        }
        if (directionSW > maxValue) {
            maxValue = directionSW;
            direction = Direction.SW;
        }
        if (directionNW > maxValue) {
            direction = Direction.NW;
        }
        return direction;

    }

    @Override
    public int travelDirection(Map<String, BoardEntity> boardData, int boardSize) {
        Direction bestDirection = getBestDirection(boardData, boardSize);
        if (bestDirection.equals(Direction.N) | bestDirection.equals(Direction.S) | bestDirection.equals(Direction.W)
        | bestDirection.equals(Direction.E)) {
           return travelOrthogonally(bestDirection, entityPosition, boardData, boardSize);
        } else {
            return travelDiagonally(bestDirection, entityPosition, boardData, boardSize);
        }
    }

    @Override
    public int getOrthogonalDirectionVisualValue(Direction dir, EntityPosition entityPosition,
                                                 Map<String, BoardEntity> boardData, int boardSize) {
        int directionVisualValue = 0;
        switch (dir) {
            case E:
                for (int i = entityPosition.getY(); i <= boardSize; i++) {
                    if (Main.getFoodPositions().containsKey(new EntityPosition(entityPosition.getX(), i).getString())
                            & !Main.getInsectsPositionAndColor().containsKey(entityPosition.getX() + " " + i)) {
                        directionVisualValue += Main.getFoodPositions().get(entityPosition.getX() + " " + i);
                    }
                }
                return directionVisualValue;
            case S:
                for (int i = entityPosition.getX(); i <= boardSize; i++) {
                    if (Main.getFoodPositions().containsKey(new EntityPosition(i, entityPosition.getY()).getString())
                            & !Main.getInsectsPositionAndColor().containsKey(i + " " + entityPosition.getY())) {
                        directionVisualValue += Main.getFoodPositions().get(i + " " + entityPosition.getY());
                    }
                }
                return directionVisualValue;
            case W:
                for (int i = entityPosition.getY(); i >= 1; i--) {
                    if (Main.getFoodPositions().containsKey(new EntityPosition(entityPosition.getX(), i).getString())
                            & !Main.getInsectsPositionAndColor().containsKey(entityPosition.getX() + " " + i)) {
                        directionVisualValue += Main.getFoodPositions().get(entityPosition.getX() + " " + i);
                    }
                }
                return directionVisualValue;
            default:
                for (int i = entityPosition.getX(); i >= 1; i--) {
                    if (Main.getFoodPositions().containsKey(new EntityPosition(i, entityPosition.getY()).getString())
                            & !Main.getInsectsPositionAndColor().containsKey(i + " " + entityPosition.getY())) {
                        directionVisualValue += Main.getFoodPositions().get(i + " " + entityPosition.getY());
                    }
                }
                return directionVisualValue;
        }
    }

    @Override
    public int travelOrthogonally(Direction dir, EntityPosition entityPosition, Map<String, BoardEntity> boardData,
                                  int boardSize) {
        int collectedFoodPoints = 0;
        switch (dir) {
            case E:
                for (int i = entityPosition.getY() + 1; i <= boardSize; i++) {
                    InsectColor insectColor = Main.getInsectsPositionAndColor().get(entityPosition.getX() + " " + i);
                    if (!color.equals(Main.getInsectsPositionAndColor().get(new EntityPosition(
                            entityPosition.getX(), i).getString()))
                            & Main.getInsectsPositionAndColor().containsKey(entityPosition.getX() + " " + i)) {
                        Main.getInsectsPositionAndColor().remove(entityPosition.getString());
                        boardData.remove(entityPosition.getString());
                        return collectedFoodPoints;
                    }
                    if (Main.getFoodPositions().containsKey(new EntityPosition(entityPosition.getX(), i).getString())) {
                        collectedFoodPoints += Main.getFoodPositions().get(new EntityPosition(
                                entityPosition.getX(), i).getString());
                        boardData.remove(new EntityPosition(entityPosition.getX(), i).getString());
                        Main.getFoodPositions().remove(entityPosition.getX() + " " + i);
                    }
                }
                Main.getInsectsPositionAndColor().remove(entityPosition.getString());
                boardData.remove(entityPosition.getString());
                return collectedFoodPoints;
            case S:
                for (int i = entityPosition.getX() + 1; i <= boardSize; i++) {
                    if (!color.equals(Main.getInsectsPositionAndColor().get(new EntityPosition(
                            i, entityPosition.getY()).getString()))
                            & Main.getInsectsPositionAndColor().containsKey(i + " " + entityPosition.getY())) {
                        Main.getInsectsPositionAndColor().remove(entityPosition.getString());
                        boardData.remove(entityPosition.getString());
                        return collectedFoodPoints;
                    }
                    if (Main.getFoodPositions().containsKey(new EntityPosition(i, entityPosition.getY()).getString())) {
                        collectedFoodPoints += Main.getFoodPositions().get(new EntityPosition(
                                i, entityPosition.getY()).getString());
                        boardData.remove(new EntityPosition(i, entityPosition.getY()).getString());
                        Main.getFoodPositions().remove(i + " " + entityPosition.getY());
                    }
                }
                boardData.remove(entityPosition.getString());
                return collectedFoodPoints;
            case W:
                for (int i = entityPosition.getY() - 1; i >= 1; i--)  {
                    if (!color.equals(Main.getInsectsPositionAndColor().get(new EntityPosition(
                            entityPosition.getX(), i).getString()))
                            & Main.getInsectsPositionAndColor().containsKey(entityPosition.getX() + " " + i)) {
                        Main.getInsectsPositionAndColor().remove(entityPosition.getString());
                        boardData.remove(entityPosition.getString());
                        return collectedFoodPoints;
                    }
                    if (Main.getFoodPositions().containsKey(new EntityPosition(entityPosition.getX(), i).getString())) {
                        collectedFoodPoints += Main.getFoodPositions().get(new EntityPosition(
                                entityPosition.getX(), i).getString());
                        boardData.remove(new EntityPosition(entityPosition.getX(), i).getString());
                        Main.getFoodPositions().remove(entityPosition.getX() + " " + i);
                    }
                }
                Main.getInsectsPositionAndColor().remove(entityPosition.getString());
                boardData.remove(entityPosition.getString());
                return collectedFoodPoints;
            default:
                for (int i = entityPosition.getX() - 1; i >= 1; i--) {
                    if (!color.equals(Main.getInsectsPositionAndColor().get(new EntityPosition(
                            i, entityPosition.getY()).getString()))
                            & Main.getInsectsPositionAndColor().containsKey(i + " " + entityPosition.getY())) {
                        Main.getInsectsPositionAndColor().remove(entityPosition.getString());
                        boardData.remove(entityPosition.getString());
                        return collectedFoodPoints;
                    }
                    if (Main.getFoodPositions().containsKey(new EntityPosition(i, entityPosition.getY()).getString())) {
                        collectedFoodPoints += Main.getFoodPositions().get(new EntityPosition(
                                i, entityPosition.getY()).getString());
                        boardData.remove(new EntityPosition(i, entityPosition.getY()).getString());
                        Main.getFoodPositions().remove(i + " " + entityPosition.getY());
                    }
                }
                Main.getInsectsPositionAndColor().remove(entityPosition.getString());
                boardData.remove(entityPosition.getString());
                return collectedFoodPoints;
        }
    }

    @Override
    public int getDiagonalDirectionVisualValue(Direction dir, EntityPosition entityPosition,
                                               Map<String, BoardEntity> boardData, int boardSize) {
        int directionVisualValue = 0;
        int xCoordinate = entityPosition.getX();
        switch (dir) {
            case SE:
                for (int i = entityPosition.getY(); i <= boardSize && xCoordinate >= 1; i++) {
                    if (Main.getFoodPositions().containsKey(xCoordinate + " " + i)
                            & !Main.getInsectsPositionAndColor().containsKey(xCoordinate + " " + i)) {
                        directionVisualValue += Main.getFoodPositions().get(xCoordinate + " " + i);
                    }
                    xCoordinate++;
                }
                return directionVisualValue;
            case SW:
                for (int i = entityPosition.getY(); i >= 1 && xCoordinate <= boardSize; i--) {
                    if (Main.getFoodPositions().containsKey(xCoordinate + " " + i)
                            & !Main.getInsectsPositionAndColor().containsKey(xCoordinate + " " + i)) {
                        directionVisualValue += Main.getFoodPositions().get(xCoordinate + " " + i);
                    }
                    xCoordinate++;
                }
                return directionVisualValue;
            case NW:
                for (int i = entityPosition.getY(); i >= 1 && xCoordinate <= boardSize; i--) {
                    if (Main.getFoodPositions().containsKey(xCoordinate + " " + i)
                            & !Main.getInsectsPositionAndColor().containsKey(xCoordinate + " " + i)) {
                        directionVisualValue += Main.getFoodPositions().get(xCoordinate + " " + i);
                    }
                    xCoordinate--;
                }
                return directionVisualValue;
            default:
                for (int i = entityPosition.getY(); i <= boardSize && xCoordinate >= 1; i++) {
                    if (Main.getFoodPositions().containsKey(xCoordinate + " " + i)
                            & !Main.getInsectsPositionAndColor().containsKey(xCoordinate + " " + i)) {
                        directionVisualValue += Main.getFoodPositions().get(xCoordinate + " " + i);
                    }
                    xCoordinate--;
                }
                return directionVisualValue;
        }
    }

    @Override
    public int travelDiagonally(Direction dir, EntityPosition entityPosition, Map<String, BoardEntity> boardData,
                                int boardSize) {
        int collectedFoodPoints = 0;
        int xCoordinateForNENW = entityPosition.getX() - 1;
        int xCoordinateForSESW = entityPosition.getX() + 1;
        switch (dir) {
            case SE:
                for (int i = entityPosition.getY() + 1; i <= boardSize && xCoordinateForSESW >= 1; i++) {
                    if (!color.equals(Main.getInsectsPositionAndColor().get(new EntityPosition(
                            xCoordinateForSESW, i).getString()))
                            & Main.getInsectsPositionAndColor().containsKey(xCoordinateForSESW + " " + i)) {
                        Main.getInsectsPositionAndColor().remove(entityPosition.getString());
                        boardData.remove(entityPosition.getString());
                        return collectedFoodPoints;
                    }
                    if (Main.getFoodPositions().containsKey(xCoordinateForSESW + " " + i)
                            & !Main.getInsectsPositionAndColor().containsKey(xCoordinateForSESW + " " + i)) {
                        collectedFoodPoints += Main.getFoodPositions().get(xCoordinateForSESW + " " + i);
                        boardData.remove(xCoordinateForSESW + " " + i);
                        Main.getFoodPositions().remove(xCoordinateForSESW + " " + i);
                    }
                    xCoordinateForSESW++;
                }
                return collectedFoodPoints;
            case SW:
                for (int i = entityPosition.getY() - 1; i >= 1 && xCoordinateForSESW <= boardSize; i--) {
                    if (!color.equals(Main.getInsectsPositionAndColor().get(new EntityPosition(
                            xCoordinateForSESW, i).getString()))
                            & Main.getInsectsPositionAndColor().containsKey(xCoordinateForSESW + " " + i)) {
                        Main.getInsectsPositionAndColor().remove(entityPosition.getString());
                        boardData.remove(entityPosition.getString());
                        return collectedFoodPoints;
                    }
                    if (Main.getFoodPositions().containsKey(xCoordinateForSESW + " " + i)
                            & !Main.getInsectsPositionAndColor().containsKey(xCoordinateForSESW + " " + i)) {
                        collectedFoodPoints += Main.getFoodPositions().get(xCoordinateForSESW + " " + i);
                        boardData.remove(xCoordinateForSESW + " " + i);
                        Main.getFoodPositions().remove(xCoordinateForSESW + " " + i);
                    }
                    xCoordinateForSESW++;
                }
                return collectedFoodPoints;
            case NW:
                for (int i = entityPosition.getY() - 1; i >= 1 && xCoordinateForNENW <= boardSize; i--) {
                    if (!color.equals(Main.getInsectsPositionAndColor().get(new EntityPosition(
                            xCoordinateForNENW, i).getString()))
                            & Main.getInsectsPositionAndColor().containsKey(xCoordinateForNENW + " " + i)) {
                        Main.getInsectsPositionAndColor().remove(entityPosition.getString());
                        boardData.remove(entityPosition.getString());
                        return collectedFoodPoints;
                    }
                    if (Main.getFoodPositions().containsKey(xCoordinateForNENW + " " + i)
                            & !Main.getInsectsPositionAndColor().containsKey(xCoordinateForNENW + " " + i)) {
                        collectedFoodPoints += Main.getFoodPositions().get(xCoordinateForNENW + " " + i);
                        boardData.remove(xCoordinateForNENW + " " + i);
                        Main.getFoodPositions().remove(xCoordinateForNENW + " " + i);
                    }
                    xCoordinateForNENW--;
                }
                return collectedFoodPoints;
            default:
                for (int i = entityPosition.getY() + 1; i <= boardSize && xCoordinateForNENW >= 1; i++) {
                    if (!color.equals(Main.getInsectsPositionAndColor().get(new EntityPosition(
                            xCoordinateForNENW, i).getString()))
                            & Main.getInsectsPositionAndColor().containsKey(xCoordinateForNENW + " " + i)) {
                        Main.getInsectsPositionAndColor().remove(entityPosition.getString());
                        boardData.remove(entityPosition.getString());
                        return collectedFoodPoints;
                    }
                    if (Main.getFoodPositions().containsKey(xCoordinateForNENW + " " + i)
                            & !Main.getInsectsPositionAndColor().containsKey(xCoordinateForNENW + " " + i)) {
                        collectedFoodPoints += Main.getFoodPositions().get(xCoordinateForNENW + " " + i);
                        boardData.remove(xCoordinateForNENW + " " + i);
                        Main.getFoodPositions().remove(xCoordinateForNENW + " " + i);
                    }
                    xCoordinateForNENW--;
                }
                return collectedFoodPoints;
        }
    }
}

/**
 * Class for a spider
 */
class Spider extends Insect implements DiagonalMoving {

    /**
     * Constructor for a spider
     * @param position position
     * @param color color
     */
    public Spider(EntityPosition position, InsectColor color) {
        super(position, color);
    }

    @Override
    public Direction getBestDirection(Map<String, BoardEntity> boardData, int boardSize) {
        int directionNE = getDiagonalDirectionVisualValue(Direction.NE, entityPosition, boardData, boardSize);
        int directionSE = getDiagonalDirectionVisualValue(Direction.SE, entityPosition, boardData, boardSize);
        int directionSW = getDiagonalDirectionVisualValue(Direction.SW, entityPosition, boardData, boardSize);
        int directionNW = getDiagonalDirectionVisualValue(Direction.NW, entityPosition, boardData, boardSize);
        int maxValue = 0;
        Direction direction = Direction.NE;
        if (directionNE > maxValue) {
            maxValue = directionNE;
        }
        if (directionSE > maxValue) {
            maxValue = directionSE;
            direction = Direction.SE;
        }
        if (directionSW > maxValue) {
            maxValue = directionSW;
            direction = Direction.SW;
        }
        if (directionNW > maxValue) {
            maxValue = directionNW;
            direction = Direction.NW;
        }
        return direction;
    }

    @Override
    public int travelDirection(Map<String, BoardEntity> boardData, int boardSize) {
        Direction bestDirection = getBestDirection(boardData, boardSize);
        return travelDiagonally(bestDirection, entityPosition, boardData, boardSize);
    }

    @Override
    public int getDiagonalDirectionVisualValue(Direction dir, EntityPosition entityPosition,
                                               Map<String, BoardEntity> boardData, int boardSize) {
        int directionVisualValue = 0;
        int xCoordinate = entityPosition.getX();
        switch (dir) {
            case SE:
                for (int i = entityPosition.getY(); i <= boardSize && xCoordinate >= 1; i++) {
                    if (Main.getFoodPositions().containsKey(xCoordinate + " " + i)
                            & !Main.getInsectsPositionAndColor().containsKey(xCoordinate + " " + i)) {
                        directionVisualValue += Main.getFoodPositions().get(xCoordinate + " " + i);
                    }
                    xCoordinate++;
                }
                return directionVisualValue;
            case SW:
                for (int i = entityPosition.getY(); i >= 1 && xCoordinate <= boardSize; i--) {
                    if (Main.getFoodPositions().containsKey(xCoordinate + " " + i)
                            & !Main.getInsectsPositionAndColor().containsKey(xCoordinate + " " + i)) {
                        directionVisualValue += Main.getFoodPositions().get(xCoordinate + " " + i);
                    }
                    xCoordinate++;
                }
                return directionVisualValue;
            case NW:
                for (int i = entityPosition.getY(); i >= 1 && xCoordinate <= boardSize; i--) {
                    if (Main.getFoodPositions().containsKey(xCoordinate + " " + i)
                            & !Main.getInsectsPositionAndColor().containsKey(xCoordinate + " " + i)) {
                        directionVisualValue += Main.getFoodPositions().get(xCoordinate + " " + i);
                    }
                    xCoordinate--;
                }
                return directionVisualValue;
            default:
                for (int i = entityPosition.getY(); i <= boardSize && xCoordinate >= 1; i++) {
                    if (Main.getFoodPositions().containsKey(xCoordinate + " " + i)
                            & !Main.getInsectsPositionAndColor().containsKey(xCoordinate + " " + i)) {
                        directionVisualValue += Main.getFoodPositions().get(xCoordinate + " " + i);
                    }
                    xCoordinate--;
                }
                return directionVisualValue;
        }
    }

    @Override
    public int travelDiagonally(Direction dir, EntityPosition entityPosition, Map<String, BoardEntity> boardData,
                                int boardSize) {
        int collectedFoodPoints = 0;
        int xCoordinateForNENW = entityPosition.getX() - 1;
        int xCoordinateForSESW = entityPosition.getX() + 1;
        switch (dir) {
            case SE:
                for (int i = entityPosition.getY() + 1; i <= boardSize && xCoordinateForSESW >= 1; i++) {
                    if (!color.equals(Main.getInsectsPositionAndColor().get(new EntityPosition(
                            xCoordinateForSESW, i).getString()))
                            & Main.getInsectsPositionAndColor().containsKey(xCoordinateForSESW + " " + i)) {
                        Main.getInsectsPositionAndColor().remove(entityPosition.getString());
                        boardData.remove(entityPosition.getString());
                        return collectedFoodPoints;
                    }
                    if (Main.getFoodPositions().containsKey(xCoordinateForSESW + " " + i)
                            & !Main.getInsectsPositionAndColor().containsKey(xCoordinateForSESW + " " + i)) {
                        collectedFoodPoints += Main.getFoodPositions().get(xCoordinateForSESW + " " + i);
                        boardData.remove(xCoordinateForSESW + " " + i);
                        Main.getFoodPositions().remove(xCoordinateForSESW + " " + i);
                    }
                    xCoordinateForSESW++;
                }
                return collectedFoodPoints;
            case SW:
                for (int i = entityPosition.getY() - 1; i >= 1 && xCoordinateForSESW <= boardSize; i--) {
                    if (!color.equals(Main.getInsectsPositionAndColor().get(new EntityPosition(
                            xCoordinateForSESW, i).getString()))
                            & Main.getInsectsPositionAndColor().containsKey(xCoordinateForSESW + " " + i)) {
                        Main.getInsectsPositionAndColor().remove(entityPosition.getString());
                        boardData.remove(entityPosition.getString());
                        return collectedFoodPoints;
                    }
                    if (Main.getFoodPositions().containsKey(xCoordinateForSESW + " " + i)
                            & !Main.getInsectsPositionAndColor().containsKey(xCoordinateForSESW + " " + i)) {
                        collectedFoodPoints += Main.getFoodPositions().get(xCoordinateForSESW + " " + i);
                        boardData.remove(xCoordinateForSESW + " " + i);
                        Main.getFoodPositions().remove(xCoordinateForSESW + " " + i);
                    }
                    xCoordinateForSESW++;
                }
                return collectedFoodPoints;
            case NW:
                for (int i = entityPosition.getY() - 1; i >= 1 && xCoordinateForNENW <= boardSize; i--) {
                    if (!color.equals(Main.getInsectsPositionAndColor().get(new EntityPosition(
                            xCoordinateForNENW, i).getString()))
                            & Main.getInsectsPositionAndColor().containsKey(xCoordinateForNENW + " " + i)) {
                        Main.getInsectsPositionAndColor().remove(entityPosition.getString());
                        boardData.remove(entityPosition.getString());
                        return collectedFoodPoints;
                    }
                    if (Main.getFoodPositions().containsKey(xCoordinateForNENW + " " + i)
                            & !Main.getInsectsPositionAndColor().containsKey(xCoordinateForNENW + " " + i)) {
                        collectedFoodPoints += Main.getFoodPositions().get(xCoordinateForNENW + " " + i);
                        boardData.remove(xCoordinateForNENW + " " + i);
                        Main.getFoodPositions().remove(xCoordinateForNENW + " " + i);
                    }
                    xCoordinateForNENW--;
                }
                return collectedFoodPoints;
            default:
                for (int i = entityPosition.getY() + 1; i <= boardSize && xCoordinateForNENW >= 1; i++) {
                    if (!color.equals(Main.getInsectsPositionAndColor().get(new EntityPosition(
                            xCoordinateForNENW, i).getString()))
                            & Main.getInsectsPositionAndColor().containsKey(xCoordinateForNENW + " " + i)) {
                        Main.getInsectsPositionAndColor().remove(entityPosition.getString());
                        boardData.remove(entityPosition.getString());
                        return collectedFoodPoints;
                    }
                    if (Main.getFoodPositions().containsKey(xCoordinateForNENW + " " + i)
                            & !Main.getInsectsPositionAndColor().containsKey(xCoordinateForNENW + " " + i)) {
                        collectedFoodPoints += Main.getFoodPositions().get(xCoordinateForNENW + " " + i);
                        boardData.remove(xCoordinateForNENW + " " + i);
                        Main.getFoodPositions().remove(xCoordinateForNENW + " " + i);
                    }
                    xCoordinateForNENW--;
                }
                return collectedFoodPoints;
        }
    }
}

/**
 * Enumeration for directions.
 */
enum Direction {
    N("North"),
    E("East"),
    S("South"),
    W("West"),
    NE("North-East"),
    SE("South-East"),
    SW("South-West"),
    NW("North-West");
    private String textRepresentation;

    /**
     * Creating text representation of enum value
     * @param text text
     */
    Direction(String text) {
        textRepresentation = text;
    }

    public String getTextRepresentation() {
        return textRepresentation;
    }
}

/**
 * Exceptions class
 */
class Exceptions {
    /**
     * Get exception message.
     *
     * @return exception message
     */
    public String getMessage() {
        return null;
    }
}

/**
 * Invalid board size exception.
 */
class InvalidBoardSizeException extends Exceptions {
    @Override
    public String getMessage() {
        return "Invalid board size";
    }
}

/**
 * Invalid number of insects exception.
 */
class InvalidNumberOfInsectsException extends Exceptions {
    @Override
    public String getMessage() {
        return "Invalid number of insects";
    }
}

/**
 * Invalid number of food points exception.
 */
class InvalidNumberOfFoodPointsException extends Exceptions {
    @Override
    public String getMessage() {
        return "Invalid number of food points";
    }
}

/**
 * Invalid insect color exception.
 */
class InvalidInsectColorException extends Exceptions {
    @Override
    public String getMessage() {
        return "Invalid insect color";
    }
}

/**
 * Invalid insect type exception.
 */
class InvalidInsectTypeException extends Exceptions {
    @Override
    public String getMessage() {
        return "Invalid insect type";
    }
}

/**
 * Invalid entity position exception.
 */
class InvalidEntityPositionException extends Exceptions {
    @Override
    public String getMessage() {
        return "Invalid entity position";
    }
}

/**
 * Duplicate insect exception.
 */
class DuplicateInsectException extends Exceptions {
    @Override
    public String getMessage() {
        return "Duplicate insects";
    }
}

/**
 * Two entities on the same position exception.
 */
class TwoEntitiesOnSamePositionException extends Exceptions {
    @Override
    public String getMessage() {
        return "Two entities in the same position";
    }
}
