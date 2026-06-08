package game.vehicles;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link HoverBike} hover blast and hover ability registration.
 *
 * @author lyan0121
 * @version 1.0
 */
class HoverBikeTest {

    /**
     * Tests that hover blast removes ground items within the blast radius.
     */
    @Test
    void hoverBlast_NormalCondition_ClearsGroundItemsInRadius() {
        HoverBike hoverBike = new HoverBike();
        Actor rider = mock(Actor.class);
        GameMap gameMap = mock(GameMap.class);
        Location blastCentre = mock(Location.class);

        when(gameMap.locationOf(rider)).thenReturn(blastCentre);
        Location affectedLocation = mock(Location.class);
        when(blastCentre.getNearbyLocations(3)).thenReturn(List.of(affectedLocation));

        Item looseGroundItem = mock(Item.class);
        when(affectedLocation.getItems()).thenReturn(List.of(looseGroundItem));
        when(blastCentre.getNearbyLocations(5)).thenReturn(new ArrayList<>());

        String response = hoverBike.hoverBlast(rider, gameMap);

        verify(affectedLocation).removeItem(looseGroundItem);
        assertTrue(response.contains("uses hover blast"));
    }

    /**
     * Tests that a hover bike registers {@link VehicleAbilities#HOVER} and has the correct name.
     */
    @Test
    void initialize_BoundaryCondition_VerifiesHoverAbilityRegistered() {
        HoverBike hoverBike = new HoverBike();

        assertTrue(hoverBike.hasAbility(VehicleAbilities.HOVER));
        assertEquals("Hover Bike", hoverBike.toString());
    }

    /**
     * Tests that hover blast completes successfully when no nearby locations are affected.
     */
    @Test
    void hoverBlast_NegativeCondition_HandlesEmptyMapScenariosGracefully() {
        HoverBike hoverBike = new HoverBike();
        Actor rider = mock(Actor.class);
        GameMap gameMap = mock(GameMap.class);
        Location blastCentre = mock(Location.class);

        when(gameMap.locationOf(rider)).thenReturn(blastCentre);
        when(blastCentre.getNearbyLocations(3)).thenReturn(new ArrayList<>());
        when(blastCentre.getNearbyLocations(5)).thenReturn(new ArrayList<>());

        String response = hoverBike.hoverBlast(rider, gameMap);

        assertNotNull(response);
        assertTrue(response.contains("uses hover blast"));
    }
}
