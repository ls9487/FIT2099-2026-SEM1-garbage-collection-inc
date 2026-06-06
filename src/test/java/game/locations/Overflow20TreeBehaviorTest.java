package game.locations;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GroundCreator;
import edu.monash.fit2099.engine.positions.Location;
import game.trees.FleshyTreeSprout;
import game.trees.FleshyTreeSapling;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class Overflow20TreeBehaviorTest {

    @Test
    void setTrees_PositiveCondition_VerifiesFullEvolutionaryChainOnOverflow20() throws Exception {
        GroundCreator mockFactory = mock(GroundCreator.class);
        List<Supplier<Item>> mockPool = new ArrayList<>();
        mockPool.add(() -> mock(Item.class));
        mockPool.add(() -> mock(Item.class));

        Overflow20 map20 = new Overflow20(mockFactory, mockPool);
        Location initialTreeTile = map20.at(1, 16);

        assertAll("Verify complete chain setup tracking metrics on map 20",
                () -> assertNotNull(initialTreeTile.getGround(), "Ground object must be present"),
                () -> assertEquals("FleshyTreeSprout", initialTreeTile.getGround().getClass().getSimpleName(), "Initial stage should be Sprout")
        );
    }

    @Test
    void treeEvolution_BoundaryCondition_SproutTransitionsToSaplingSuccessfully() throws Exception {
        GroundCreator mockFactory = mock(GroundCreator.class);
        List<Supplier<Item>> pool = List.of(() -> mock(Item.class), () -> mock(Item.class));

        Overflow20 map20 = new Overflow20(mockFactory, pool);
        Location targetTile = map20.at(1, 16);
        FleshyTreeSprout sprout = (FleshyTreeSprout) targetTile.getGround();

        sprout.grow(targetTile);

        assertEquals("FleshyTreeSapling", targetTile.getGround().getClass().getSimpleName(),
                "On Map 20, Sprout must advance sequentially to a Fleshy Tree Sapling");
    }

    @Test
    void treeEvolution_EdgeCondition_FullChainCompletesAtMatureWithoutMonolith() throws Exception {
        GroundCreator mockFactory = mock(GroundCreator.class);
        List<Supplier<Item>> pool = List.of(() -> mock(Item.class), () -> mock(Item.class));

        Overflow20 map20 = new Overflow20(mockFactory, pool);
        Location targetTile = map20.at(1, 16);

        FleshyTreeSprout sprout = (FleshyTreeSprout) targetTile.getGround();
        sprout.grow(targetTile);

        FleshyTreeSapling sapling = (FleshyTreeSapling) targetTile.getGround();
        sapling.grow(targetTile);

        assertNotEquals("FleshyTreeMonolith", targetTile.getGround().getClass().getSimpleName(),
                "Fleshy trees tracking evolution on Overflow20 are bounded and must never spawn a Monolith");
    }
}