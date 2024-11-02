package inkball;

/**
 * The Interactable interface represents objects that can interact with balls in the Inkball game.
 * Any class implementing this interface should define behavior for checking and handling collisions with a ball.
 */
public interface Interactable {

    /**
     * Checks if a ball is colliding with this object.
     *
     * @param ball The ball to check for collision.
     * @return true if the ball is colliding with this object, false otherwise.
     */
    boolean checkCollision(Ball ball);
}
