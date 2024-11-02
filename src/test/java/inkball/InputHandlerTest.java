package inkball;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.core.PConstants;

import static org.junit.jupiter.api.Assertions.*;

public class InputHandlerTest {

    private App app;
    private Level level;
    private InputHandler inputHandler;

    @BeforeEach
    public void setUp() {
        // Initialize the Processing environment (App class)
        app = new App();
        app.random.setSeed(123);
        String[] args = {"App"};
        PApplet.runSketch(args, app);
    
        // Ensure setup completes before starting tests
        app.loop();
        app.delay(500); // Allow time for setup
    
        // Create a Level instance with basic parameters
        String layoutFile = "test_layout.txt";
        int timeLimit = 120;
        int spawnIntervalInSeconds = 10;
        float scoreIncreaseModifier = 1.2f;
        float scoreDecreaseModifier = 0.8f;
        String[] entityColors = {"red", "blue", "green"};
    
        level = new Level(app, layoutFile, timeLimit, spawnIntervalInSeconds, scoreIncreaseModifier, scoreDecreaseModifier, entityColors, null, null);
    
        // Initialize the InputHandler
        inputHandler = new InputHandler(level);
    }
    
    // Tests whether a line starts being drawn after a left mouse click
    // by checking if the current line is initialized and contains the initial point.
    @Test
    public void testStartDrawingLine() {
        // Simulate left mouse click to start drawing a line
        inputHandler.handleMouseClick(100, 100, PConstants.LEFT);

        // Verify the current line is not null and has the initial point
        Line currentLine = inputHandler.getCurrentLine();
        assertNotNull(currentLine, "A line should be initialized and start drawing");
        assertEquals(1, currentLine.getPoints().size(), "The line should contain one point after starting to draw");
    }

    // Tests whether points are correctly added to the line when dragging the mouse
    // after starting a line.
    @Test
    public void testAddPointToLine() {
        // Start drawing a line
        inputHandler.handleMouseClick(50, 50, PConstants.LEFT);

        // Simulate dragging the mouse to add points to the line
        inputHandler.addPointToLine(60, 60);
        inputHandler.addPointToLine(70, 70);

        // Verify the points have been added to the current line
        Line currentLine = inputHandler.getCurrentLine();
        assertEquals(3, currentLine.getPoints().size(), "The line should contain three points after adding two more");
    }

    // Tests whether the line is correctly finished, added to the level's list,
    // and the drawing process is properly stopped.
    @Test
    public void testFinishDrawingLine() {
        // Start drawing a line
        inputHandler.handleMouseClick(100, 100, PConstants.LEFT);

        // Add a point
        inputHandler.addPointToLine(110, 110);

        // Finish drawing the line
        inputHandler.finishDrawingLine();

        // Verify that the line has been added to the level and drawing has stopped
        assertNull(inputHandler.getCurrentLine(), "No line should be currently drawn after finishing");
        assertEquals(1, level.getLines().size(), "The finished line should be added to the level's line list");
    }

    // Tests whether a line is removed when a right-click occurs near its position.
    @Test
    public void testRemoveLine() {
        // Start drawing a line
        inputHandler.handleMouseClick(100, 100, PConstants.LEFT);
        inputHandler.addPointToLine(110, 110);
        inputHandler.finishDrawingLine();  // Finish the line

        // Simulate right-click to remove the line at the same position
        inputHandler.handleMouseClick(105, 105, PConstants.RIGHT);  // Right-click near the line

        // Verify that the line was removed
        assertTrue(level.getLines().isEmpty(), "The line should be removed after right-clicking near it");
    }

    // Tests whether the lines are drawn on the screen without errors,
    // ensuring the draw method runs correctly.
    @Test
    public void testDrawLine() {
        // Start drawing a line
        inputHandler.handleMouseClick(50, 50, PConstants.LEFT);
        inputHandler.addPointToLine(100, 100);
        inputHandler.finishDrawingLine();  // Finish the line

        // Call the draw method
        app.noLoop(); // Prevent continuous drawing
        app.background(255);  // Set background color to white for visibility
        for (Line line : level.getLines()) {
            line.draw();  // Draw the line
        }

        assertTrue(true, "The draw method executed without errors");
    }
}
