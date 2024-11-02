package inkball;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

public class LevelCompletionAnimationTest {

    private LevelCompletionAnimation animation;
    private Level mockLevel;
    private App mockApp;

    @BeforeEach
    public void setUp() {
        // Mock the App and Level dependencies
        mockApp = Mockito.mock(App.class);
        mockLevel = Mockito.mock(Level.class);
        
        // Mock methods on App and Level
        Mockito.when(mockLevel.getApp()).thenReturn(mockApp);
        Mockito.when(mockLevel.getTimeRemaining()).thenReturn(100); // 100 seconds for timeRemaining


        // Initialize the LevelCompletionAnimation object with mock dependencies
        animation = new LevelCompletionAnimation(mockLevel);
    }

    // Tests the behavior of the update method: ensures that time is decremented
    // and that the score is increased correctly after multiple updates.
    @Test
    public void testUpdate() {
        int initialTime = animation.getTimeRemaining();

        // Simulate a few update calls
        for (int i = 0; i < 3; i++) {
            animation.update();
        }

        // Check that time is decremented and the score was increased
        assertEquals(initialTime - 3, animation.getTimeRemaining());
        verify(mockApp, Mockito.times(3)).addToAccumulatedScore(1); // Verify score addition
    }

    // Tests whether the animation correctly identifies when the level completion animation is done,
    // i.e when time remaining reaches zero.
    @Test
    public void testAnimationCompleted() {
        // Reduce the time to near zero
        Mockito.when(mockLevel.getTimeRemaining()).thenReturn(2); 
        
        // Simulate updates until time runs out
        animation.update();
        animation.update(); // Time reaches zero after this call

        // Check that the animation is completed
        assertTrue(animation.isAnimationCompleted());
    }

    // Tests if yellow tiles move correctly along the path during the animation.
    @Test
    public void testMoveYellowTiles() {
        int initialTileIndex1 = animation.getTileIndex1();
        int initialTileIndex2 = animation.getTileIndex2();

        // Simulate several update cycles to move the tiles
        for (int i = 0; i < animation.getTileSpeed(); i++) {
            animation.update();
        }

        // Check that the tiles have moved
        assertNotEquals(initialTileIndex1, animation.getTileIndex1());
        assertNotEquals(initialTileIndex2, animation.getTileIndex2());
    }

    // Ensures that the tile path is initialized correctly with valid X and Y coordinates.
    @Test
    public void testTilePathInitialization() {
        int[] tilePathX = animation.getTilePathX();
        int[] tilePathY = animation.getTilePathY();

        // Check that the path coordinates are set correctly
        assertNotNull(tilePathX);
        assertNotNull(tilePathY);

        // Check that the path is non-empty
        assertTrue(tilePathX.length > 0);
        assertTrue(tilePathY.length > 0);
    }

    // Ensures that the yellow tiles are initialized with the correct initial positions
    // based on the path coordinates.
    @Test
    public void testYellowTilesInitialization() {
        List<Tile> yellowTiles = animation.getYellowTiles();
        assertEquals(2, yellowTiles.size());

        // Check initial positions of the yellow tiles
        assertEquals(animation.getTilePathX()[0], yellowTiles.get(0).getX());
        assertEquals(animation.getTilePathY()[0], yellowTiles.get(0).getY());

        int midIndex = animation.getTilePathX().length / 2;
        assertEquals(animation.getTilePathX()[midIndex], yellowTiles.get(1).getX());
        assertEquals(animation.getTilePathY()[midIndex], yellowTiles.get(1).getY());
    }
}
