package inkball;

import processing.core.PImage;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Random;

/**
 * The Level class represents a level in the Inkball game. 
 * It manages the layout, entities (balls, walls, holes, spawners), game state (score, time), and user interactions.
 */
public class Level {

    /**
     * Reference to the main application (App class).
     */
    private App p;

    /**
     * List of all wall objects in the level.
     */
    private ArrayList<Wall> walls;

    /**
     * List of all hole objects in the level.
     */
    private ArrayList<Hole> holes;

    /**
     * List of all spawner objects in the level.
     */
    private ArrayList<Spawner> spawners;

    /**
     * List of all active ball objects in the level.
     */
    private ArrayList<Ball> balls;

    /**
     * List of all tile objects in the level.
     */
    private ArrayList<Tile> tiles;

    /**
     * List of all lines drawn by the player.
     */
    private ArrayList<Line> lines;

    /**
     * List of all acceleration tiles in the level.
     */
    private ArrayList<AccelerationTile> accelerationTiles;


    /**
     * Flag indicating whether the level has been completed.
     */
    private boolean levelCompleted;

    /**
     * Flag indicating whether the game has ended.
     */
    private boolean gameEnded;

    /**
     * Flag indicating whether the game is currently paused.
     */
    private boolean isPaused = false;

    /**
     * Flag indicating whether the time is up for the level.
     */
    private boolean timeUp = false;

    /**
     * Time limit for the level in seconds.
     */
    private int timeLimit;

    /**
     * Time remaining in the level in seconds.
     */
    private int timeRemaining;

    /**
     * Spawn interval for balls in frames.
     */
    private int spawnInterval;

    /**
     * Timer for spawning balls.
     */
    private int spawnTimer = 0;

    /**
     * Score modifier for increasing score when a ball is captured by the correct hole.
     */
    private float scoreIncreaseModifier;

    /**
     * Score modifier for decreasing score when a ball is captured by the wrong hole.
     */
    private float scoreDecreaseModifier;

    /**
     * Map storing score increases for each ball color.
     */
    private Map<Integer, Integer> scoreIncreaseMap;

    /**
     * Map storing score decreases for each ball color.
     */
    private Map<Integer, Integer> scoreDecreaseMap;

    /**
     * Score before the level was reset.
     */
    private int scoreBeforeReset;

    /**
     * Size of the grid (number of tiles per side).
     */
    public static final int GRIDSIZE = 18;

    /**
     * Vertical offset for the game board to shift it down from the top of the screen.
     */
    public static final int yOffset = 64;

    /**
     * Queue of balls to be spawned in the level.
     */
    private ArrayList<Ball> spawnQueue = new ArrayList<>();

    /**
     * Random generator used for randomizing elements in the level.
     */
    private Random random;

    /**
     * File path for the layout of the current level.
     */
    private String layoutFile;

    /**
     * Array of entity colors used for the balls in the level.
     */
    private String[] entityColors;

    /**
     * Handles player input for drawing lines and other interactions.
     */
    private InputHandler inputHandler;

    /**
     * Animation shown when the level is completed.
     */
    private LevelCompletionAnimation levelCompletionAnimation;

    /**
     * Renderer instance responsible for drawing all game elements in the level.
     */
    private Renderer renderer;


    /**
     * Constructor for the Level class.
     * Initializes the level with its configuration and loads the level layout and spawn queue.
     *
     * @param p                The main App instance.
     * @param layoutFile       The layout file for the level.
     * @param timeLimit        Time limit for the level in seconds.
     * @param spawnIntervalInSeconds Spawn interval for balls in seconds.
     * @param scoreIncreaseModifier  Modifier for score increases when capturing balls.
     * @param scoreDecreaseModifier  Modifier for score decreases when capturing balls in the wrong hole.
     * @param entityColors     Array of ball colors to be spawned.
     * @param scoreIncreaseMap Map of score increases by ball color.
     * @param scoreDecreaseMap Map of score decreases by ball color.
     */
    public Level(App p, String layoutFile, int timeLimit, int spawnIntervalInSeconds, float scoreIncreaseModifier, float scoreDecreaseModifier, String[] entityColors, Map<Integer, Integer> scoreIncreaseMap, Map<Integer, Integer> scoreDecreaseMap) {
        this.p = p;
        this.levelCompleted = false;
        this.gameEnded = false;

        walls = new ArrayList<>();
        holes = new ArrayList<>();
        spawners = new ArrayList<>();
        balls = new ArrayList<>();
        tiles = new ArrayList<>();
        lines = new ArrayList<>();
        accelerationTiles = new ArrayList<>();

        this.timeLimit = timeLimit;
        this.timeRemaining = timeLimit;
        this.layoutFile = layoutFile;
        this.entityColors = entityColors;
        this.spawnInterval = spawnIntervalInSeconds * App.FPS; // In frames
        this.spawnTimer = this.spawnInterval; // In frames
        this.scoreIncreaseModifier = scoreIncreaseModifier;
        this.scoreDecreaseModifier = scoreDecreaseModifier;
        this.scoreIncreaseMap = scoreIncreaseMap;
        this.scoreDecreaseMap = scoreDecreaseMap;

        random = new Random();
        this.scoreBeforeReset = p.getAccumulatedScore();

        loadLevel(layoutFile);
        initializeSpawnQueue(entityColors);

        inputHandler = new InputHandler(this);
        renderer = new Renderer(this, p);
    }

    /**
     * Loads the level layout from a file and initializes walls, holes, spawners, and other entities.
     *
     * @param layoutFile The file path for the level layout.
     */
    public void loadLevel(String layoutFile) {
        String[] levelLines = p.loadStrings(layoutFile);
        boolean[][] processed = new boolean[GRIDSIZE][GRIDSIZE];

        for (int row = 0; row < levelLines.length; row++) {
            String line = levelLines[row];
            for (int col = 0; col < line.length(); col++) {
                int x = col * App.CELLSIZE;
                int y = row * App.CELLSIZE + yOffset;

                PImage tileImage = App.getTileImage();
                tiles.add(new Tile(x, y, tileImage, p));

                if (processed[row][col]) {
                    continue;
                }

                char tileChar = line.charAt(col);

                // Handle walls
                if (tileChar == 'X' || (tileChar >= '1' && tileChar <= '4')) {
                    int wallNumber = getWallNumberFromChar(tileChar);
                    PImage wallImage = App.getWallImages()[wallNumber];
                    walls.add(new Wall(x, y, wallImage, wallNumber, p));
                    processed[row][col] = true;
                } 
                // Handle spawners
                else if (tileChar == 'S') {
                    PImage spawnerImage = App.getSpawnerImage();
                    spawners.add(new Spawner(x, y, spawnerImage, p));
                    processed[row][col] = true;
                } 
                // Handle holes
                else if (tileChar == 'H' && col + 1 < line.length()) {
                    char holeColorChar = line.charAt(col + 1);
                    int holeColor = Character.getNumericValue(holeColorChar);
                    PImage holeImage = App.getHoleImages()[holeColor];
                    holes.add(new Hole(x, y, holeImage, holeColor, p));

                    processed[row][col] = true;
                    processed[row][col + 1] = true;
                    if (row + 1 < levelLines.length) {
                        processed[row + 1][col] = true;
                        processed[row + 1][col + 1] = true;
                    }
                    col++;
                } 
                // Handle balls
                else if (tileChar == 'B' && col + 1 < line.length()) {
                    char ballColorChar = line.charAt(col + 1);
                    int ballColor = Character.getNumericValue(ballColorChar);
                    PImage ballImage = App.getBallImages()[ballColor];
                    balls.add(new Ball(x, y, ballImage, ballColor, p));

                    processed[row][col] = true;
                    processed[row][col + 1] = true;
                }
                // Handle acceleration tiles
                else if (tileChar == 'A' && col + 1 < line.length()) {
                    char directionChar = line.charAt(col + 1);
                    int direction = Character.getNumericValue(directionChar);

                    // Get the appropriate image and acceleration vector
                    PImage accelerationImage = App.getAccelerationImages()[direction];
                    if (accelerationImage == null) {
                        System.err.println("Acceleration image for direction " + direction + " is null.");
                        continue; 
                    }
                    float accelerationX = 0;
                    float accelerationY = 0;
                    switch (direction) {
                        case 0: // Up
                            accelerationY = -0.1f;
                            break;
                        case 1: // Down
                            accelerationY = 0.1f;
                            break;
                        case 2: // Left
                            accelerationX = -0.1f;
                            break;
                        case 3: // Right
                            accelerationX = 0.1f;
                            break;
                        default:
                            throw new IllegalArgumentException("Invalid acceleration tile direction: " + direction);
                    }

                    accelerationTiles.add(new AccelerationTile(x, y, accelerationImage, accelerationX, accelerationY, p));

                    processed[row][col] = true;
                    processed[row][col + 1] = true;
                }
            }
        }
    }

    /**
     * Initializes the spawn queue with balls of the specified colors.
     *
     * @param entityColors Array of ball colors to be spawned.
     */
    public void initializeSpawnQueue(String[] entityColors) {
        for (String color : entityColors) {
            int colorIndex = getColorIndexFromName(color);
            PImage ballImage = App.getBallImages()[colorIndex];
            spawnQueue.add(new Ball(0, 0, ballImage, colorIndex, p));
        }
    }

    /**
     * Converts a color name into its corresponding color index.
     *
     * @param colorName Name of the color.
     * @return The color index.
     */
    public static int getColorIndexFromName(String colorName) {
        switch (colorName) {
            case "grey":
                return 0;
            case "orange":
                return 1;
            case "blue":
                return 2;
            case "green":
                return 3;
            case "yellow":
                return 4;
            default:
                throw new IllegalArgumentException("Invalid ball color: " + colorName);
        }
    }

    /**
     * Maps a character to a wall number.
     *
     * @param tile Character representing a wall.
     * @return The corresponding wall number.
     */
    public int getWallNumberFromChar(char tile) {
        switch (tile) {
            case 'X':
                return 0;
            case '1':
                return 1;
            case '2':
                return 2;
            case '3':
                return 3;
            case '4':
                return 4;
            default:
                throw new IllegalArgumentException("Invalid wall tile: " + tile);
        }
    }

    /**
     * Updates the game state for each frame, including updating balls, checking for collisions,
     * updating the timer, and handling level completion.
     */
    public void update() {
        if (timeUp || gameEnded) {
            return;
        }

        if (!isPaused) {
            if (levelCompleted) {
                levelCompletionAnimation.update();
                if (levelCompletionAnimation.isAnimationCompleted()) {
                    proceedToNextLevel();
                }
            } else {
                updateBalls();
                updateTimer();

                if (balls.isEmpty() && spawnQueue.isEmpty()) {
                    levelCompleted = true;
                    levelCompletionAnimation = new LevelCompletionAnimation(this);
                }
            }
        }
    }

    /**
     * Updates the state of all balls, checking for collisions and removing balls that go out of bounds.
     */
    public void updateBalls() {
        Iterator<Ball> ballIterator = balls.iterator();
        while (ballIterator.hasNext()) {
            Ball ball = ballIterator.next();
            ball.updatePosition();

            for (Wall wall : walls) {
                wall.checkCollision(ball);
            }
            
            boolean ballInteractedWithHole = false;

            for (Hole hole : holes) {
                if (hole.handleBall(ball, this)) {
                    // Ball has been captured; remove it from the iterator
                    ballIterator.remove();
                    ballInteractedWithHole = true;
                    break; 
                } else if (hole.checkCollision(ball)) {
                    // The ball is within the attraction radius of this hole
                    ballInteractedWithHole = true;
                }
            }
    
            if (!ballInteractedWithHole) {
                // No holes are interacting with the ball; reset scale
                ball.setScale(1.0f);
            }

            // Apply acceleration tiles
            for (AccelerationTile accelTile : accelerationTiles) {
                accelTile.checkCollision(ball);
            }

            Iterator<Line> lineIterator = lines.iterator();
            while (lineIterator.hasNext()) {
                Line line = lineIterator.next();
                if (line.checkCollision(ball)) {
                    lineIterator.remove();
                    break;
                }
            }
        }

        // Handle ball spawning
        if (spawnTimer <= 0) {
            spawnBallsFromQueue();
            spawnTimer = spawnInterval;
        } else {
            spawnTimer--;
        }
    }

    /**
     * Calculates the time until the next ball is spawned.
     * 
     * @return time until the next spawn in seconds.
     */
    public float calculateTimeUntilNextSpawn() {
        return spawnTimer / (float)App.FPS;
    }

    /**
     * Updates the timer for the level and checks if time has run out.
     */
    public void updateTimer() {
        if (!timeUp && !levelCompleted) {
            if (p.frameCount % App.FPS == 0 && timeRemaining > 0) {
                timeRemaining--;
            }
            if (timeRemaining <= 0) {
                timeUp = true;
            }
        }
    }

    /**
     * Proceeds to the next level or ends the game if there are no more levels.
     */
    public void proceedToNextLevel() {
        if (p.hasNextLevel()) {
            p.loadNextLevel();
        } else {
            gameEnded = true;
        }
    }

    /**
     * Spawns balls from the spawn queue at the spawner locations.
     */
    public void spawnBallsFromQueue() {
        if (!spawnQueue.isEmpty()) {
            Ball ball = spawnQueue.remove(0);
            if (!spawners.isEmpty()) {
                Spawner spawner = spawners.get(random.nextInt(spawners.size()));
                ball.setX(spawner.getX());
                ball.setY(spawner.getY());
            }
            balls.add(ball);
        }
    }

    /**
     * Starts drawing a new line based on the player's mouse input.
     *
     * @param x           The X coordinate of the starting point.
     * @param y           The Y coordinate of the starting point.
     * @param mouseButton The mouse button that was pressed (e.g., left, right).
     */
    public void handleMouseClick(int x, int y, int mouseButton) {
        inputHandler.handleMouseClick(x, y, mouseButton);
    }


    /**
     * Adds a point to the current line being drawn by the player.
     *
     * @param x The X coordinate of the point.
     * @param y The Y coordinate of the point.
     */
    public void addPointToLine(int x, int y) {
        inputHandler.addPointToLine(x, y);
    }

    /**
     * Finishes drawing the current line.
     */
    public void finishDrawingLine() {
        inputHandler.finishDrawingLine();
    }

    /**
     * Adds a line to the level's list of lines.
     * This line represents the user's drawn path for interaction with game objects.
     *
     * @param line The line to be added to the level.
     */
    public void addLine(Line line) {
        lines.add(line);
    }

    /**
     * Increases the accumulated score based on the ball's color.
     *
     * @param ballColor The color index of the ball.
     */
    public void increaseScore(int ballColor) {
        int increaseAmount = scoreIncreaseMap.get(ballColor);
        int scoreToAdd = (int) (increaseAmount * scoreIncreaseModifier);
        p.addToAccumulatedScore(scoreToAdd);
    }

    /**
     * Decreases the accumulated score based on the ball's color.
     *
     * @param ballColor The color index of the ball.
     */
    public void decreaseScore(int ballColor) {
        int decreaseAmount = scoreDecreaseMap.get(ballColor);
        int scoreToSubtract = (int) (decreaseAmount * scoreDecreaseModifier);
        p.addToAccumulatedScore(-scoreToSubtract);
        if (p.getAccumulatedScore() < 0) {
            p.resetAccumulatedScore();
        }
    }

    /**
     * Toggles the pause state of the level.
     */
    public void togglePause() {
        isPaused = !isPaused;
    }

    /**
     * Checks if the time is up in the level.
     *
     * @return true if the time is up, false otherwise.
     */
    public boolean isTimeUp() {
        return timeUp;
    }

    /**
     * Checks if the game is currently paused.
     *
     * @return true if the game is paused, false otherwise.
     */
    public boolean isPaused() {
        return isPaused;
    }

    /**
     * Checks if the level is completed.
     *
     * @return true if the level is completed, false otherwise.
     */
    public boolean isLevelCompleted() {
        return levelCompleted;
    }

    /**
     * Checks if the game has ended.
     *
     * @return true if the game has ended, false otherwise.
     */
    public boolean isGameEnded() {
        return gameEnded;
    }

    /**
     * Adds a ball to the spawn queue for later spawning.
     *
     * @param ball The ball to be added to the spawn queue.
     */
    public void addBallToSpawnQueue(Ball ball) {
        spawnQueue.add(ball);  // Add the ball to the queue for later respawning
    }

    /**
     * Resets the level, clearing all entities and reloading the layout and spawn queue.
     *
     * @param layoutFile    The layout file path.
     * @param entityColors  The array of ball colors to be spawned.
     */
    public void resetLevel(String layoutFile, String[] entityColors) {
        p.setAccumulatedScore(scoreBeforeReset);

        walls.clear();
        holes.clear();
        spawners.clear();
        balls.clear();
        tiles.clear();
        lines.clear();
        spawnQueue.clear();

        levelCompleted = false;
        timeUp = false;
        isPaused = false;
        timeRemaining = timeLimit;
        spawnTimer = spawnInterval;

        loadLevel(layoutFile);
        initializeSpawnQueue(entityColors);
    }
    
    // Getters
    public String getLayoutFile() {
        return layoutFile;
    }

    public String[] getEntityColors() {
        return entityColors;
    }

    public int getScoreBeforeReset() {
        return scoreBeforeReset;
    }

    public int getTimeRemaining() {
        return timeRemaining;
    }

    public App getApp() {
        return p;
    }

    public ArrayList<Tile> getTiles() {
        return tiles;
    }

    public ArrayList<Wall> getWalls() {
        return walls;
    }

    public ArrayList<Hole> getHoles() {
        return holes;
    }

    public ArrayList<Spawner> getSpawners() {
        return spawners;
    }

    public ArrayList<Ball> getBalls() {
        return balls;
    }

    public ArrayList<AccelerationTile> getAccelerationTiles() {
        return accelerationTiles;
    }

    public ArrayList<Line> getLines() {
        return lines;
    }

    public ArrayList<Ball> getSpawnQueue() {
        return spawnQueue;
    }

    public Map<Integer, Integer> getScoreIncreaseMap() {
        return scoreIncreaseMap;
    }

    public Map<Integer, Integer> getScoreDecreaseMap() {
        return scoreDecreaseMap;
    }

    public float getScoreIncreaseModifier() {
        return scoreIncreaseModifier;
    }

    public float getScoreDecreaseModifier() {
        return scoreDecreaseModifier;
    }

    public InputHandler getInputHandler() {
        return inputHandler;
    }
    
    public int getTimeLimit() {
        return timeLimit;
    }
    
    public int getSpawnInterval() {
        return spawnInterval;
    }
    
    public int getSpawnTimer() {
        return spawnTimer;
    }

    public LevelCompletionAnimation getLevelCompletionAnimation() {
        return levelCompletionAnimation;
    }

    public Renderer getRenderer() {
        return renderer;
    }    

    public List<Ball> getNextBallsToShow(int numberOfBallsToShow) {
        List<Ball> ballsToShow = new ArrayList<>();
        for (int i = 0; i < numberOfBallsToShow && i < spawnQueue.size(); i++) {
            ballsToShow.add(spawnQueue.get(i));
        }
        return ballsToShow;
    }

    // Setters for testing purposes
    public void setTimeRemaining(int timeRemaining) {
        this.timeRemaining = timeRemaining;
    }

    public void addSpawner(Spawner spawner) {
        this.spawners.add(spawner);
    }

    public void setTimeUp(boolean timeUp) {
        this.timeUp = timeUp;
    }

    public void setScoreBeforeReset(int scoreBeforeReset) {
        this.scoreBeforeReset = scoreBeforeReset;
    }

    public void setSpawnTimer(int spawnTimer) {
        this.spawnTimer = spawnTimer;
    }

    public void setGameEnded(boolean gameEnded) {
        this.gameEnded = gameEnded;
    }

    public void setInputHandler(InputHandler inputHandler) {
        this.inputHandler = inputHandler;
    }

    public void setLevelCompleted(boolean levelCompleted) {
        this.levelCompleted = levelCompleted;
    }
}
