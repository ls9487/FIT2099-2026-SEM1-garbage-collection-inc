package game.vehicles;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.ConsumeAction;
import game.items.Consumable;

/**
 * Rideable alien beast that grants VehicleAbilities.PURIFY
 * While mounted, automatically consumes Consumable ground under the rider each turn.
 *
 * @author lyan0121
 * @version 1.0
 */
public class AlienBeast extends Rideable {
    private static final String NAME = "Alien Beast";
    private static final char DISPLAY_CHAR = 'Ɐ';

    /**
     * Creates an alien beast with purify ability enabled.
     */
    public AlienBeast() {
        super(NAME, DISPLAY_CHAR);
        enableAbility(VehicleAbilities.PURIFY);
    }

    /**
     * Each turn while mounted, consumes consumable ground at the rider's location.
     *
     * @param currentLocation the rider's current location
     * @param actor the actor riding this beast
     */
    @Override
    public void tick(Location currentLocation, Actor actor) {
        Display display = new Display();
        Consumable consumable = currentLocation.getGround().asCapability(Consumable.class).orElse(null);
        if (consumable != null) {
            display.println(String.format("%s is too hungry and consumed %s", this, consumable));
            new ConsumeAction(consumable).execute(actor, currentLocation.map());
        }
    }
}
