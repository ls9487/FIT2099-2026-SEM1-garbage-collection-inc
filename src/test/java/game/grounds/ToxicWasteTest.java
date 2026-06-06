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

class ToxicWasteTest {

    private static class TestActor extends Actor {
        public TestActor(String name, char displayChar, int hitPoints, Inventory inventory) {
            super(name, displayChar, hitPoints, inventory);
        }

        @Override
        public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
            return null;
        }
    }

    @Test
    void hazard_NormalCondition_DamagesWalkingActors() {
        ToxicWaste waste = new ToxicWaste();
        Location location = mock(Location.class);
        TestActor victim = new TestActor("Victim", 'V', 50, new BasicInventory());

        when(location.containsAnActor()).thenReturn(true);
        when(location.getActor()).thenReturn(victim);

        // Case 1: Damage calculation path reduces overall health profile
        waste.tick(location);

        // Assert that the real internal attributes collections were correctly mutated
        assertNotEquals(50, victim.getStatistic(ActorStatistics.HEALTH));
    }

    @Test
    void hazard_BoundaryCondition_ImmunityForHoveringActors() {
        ToxicWaste waste = new ToxicWaste();
        Location location = mock(Location.class);
        TestActor flyer = new TestActor("Flyer", 'F', 50, new BasicInventory());

        flyer.enableAbility(VehicleAbilities.HOVER);
        when(location.containsAnActor()).thenReturn(true);
        when(location.getActor()).thenReturn(flyer);

        // Case 2: Hovering status provides perfect immunity to environmental damage loops
        waste.tick(location);

        assertEquals(50, flyer.getStatistic(ActorStatistics.HEALTH));
    }

    @Test
    void hazard_EdgeCondition_NoOpOnEmptyTile() {
        ToxicWaste waste = new ToxicWaste();
        Location location = mock(Location.class);

        when(location.containsAnActor()).thenReturn(false);

        // Case 3: Scanning an empty tile processes smoothly with no mutations
        assertDoesNotThrow(() -> waste.tick(location));
        verify(location, never()).getActor();
    }
}