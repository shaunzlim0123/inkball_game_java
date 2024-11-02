package inkball;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

class AppTest {

    private App app;

    @BeforeEach
    void setUp() {
        app = new App();
        app.random.setSeed(123); // Initialize the app with a set random seed for consistency.
    }

    @Test
    void testConstructor() {
        // Test that the default config path is correctly initialized in the constructor.
        assertEquals("config.json", app.getConfigPath());
    }

    @Test
    void testSetAndGetConfigPath() {
        // Test the getter and setter for configPath.
        app.setConfigPath("testConfig.json");
        assertEquals("testConfig.json", app.getConfigPath());
    }

    @Test
    void testSetAndGetCurrentLevel() {
        // Test setting and getting the current level.
        Level level = mock(Level.class);
        app.setCurrentLevel(level);
        assertEquals(level, app.getCurrentLevel());
    }

    @Test
    void testSetAndGetCurrentLevelIndex() {
        // Test setting and getting the current level index.
        app.setCurrentLevelIndex(2);
        assertEquals(2, app.getCurrentLevelIndex());
    }

    @Test
    void testSetAndGetAccumulatedScore() {
        // Test setting and getting the accumulated score.
        app.setAccumulatedScore(500);
        assertEquals(500, app.getAccumulatedScore());
    }

    @Test
    void testAddToAccumulatedScore() {
        // Test adding positive and negative values to the accumulated score.
        app.setAccumulatedScore(100);
        app.addToAccumulatedScore(50); // Add positive value.
        assertEquals(150, app.getAccumulatedScore());
        app.addToAccumulatedScore(-30); // Add negative value.
        assertEquals(120, app.getAccumulatedScore());
    }

    @Test
    void testResetAccumulatedScore() {
        // Test resetting the accumulated score to zero.
        app.setAccumulatedScore(100);
        app.resetAccumulatedScore();
        assertEquals(0, app.getAccumulatedScore());
    }

    @Test
    void testSetAndGetWallImages() {
        // Test setting and getting wall images.
        PImage[] images = new PImage[5];
        App.setWallImages(images);
        assertEquals(images, App.getWallImages());
    }

    @Test
    void testSetAndGetBallImages() {
        // Test setting and getting ball images.
        PImage[] images = new PImage[5];
        App.setBallImages(images);
        assertEquals(images, App.getBallImages());
    }

    @Test
    void testSetAndGetHoleImages() {
        // Test setting and getting hole images.
        PImage[] images = new PImage[5];
        App.setHoleImages(images);
        assertEquals(images, App.getHoleImages());
    }

    @Test
    void testSetAndGetAccelerationImages() {
        // Test setting and getting acceleration images.
        PImage[] images = new PImage[4];
        App.setAccelerationImages(images);
        assertEquals(images, App.getAccelerationImages());
    }

    @Test
    void testSetAndGetSpawnerImage() {
        // Test setting and getting spawner image.
        PImage image = mock(PImage.class);
        App.setSpawnerImage(image);
        assertEquals(image, App.getSpawnerImage());
    }

    @Test
    void testSetAndGetTileImage() {
        // Test setting and getting tile image.
        PImage image = mock(PImage.class);
        App.setTileImage(image);
        assertEquals(image, App.getTileImage());
    }

    @Test
    void testHasNextLevel() {
        // Test checking if there is a next level using reflection to manipulate private fields.
        try {
            Field levelsField = App.class.getDeclaredField("levels");
            levelsField.setAccessible(true);
            JSONArray levels = new JSONArray();
            levels.append(new JSONObject());
            levels.append(new JSONObject());
            levelsField.set(app, levels);

            Field currentLevelIndexField = App.class.getDeclaredField("currentLevelIndex");
            currentLevelIndexField.setAccessible(true);
            currentLevelIndexField.set(app, 0);

            assertTrue(app.hasNextLevel());

            currentLevelIndexField.set(app, 1);
            assertFalse(app.hasNextLevel());
        } catch (Exception e) {
            fail("Exception during reflection: " + e.getMessage());
        }
    }

    @Test
    void testIsLastLevel() {
        // Test checking if the current level is the last one using reflection.
        try {
            Field levelsField = App.class.getDeclaredField("levels");
            levelsField.setAccessible(true);
            JSONArray levels = new JSONArray();
            levels.append(new JSONObject());
            levels.append(new JSONObject());
            levelsField.set(app, levels);

            Field currentLevelIndexField = App.class.getDeclaredField("currentLevelIndex");
            currentLevelIndexField.setAccessible(true);
            currentLevelIndexField.set(app, 0);

            assertFalse(app.isLastLevel());

            currentLevelIndexField.set(app, 1);
            assertTrue(app.isLastLevel());
        } catch (Exception e) {
            fail("Exception during reflection: " + e.getMessage());
        }
    }

    @Test
    void testLoadLevel() {
        // Test loading a level by simulating the internal configuration using reflection.
        try {
            Field configField = App.class.getDeclaredField("config");
            configField.setAccessible(true);
            JSONObject config = new JSONObject();

            // Mock configuration with level data
            JSONArray levels = new JSONArray();
            JSONObject levelConfig = new JSONObject();
            levelConfig.setString("layout", "level1.txt");
            levelConfig.setInt("time", 60);
            levelConfig.setInt("spawn_interval", 5);
            levelConfig.setFloat("score_increase_from_hole_capture_modifier", 1.0f);
            levelConfig.setFloat("score_decrease_from_wrong_hole_modifier", 1.0f);

            JSONArray balls = new JSONArray();
            balls.append("orange");
            balls.append("blue");
            levelConfig.setJSONArray("balls", balls);

            levels.append(levelConfig);
            config.setJSONArray("levels", levels);

            // Set up mock score increase and decrease maps
            JSONObject scoreIncreaseJson = new JSONObject();
            scoreIncreaseJson.setInt("orange", 10);
            scoreIncreaseJson.setInt("blue", 20);
            config.setJSONObject("score_increase_from_hole_capture", scoreIncreaseJson);

            JSONObject scoreDecreaseJson = new JSONObject();
            scoreDecreaseJson.setInt("orange", -5);
            scoreDecreaseJson.setInt("blue", -10);
            config.setJSONObject("score_decrease_from_wrong_hole", scoreDecreaseJson);

            configField.set(app, config);

            // Mock levels in the app
            Field levelsField = App.class.getDeclaredField("levels");
            levelsField.setAccessible(true);
            levelsField.set(app, levels);

            // Initialize score maps
            Field scoreIncreaseMapField = App.class.getDeclaredField("scoreIncreaseMap");
            scoreIncreaseMapField.setAccessible(true);
            scoreIncreaseMapField.set(app, new HashMap<>());

            Field scoreDecreaseMapField = App.class.getDeclaredField("scoreDecreaseMap");
            scoreDecreaseMapField.setAccessible(true);
            scoreDecreaseMapField.set(app, new HashMap<>());

            // Load level and assert its properties
            app.loadLevel(0);

            Level currentLevel = app.getCurrentLevel();
            assertNotNull(currentLevel);
            assertEquals("level1.txt", currentLevel.getLayoutFile());
            assertEquals(60, currentLevel.getTimeLimit());
            assertEquals(5, currentLevel.getSpawnInterval());
            assertEquals(1.0f, currentLevel.getScoreIncreaseModifier());
            assertEquals(1.0f, currentLevel.getScoreDecreaseModifier());
        } catch (Exception e) {
            fail("Exception during reflection: " + e.getMessage());
        }
    }

    @Test
    void testLoadLevel_NullLevels() {
        // Test that loadLevel handles null levels gracefully.
        app.setLevels(null); // Set levels to null

        app.loadLevel(0); // Attempt to load a level

        // Ensure the current level is null
        assertNull(app.getCurrentLevel(), "Current level should remain null when levels is null.");
    }

    @Test
    void testLoadNextLevel() {
        // Test loading the next level by mocking loadLevel and verifying behavior.
        try {
            Field levelsField = App.class.getDeclaredField("levels");
            levelsField.setAccessible(true);
            JSONArray levels = new JSONArray();
            levels.append(new JSONObject());
            levels.append(new JSONObject());
            levelsField.set(app, levels);

            Field currentLevelIndexField = App.class.getDeclaredField("currentLevelIndex");
            currentLevelIndexField.setAccessible(true);
            currentLevelIndexField.set(app, 0);

            // Mock loadLevel method
            App spyApp = spy(app);
            doNothing().when(spyApp).loadLevel(anyInt());

            spyApp.loadNextLevel();

            verify(spyApp).loadLevel(1); // Verify loadLevel(1) was called
            assertEquals(1, spyApp.getCurrentLevelIndex());
        } catch (Exception e) {
            fail("Exception during reflection: " + e.getMessage());
        }
    }

    @Test
    void testKeyPressedWithNullCurrentLevel() {
        // Test that keyPressed handles a null current level without throwing an exception.
        app.keyPressed(); 
        // No assertions; we expect no exceptions.
    }

    @Test
    void testMousePressedWithNullCurrentLevel() {
        // Test that mousePressed handles a null current level without throwing an exception.
        app.mousePressed(); 
        // No assertions; we expect no exceptions.
    }

    @Test
    void testMouseDraggedWithNullCurrentLevel() {
        // Test that mouseDragged handles a null current level without throwing an exception.
        app.mouseDragged(); 
        // No assertions; we expect no exceptions.
    }

    @Test
    void testMouseReleasedWithNullCurrentLevel() {
        // Test that mouseReleased handles a null current level without throwing an exception.
        app.mouseReleased(); 
        // No assertions; we expect no exceptions.
    }

    @Test
    void testKeyPressed_RestartLevel() {
        // Test that pressing 'r' restarts the current level if it hasn't ended.
        Level level = mock(Level.class);

        // Stub methods to return specific values
        when(level.isGameEnded()).thenReturn(false);
        when(level.getLayoutFile()).thenReturn("testLayout.txt");
        when(level.getEntityColors()).thenReturn(new String[]{"red", "blue"});

        app.setCurrentLevel(level);

        app.key = 'r';  // Simulate pressing the 'r' key
        app.keyPressed();  // Call keyPressed

        // Verify the level reset was triggered with the correct layout and colors
        verify(level).resetLevel("testLayout.txt", new String[]{"red", "blue"});
    }

    @Test
    void testAddToScoreMaps() {
        // Test adding values to score increase and decrease maps using reflection.
        try {
            Field scoreIncreaseMapField = App.class.getDeclaredField("scoreIncreaseMap");
            scoreIncreaseMapField.setAccessible(true);
            Map<Integer, Integer> increaseMap = new HashMap<>();
            increaseMap.put(0, 10);
            scoreIncreaseMapField.set(app, increaseMap);

            Field scoreDecreaseMapField = App.class.getDeclaredField("scoreDecreaseMap");
            scoreDecreaseMapField.setAccessible(true);
            Map<Integer, Integer> decreaseMap = new HashMap<>();
            decreaseMap.put(0, -5);
            scoreDecreaseMapField.set(app, decreaseMap);

            assertEquals(increaseMap, app.getScoreIncreaseMap());
            assertEquals(decreaseMap, app.getScoreDecreaseMap());
        } catch (Exception e) {
            fail("Exception during reflection: " + e.getMessage());
        }
    }
}
