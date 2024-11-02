package inkball;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

/**
 * The Hole class represents a hole in the Inkball game.
 * It attracts nearby balls and captures them if they reach the center.
 */
public class Hole extends GameObject implements Interactable{
    private PApplet p;
    private PImage image;
    private int color;

    // Center position of the hole
    private float centerX;
    private float centerY;

    // Constants for hole size (2x2 tiles)
    private static final int HOLE_WIDTH = App.CELLSIZE * 2;
    private static final int HOLE_HEIGHT = App.CELLSIZE * 2;

    // Attraction settings
    private static final float ATTRACTION_RADIUS = 32.0f; // Attraction starts within 32 pixels
    private static final float ATTRACTION_FORCE_CONSTANT = 0.005f; // 0.5% of the vector

    public Hole(float x, float y, PImage image, int color, PApplet p) {
        super(x, y, HOLE_WIDTH, HOLE_HEIGHT);
        this.image = image;
        this.p = p;
        this.color = color;

        this.centerX = x + HOLE_WIDTH / 2;
        this.centerY = y + HOLE_HEIGHT / 2;
    }

    /**
     * Draws the hole on the screen.
     */
    public void draw() {
        p.image(image, x, y, HOLE_WIDTH, HOLE_HEIGHT);
    }

    /**
     * Handles interaction with a ball.
     * @param ball The ball to handle.
     * @param level The current level.
     * @return True if the ball has been captured, false otherwise.
     */
    public boolean handleBall(Ball ball, Level level) {
        // Get the scaled radius
        float scaledRadius = ball.getRadius() * ball.getScale();

        // Calculate the distance from the ball's center to the hole's center
        float ballCenterX = ball.getX() + scaledRadius;
        float ballCenterY = ball.getY() + scaledRadius;
        float distance = PApplet.dist(ballCenterX, ballCenterY, centerX, centerY);

        if (checkCollision(ball)) {
            float scaleFactor = distance / ATTRACTION_RADIUS;
            scaleFactor = PApplet.constrain(scaleFactor, 0.5f, 1f); // Ensure it's between 0.5 and 1

            // Update the ball's scale
            ball.setScale(scaleFactor);

            // Calculate the vector from the ball to the hole's center
            float attractionX = centerX - ballCenterX;
            float attractionY = centerY - ballCenterY;

            // Create the attraction vector
            PVector attractionVector = new PVector(attractionX, attractionY);

            // Calculate the attraction force 
            attractionVector.mult(ATTRACTION_FORCE_CONSTANT);

            // Apply the attraction force to the ball's velocity
            ball.setXVelocity(ball.getXVelocity() + attractionVector.x);
            ball.setYVelocity(ball.getYVelocity() + attractionVector.y);

            // Check if the ball has reached the center
            if (distance < scaledRadius) {
                // Ball has been captured
                return captureBall(ball, level);
            }
        }
        return false; 
    }

    /**
     * Captures the ball and updates the score.
     * @param ball The ball to capture.
     * @param level The current level.
     * @return True to indicate the ball has been captured.
     */
    private boolean captureBall(Ball ball, Level level) {
        // Check for color match or grey ball/hole
        if (this.color == ball.getColor() || this.color == 0 || ball.getColor() == 0) {
            // Successful capture
            level.increaseScore(ball.getColor());
        } else {
            // Unsuccessful capture
            level.decreaseScore(ball.getColor());
            // Re-add the ball to the spawn queue
            level.addBallToSpawnQueue(ball);
        }
        return true; 
    }

    /**
     * Checks if the ball is within the attraction radius of the hole (collision check).
     * @param ball The ball to check for collision.
     * @return true if the ball is within the attraction radius, false otherwise.
     */
    @Override
    public boolean checkCollision(Ball ball) {
        // Calculate the distance from the ball's center to the hole's center
        float ballCenterX = ball.getX() + ball.getRadius();
        float ballCenterY = ball.getY() + ball.getRadius();
        float distance = PApplet.dist(ballCenterX, ballCenterY, centerX, centerY);

        return distance <= ATTRACTION_RADIUS;
    }
}
