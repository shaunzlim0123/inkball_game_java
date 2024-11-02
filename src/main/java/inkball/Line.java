package inkball;

import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

/**
 * The Line class represents a line in the Inkball game that the player can draw.
 * It handles the points forming the line, collision detection with the ball, and drawing the line on the screen.
 */
public class Line implements Interactable {

    /**
     * Reference to the main PApplet instance for drawing and interaction.
     */
    private PApplet p;

    /**
     * List of points that form the line segments.
     */
    private List<PVector> points;

    /**
     * Thickness of the line used for both drawing and collision detection.
     */
    private static final float THICKNESS = 10.0f;

    /**
     * Constructs a Line object to be drawn by the player.
     *
     * @param p The main PApplet instance for drawing.
     */
    public Line(PApplet p) {
        this.p = p;
        this.points = new ArrayList<>();
    }

    /**
     * Adds a point to the line, extending it with a new segment.
     *
     * @param x The x-coordinate of the point to add.
     * @param y The y-coordinate of the point to add.
     */
    public void addPoint(float x, float y) {
        points.add(new PVector(x, y));
    }

    /**
     * Draws the line on the screen, using a set thickness and black color.
     * The line is drawn as a series of connected points (vertices).
     */
    public void draw() {
        p.stroke(0);  // Set line color to black
        p.strokeWeight(THICKNESS);  // Set line thickness
        p.noFill();
        p.beginShape();
        for (PVector point : points) {
            p.vertex(point.x, point.y);  // Draws each point as a vertex in the line
        }
        p.endShape();
    }

    /**
     * Checks for a collision between the ball and the line.
     * If a collision occurs, it reflects the ball's velocity based on the collision angle.
     *
     * @param ball The ball to check for collision with the line.
     * @return true if a collision occurred, false otherwise.
     */
    public boolean checkCollision(Ball ball) {
        PVector ballPos = new PVector(ball.getX() + ball.getRadius(), ball.getY() + ball.getRadius());
        PVector ballVel = new PVector(ball.getXVelocity(), ball.getYVelocity());
        float ballRadius = ball.getRadius();

        // Calculate the future position of the ball
        PVector futureBallPos = PVector.add(ballPos, ballVel);

        // Check collision with each line segment
        for (int i = 0; i < points.size() - 1; i++) {
            PVector p1 = points.get(i);
            PVector p2 = points.get(i + 1);

            if (isCollidingWithSegment(futureBallPos, ballRadius, p1, p2)) {
                // Calculate new velocity after collision
                PVector newVelocity = calculateNewVelocity(ballVel, p1, p2, ballPos);

                // Update the ball's velocity based on the collision
                ball.setXVelocity(newVelocity.x);
                ball.setYVelocity(newVelocity.y);

                return true;  // Collision occurred
            }
        }
        return false;  // No collision occurred
    }

    /**
     * Checks if the ball is colliding with a line segment between two points.
     *
     * @param ballPos   The current position of the ball.
     * @param ballRadius The radius of the ball.
     * @param p1        The start point of the line segment.
     * @param p2        The end point of the line segment.
     * @return true if the ball is colliding with the segment, false otherwise.
     */
    private boolean isCollidingWithSegment(PVector ballPos, float ballRadius, PVector p1, PVector p2) {
        float distanceToSegment = distanceFromPointToSegment(ballPos, p1, p2);
        return distanceToSegment <= ballRadius + THICKNESS / 2;
    }

    /**
     * Calculates the shortest distance from a point (ball position) to a line segment (formed by two points).
     *
     * @param point    The point (ball position) to calculate the distance from.
     * @param segStart The start point of the line segment.
     * @param segEnd   The end point of the line segment.
     * @return The shortest distance between the point and the line segment.
     */
    private float distanceFromPointToSegment(PVector point, PVector segStart, PVector segEnd) {
        PVector segVector = PVector.sub(segEnd, segStart);
        PVector pointVector = PVector.sub(point, segStart);
        float segLengthSquared = segVector.magSq();  // Square of the segment length

        // Calculate the projection factor 't' using the dot product
        float t = PVector.dot(pointVector, segVector) / segLengthSquared;
        t = PApplet.constrain(t, 0, 1);  // Constrain 't' to the range [0, 1]

        // Calculate the projection point on the line segment
        PVector projection = PVector.add(segStart, PVector.mult(segVector, t));
        return PVector.dist(point, projection);  // Return the distance between the point and the projection
    }

    /**
     * Calculates the new velocity for the ball after it collides with a line segment.
     * The velocity is reflected based on the normal vector of the line segment.
     *
     * @param ballVel  The current velocity of the ball.
     * @param p1       The start point of the line segment.
     * @param p2       The end point of the line segment.
     * @param ballPos  The current position of the ball.
     * @return The new velocity vector for the ball after the collision.
     */
    private PVector calculateNewVelocity(PVector ballVel, PVector p1, PVector p2, PVector ballPos) {
        // Calculate the normal vectors for the line segment
        float dx = p2.x - p1.x;
        float dy = p2.y - p1.y;

        // Two possible normals
        PVector normal1 = new PVector(-dy, dx);
        PVector normal2 = new PVector(dy, -dx);

        // Normalize both normal vectors
        normal1.normalize();
        normal2.normalize();

        // Determine which normal is closer to the ball
        PVector midpoint = PVector.add(p1, p2).mult(0.5f);
        PVector normalPoint1 = PVector.add(midpoint, normal1);
        PVector normalPoint2 = PVector.add(midpoint, normal2);

        float distance1 = PVector.dist(ballPos, normalPoint1);
        float distance2 = PVector.dist(ballPos, normalPoint2);

        // Choose the normal that is closer to the ball
        PVector chosenNormal = (distance1 < distance2) ? normal1 : normal2;

        // Reflect the ball's velocity based on the chosen normal
        float dotProduct = PVector.dot(ballVel, chosenNormal);
        return PVector.sub(ballVel, PVector.mult(chosenNormal, 2 * dotProduct));
    }

    /**
     * Checks if the line contains the given point (within a tolerance range).
     *
     * @param x X-coordinate of the point.
     * @param y Y-coordinate of the point.
     * @return True if the point is on the line, false otherwise.
     */
    public boolean contains(float x, float y) {
        float tolerance = 5; // Tolerance to account for mouse precision
        for (int i = 0; i < points.size() - 1; i++) {
            PVector p1 = points.get(i);
            PVector p2 = points.get(i + 1);
            if (isPointOnLineSegment(p1, p2, x, y, tolerance)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a point is near a line segment between two points.
     */
    private boolean isPointOnLineSegment(PVector p1, PVector p2, float x, float y, float tolerance) {
        float d1 = PApplet.dist(x, y, p1.x, p1.y);
        float d2 = PApplet.dist(x, y, p2.x, p2.y);
        float lineLength = PApplet.dist(p1.x, p1.y, p2.x, p2.y);

        // Check if the point is near enough to the line segment
        return d1 + d2 >= lineLength - tolerance && d1 + d2 <= lineLength + tolerance;
    }
    
    // Getters
    public List<PVector> getPoints() {
        return points;
    }
}
