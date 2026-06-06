package game.locations;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GroundCreator;
import edu.monash.fit2099.engine.positions.Location;
import game.trees.FleshyTreeSprout;
import game.trees.FleshyTreeMature;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class Deprecated99TreeBehaviorTest {

    @Test
    void setTrees_PositiveCondition_VerifiesCorrectTreeHierarchyOnMoon99() throws Exception {
        GroundCreator mockFactory = mock(GroundCreator.class);
        List<Supplier<Item>> mockPool = new ArrayList<>();
        mockPool.add(() -> mock(Item.class));
        mockPool.add(() -> mock(Item.class));

        Deprecated99 map99 = new Deprecated99(mockFactory, mockPool);
        Location treeLocation = map99.at(1, 16);

        // Combined string identity and character validation from friend's test runner checks
        FleshyTreeSprout sprout = (FleshyTreeSprout) treeLocation.getGround();

        assertAll("Verify Deprecated99 specific configuration and display icons",
                () -> assertNotNull(treeLocation.getGround(), "Tree ground object must exist"),
                () -> assertEquals("FleshyTreeSprout", treeLocation.getGround().getClass().getSimpleName(), "Should spawn a Sprout initially"),
                () -> assertEquals('y', sprout.getDisplayChar(), "Sprout display character must be 'y'")
        );
    }

    @Test
    void treeEvolution_BoundaryCondition_DirectlyLinksSproutToMatureSkippingSapling() throws Exception {
        GroundCreator mockFactory = mock(GroundCreator.class);
        List<Supplier<Item>> pool = List.of(() -> mock(Item.class), () -> mock(Item.class));

        Deprecated99 map99 = new Deprecated99(mockFactory, pool);
        Location testLocation = map99.at(1, 16);
        FleshyTreeSprout sprout = (FleshyTreeSprout) testLocation.getGround();

        sprout.grow(testLocation);

        assertEquals("FleshyTreeMature", testLocation.getGround().getClass().getSimpleName(),
                "On Map 99, Sprout must bypass Sapling and transition directly to Mature");
    }

    @Test
    void treeEvolution_EdgeCondition_MatureEvolvesIntoFleshyMonolithOnMap99() throws Exception {
        GroundCreator mockFactory = mock(GroundCreator.class);
        List<Supplier<Item>> pool = List.of(() -> mock(Item.class), () -> mock(Item.class));

        Deprecated99 map99 = new Deprecated99(mockFactory, pool);
        Location testLocation = map99.at(1, 16);

        FleshyTreeSprout sprout = (FleshyTreeSprout) testLocation.getGround();
        sprout.grow(testLocation);

        FleshyTreeMature matureTree = (FleshyTreeMature) testLocation.getGround();
        matureTree.grow(testLocation);

        assertEquals("FleshyTreeMonolith", testLocation.getGround().getClass().getSimpleName(),
                "A mature tree on Map 99 must grow into a terminal Fleshy Tree Monolith");
    }
}