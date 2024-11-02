package inkball;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import java.util.List;

public class LineTest {
    private App app;
    private Line line;

    @BeforeEach
    public void setUp() {
        // Create the PApplet instance and initialize the Processing sketch
        app = new App();
        app.random.setSeed(123);
        String[] args = {"App"};
        PApplet.runSketch(args, app);

        // Ensure the Processing setup completes
        app.loop();
        app.delay(500);  // Give time for the setup to complete before testing

        // Initialize the line object
        line = new Line(app);
    }

    // Test the construction of the Line object.
    // Ensures that the Line object is created and the points list is initialized.
    @Test
    public void testConstructor() {
        PApplet p = new PApplet();
        Line line = new Line(p);
        assertNotNull(line, "Line object should not be null after construction");
        List<PVector> points = line.getPoints();
        assertNotNull(points, "Points list should be initialized");
        assertEquals(0, points.size(), "Line should be initialized with no points");
    }

    // Test adding points to the line.
    // Verifies that points are correctly added to the points list.
    @Test
    public void testAddPoint() {
        PApplet p = new PApplet();
        Line line = new Line(p);
        line.addPoint(10, 20);
        line.addPoint(30, 40);
        List<PVector> points = line.getPoints();
        assertEquals(2, points.size(), "Line should contain two points after adding");
        assertEquals(new PVector(10, 20), points.get(0), "First point should match the added coordinates");
        assertEquals(new PVector(30, 40), points.get(1), "Second point should match the added coordinates");
    }

    // Add points to the line, then test the draw method (which uses the Processing applet).
    @Test
    public void testDraw() {
        line.addPoint(50, 50);
        line.addPoint(100, 100);
        line.addPoint(150, 150);

        app.noLoop(); // Stop the loop for consistent testing
        app.delay(500); // Allow some time for the frame to be drawn

        app.background(255); // Set background to white for visibility
        line.draw();

        assertTrue(true, "Draw method executed without throwing exceptions");
    }

    // Test the contains method with a point exactly on the line.
    // Verifies that the method correctly identifies points on the line.
    @Test
    public void testContainsPointOnLine() {
        PApplet p = new PApplet();
        Line line = new Line(p);
        line.addPoint(0, 0);
        line.addPoint(10, 10);

        assertTrue(line.contains(5, 5), "Point (5,5) should be considered on the line");
    }

    // Test the contains method with a point near the line within tolerance.
    // Checks if the method correctly includes points within the tolerance range.
    @Test
    public void testContainsPointNearLine() {
        PApplet p = new PApplet();
        Line line = new Line(p);
        line.addPoint(0, 0);
        line.addPoint(10, 10);

        assertTrue(line.contains(5, 5.1f), "Point near the line should be considered on the line within tolerance");
    }

    // Test the contains method with a point not on the line.
    // Ensures that points outside the tolerance are correctly identified as not on the line.
    @Test
    public void testContainsPointNotOnLine() {
        PApplet p = new PApplet();
        Line line = new Line(p);
        line.addPoint(0, 0);
        line.addPoint(10, 10);

        assertFalse(line.contains(5, 6), "Point (5,6) should not be considered on the line");
    }

    // Test the checkCollision method when there is no collision.
    // Verifies that collision detection correctly identifies non-colliding scenarios.
    @Test
    public void testCheckCollisionNoCollision() {
        PApplet p = new PApplet();
        PImage image = p.createImage(10, 10, PApplet.RGB); // Dummy PImage
        int color = 0xFFFFFF; // Dummy color for the ball

        Line line = new Line(p);
        line.addPoint(100, 100);
        line.addPoint(200, 200);

        Ball ball = new Ball(50, 50, image, color, p);

        boolean collision = line.checkCollision(ball);
        assertFalse(collision, "Ball should not collide with the line when far away");
    }

    // Test the checkCollision method when there is a collision.
    // Checks that the method detects collision and updates the ball's velocity.
    @Test
    public void testCheckCollisionWithCollision() {
        PApplet p = new PApplet();
        PImage image = p.createImage(10, 10, PApplet.RGB); // Dummy PImage
        int color = 0xFFFFFF; // Dummy color for the ball

        Line line = new Line(p);
        line.addPoint(100, 100);
        line.addPoint(200, 100);

        Ball ball = new Ball(150, 90, image, color, p);

        boolean collision = line.checkCollision(ball);
        assertTrue(collision, "Ball should collide with the line when moving towards it");
        assertNotEquals(5, ball.getYVelocity(), "Ball's Y velocity should change after collision");
    }

    // Test collision at the edge of the line segment.
    // Ensures collision detection works at the endpoints of the line.
    @Test
    public void testCheckCollisionAtEdge() {
        PApplet p = new PApplet();
        PImage image = p.createImage(10, 10, PApplet.RGB); // Dummy PImage
        int color = 0xFFFFFF; // Dummy color for the ball

        Line line = new Line(p);
        line.addPoint(100, 100);
        line.addPoint(200, 100);

        Ball ball = new Ball(95, 100, image, color, p);

        boolean collision = line.checkCollision(ball);
        assertTrue(collision, "Ball should collide with the end of the line");
    }

    // Test collision when the line has zero points.
    // Verifies that collision detection handles empty lines gracefully.
    @Test
    public void testCheckCollisionWithZeroPoints() {
        PApplet p = new PApplet();
        PImage image = p.createImage(10, 10, PApplet.RGB); // Dummy PImage
        int color = 0xFFFFFF; // Dummy color for the ball

        Line line = new Line(p);

        Ball ball = new Ball(50, 50, image, color, p);

        boolean collision = line.checkCollision(ball);
        assertFalse(collision, "Ball should not collide with an empty line");
    }

    // Test collision when the line has only one point.
    // Ensures that a line with insufficient points does not cause errors in collision detection.
    @Test
    public void testCheckCollisionWithOnePoint() {
        PApplet p = new PApplet();
        PImage image = p.createImage(10, 10, PApplet.RGB); // Dummy PImage
        int color = 0xFFFFFF; // Dummy color for the ball

        Line line = new Line(p);
        line.addPoint(100, 100);

        Ball ball = new Ball(100, 100, image, color, p);

        boolean collision = line.checkCollision(ball);
        assertFalse(collision, "Ball should not collide with a line consisting of a single point");
    }

    // Test if the ball's velocity is correctly updated after collision.
    // Verifies that the ball reflects off the line with the correct new velocity.
    @Test
    public void testBallVelocityAfterCollision() {
        PApplet p = new PApplet();
        PImage image = p.createImage(10, 10, PApplet.RGB); // Dummy PImage
        int color = 0xFFFFFF; // Dummy color for the ball

        Line line = new Line(p);
        line.addPoint(100, 100);
        line.addPoint(200, 100);

        Ball ball = new Ball(150, 90, image, color, p);

        boolean collision = line.checkCollision(ball);
        assertTrue(collision, "Ball should collide with the line");

        PVector expectedVelocity = new PVector(0, -5);
        PVector actualVelocity = new PVector(ball.getXVelocity(), ball.getYVelocity());

        assertEquals(expectedVelocity.x, actualVelocity.x, 0.01, "Ball's X velocity should be correctly reflected");
        assertEquals(expectedVelocity.y, actualVelocity.y, 0.01, "Ball's Y velocity should be correctly reflected");
    }

    // Test the checkCollision method when the ball is moving away from the line.
    // Verifies that no collision is detected in such scenarios.
    @Test
    public void testCheckCollisionBallMovingAway() {
        PApplet p = new PApplet();
        PImage image = p.createImage(10, 10, PApplet.RGB); // Dummy PImage
        int color = 0xFFFFFF; // Dummy color for the ball

        Line line = new Line(p);
        line.addPoint(100, 100);
        line.addPoint(200, 100);

        Ball ball = new Ball(150, 110, image, color, p);

        boolean collision = line.checkCollision(ball);
        assertFalse(collision, "Ball moving away should not collide with the line");
    }

    // Test collision where the first normal vector is chosen.
    // Ensures correct normal vector selection based on the ball's position.
    @Test
    public void testCalculateNewVelocityNormal1() {
        PApplet p = new PApplet();
        PImage image = p.createImage(10, 10, PApplet.RGB); // Dummy PImage
        int color = 0xFFFFFF; // Dummy color for the ball

        Line line = new Line(p);
        line.addPoint(0, 0);
        line.addPoint(0, 100);

        Ball ball = new Ball(10, 50, image, color, p);

        boolean collision = line.checkCollision(ball);
        assertTrue(collision, "Ball should collide with the vertical line");

        PVector expectedVelocity = new PVector(5, 0);
        PVector actualVelocity = new PVector(ball.getXVelocity(), ball.getYVelocity());

        assertEquals(expectedVelocity.x, actualVelocity.x, 0.01, "Ball's X velocity should be correctly reflected");
        assertEquals(expectedVelocity.y, actualVelocity.y, 0.01, "Ball's Y velocity should remain unchanged");
    }

    // Test collision where the second normal vector is chosen.
    // Verifies normal vector selection when the ball is on the opposite side.
    @Test
    public void testCalculateNewVelocityNormal2() {
        PApplet p = new PApplet();
        PImage image = p.createImage(10, 10, PApplet.RGB); // Dummy PImage
        int color = 0xFFFFFF; // Dummy color for the ball

        Line line = new Line(p);
        line.addPoint(0, 0);
        line.addPoint(0, 100);

        Ball ball = new Ball(-10, 50, image, color, p);

        boolean collision = line.checkCollision(ball);
        assertTrue(collision, "Ball should collide with the vertical line from the opposite side");

        PVector expectedVelocity = new PVector(-5, 0);
        PVector actualVelocity = new PVector(ball.getXVelocity(), ball.getYVelocity());

        assertEquals(expectedVelocity.x, actualVelocity.x, 0.01, "Ball's X velocity should be correctly reflected");
        assertEquals(expectedVelocity.y, actualVelocity.y, 0.01, "Ball's Y velocity should remain unchanged");
    }
}
