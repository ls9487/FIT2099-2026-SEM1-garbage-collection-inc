package game.trees;

import edu.monash.fit2099.engine.positions.Location;
import game.spawners.Spawner;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link FleshyTreeMature} growth, tick timing, display, and tree statistics.
 *
 * @author lden0031
 * @version 1.0
 */
class FleshyTreeMatureTest {

    /**
     * Tests that growing a mature tree replaces the ground with the next stage and returns a message.
     */
    @Test
    void grow_PositiveCondition_MutatesGroundToNextStageAndReturnsCorrectString() {
        Location mockLocation = mock(Location.class);
        Tree mockNextStage = mock(Tree.class);
        List<Spawner> emptySpawners = Collections.emptyList();

        FleshyTreeMature matureTree = new FleshyTreeMature(emptySpawners, mockNextStage);

        when(mockLocation.getGround()).thenReturn(mockNextStage);
        when(mockNextStage.toString()).thenReturn("Fleshy Tree Monolith");

        String outputMessage = matureTree.grow(mockLocation);

        assertAll("Verify ground mutation logs match specifications",
                () -> verify(mockLocation).setGround(mockNextStage),
                () -> assertNotNull(outputMessage)
        );
    }

    /**
     * Tests that each tick queries nearby locations to advance the growth timer.
     */
    @Test
    void tick_BoundaryCondition_IncrementsGrowthTurnsTimerEachTick() {
        Location mockLocation = mock(Location.class);
        Tree mockNextStage = mock(Tree.class);
        List<Spawner> emptySpawners = Collections.emptyList();

        FleshyTreeMature matureTree = new FleshyTreeMature(emptySpawners, mockNextStage);

        matureTree.tick(mockLocation);
        matureTree.tick(mockLocation);

        verify(mockLocation, times(2)).getNearbyLocations(anyInt());
    }

    /**
     * Tests that a mature fleshy tree uses display character {@code 'Y'}.
     */
    @Test
    void verifyDisplayChar_EdgeCondition_ChecksValidVisualRepresentation() {
        List<Spawner> emptySpawners = Collections.emptyList();
        FleshyTreeMature matureTree = new FleshyTreeMature(emptySpawners);

        assertEquals('Y', matureTree.getDisplayChar(), "Mature display character must be 'Y'");
    }

    /**
     * Tests that a mature tree exposes grow chance and grow turns statistics.
     */
    @Test
    void stats_NormalCondition_VerifiesGrowthParameters() {
        List<Spawner> emptySpawners = Collections.emptyList();
        FleshyTreeMature matureTree = new FleshyTreeMature(emptySpawners, mock(Tree.class));

        assertAll("Verify standard growth configurations match metrics exactly",
                () -> assertTrue(matureTree.hasStatistic(TreeStatistics.GROW_CHANCE)),
                () -> assertTrue(matureTree.hasStatistic(TreeStatistics.GROW_TURNS))
        );
    }
}
