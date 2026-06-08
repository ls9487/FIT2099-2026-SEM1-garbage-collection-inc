package game.items;

import edu.monash.fit2099.engine.actors.ActorLocationsIterator;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GroundCreator;
import game.actors.ContractedWorker;
import game.grounds.Floor;
import game.inventories.BasicInventory;
import game.spawners.UndeadSpawner;
import game.spawners.Spawner;
import game.statuses.PoisonStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AlienCube.cutBy()(REQ1).
 * Tests the behaviour of cutting an Alien Cube with a Plasma Cutter.
 *
 * NOTE: GameMap.actorLocations is only set when a map is added to a World.
 * Since tests don't use a World, actorLocations is injected via reflection
 * in setUp() so that addActor() works correctly.
 *
 * @author eche0116
 */
public class AlienCubeTest {

    private GameMap map;
    private ContractedWorker actor;
    private AlienCube alienCube;
    private Location actorLocation;

    /**
     * Sets up a small real GameMap:
     *   _ _ _
     *   _ _ _
     *   _ _ _
     * Actor placed at (1,1). AlienCube added to actor's inventory.
     * actorLocations is injected via reflection because GameMap only
     * initialises it when registered with a World.
     */
    @BeforeEach
    public void setUp() throws Exception {
        // Suppress Display output during tests
        System.setOut(new PrintStream(new OutputStream() {
            public void write(int b) { /* discard */ }
        }));

        GroundCreator groundCreator = new DefaultGroundCreator();
        groundCreator.registerGround('_', Floor::new);

        map = new GameMap("TestMap", groundCreator, Arrays.asList(
                "___",
                "___",
                "___"
        ));

        // Inject a real ActorLocationsIterator so addActor() doesn't NPE.
        // GameMap.actorLocations is only set by World, which we don't use in tests.
        Field actorLocationsField = GameMap.class.getDeclaredField("actorLocations");
        actorLocationsField.setAccessible(true);
        actorLocationsField.set(map, new ActorLocationsIterator());

        Inventory inventory = new BasicInventory();
        actor = new ContractedWorker("Worker", 'W', 100, inventory);
        actorLocation = map.at(1, 1);
        actorLocation.addActor(actor);

        List<Spawner> spawners = new ArrayList<>();
        spawners.add(new UndeadSpawner());
        alienCube = new AlienCube(spawners);

        actor.getInventory().add(alienCube);
    }

    /**
     * Cutting the Alien Cube drops an AlienArtifact at the actor's location.
     * AlienArtifact has weight 1.
     */
    @Test
    public void cuttingAlienCubeDropsAlienArtifact() {
        alienCube.cutBy(actor, map, actorLocation);

        assertTrue(actorLocation.getItems().size() > 0,
                "Cutting the Alien Cube should drop an AlienArtifact at the actor's location.");
        assertEquals(1,
                actorLocation.getItems().get(0).getStatistic(ItemStatistics.WEIGHT),
                "Dropped item should have weight 1 (AlienArtifact).");
    }

    /**
     * Cutting the Alien Cube removes it from the actor's inventory.
     * Inventory size should decrease by 1 after cutting.
     */
    @Test
    public void cuttingAlienCubeRemovesCubeFromInventory() {
        int sizeBefore = actor.getInventory().getItems().size();
        alienCube.cutBy(actor, map, actorLocation);
        int sizeAfter = actor.getInventory().getItems().size();

        assertEquals(sizeBefore - 1, sizeAfter,
                "Cutting the Alien Cube should remove it from the actor's inventory.");
    }

    /**
     * Cutting the Alien Cube applies PoisonStatus to the worker.
     * ContractedWorker implements Poisonable (via EclipseActor), so
     * cutBy() calls actor.addStatus
     * Checked by actor.hasStatus(PoisonStatus.class).
     */
    @Test
    public void cuttingAlienCubeAppliesPoisonStatus() {
        alienCube.cutBy(actor, map, actorLocation);
        assertTrue(actor.hasStatus(PoisonStatus.class),
                "Worker should have PoisonStatus applied after cutting the Alien Cube.");
    }

    /**
     * Cut result message contains key information.
     * Should mention cutting, artifact, and poison.
     */
    @Test
    public void cuttingAlienCubeResultMessageContainsKeyInfo() {
        String result = alienCube.cutBy(actor, map, actorLocation);

        assertTrue(result.contains("cuts") || result.toLowerCase().contains("alien cube"),
                "Result should mention the cutting action or Alien Cube.");
        assertTrue(result.toLowerCase().contains("artifact"),
                "Result should mention the Alien Artifact being produced.");
        assertTrue(result.toLowerCase().contains("poison"),
                "Result should mention the poison effect.");
    }

    /**
     * Cutting the cube when it's not in the actor's inventory does not crash.
     * Inventory removal of a non-existent item should be handled gracefully.
     */
    @Test
    public void cuttingAlienCubeNotInInventoryDoesNotCrash() {
        actor.getInventory().remove(alienCube); // remove before cutting

        assertDoesNotThrow(() -> alienCube.cutBy(actor, map, actorLocation),
                "Cutting an Alien Cube not in inventory should not throw.");
    }
}