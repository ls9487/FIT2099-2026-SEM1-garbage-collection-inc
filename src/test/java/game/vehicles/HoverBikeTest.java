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

class HoverBikeTest {

    @Test
    void hoverBlast_NormalCondition_ClearsGroundItemsInRadius() {
        HoverBike hoverBike = new HoverBike();
        Actor rider = mock(Actor.class);
        GameMap gameMap = mock(GameMap.class);
        Location blastCentre = mock(Location.class);

        // Case 1: Loose ground items get completely removed within the range profile
        when(gameMap.locationOf(rider)).thenReturn(blastCentre);
        Location affectedLocation = mock(Location.class);
        when(blastCentre.getNearbyLocations(3)).thenReturn(List.of(affectedLocation));

        Item looseGroundItem = mock(Item.class);
        when(affectedLocation.getItems()).thenReturn(List.of(looseGroundItem));
        when(blastCentre.getNearbyLocations(5)).thenReturn(new ArrayList<>());

        String response = hoverBike.hoverBlast(rider, gameMap);

        // Verification of item destruction rules
        verify(affectedLocation).removeItem(looseGroundItem);
        assertTrue(response.contains("uses hover blast"));
    }

    @Test
    void initialize_BoundaryCondition_VerifiesHoverAbilityRegistered() {
        HoverBike hoverBike = new HoverBike();

        // Case 2: Assures system configuration properties are set properly on boot
        assertTrue(hoverBike.hasAbility(VehicleAbilities.HOVER));
        assertEquals("Hover Bike", hoverBike.toString());
    }

    @Test
    void hoverBlast_NegativeCondition_HandlesEmptyMapScenariosGracefully() {
        HoverBike hoverBike = new HoverBike();
        Actor rider = mock(Actor.class);
        GameMap gameMap = mock(GameMap.class);
        Location blastCentre = mock(Location.class);

        // Case 3: Empty tile profile checks execute cleanly without hitting null pointers
        when(gameMap.locationOf(rider)).thenReturn(blastCentre);
        when(blastCentre.getNearbyLocations(3)).thenReturn(new ArrayList<>());
        when(blastCentre.getNearbyLocations(5)).thenReturn(new ArrayList<>());

        String response = hoverBike.hoverBlast(rider, gameMap);

        assertNotNull(response);
        assertTrue(response.contains("uses hover blast"));
    }
}