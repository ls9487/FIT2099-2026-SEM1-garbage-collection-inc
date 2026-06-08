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
 * REQ2 unit tests for fleshy tree evolution behaviour on the {@link Overflow20} map.
 * Verifies the sprout-sapling-mature chain and confirms evolution stops before
 * a teleporter monolith by checking growth statistics and capabilities.
 *
 * @author lden0031
 * @version 1.0
 */
class Overflow20TreeBehaviorTest {

    /**
     * Tests that Overflow20 places a growable sprout at the configured tree tile.
     */
    @Test
    void setTrees_PositiveCondition_VerifiesFullEvolutionaryChainOnOverflow20() throws Exception {
        GroundCreator mockFactory = mock(GroundCreator.class);
        List<Supplier<Item>> mockPool = new ArrayList<>();
        mockPool.add(() -> mock(Item.class));
        mockPool.add(() -> mock(Item.class));

        Overflow20 map20 = new Overflow20(mockFactory, mockPool);
        Location initialTreeTile = map20.at(1, 16);

        assertAll("Verify Overflow20 starts with a growable sprout",
                () -> assertNotNull(initialTreeTile.getGround(), "Ground object must be present"),
                () -> assertTrue(initialTreeTile.getGround().asCapability(Growable.class).isPresent(),
                        "Initial stage must be growable"),
                () -> assertTrue(initialTreeTile.getGround().hasStatistic(TreeStatistics.GROW_TURNS),
                        "Sprout stage must track growth turns")
        );
    }

    /**
     * Tests that a sprout on Overflow20 advances to an intermediate growable stage.
     */
    @Test
    void treeEvolution_BoundaryCondition_SproutTransitionsToSaplingSuccessfully() throws Exception {
        GroundCreator mockFactory = mock(GroundCreator.class);
        List<Supplier<Item>> pool = List.of(() -> mock(Item.class), () -> mock(Item.class));

        Overflow20 map20 = new Overflow20(mockFactory, pool);
        Location targetTile = map20.at(1, 16);

        Growable sprout = targetTile.getGround().asCapability(Growable.class)
                .orElseThrow(() -> new AssertionError("Expected initial growable sprout"));
        sprout.grow(targetTile);

        assertAll("Verify sprout advances to another growable stage on Map 20",
                () -> assertTrue(targetTile.getGround().asCapability(Growable.class).isPresent(),
                        "Intermediate stage must remain growable"),
                () -> assertTrue(targetTile.getGround().hasStatistic(TreeStatistics.GROW_TURNS),
                        "Intermediate stage must still track growth turns"),
                () -> assertFalse(targetTile.getGround().asCapability(Teleporter.class).isPresent(),
                        "Intermediate stage must not be a teleporter")
        );
    }

    /**
     * Tests that tree evolution on Overflow20 stops at mature and never reaches monolith stage.
     */
    @Test
    void treeEvolution_EdgeCondition_FullChainCompletesAtMatureWithoutMonolith() throws Exception {
        GroundCreator mockFactory = mock(GroundCreator.class);
        List<Supplier<Item>> pool = List.of(() -> mock(Item.class), () -> mock(Item.class));

        Overflow20 map20 = new Overflow20(mockFactory, pool);
        Location targetTile = map20.at(1, 16);

        Growable sprout = targetTile.getGround().asCapability(Growable.class)
                .orElseThrow(() -> new AssertionError("Expected initial growable sprout"));
        sprout.grow(targetTile);

        Growable sapling = targetTile.getGround().asCapability(Growable.class)
                .orElseThrow(() -> new AssertionError("Expected intermediate growable stage"));
        sapling.grow(targetTile);

        assertAll("Verify Overflow20 evolution terminates before a teleporter monolith",
                () -> assertFalse(targetTile.getGround().asCapability(Teleporter.class).isPresent(),
                        "Final stage on Map 20 must not teleport actors"),
                () -> assertFalse(targetTile.getGround().hasStatistic(TreeStatistics.GROW_TURNS),
                        "Terminal mature stage must stop tracking further growth turns")
        );
    }
}
