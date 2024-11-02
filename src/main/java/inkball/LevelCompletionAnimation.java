package inkball;

import java.util.ArrayList;
import java.util.List;

/**
 * The LevelCompletionAnimation class handles the animation that plays when a level is completed.
 * It moves two yellow tiles around the edge of the game board in opposite directions, while adding the remaining
 * level time to the accumulated score at a rate of 1 unit per 0.067 seconds.
 * The animation continues until the remaining time reaches zero, 
 * and if it's the last level, a game completion message is displayed.
*/
public class LevelCompletionAnimation {
    /**
     * Reference to the current level.
     */
    private Level level;

    /**
     * Reference to the main App instance.
     */
    private App p;

    /**
     * Remaining time for the animation, based on the level's time.
     */
    private int timeRemaining;

    /**
     * Timer for tracking the animation progress.
     */
    private int completionTimer = 0;

    /**
     * Speed at which the tiles move, defined as the number of frames between tile movements.
     */
    private int tileSpeed = App.FPS / 15; // Tile moves every 0.067 seconds

    /**
     * Array of X coordinates for the tile path around the game board.
     */
    private int[] tilePathX;

    /**
     * Array of Y coordinates for the tile path around the game board.
     */
    private int[] tilePathY;

    /**
     * Index for the first tile's position on the path.
     */
    private int tileIndex1 = 0;

    /**
     * Index for the second tile's position on the path.
     */
    private int tileIndex2 = 0;

    /**
     * List of yellow tiles to be animated along the path.
     */
    private List<Tile> yellowTiles;

    /**
     * Flag indicating whether the animation has been completed.
     */
    private boolean animationCompleted = false;

    /**
     * Constructor for the LevelCompletionAnimation class.
     * Initializes the animation with the level, time, and tile paths.
     *
     * @param level The current level that is being animated.
     */
    public LevelCompletionAnimation(Level level) {
        this.level = level;
        this.p = level.getApp();
        this.timeRemaining = level.getTimeRemaining();

        yellowTiles = new ArrayList<>();

        // Initialize yellow tiles and the path they will follow
        initializeTilePath();
        initializeYellowTiles();
    }

    /**
     * Initializes the path that yellow tiles will follow, which goes around the edge of the game board.
     */
    private void initializeTilePath() {
        int numTilesX = p.width / App.CELLSIZE;
        int numTilesY = Level.GRIDSIZE;

        // Define the path around the game board
        ArrayList<Integer> pathX = new ArrayList<>();
        ArrayList<Integer> pathY = new ArrayList<>();

        // Top row (left to right)
        for (int x = 0; x < numTilesX; x++) {
            pathX.add(x * App.CELLSIZE);
            pathY.add(Level.yOffset);
        }

        // Right column (top to bottom)
        for (int y = 1; y < numTilesY; y++) {
            pathX.add((numTilesX - 1) * App.CELLSIZE);
            pathY.add(y * App.CELLSIZE + Level.yOffset);
        }

        // Bottom row (right to left)
        for (int x = numTilesX - 2; x >= 0; x--) {
            pathX.add(x * App.CELLSIZE);
            pathY.add((numTilesY - 1) * App.CELLSIZE + Level.yOffset);
        }

        // Left column (bottom to top)
        for (int y = numTilesY - 2; y > 0; y--) {
            pathX.add(0);
            pathY.add(y * App.CELLSIZE + Level.yOffset);
        }

        // Store the path coordinates in arrays
        tilePathX = convertListToArray(pathX);
        tilePathY = convertListToArray(pathY);
    }

    /**
     * Converts an ArrayList of Integer to an int array.
     */
    private int[] convertListToArray(ArrayList<Integer> list) {
        int[] array = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    /**
     * Initializes the yellow tiles that will be animated around the game board.
     * Two tiles are created: one starting at the beginning of the path and another starting halfway along the path.
     */
    private void initializeYellowTiles() {
        // First tile starts at the beginning of the path
        Tile tile1 = new Tile(tilePathX[0], tilePathY[0], App.getWallImages()[4], p);
        yellowTiles.add(tile1);
        tileIndex1 = 0;

        // Second tile starts halfway around the path
        tileIndex2 = tilePathX.length / 2;
        Tile tile2 = new Tile(tilePathX[tileIndex2], tilePathY[tileIndex2], App.getWallImages()[4], p);
        yellowTiles.add(tile2);
    }

    /**
     * Updates the animation each frame, moving the yellow tiles around the board and incrementing the score.
     * The animation continues until the remaining time reaches zero.
     */
    public void update() {
        // Increment the score every 0.067 seconds (15 times per second)
        if (completionTimer % tileSpeed == 0 && timeRemaining > 0) {
            p.addToAccumulatedScore(1);
            timeRemaining--;
        }

        // Move the yellow tiles along the edge of the board
        moveYellowTiles();

        completionTimer++;

        // Check if all time has been added to the score
        if (timeRemaining <= 0) {
            animationCompleted = true;
        }
    }

    /**
     * Moves the yellow tiles around the edge of the game board.
     * One tile moves clockwise and the other moves counter-clockwise.
     */
    private void moveYellowTiles() {
        if (completionTimer % tileSpeed == 0) {
            // Move the first yellow tile clockwise
            tileIndex1 = (tileIndex1 + 1) % tilePathX.length;
            yellowTiles.get(0).setX(tilePathX[tileIndex1]);
            yellowTiles.get(0).setY(tilePathY[tileIndex1]);

            // Move the second yellow tile clockwise
            tileIndex2 = (tileIndex2 + 1) % tilePathX.length;
            yellowTiles.get(1).setX(tilePathX[tileIndex2]);
            yellowTiles.get(1).setY(tilePathY[tileIndex2]);
        }
    }

    /**
     * Draws the level completion animation, including yellow tiles and score/timer display.
     * If this is the last level, a final "ENDED" message is displayed when the animation is completed.
     */
    public void draw() {
        // Draw the yellow tiles
        for (Tile tile : yellowTiles) {
            tile.draw();
        }
    }

    /**
     * Checks if the animation has been completed.
     *
     * @return true if the animation is completed, false otherwise.
     */
    public boolean isAnimationCompleted() {
        return animationCompleted;
    }

    // Getters
    public int getTimeRemaining() {
        return timeRemaining;
    }

    public int getCompletionTimer() {
        return completionTimer;
    }

    public int getTileSpeed() {
        return tileSpeed;
    }

    public int[] getTilePathX() {
        return tilePathX;
    }

    public int[] getTilePathY() {
        return tilePathY;
    }

    public int getTileIndex1() {
        return tileIndex1;
    }

    public int getTileIndex2() {
        return tileIndex2;
    }

    public List<Tile> getYellowTiles() {
        return yellowTiles;
    }

    public App getApp() {
        return p;
    }

    public Level getLevel() {
        return level;
    }

}
