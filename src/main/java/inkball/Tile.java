package inkball;

import processing.core.PApplet;
import processing.core.PImage;

/**
 * The Tile class represents a tile in the Inkball game. 
 * Tiles form the base grid of the game board and are drawn using an image.
 * It extends the GameObject class and handles the rendering of the tile.
 */
public class Tile extends GameObject {

    /**
     * Reference to the main PApplet instance for drawing and interaction.
     */
    private PApplet p;

    /**
     * Image used to visually represent the tile.
     */
    private PImage image;

    /**
     * Constructs a Tile object with the specified position and image.
     *
     * @param x      The x-coordinate of the tile.
     * @param y      The y-coordinate of the tile.
     * @param image  The image used to represent the tile.
     * @param p      The main PApplet instance for drawing.
     */
    public Tile(float x, float y, PImage image, PApplet p) {
        super(x, y, App.CELLSIZE, App.CELLSIZE);  // Calls the superclass constructor for size and position
        this.image = image;
        this.p = p;
    }

    /**
     * Draws the tile on the game board using the provided image.
     * The tile is drawn at its specified position and scaled to the cell size.
     */
    @Override
    public void draw() {
        p.image(image, x, y, App.CELLSIZE, App.CELLSIZE);  // Draw the tile image
    }

    // Getters and Setters
    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

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
