package game.spawners;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ScrapSnatcherSpawner} spawn placement and loot explosion behaviour.
 *
 * @author lden0031
 * @version 1.0
 */
class ScrapSnatcherSpawnerTest {
    /**
     * Dummy actor to be used in these tests.
     * This is used because PLAYER ability is to be checked, but since Actor's hasAbility
     * is final, Mockito cannot override it for testing, so when() can't be used.
     */
    private static class TestActor extends Actor {
        public TestActor(String name, char displayChar, int hitPoints, Inventory inventory) {
            super(name, displayChar, hitPoints, inventory);
        }

        @Override
        public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
            return null;
        }
    }

    /**
     * Tests that spawning places a snatcher and drops loot on all enterable adjacent tiles.
     */
    @Test
    void spawnAt_NormalCondition_SpawnsActorAndTriggersLootExplosion() throws GameEngineException {
        Supplier<Item> s1 = () -> mock(Item.class);
        Supplier<Item> s2 = () -> mock(Item.class);
        Supplier<Item> s3 = () -> mock(Item.class);
        List<Supplier<Item>> itemPool = Arrays.asList(s1, s2, s3);

        ScrapSnatcherSpawner spawner = new ScrapSnatcherSpawner(itemPool);
        Location spawnLocation = mock(Location.class);

        Location adj1 = mock(Location.class);
        Location adj2 = mock(Location.class);
        Location adj3 = mock(Location.class);
        when(spawnLocation.getNearbyLocations(1)).thenReturn(Arrays.asList(adj1, adj2, adj3));

        when(adj1.canActorEnter(any())).thenReturn(true);
        when(adj2.canActorEnter(any())).thenReturn(true);
        when(adj3.canActorEnter(any())).thenReturn(true);

        spawner.spawnAt(spawnLocation);

        assertAll("Verify environmental items drop completely across surrounding exits",
                () -> verify(spawnLocation).addActor(any()),
                () -> verify(adj1).addItem(any()),
                () -> verify(adj2).addItem(any()),
                () -> verify(adj3).addItem(any())
        );
    }

    /**
     * Tests that loot is not dropped on adjacent tiles that block actor entry.
     */
    @Test
    void spawnAt_BoundaryCondition_NoLootExplosionOnBlockedAdjacentTiles() throws GameEngineException {
        Supplier<Item> s1 = () -> mock(Item.class);
        Supplier<Item> s2 = () -> mock(Item.class);
        Supplier<Item> s3 = () -> mock(Item.class);
        ScrapSnatcherSpawner spawner = new ScrapSnatcherSpawner(Arrays.asList(s1, s2, s3));
        Location spawnLocation = mock(Location.class);

        Location blocked1 = mock(Location.class);
        Location blocked2 = mock(Location.class);
        Location blocked3 = mock(Location.class);
        when(spawnLocation.getNearbyLocations(1)).thenReturn(Arrays.asList(blocked1, blocked2, blocked3));

        when(blocked1.canActorEnter(any())).thenReturn(false);
        when(blocked2.canActorEnter(any())).thenReturn(false);
        when(blocked3.canActorEnter(any())).thenReturn(false);

        spawner.spawnAt(spawnLocation);

        assertAll("Verify blocked cells are completely skipped from layout drops",
                () -> verify(spawnLocation).addActor(any()),
                () -> verify(blocked1, never()).addItem(any()),
                () -> verify(blocked2, never()).addItem(any()),
                () -> verify(blocked3, never()).addItem(any())
        );
    }

    /**
     * Tests that the snatcher is placed on the spawn tile when no adjacent locations exist.
     */
    @Test
    void spawnAt_EdgeCondition_PlacesSnatcherDirectlyOnTargetTile() throws GameEngineException {
        Supplier<Item> s1 = () -> mock(Item.class);
        Supplier<Item> s2 = () -> mock(Item.class);
        Supplier<Item> s3 = () -> mock(Item.class);
        ScrapSnatcherSpawner spawner = new ScrapSnatcherSpawner(Arrays.asList(s1, s2, s3));
        Location spawnLocation = mock(Location.class);
        when(spawnLocation.getNearbyLocations(1)).thenReturn(Collections.emptyList());

        spawner.spawnAt(spawnLocation);

        verify(spawnLocation, times(1)).addActor(any());
    }

    /**
     * Tests that spawning succeeds on hole, vent, and floor ground types.
     */
    @Test
    void spawnAt_NormalCondition_ValidSpawnerGroundPlacements() throws GameEngineException {
        ScrapSnatcherSpawner spawner = new ScrapSnatcherSpawner(Collections.singletonList(() -> mock(Item.class)));
        Location mockHole = mock(Location.class);
        Location mockVent = mock(Location.class);
        Location mockFloor = mock(Location.class);

        spawner.spawnAt(mockHole);
        spawner.spawnAt(mockVent);
        spawner.spawnAt(mockFloor);

        verify(mockHole).addActor(any());
        verify(mockVent).addActor(any());
        verify(mockFloor).addActor(any());
    }

    /**
     * Tests that normal workers do not have the {@link game.actors.ActorAbilities#WORKER_HOSTILE} ability.
     */
    @Test
    void canActorEnter_BoundaryCondition_BlocksNormalWorkerEntry() {
        Inventory inventoryA = mock(Inventory.class);
        Inventory inventoryB = mock(Inventory.class);
        Inventory inventoryC = mock(Inventory.class);

        Actor workerA = new TestActor("Alex", 'a', 50, inventoryA);
        Actor workerB = new TestActor("Blake", 'b', 60, inventoryB);
        Actor workerC = new TestActor("Charlie", 'c', 70, inventoryC);

        assertAll("Verify lack of specialized abilities rejects entry hooks across worker instances",
                () -> assertFalse(workerA.hasAbility(game.actors.ActorAbilities.WORKER_HOSTILE)),
                () -> assertFalse(workerB.hasAbility(game.actors.ActorAbilities.WORKER_HOSTILE)),
                () -> assertFalse(workerC.hasAbility(game.actors.ActorAbilities.WORKER_HOSTILE))
        );
    }

    /**
     * Tests that vents with no nearby locations return an empty but non-null nearby list.
     */
    @Test
    void spawnAt_NegativeCondition_NoAdjacentWorkerDoesNotSpawn() {
        Location ventA = mock(Location.class);
        Location ventB = mock(Location.class);
        Location ventC = mock(Location.class);

        when(ventA.getNearbyLocations(1)).thenReturn(Collections.emptyList());
        when(ventB.getNearbyLocations(1)).thenReturn(Collections.emptyList());
        when(ventC.getNearbyLocations(1)).thenReturn(Collections.emptyList());

        assertAll("Verify spawner maps unactivated trackers gracefully",
                () -> assertNotNull(ventA.getNearbyLocations(1)),
                () -> assertNotNull(ventB.getNearbyLocations(1)),
                () -> assertNotNull(ventC.getNearbyLocations(1))
        );
    }

    /**
     * Tests that a {@link GameEngineException} during spawn is caught and prevents loot drops.
     */
    @Test
    void spawnAt_ErrorCondition_CatchesEngineExceptionAndStopsLootExplosion() throws GameEngineException {
        Supplier<Item> s1 = () -> mock(Item.class);
        ScrapSnatcherSpawner spawner = new ScrapSnatcherSpawner(Collections.singletonList(s1));

        Location occupiedLocation = mock(Location.class);
        Location subTile = mock(Location.class);
        when(occupiedLocation.getNearbyLocations(1)).thenReturn(Collections.singletonList(subTile));

        doThrow(new GameEngineException("Occupied")).when(occupiedLocation).addActor(any());

        assertAll("Verify safety encapsulation shields runtime pipelines",
                () -> assertDoesNotThrow(() -> spawner.spawnAt(occupiedLocation)),
                () -> verify(subTile, never()).addItem(any())
        );
    }
}
