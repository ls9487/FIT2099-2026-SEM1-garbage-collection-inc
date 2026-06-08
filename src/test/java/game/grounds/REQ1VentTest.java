package game.grounds;

import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GroundCreator;
import game.actors.ContractedWorker;
import game.inventories.BasicInventory;
import game.items.ItemStatistics;
import game.spawners.SlimeSpawner;
import game.spawners.Spawner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Vent.cutBy()(REQ1).
 * Tests the behaviour of cutting a Vent with a Plasma Cutter.
 * Checks for:
 *  - IndustrialFan is dropped on the vent tile after cutting
 *  - The vent tile transforms into a Floor (passable) after cutting
 *  - An Undead is spawned on the exact tile after cutting
 *  - The result message contains key information about the cut
 *  - Cutting a vent when the tile is occupied does not crash
 *
 * @author eche0116
 */
public class REQ1VentTest {

    private GameMap map;
    private ContractedWorker actor;
    private Vent vent;

    /**
     * Sets up a small real GameMap with:
     *   _ _ _
     *   _ V _    (V is the Vent at x=1, y=1)
     *   _ _ _
     * Actor is placed at (1, 0), adjacent to the vent.
     * Vent is created manually with a SlimeSpawner as its tickSpawner.
     */
    @BeforeEach
    public void setUp() throws Exception {
        GroundCreator groundCreator = new DefaultGroundCreator();
        groundCreator.registerGround('_', Floor::new);
        groundCreator.registerGround('V', () -> {
            List<Spawner> spawners = new ArrayList<>();
            spawners.add(new SlimeSpawner());
            return new Vent(spawners);
        });

        map = new GameMap("TestMap", groundCreator, Arrays.asList(
                "___",
                "_V_",
                "___"
        ));

        // Actor placed north of the vent at (1, 0)
        Inventory inventory = new BasicInventory();
        actor = new ContractedWorker("Worker", 'W', 100, inventory);
        map.at(1, 0).addActor(actor);

        // Get the vent instance at (1, 1)
        vent = (Vent) map.at(1, 1).getGround();
    }

    /**
     * Cutting the vent drops an IndustrialFan on the vent tile.
     * IndustrialFan has weight 5.
     */
    @Test
    public void cuttingVentDropsIndustrialFan() {
        Location ventLocation = map.at(1, 1);
        vent.cutBy(actor, map, ventLocation);

        assertTrue(ventLocation.getItems().size() > 0,
                "Cutting the vent should drop an IndustrialFan on the tile.");
        assertEquals(5,
                ventLocation.getItems().get(0).getStatistic(ItemStatistics.WEIGHT),
                "Dropped item should have weight 5 (IndustrialFan).");
    }

    /**
     * Cutting the vent transforms the tile into a Floor.
     * After cutBy(), the ground at (1,1) should allow actors to enter.
     * Floor.canActorEnter() returns true; Vent.canActorEnter() returns false (no HOVER).
     */
    @Test
    public void cuttingVentTransformsTileToFloor() {
        Location ventLocation = map.at(1, 1);
        vent.cutBy(actor, map, ventLocation);

        assertTrue(ventLocation.getGround().canActorEnter(actor),
                "After cutting, the vent tile should become a Floor (passable by actors).");
    }

    /**
     * Cutting the vent spawns an Undead on the exact tile.
     * After cutBy(), the vent location should contain an actor (the spawned Undead).
     */
    @Test
    public void cuttingVentSpawnsActorOnTile() {
        Location ventLocation = map.at(1, 1);
        vent.cutBy(actor, map, ventLocation);

        assertTrue(ventLocation.containsAnActor(),
                "Cutting the vent should spawn an Undead on the exact tile.");
    }

    /**
     * Cut result message contains key information.
     * The string should mention the vent and industrial fan.
     */
    @Test
    public void cuttingVentResultMessageContainsKeyInfo() {
        Location ventLocation = map.at(1, 1);
        String result = vent.cutBy(actor, map, ventLocation);

        assertTrue(result.contains("cuts") || result.toLowerCase().contains("vent"),
                "Result message should mention the cutting action or vent.");
        assertTrue(result.toLowerCase().contains("fan") || result.toLowerCase().contains("industrial"),
                "Result message should mention the Industrial Fan being dropped.");
    }

    /**
     * Cutting the same vent tile twice does not crash.
     * After the first cut, the tile is a Floor, the second cut
     * should not throw even though the vent is gone.
     */
    @Test
    public void cuttingAlreadyCutVentDoesNotCrash() {
        Location ventLocation = map.at(1, 1);
        vent.cutBy(actor, map, ventLocation); // first cut tile becomes Floor

        assertDoesNotThrow(() -> vent.cutBy(actor, map, ventLocation),
                "Cutting an already-cut vent tile should not throw.");
    }
}