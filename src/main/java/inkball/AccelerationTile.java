package inkball;

import processing.core.PApplet;
import processing.core.PImage;

/**
 * Represents an acceleration tile that accelerates balls in a specific direction.
 */
public class AccelerationTile extends GameObject implements Interactable {
    private PImage image;
    private PApplet p;
    private float accelerationX;
    private float accelerationY;

    /**
     * Constructor for AccelerationTile.
     *
     * @param x             The x-coordinate of the tile.
     * @param y             The y-coordinate of the tile.
     * @param image         The image representing the tile.
     * @param accelerationX The acceleration in the x-direction.
     * @param accelerationY The acceleration in the y-direction.
     * @param p             The PApplet instance.
     */
    public AccelerationTile(float x, float y, PImage image, float accelerationX, float accelerationY, PApplet p) {
        super(x, y, App.CELLSIZE, App.CELLSIZE);
        this.image = image;
        this.accelerationX = accelerationX;
        this.accelerationY = accelerationY;
        this.p = p;
        this.image.resize(App.CELLSIZE, App.CELLSIZE);
    }

    /**
     * Draws the acceleration tile on the screen.
     */
    public void draw() {
        p.image(image, x, y);
    }

    /**
     * Checks if a ball is colliding with this tile and applies the specified acceleration 
     * to the ball's velocity.
     *
     * @param ball The ball to check for a collision.
     * @return true if the ball is colliding with the tile, false otherwise.
     */
    public boolean checkCollision(Ball ball) {
        // Ball's center and radius
        float ballCenterX = ball.getX() + ball.getRadius();
        float ballCenterY = ball.getY() + ball.getRadius();
        float ballRadius = ball.getRadius();
    
        // Bounding box collision check
        if (ballCenterX + ballRadius > x && ballCenterX - ballRadius < x + WIDTH &&
            ballCenterY + ballRadius > y && ballCenterY - ballRadius < y + HEIGHT) {
                ball.setXVelocity(ball.getXVelocity() + accelerationX);
                ball.setYVelocity(ball.getYVelocity() + accelerationY);
                return true;
        }
        return false;
    }
}
