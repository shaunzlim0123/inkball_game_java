package inkball;

import processing.core.PApplet;
import processing.core.PImage;
import java.util.List;

/**
 * The Renderer class is responsible for rendering all the visual elements of the Inkball game.
 * It draws the level, including tiles, walls, balls, holes, spawners, acceleration tiles, lines, timers, and game status messages.
 * The Renderer interacts with the App and Level classes to retrieve game elements and their states for rendering.
 */
public class Renderer {
    private Level level;
    private App p;

    /**
     * Constructor for the Renderer class.
     *
     * @param level The Level instance to render.
     * @param p     The main App instance for drawing.
     */
    public Renderer(Level level, App p) {
        this.level = level;
        this.p = p;
    }

    /**
     * Draws all the game elements on the screen, including tiles, walls, balls, and more.
     * It also checks if the level is completed and renders appropriate messages or animations.
     */
    public void draw() {
        drawTiles();
        drawWalls();
        drawHoles();
        drawSpawners();
        drawAccelerationTiles();
        drawBalls();
        drawLines();
        drawTimer();
        drawSpawnQueueVisuals();

        if (level.isLevelCompleted()) {
            level.getLevelCompletionAnimation().draw();
        }

        drawPauseMessage();
        drawTimeUpMessage();
        drawGameEndedMessage();
    }

    /**
     * Draws all the tiles in the level. Each tile is rendered based on its location and type.
     */
    private void drawTiles() {
        for (Tile tile : level.getTiles()) {
            tile.draw();
        }
    }

    /**
     * Draws all the walls in the level. Walls are static objects that affect ball movement.
     */
    private void drawWalls() {
        for (Wall wall : level.getWalls()) {
            wall.draw();
        }
    }

    /**
     * Draws all the holes in the level. Holes are the targets where balls can be captured based on their colors.
     */
    private void drawHoles() {
        for (Hole hole : level.getHoles()) {
            hole.draw();
        }
    }

    /**
     * Draws all the spawners in the level. Spawners are responsible for generating new balls.
     */
    private void drawSpawners() {
        for (Spawner spawner : level.getSpawners()) {
            spawner.draw();
        }
    }

    /**
     * Draws all the acceleration tiles in the level. These tiles apply forces to the balls, modifying their velocity.
     */
    private void drawAccelerationTiles() {
        for (AccelerationTile accelTile : level.getAccelerationTiles()) {
            accelTile.draw();
        }
    }

    /**
     * Draws all the balls currently active in the level. Each ball's position and velocity are updated as part of the game loop.
     */
    private void drawBalls() {
        for (Ball ball : level.getBalls()) {
            ball.draw();
        }
    }

    /**
     * Draws all the lines in the level, including both completed lines and the currently drawn line (if any).
     * Lines are used by the player to control the movement of the balls.
     */
    private void drawLines() {
        for (Line line : level.getLines()) {
            line.draw();
        }

        if (level.getInputHandler().getCurrentLine() != null) {
            level.getInputHandler().getCurrentLine().draw();
        }
    }

    /**
     * Draws the remaining time on the screen, showing how much time is left for the current level.
     * If the level is completed, it displays the time remaining in the completion animation.
     */
    private void drawTimer() {
        p.fill(0);
        p.textSize(18);
        p.textAlign(PApplet.RIGHT, PApplet.TOP);
        int timeToDisplay = level.getTimeRemaining();
        if (level.isLevelCompleted() && level.getLevelCompletionAnimation() != null) {
            timeToDisplay = level.getLevelCompletionAnimation().getTimeRemaining();
        }
        p.text("Time: " + timeToDisplay, p.width - 10, 40);
    }
    

    /**
     * Draws the visuals of the spawn queue, including the next five balls that will be spawned and the time until the next spawn.
     * It provides visual feedback to the player regarding the upcoming balls and timing.
     */
    private void drawSpawnQueueVisuals() {
        int numberOfBallsToShow = 5;
        int ballSize = 32;

        // Compute time until the next spawn
        float timeUntilNextSpawn = level.calculateTimeUntilNextSpawn();

        // Draw the black canvas behind the balls and timer
        int canvasWidth = (numberOfBallsToShow * ballSize) + 10;
        int canvasHeight = ballSize + 10;

        p.fill(0);
        p.noStroke();
        p.rect(5, 10, canvasWidth, canvasHeight);

        p.fill(0);
        p.textSize(20);
        p.textAlign(PApplet.LEFT, PApplet.TOP);
        p.text(String.format("%.1f", timeUntilNextSpawn), 180, 20);

        // Draw the upcoming balls horizontally
        int y = 20;
        int x = 10;
        List<Ball> ballsToShow = level.getNextBallsToShow(numberOfBallsToShow);
        for (Ball ball : ballsToShow) {
            PImage ballImage = ball.getImage();
            p.image(ballImage, x, y);
            x += ballSize; // Move to the next position
        }
    }

    /**
     * Draws a pause message if the game is paused. It displays a message in the center of the screen.
     */
    private void drawPauseMessage() {
        if (level.isPaused()) {
            p.fill(255, 255, 0);
            p.textSize(32);
            p.textAlign(PApplet.CENTER, PApplet.CENTER);
            p.text("*** PAUSED ***", p.width / 2, Level.yOffset / 2);
        }
    }    

    /**
     * Draws a "Time's up" message if the time runs out in the current level.
     * This message is displayed in the center of the screen when the timer hits zero.
     */
    private void drawTimeUpMessage() {
        if (level.isTimeUp()) {
            p.fill(255, 0, 0);
            p.textSize(32);
            p.textAlign(PApplet.CENTER, PApplet.CENTER);
            p.text("=== TIME'S UP ===", p.width / 2, Level.yOffset / 2);
        }
    }

    /**
     * Draws a "Game ended" message when the game is over. 
     * This message is displayed when all levels are completed or if the player fails.
     */
    private void drawGameEndedMessage() {
        if (level.isGameEnded()) {
            p.fill(0, 255, 0);
            p.textSize(32);
            p.textAlign(PApplet.CENTER, PApplet.CENTER);
            p.text("=== GAME ENDED ===", p.width / 2, Level.yOffset / 2);
        }
    }
}
