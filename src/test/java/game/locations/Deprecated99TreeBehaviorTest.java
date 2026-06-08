package game.locations;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GroundCreator;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.Teleporter;
import game.trees.Growable;
import game.trees.TreeStatistics;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * REQ2 unit tests for fleshy tree evolution behaviour on the {@link Deprecated99} map.
 * Verifies sprout-to-mature-to-monolith growth by checking growable and teleporter
 * capabilities rather than ground type names or display characters.
 *
 * @author lden0031
 * @version 1.0
 */
class Deprecated99TreeBehaviorTest {

    /**
     * Tests that Deprecated99 places a growable sprout that is not yet a teleporter.
     */
    @Test
    void setTrees_PositiveCondition_VerifiesCorrectTreeHierarchyOnMoon99() throws Exception {
        GroundCreator mockFactory = mock(GroundCreator.class);
        List<Supplier<Item>> mockPool = new ArrayList<>();
        mockPool.add(() -> mock(Item.class));
        mockPool.add(() -> mock(Item.class));

        Deprecated99 map99 = new Deprecated99(mockFactory, mockPool);
        Location treeLocation = map99.at(1, 16);

        assertAll("Verify Deprecated99 spawns an early-stage growable tree",
                () -> assertNotNull(treeLocation.getGround(), "Tree ground object must exist"),
                () -> assertTrue(treeLocation.getGround().asCapability(Growable.class).isPresent(),
                        "Initial tree stage must be growable"),
                () -> assertTrue(treeLocation.getGround().hasStatistic(TreeStatistics.GROW_TURNS),
                        "Sprout stage must track growth turns"),
                () -> assertFalse(treeLocation.getGround().asCapability(Teleporter.class).isPresent(),
                        "Sprout stage must not teleport actors")
        );
    }

    /**
     * Tests that a sprout on Deprecated99 skips sapling and grows directly into mature stage.
     */
    @Test
    void treeEvolution_BoundaryCondition_DirectlyLinksSproutToMatureSkippingSapling() throws Exception {
        GroundCreator mockFactory = mock(GroundCreator.class);
        List<Supplier<Item>> pool = List.of(() -> mock(Item.class), () -> mock(Item.class));

        Deprecated99 map99 = new Deprecated99(mockFactory, pool);
        Location testLocation = map99.at(1, 16);

        Growable sprout = testLocation.getGround().asCapability(Growable.class)
                .orElseThrow(() -> new AssertionError("Expected initial growable sprout"));
        sprout.grow(testLocation);

        assertAll("Verify sprout bypasses sapling and reaches a further growable stage",
                () -> assertTrue(testLocation.getGround().asCapability(Growable.class).isPresent(),
                        "Mature stage must remain growable on Map 99"),
                () -> assertTrue(testLocation.getGround().hasStatistic(TreeStatistics.GROW_CHANCE),
                        "Mature stage must retain growth chance"),
                () -> assertFalse(testLocation.getGround().asCapability(Teleporter.class).isPresent(),
                        "Mature stage must not yet be a teleporter")
        );
    }

    /**
     * Tests that a mature tree on Deprecated99 evolves into a teleporter monolith.
     */
    @Test
    void treeEvolution_EdgeCondition_MatureEvolvesIntoFleshyMonolithOnMap99() throws Exception {
        GroundCreator mockFactory = mock(GroundCreator.class);
        List<Supplier<Item>> pool = List.of(() -> mock(Item.class), () -> mock(Item.class));

        Deprecated99 map99 = new Deprecated99(mockFactory, pool);
        Location testLocation = map99.at(1, 16);

        Growable sprout = testLocation.getGround().asCapability(Growable.class)
                .orElseThrow(() -> new AssertionError("Expected initial growable sprout"));
        sprout.grow(testLocation);

        Growable matureTree = testLocation.getGround().asCapability(Growable.class)
                .orElseThrow(() -> new AssertionError("Expected growable mature stage"));
        matureTree.grow(testLocation);

        assertAll("Verify mature tree becomes a terminal teleporter on Map 99",
                () -> assertTrue(testLocation.getGround().asCapability(Teleporter.class).isPresent(),
                        "Final stage must expose teleporter capability"),
                () -> assertFalse(testLocation.getGround().asCapability(Growable.class).isPresent(),
                        "Terminal monolith must no longer be growable")
        );
    }
}
