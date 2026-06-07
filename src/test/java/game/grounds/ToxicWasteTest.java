package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.items.Inventory;
import game.inventories.BasicInventory;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.vehicles.VehicleAbilities;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ToxicWaste} hazard damage and hover immunity during tick.
 *
 * @author lyan0121
 * @version 1.0
 */
class ToxicWasteTest {
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
     * Tests that {@link ToxicWaste#tick(Location)} damages a walking actor on the tile.
     */
    @Test
    void hazard_NormalCondition_DamagesWalkingActors() {
        ToxicWaste waste = new ToxicWaste();
        Location location = mock(Location.class);
        TestActor victim = new TestActor("Victim", 'V', 50, new BasicInventory());

        when(location.containsAnActor()).thenReturn(true);
        when(location.getActor()).thenReturn(victim);

        waste.tick(location);

        assertNotEquals(50, victim.getStatistic(ActorStatistics.HEALTH));
    }

    /**
     * Tests that actors with {@link VehicleAbilities#HOVER} take no damage from toxic waste.
     */
    @Test
    void hazard_BoundaryCondition_ImmunityForHoveringActors() {
        ToxicWaste waste = new ToxicWaste();
        Location location = mock(Location.class);
        TestActor flyer = new TestActor("Flyer", 'F', 50, new BasicInventory());

        flyer.enableAbility(VehicleAbilities.HOVER);
        when(location.containsAnActor()).thenReturn(true);
        when(location.getActor()).thenReturn(flyer);

        waste.tick(location);

        assertEquals(50, flyer.getStatistic(ActorStatistics.HEALTH));
    }

    /**
     * Tests that ticking an empty tile does not throw and never queries the actor on that tile.
     */
    @Test
    void hazard_EdgeCondition_NoOpOnEmptyTile() {
        ToxicWaste waste = new ToxicWaste();
        Location location = mock(Location.class);

        when(location.containsAnActor()).thenReturn(false);

        assertDoesNotThrow(() -> waste.tick(location));
        verify(location, never()).getActor();
    }
}
