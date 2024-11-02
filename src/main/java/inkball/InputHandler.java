package inkball;

import java.util.Iterator;

import processing.core.PConstants;

/**
 * The InputHandler class manages user input related to drawing and removing lines in the Inkball game.
 * Players can draw lines with the left mouse button and remove lines with the right mouse button.
 * The class also tracks the current line being drawn by the player and interacts with the Level class to modify the game state.
 */
public class InputHandler {
    /**
     * Reference to the current level, used for adding and removing lines and checking game state.
     */
    private Level level;

    /**
     * The current line being drawn by the player.
     */
    private Line currentLine;

    /**
     * Flag indicating whether the player is currently drawing a line.
     */
    private boolean isDrawing;

    /**
     * Constructs an InputHandler object to manage user input for drawing and removing lines in the game.
     *
     * @param level The current level, used for accessing game state and adding/removing lines.
     */
    public InputHandler(Level level) {
        this.level = level;
        this.isDrawing = false;
    }

    /**
     * Starts drawing a new line when the player initiates a drawing action.
     * This method creates a new Line object and adds the starting point to the line.
     *
     * @param x The x-coordinate where the drawing begins.
     * @param y The y-coordinate where the drawing begins.
     */
    public void startDrawingLine(int x, int y) {
        if (!level.isTimeUp() && !level.isLevelCompleted()) {
            isDrawing = true;
            currentLine = new Line(level.getApp());  
            currentLine.addPoint(x, y); // Adds the starting point to the line
        }
    }

    /**
     * Adds a point to the current line as the player continues drawing.
     * This method ensures the line is being drawn and that the game is not over.
     *
     * @param x The x-coordinate of the new point to add.
     * @param y The y-coordinate of the new point to add.
     */
    public void addPointToLine(int x, int y) {
        if (isDrawing && currentLine != null && !level.isTimeUp() && !level.isLevelCompleted()) {
            currentLine.addPoint(x, y); // Adds a point to the line if drawing is active
        }
    }

    /**
     * Finishes drawing the current line when the player releases the drawing input.
     * This method adds the line to the level and resets the drawing state.
     */
    public void finishDrawingLine() {
        if (isDrawing && currentLine != null) {
            level.addLine(currentLine);  // Adds the completed line to the level
            currentLine = null;          // Resets the current line
            isDrawing = false;           // Stops the drawing process
        }
    }

    /**
     * Handles mouse click events for both drawing and removing lines.
     * Left-click draws lines, and right-click removes lines.
     *
     * @param x The x-coordinate of the mouse click.
     * @param y The y-coordinate of the mouse click.
     * @param mouseButton The mouse button pressed (left or right).
     */
    public void handleMouseClick(int x, int y, int mouseButton) {
        if (mouseButton == PConstants.LEFT) {
            // Handle left-click for drawing lines
            startDrawingLine(x, y);
        } else if (mouseButton == PConstants.RIGHT) {
            // Handle right-click for removing lines
            removeLineAt(x, y);
        }
    }

    /**
     * Removes a line at the specified mouse coordinates.
     * This checks if any line exists at the clicked location and removes it if found.
     *
     * @param mouseX X-coordinate of the mouse event.
     * @param mouseY Y-coordinate of the mouse event.
     */
    public void removeLineAt(int mouseX, int mouseY) {
        // Iterate through the lines and check if any line contains the clicked point
        Iterator<Line> lineIterator = level.getLines().iterator();
        while (lineIterator.hasNext()) {
            Line line = lineIterator.next();
            if (line.contains(mouseX, mouseY)) {
                lineIterator.remove(); // Remove the line if the mouse click is near it
                break; 
            }
        }
    }

    // Getter
    public Line getCurrentLine() {
        return currentLine;
    }
}
