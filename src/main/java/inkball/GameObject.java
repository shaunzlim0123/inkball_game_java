package inkball;

/**
 * The GameObject class is an abstract class representing a general game object in the Inkball game.
 * It defines the basic properties of a game object, such as position and size, 
 * and includes abstract methods for drawing the object, which must be implemented by subclasses.
 */
public abstract class GameObject {

    /**
     * The x-coordinate of the game object.
     */
    protected float x;

    /**
     * The y-coordinate of the game object.
     */
    protected float y;

    /**
     * The width of the game object.
     */
    protected int WIDTH;

    /**
     * The height of the game object.
     */
    protected int HEIGHT;

    /**
     * Constructs a GameObject with the given position and size.
     *
     * @param x      The x-coordinate of the game object.
     * @param y      The y-coordinate of the game object.
     * @param WIDTH  The width of the game object.
     * @param HEIGHT The height of the game object.
     */
    public GameObject(float x, float y, int WIDTH, int HEIGHT) {
        this.x = x;
        this.y = y;
        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;
    }

    /**
     * Abstract method for drawing the game object.
     * Subclasses must implement this method to define how the game object is rendered.
     */
    public abstract void draw();

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
