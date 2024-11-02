package inkball;

import processing.core.PApplet;
import processing.core.PImage;
import java.util.Random;

/**
 * The Ball class represents a ball in the Inkball game.
 * It extends the GameObject class and handles the ball's position, velocity, movement, and collision behavior.
 */
public class Ball extends GameObject {

    /**
     * Reference to the main PApplet instance for drawing and updating the ball.
     */
    private PApplet p;

    /**
     * Image used to visually represent the ball.
     */
    private PImage image;

    /**
     * Color index of the ball, representing its color.
     */
    private int color;

    /**
     * Horizontal velocity of the ball.
     */
    private float xVelocity;

    /**
     * Vertical velocity of the ball.
     */
    private float yVelocity;

    /**
     * Max velocity for the ball.
     */
    private static final float MAX_VELOCITY = 2.0f; // Adjust based on gameplay needs


    /**
     * Radius of the ball, calculated as half the cell size.
     */
    private float radius;

    /**
     * Random object for initializing random velocity directions.
     */
    private static Random random = new Random();

    /**
     * The scale factor for the ball, used to adjust its size during rendering.
     */
    private float scale = 1.0f;

    /**
     * Constructs a Ball object with the given parameters.
     *
     * @param x      The initial x-coordinate of the ball.
     * @param y      The initial y-coordinate of the ball.
     * @param image  The image to represent the ball.
     * @param color  The color index of the ball.
     * @param p      The main PApplet instance for drawing.
     */
    public Ball(float x, float y, PImage image, int color, PApplet p) {
        super(x, y, App.CELLSIZE, App.CELLSIZE);  
        this.image = image;
        this.p = p;
        this.color = color;
        this.radius = App.CELLSIZE / 2.0f;

        // Initialize velocities with a random direction
        this.xVelocity = random.nextBoolean() ? 1 : -1;
        this.yVelocity = random.nextBoolean() ? 1 : -1;
    }

    /**
     * Updates the position of the ball based on its velocity.
     * It also decrements the collision buffer to handle collision timing.
     */
    public void updatePosition() {
        x += xVelocity;
        y += yVelocity;
    }

    /**
     * Reflects the ball's movement along the x-axis, used for handling collisions with vertical walls.
     */
    public void reflectX() {
        xVelocity *= -1;
    }

    /**
     * Reflects the ball's movement along the y-axis, used for handling collisions with horizontal walls.
     */
    public void reflectY() {
        yVelocity *= -1;
    }

    /**
     * Draws the ball at its current position with scaling applied.
     * The size of the ball is adjusted based on its radius and scale factor.
     */
    public void draw() {
        // Adjust the drawing size based on the scale
        float scaledDiameter = this.radius * 2 * this.scale;
        p.image(image, x, y, scaledDiameter, scaledDiameter);
    }

    // Getters and Setters
    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public float getXVelocity() {
        return xVelocity;
    }

    public float getYVelocity() {
        return yVelocity;
    }

    public void setXVelocity(float xVelocity) {
        this.xVelocity = Math.max(-MAX_VELOCITY, Math.min(xVelocity, MAX_VELOCITY)); // Cap velocity
    }  

    public void setYVelocity(float yVelocity) {
        this.yVelocity = Math.max(-MAX_VELOCITY, Math.min(yVelocity, MAX_VELOCITY)); // Cap velocity
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public PImage getImage() {
        return image;
    }

    public void setImage(PImage image) {
        this.image = image;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getScale() {
        return this.scale;
    }
}
