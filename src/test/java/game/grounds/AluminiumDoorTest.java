package game.grounds;

import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GroundCreator;
import game.actors.ContractedWorker;
import game.inventories.BasicInventory;
import game.items.ItemStatistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AluminiumDoor.cutBy()(REQ1).
 * Tests the behaviour of cutting an Aluminium Door with a Plasma Cutter.
 *
 * @author eche0116
 */
public class AluminiumDoorTest {

    private GameMap map;
    private ContractedWorker actor;
    private AluminiumDoor door;

    /**
     * Sets up a small real GameMap with
     *   _ _ _
     *   _ = _    (= is the AluminiumDoor at x=1, y=1)
     *   _ _ _
     * Actor is placed at (1, 0), adjacent to the door.
     */
    @BeforeEach
    public void setUp() throws Exception {
        GroundCreator groundCreator = new DefaultGroundCreator();
        groundCreator.registerGround('_', Floor::new);
        groundCreator.registerGround('=', AluminiumDoor::new);

        map = new GameMap("TestMap", groundCreator, Arrays.asList(
                "___",
                "_=_",
                "___"
        ));

        // Actor placed north of the door at (1, 0)
        Inventory inventory = new BasicInventory();
        actor = new ContractedWorker("Worker", 'W', 100, inventory);
        map.at(1, 0).addActor(actor);

        // Get the door instance at (1, 1)
        door = (AluminiumDoor) map.at(1, 1).getGround();
    }

    /**
     * Typical: Cutting the door drops AluminiumScrap on the door tile.
     * After cutBy(), the tile at (1,1) should have at least one item.
     * AluminiumScrap has weight 2
     */
    @Test
    public void cuttingDoorDropsAluminiumScrap() {
        Location doorLocation = map.at(1, 1);
        door.cutBy(actor, map, doorLocation);

        assertTrue(doorLocation.getItems().size() > 0,
                "Cutting the door should drop AluminiumScrap on the tile.");
        assertEquals(2,
                doorLocation.getItems().get(0).getStatistic(ItemStatistics.WEIGHT),
                "Dropped item should have weight 2 (AluminiumScrap).");
    }

    /**
     * Cutting the door transforms the tile into a Floor.
     * After cutBy(), the ground at (1,1) should allow actors to enter.
     * Floor.canActorEnter() returns true, AluminiumDoor.canActorEnter() returns false.
     */
    @Test
    public void cuttingDoorTransformsTileToFloor() {
        Location doorLocation = map.at(1, 1);
        door.cutBy(actor, map, doorLocation);

        assertTrue(doorLocation.getGround().canActorEnter(actor),
                "After cutting, the tile should become a Floor (passable by actors).");
    }

    /**
     * Cut result message contains key information.
     * The string should mention the actor cutting and scrap being produced.
     */
    @Test
    public void cuttingDoorResultMessageContainsKeyInfo() {
        Location doorLocation = map.at(1, 1);
        String result = door.cutBy(actor, map, doorLocation);

        assertTrue(result.contains("cuts"),
                "Result message should mention the cutting action.");
        assertTrue(result.toLowerCase().contains("scrap"),
                "Result message should mention scrap being produced.");
    }

    /**
     * Cutting a door with no adjacent actors does not crash.
     * When no actors are adjacent, the explosion (if triggered) has no targets.
     * The operation should complete without throwing an exception.
     */
    @Test
    public void cuttingDoorWithNoAdjacentActorsDoesNotCrash() {
        // Move actor away, place them at (0,0), far from the door
        map.moveActor(actor, map.at(0, 0));
        Location doorLocation = map.at(1, 1);

        assertDoesNotThrow(() -> door.cutBy(actor, map, doorLocation),
                "Cutting a door with no adjacent actors should not throw.");
    }

    /**
     * Invalid test. Cutting the same tile twice does not crash.
     * After the first cut, the tile is a Floor cutting it again
     * should not throw even though it's no longer a door.
     */
    @Test
    public void cuttingAlreadyCutTileDoesNotCrash() {
        Location doorLocation = map.at(1, 1);
        door.cutBy(actor, map, doorLocation); // first cut tile becomes Floor

        assertDoesNotThrow(() -> door.cutBy(actor, map, doorLocation),
                "Cutting an already-cut tile should not throw.");
    }
}