package inkball;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.core.PImage;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LevelTest {
    private App app;
    private Level level;

    @BeforeEach
    public void setUp() {
        // Initialize the app
        app = new App();
        app.random.setSeed(0);

        // Start the Processing environment
        String[] args = {"App"};
        PApplet.runSketch(args, app);

        // Start the draw loop
        app.loop();

        // Wait until Processing's setup method completes using a flag
        while (!app.setupComplete) {
            try {
                Thread.sleep(500); // Poll every 100ms until setup is complete
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Dummy image
        PImage dummyImage = new PImage(App.CELLSIZE, App.CELLSIZE);

        // Set up dummy resources (images for walls, balls, holes, etc.)
        PImage[] wallImages = new PImage[5];
        PImage[] ballImages = new PImage[5];
        PImage[] holeImages = new PImage[5];
        PImage[] accelerationImages = new PImage[4];
        
        for (int i = 0; i < wallImages.length; i++) wallImages[i] = dummyImage;
        for (int i = 0; i < ballImages.length; i++) ballImages[i] = dummyImage;
        for (int i = 0; i < holeImages.length; i++) holeImages[i] = dummyImage;
        for (int i = 0; i < accelerationImages.length; i++) accelerationImages[i] = dummyImage;

        PImage spawnerImage = dummyImage;
        PImage tileImage = dummyImage;

        App.setWallImages(wallImages);
        App.setBallImages(ballImages);
        App.setHoleImages(holeImages);
        App.setAccelerationImages(accelerationImages);
        App.setSpawnerImage(spawnerImage);
        App.setTileImage(tileImage);

        // Dummy Level initialization
        String layoutFile = "test_layout.txt";
        int timeLimit = 120;
        int spawnIntervalInSeconds = 10;
        float scoreIncreaseModifier = 1.2f;
        float scoreDecreaseModifier = 0.8f;
        String[] entityColors = {"red", "blue", "green"};

        Map<Integer, Integer> scoreIncreaseMap = new HashMap<>();
        Map<Integer, Integer> scoreDecreaseMap = new HashMap<>();

        // Initialize the Level instance
        level = new Level(app, layoutFile, timeLimit, spawnIntervalInSeconds,
                scoreIncreaseModifier, scoreDecreaseModifier, entityColors, scoreIncreaseMap, scoreDecreaseMap);
        level.loadLevel(layoutFile);
    }

    @Test
    public void testLevelInitialization() {
        assertNotNull(level, "Level should be initialized.");
    }

    @Test
    public void testDrawSpawnQueueVisuals_BallCount() {
        PImage ballImage = new PImage();

        // Arrange: Set up the spawn queue with balls
        Ball ball1 = new Ball(10, 10, ballImage, app.color(255, 0, 0), app);  // Red ball
        Ball ball2 = new Ball(20, 20, ballImage, app.color(0, 0, 255), app);  // Blue ball
        Ball ball3 = new Ball(30, 30, ballImage, app.color(0, 255, 0), app);  // Green ball
        level.getSpawnQueue().add(ball1);
        level.getSpawnQueue().add(ball2);
        level.getSpawnQueue().add(ball3);

        // Act: Retrieve the balls to display
        List<Ball> ballsToShow = level.getNextBallsToShow(5);

        // Assert: Check that it retrieves the correct number of balls
        assertEquals(3, ballsToShow.size(), "There should be 3 balls in the queue to display");
    }

    @Test
    public void testDrawSpawnQueueVisuals_Timer() {
        // Arrange: Set a specific spawn timer
        level.setSpawnTimer(120);  // Assume spawn happens every 120 frames

        // Act: Calculate time until the next spawn
        float timeUntilNextSpawn = level.calculateTimeUntilNextSpawn();

        // Assert: Verify that the calculated time is correct
        assertEquals(120 / (float) App.FPS, timeUntilNextSpawn, 0.1, "Time until next spawn should be correctly calculated");
    }

    @Test
    public void testClearSpawnQueue() {
        // Clear the spawn queue
        level.getSpawnQueue().clear();

        // Assert that the spawn queue is empty
        assertTrue(level.getSpawnQueue().isEmpty(), "The spawn queue should be empty");
    }

    @Test
    public void testResetLevel() {
        String newLayoutFile = "new_test_layout.json";
        String[] newEntityColors = {"yellow", "purple"};

        // Call the resetLevel method
        level.resetLevel(newLayoutFile, newEntityColors);

        // Assert that all entities are cleared
        assertTrue(level.getWalls().isEmpty(), "Walls should be cleared after reset.");
        assertTrue(level.getBalls().isEmpty(), "Balls should be cleared after reset.");
        assertTrue(level.getHoles().isEmpty(), "Holes should be cleared after reset.");
        assertTrue(level.getSpawnQueue().isEmpty(), "Spawn queue should be cleared after reset.");
        assertTrue(level.getLines().isEmpty(), "Lines should be cleared after reset.");

        // Assert that flags are reset
        assertFalse(level.isLevelCompleted(), "Level should not be completed after reset.");
        assertFalse(level.isTimeUp(), "Time should not be up after reset.");
        assertFalse(level.isPaused(), "Level should not be paused after reset.");

        // Assert that time and spawn timers are reset
        assertEquals(level.getTimeLimit(), level.getTimeRemaining(), "Time remaining should be reset to the time limit.");
        assertEquals(level.getSpawnInterval(), level.getSpawnTimer(), "Spawn timer should be reset to the spawn interval.");

        // Assert that spawn queue is initialized with new entity colors
        assertEquals(2, level.getSpawnQueue().size(), "Spawn queue should be initialized with the correct number of entities.");
    }
}
