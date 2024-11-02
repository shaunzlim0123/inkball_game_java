package inkball;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.MouseEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * The main application class for the Inkball game.
 * Handles game setup, level loading, image management, user input events (keyboard and mouse),
 * and manages the game's progression through multiple levels.
 */
public class App extends PApplet {

    /**
     * Size of each cell in the game grid.
     */
    public static final int CELLSIZE = 32;

    /**
     * Frames per second (FPS) for rendering the game.
     */
    public static final int FPS = 60;

    /**
     * Random object used for randomization within the game.
     */
    public Random random = new Random();

    /**
     * Array of wall images used for rendering different types of walls in the game.
     */
    private static PImage[] wallImages = new PImage[5];

    /**
     * Array of ball images used for rendering different colored balls in the game.
     */
    private static PImage[] ballImages = new PImage[5];

    /**
     * Array of hole images used for rendering different colored holes in the game.
     */
    private static PImage[] holeImages = new PImage[5];

    /**
     * Array of acceleration tile images used for rendering different directions of acceleration in the game.
     */
    private static PImage[] accelerationImages = new PImage[4];

    /**
     * Image used for the spawner/entry point of balls.
     */
    private static PImage spawnerImage;

    /**
     * Image used for regular tiles in the game grid.
     */
    private static PImage tileImage;

    /**
     * Image used for yellow tiles in the game grid.
     */
    private static PImage yellowTileImage;

    /**
     * The current level being played by the user.
     */
    private Level currentLevel;

    /**
     * JSON object containing the game's configuration loaded from a file.
     */
    private JSONObject config;

    /**
     * JSONArray of level configurations extracted from the config file.
     */
    private JSONArray levels;

    /**
     * Index of the current level being played, starting from 0.
     */
    private int currentLevelIndex = 0;

    /**
     * Accumulated score across all levels played so far.
     */
    private int accumulatedScore = 0;

    /**
     * Path to the JSON configuration file used for setting up levels.
     */
    private String configPath;

    /**
     * Renderer instance responsible for drawing all game elements in the level.
     */
    private Renderer renderer;

    /**
     * Map storing the score increases for different ball colors when successfully captured in the correct hole.
     * The key is the color index, and the value is the score increase for that color.
     */
    private Map<Integer, Integer> scoreIncreaseMap = new HashMap<>();

    /**
     * Map storing the score decreases for different ball colors when captured in the wrong hole.
     * The key is the color index, and the value is the score decrease for that color.
     */
    private Map<Integer, Integer> scoreDecreaseMap = new HashMap<>();

    public boolean setupComplete = false;

    /**
     * Default constructor for the App class.
     * Initializes the path to the game's configuration file.
     */
    public App() {
        this.configPath = "config.json";
    }

    /**
     * Sets up the size of the game window.
     */
    @Override
    public void settings() {
        size(576, 640);
    }

    /**
     * Initializes the game environment, including setting the frame rate,
     * loading images, loading the configuration file, and loading the first level.
     */
    @Override
    public void setup() {
        frameRate(FPS);
        loadImages();
        config = this.loadJSONObject(configPath);
        levels = config.getJSONArray("levels");

        // Load the first level
        loadLevel(currentLevelIndex);

        setupComplete = true;
    }

    /**
     * Loads images used for walls, balls, holes, and tiles.
     * Images are loaded from the specified resource directory.
     */
    private void loadImages() {
        // Load wall, ball, and hole images
        for (int i = 0; i < 5; i++) {
            wallImages[i] = loadImage("src/main/resources/inkball/wall" + i + ".png");
            ballImages[i] = loadImage("src/main/resources/inkball/ball" + i + ".png");
            holeImages[i] = loadImage("src/main/resources/inkball/hole" + i + ".png");
        }

        // Load acceleration tiles
        String[] directions = {"up", "down", "left", "right"};
        for (int i = 0; i < 4; i++) {
            accelerationImages[i] = loadImage("src/main/resources/inkball/acceleration_" + directions[i] + ".png");
        }

        // Load spawner and tile images
        spawnerImage = loadImage("src/main/resources/inkball/entrypoint.png");
        tileImage = loadImage("src/main/resources/inkball/tile.png");
    }

    /**
     * Loads a specific level based on the provided level index.
     * Extracts level-specific configurations such as layout, time, spawn interval,
     * score modifiers, and ball colors.
     *
     * @param levelIndex The index of the level to load.
     */
    @SuppressWarnings("unchecked")
    public void loadLevel(int levelIndex) {
        if (levels == null || levelIndex >= levels.size()) {
            return;
        }

        JSONObject levelConfig = levels.getJSONObject(levelIndex);

        String layout = levelConfig.getString("layout");
        int time = levelConfig.getInt("time");
        int spawnIntervalInSeconds = levelConfig.getInt("spawn_interval");
        float scoreIncreaseModifier = levelConfig.getFloat("score_increase_from_hole_capture_modifier");
        float scoreDecreaseModifier = levelConfig.getFloat("score_decrease_from_wrong_hole_modifier");
        JSONArray ballsJsonArray = levelConfig.getJSONArray("balls");

        // Convert JSONArray to String array
        String[] entityColors = new String[ballsJsonArray.size()];
        for (int j = 0; j < ballsJsonArray.size(); j++) {
            entityColors[j] = ballsJsonArray.getString(j);
        }

        // Load score increase and decrease values for each color from JSON
        JSONObject scoreIncreaseJson = config.getJSONObject("score_increase_from_hole_capture");
        JSONObject scoreDecreaseJson = config.getJSONObject("score_decrease_from_wrong_hole");

        Set<String> scoreIncreaseKeys = scoreIncreaseJson.keys();
        for (String key : scoreIncreaseKeys) {
            int colorIndex = Level.getColorIndexFromName(key);
            int value = scoreIncreaseJson.getInt(key);
            scoreIncreaseMap.put(colorIndex, value);
        }

        Set<String> scoreDecreaseKeys = scoreDecreaseJson.keys();
        for (String key : scoreDecreaseKeys) {
            int colorIndex = Level.getColorIndexFromName(key);
            int value = scoreDecreaseJson.getInt(key);
            scoreDecreaseMap.put(colorIndex, value);
        }

        // Initialize the level with the configuration data
        currentLevel = new Level(this, layout, time, spawnIntervalInSeconds, scoreIncreaseModifier, scoreDecreaseModifier, entityColors, scoreIncreaseMap, scoreDecreaseMap);
        renderer = currentLevel.getRenderer();
    }

    /**
     * Checks if there is a next level to load in the game.
     *
     * @return true if there is another level to play, false otherwise.
     */
    public boolean hasNextLevel() {
        return levels != null && currentLevelIndex + 1 < levels.size();
    }

    /**
     * Loads the next level in the game and accumulates the score from the previous level.
     */
    public void loadNextLevel() {
        if (hasNextLevel()) {
            currentLevelIndex++;
            loadLevel(currentLevelIndex);
        }
    }

    /**
     * Handles key press events for game controls.
     * - 'R' or 'r': Restarts the game or the current level.
     * - Space: Toggles pause in the current level.
     */
    @Override
    public void keyPressed() {
        if (currentLevel == null) {
            return;
        }
        if (key == 'r' || key == 'R') {
            if (currentLevel.isGameEnded()) {
                resetAccumulatedScore();
                currentLevelIndex = 0;
                loadLevel(currentLevelIndex);
            } else {
                currentLevel.resetLevel(currentLevel.getLayoutFile(), currentLevel.getEntityColors());
            }
        } else if (key == ' ') {
            currentLevel.togglePause();
        }
    }

    /**
     * Handles mouse press events to start drawing a line at the clicked position.
     */
    @Override
    public void mousePressed() {
        if (currentLevel != null) {
            currentLevel.handleMouseClick(mouseX, mouseY, mouseButton);
        }
    }

    /**
     * Handles mouse drag events to add points to the line being drawn.
     */
    @Override
    public void mouseDragged() {
        if (currentLevel != null) {
            currentLevel.addPointToLine(mouseX, mouseY);
        }
    }

    /**
     * Handles mouse release events to complete the line being drawn.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (currentLevel != null) {
            currentLevel.finishDrawingLine();
        }
    }

    /**
     * The main game loop that updates and renders the current level.
     * Continuously called at the set frame rate.
     */
    @Override
    public void draw() {
        background(200); // Set the background color

        if (currentLevel != null) {
            currentLevel.update();
            renderer.draw();
        }

        this.drawScore(); // Render accumulated score
    }

    /**
     * Draws the current score on the screen.
     */
    public void drawScore() {
        this.fill(0);
        this.textSize(18);
        this.textAlign(PApplet.RIGHT, PApplet.TOP);
        this.text("Score: " + accumulatedScore, this.width - 10, 20);
    }

    /**
     * Adds a specified amount to the accumulated score.
     * This method increases the player's total score across all levels.
     *
     * @param scoreToAdd The amount of score to add to the accumulated score.
     *                   Can be positive (for score increase) or negative (for score decrease).
     */
    public void addToAccumulatedScore(int scoreToAdd) {
        accumulatedScore += scoreToAdd;
    }

    /**
     * Resets the accumulated score to zero.
     * This method is used to restart the player's total score across all levels.
     */
    public void resetAccumulatedScore() {
        accumulatedScore = 0;
    }

    /**
     * Checks if the current level is the last level in the game.
     *
     * @return true if the current level is the last level, false otherwise.
     */
    public boolean isLastLevel() {
        return levels != null && currentLevelIndex == levels.size() - 1;
    }

    /**
     * The main method to launch the Inkball game application.
     *
     * @param args command-line arguments (not used).
     */
    public static void main(String[] args) {
        PApplet.main("inkball.App");
    }

    // Getter methods for private attributes
    public static PImage[] getWallImages() {
        return wallImages;
    }

    public static PImage[] getBallImages() {
        return ballImages;
    }

    public static PImage[] getHoleImages() {
        return holeImages;
    }

    public static PImage[] getAccelerationImages() {
        return accelerationImages;
    }

    public static PImage getSpawnerImage() {
        return spawnerImage;
    }

    public static PImage getTileImage() {
        return tileImage;
    }

    public static PImage getYellowTileImage() {
        return yellowTileImage;
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }

    public JSONObject getConfig() {
        return config;
    }

    public JSONArray getLevels() {
        return levels;
    }

    public int getCurrentLevelIndex() {
        return currentLevelIndex;
    }

    public int getAccumulatedScore() {
        return accumulatedScore;
    }

    public String getConfigPath() {
        return configPath;
    }

    public Map<Integer, Integer> getScoreIncreaseMap() {
        return scoreIncreaseMap;
    }

    public Map<Integer, Integer> getScoreDecreaseMap() {
        return scoreDecreaseMap;
    }

    // Setter methods
    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    public void setCurrentLevel(Level level) {
        this.currentLevel = level;
    }

    public void setCurrentLevelIndex(int levelIndex) {
        this.currentLevelIndex = levelIndex;
    }

    public void setLevels(JSONArray levels) {
        this.levels = levels;
    }

    public static void setWallImages(PImage[] images) {
        wallImages = images;
    }

    public static void setBallImages(PImage[] images) {
        ballImages = images;
    }

    public static void setHoleImages(PImage[] images) {
        holeImages = images;
    }

    public static void setAccelerationImages(PImage[] images) {
        accelerationImages = images;
    }

    public static void setSpawnerImage(PImage image) {
        spawnerImage = image;
    }

    public static void setTileImage(PImage image) {
        tileImage = image;
    }

    public void setAccumulatedScore(int score) {
        accumulatedScore = score;
    }
}
