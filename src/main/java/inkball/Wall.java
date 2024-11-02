package inkball;

import processing.core.PApplet;
import processing.core.PImage;

/**
 * The Wall class represents a wall in the Inkball game.
 * It extends the GameObject class and implements the Interactable interface.
 * It handles drawing the wall and checking for collisions with the ball.
 */
public class Wall extends GameObject implements Interactable {

    /**
     * Reference to the main PApplet instance for drawing and interaction.
     */
    private PApplet p;

    /**
     * Image used to visually represent the wall.
     */
    private PImage image;

    /**
     * Color index of the wall, representing the wall's color.
     */
    private int color;

    /**
     * Constructs a Wall object with the given parameters.
     *
     * @param x      The x-coordinate of the wall.
     * @param y      The y-coordinate of the wall.
     * @param image  The image to represent the wall.
     * @param color  The color index of the wall.
     * @param p      The main PApplet instance for drawing.
     */
    public Wall(float x, float y, PImage image, int color, PApplet p) {
        super(x, y, App.CELLSIZE, App.CELLSIZE);  
        this.image = image;
        this.color = color;
        this.p = p;
    }

    /**
     * Draws the wall at its current position using the image provided.
     * The image is scaled to fit within the defined cell size.
     */
    public void draw() {
        p.image(image, x, y, App.CELLSIZE, App.CELLSIZE);
    }

    /**
     * Checks if the ball is colliding with the wall and reflects its direction if a collision occurs.
     * It also handles corner (diagonal) collisions and sets a collision buffer to avoid rapid repeated collisions.
     *
     * @param ball The ball to check for collision with.
     * @return true if a collision occurred, false otherwise.
     */
    public boolean checkCollision(Ball ball) {  
        // Ball's center and radius
        float ballCenterX = ball.getX() + ball.getRadius();
        float ballCenterY = ball.getY() + ball.getRadius();
        float ballRadius = ball.getRadius();
    
        // Bounding box collision check
        if (ballCenterX + ballRadius > x && ballCenterX - ballRadius < x + WIDTH &&
            ballCenterY + ballRadius > y && ballCenterY - ballRadius < y + HEIGHT) {
    
            // Calculate overlaps on both axes
            float overlapLeft = (ballCenterX + ballRadius) - x;
            float overlapRight = (x + WIDTH) - (ballCenterX - ballRadius);
            float overlapTop = (ballCenterY + ballRadius) - y;
            float overlapBottom = (y + HEIGHT) - (ballCenterY - ballRadius);
    
            float minOverlap = Math.min(Math.min(overlapLeft, overlapRight), Math.min(overlapTop, overlapBottom));
    
            // Adjust position and reflect velocity based on collision side
            if (minOverlap == overlapLeft) {
                // Collision on the left side
                ball.setX(x - ballRadius * 2); 
                ball.reflectX();
            } else if (minOverlap == overlapRight) {
                // Collision on the right side
                ball.setX(x + WIDTH);
                ball.reflectX();
            } else if (minOverlap == overlapTop) {
                // Collision on the top side
                ball.setY(y - ballRadius * 2);
                ball.reflectY();
            } else if (minOverlap == overlapBottom) {
                // Collision on the bottom side
                ball.setY(y + HEIGHT);
                ball.reflectY();
            }
    
            changeBallColor(ball);
    
            return true;
        }
        return false; 
    }
    

    /**
     * Changes the color of the ball to match the wall's color, if applicable.
     * Also updates the ball's image to reflect the new color.
     *
     * @param ball The ball whose color will be changed.
     */
    private void changeBallColor(Ball ball) {
        if (this.color != 0) {  // Only change color if the wall has a non-default color
            ball.setColor(this.color);
            ball.setImage(App.getBallImages()[this.color]);
        }
    }

    // Getters 
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public PImage getImage() {
        return image;
    }
}
