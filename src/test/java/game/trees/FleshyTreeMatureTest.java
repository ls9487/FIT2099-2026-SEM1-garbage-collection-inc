package game.trees;

import edu.monash.fit2099.engine.positions.Location;
import game.spawners.Spawner;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * REQ2 unit tests for {@link FleshyTreeMature} growth, tick timing, and tree statistics.
 * Verifies ground mutation and growth parameters through behaviour rather than display symbols.
 *
 * @author lden0031
 * @version 1.0
 */
class FleshyTreeMatureTest {

    /**
     * Tests that growing a mature tree replaces the ground with the next stage.
     */
    @Test
    void grow_PositiveCondition_MutatesGroundToNextStageAndReturnsCorrectString() {
        Location mockLocation = mock(Location.class);
        Tree mockNextStage = mock(Tree.class);
        List<Spawner> emptySpawners = Collections.emptyList();

        FleshyTreeMature matureTree = new FleshyTreeMature(emptySpawners, mockNextStage);

        when(mockLocation.getGround()).thenReturn(mockNextStage);

        String outputMessage = matureTree.grow(mockLocation);

        assertAll("Verify ground mutation occurs and growth is reported",
                () -> verify(mockLocation).setGround(mockNextStage),
                () -> assertNotNull(outputMessage),
                () -> assertTrue(outputMessage.toLowerCase().contains("grow"))
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
     * Tests that a mature tree with a next stage exposes growth statistics.
     */
    @Test
    void stats_NormalCondition_VerifiesGrowthParameters() {
        List<Spawner> emptySpawners = Collections.emptyList();
        FleshyTreeMature matureTree = new FleshyTreeMature(emptySpawners, mock(Tree.class));

        assertAll("Verify standard growth configurations are present",
                () -> assertTrue(matureTree.hasStatistic(TreeStatistics.GROW_CHANCE)),
                () -> assertTrue(matureTree.hasStatistic(TreeStatistics.GROW_TURNS))
        );
    }

    /**
     * Tests that a mature tree without a next stage remains a spawner tree only.
     */
    @Test
    void stats_EdgeCondition_TerminalMatureTreeOmitsGrowthStatistics() {
        List<Spawner> emptySpawners = Collections.emptyList();
        FleshyTreeMature matureTree = new FleshyTreeMature(emptySpawners);

        assertAll("Verify terminal mature tree does not track further growth",
                () -> assertFalse(matureTree.hasStatistic(TreeStatistics.GROW_CHANCE)),
                () -> assertFalse(matureTree.hasStatistic(TreeStatistics.GROW_TURNS))
        );
    }
}
