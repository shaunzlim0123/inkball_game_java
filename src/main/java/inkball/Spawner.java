package inkball;

import processing.core.PApplet;
import processing.core.PImage;

/**
 * The Spawner class represents a spawner object in the Inkball game, responsible for spawning balls.
 * It extends the GameObject class and handles the drawing of the spawner on the game board.
 */
public class Spawner extends GameObject {

    /**
     * Reference to the main PApplet instance for drawing and interaction.
     */
    private PApplet p;

    /**
     * Image used to visually represent the spawner.
     */
    private PImage image;

    /**
     * Constructs a Spawner object at a specified position with a given image.
     *
     * @param x      The x-coordinate of the spawner.
     * @param y      The y-coordinate of the spawner.
     * @param image  The image used to represent the spawner.
     * @param p      The main PApplet instance for drawing.
     */
    public Spawner(float x, float y, PImage image, PApplet p) {
        super(x, y, App.CELLSIZE, App.CELLSIZE); 
        this.image = image;
        this.p = p;
    }

    /**
     * Draws the spawner on the game board using the provided image.
     * The spawner is drawn at its specified position and scaled to the cell size.
     */
    @Override
    public void draw() {
        p.image(image, x, y, App.CELLSIZE, App.CELLSIZE);  // Draw the spawner image
    }
    
    // Getters
    public PImage getImage() {
        return image;
    }
}
