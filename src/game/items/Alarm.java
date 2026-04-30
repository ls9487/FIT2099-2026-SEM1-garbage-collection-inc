package game.items;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.statuses.AlarmStatus;
import game.statuses.Alarmable;

import java.util.ArrayList;
import java.util.List;

/**
 * Alarm represents an alarm that activates upon being picked up.
 * This alarm will span the entire map, alarming various alarmable entities, and lasts 10 turns.
 *
 * @author echu0057
 */
public class Alarm extends EclipseItem {

    // Keep track of whether it's currently inside an inventory.
    private boolean insideInventory;

    /**
     * Constructor for the Alarm class.
     * Has a weight of 1 unit. Has a cooldown of 15 turns, but starts off live.
     */
    public Alarm() {
        super("Alarm", 'A', 1);
        this.addNewStatistic(ItemStatistics.COOLDOWN, new BaseStatistic(15));
        this.modifyStatistic(ItemStatistics.COOLDOWN, StatisticOperations.UPDATE, 0);
        this.insideInventory = false;
    }

    /**
     * Inform the alarm of the passage of time ONLY when being carried.
     * So once per turn, tick down the cooldown.
     * @param currentLocation The location of the actor carrying this Item.
     * @param actor The actor carrying this Item.
     */
    @Override
    public void tick(Location currentLocation, Actor actor) {
        // If this method is called and insideInventory is false, that means it was picked up.
        // And if the alarm is live, the whole map will be alarmed.
        if (!this.insideInventory && this.isLive()) {
            this.alarmMapWide(currentLocation, actor);
            // Reset the cooldown to max (which is 15).
            this.modifyStatistic(ItemStatistics.COOLDOWN, StatisticOperations.UPDATE,
                    this.getMaximumStatistic(ItemStatistics.COOLDOWN));
        }

        // Cooldown ticks down while in the inventory.
        this.modifyStatistic(ItemStatistics.COOLDOWN, StatisticOperations.DECREASE, 1);
        // Subsequent ticks inside the inventory won't continue to trip the alarm.
        this.insideInventory = true;
    }

    /**
     * Inform the alarm on the ground of the passage of time.
     * This method is called once per turn, ticking down its cooldown.
     * @param currentLocation The location of the ground on which the alarm is on.
     */
    @Override
    public void tick(Location currentLocation) {
        // Cooldown still continues ticking down while on the ground.
        this.modifyStatistic(ItemStatistics.COOLDOWN, StatisticOperations.DECREASE, 1);
        // Also, if the alarm is dropped, this'll switch to false.
        this.insideInventory = false;
    }

    /**
     * Indicates whether the alarm is able to be tripped.
     * To be used only internally within this class.
     * @return A boolean indicating whether the alarm can be tripped.
     */
    private boolean isLive() {
        return this.getStatistic(ItemStatistics.COOLDOWN) == 0;
    }

    /**
     * Alarms the entire map based on which map the actor who tripped it is in.
     * This means all (alarmable) entities at every location will receive the alarmed status effect.
     * Lasts for 10 turns.
     * @param currentLocation The current location where the alarm is being carried.
     * @param alarmTripper The guilty actor who tripped the alarm.
     */
    private void alarmMapWide(Location currentLocation, Actor alarmTripper) {
        // Get the GameMap this location is in.
        GameMap map = currentLocation.map();
        // Iterate through all locations in the map.
        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                // A location to apply the alarm on.
                Location alarmLocation = map.at(x, y);
                // Keep track of a list of all entities at this location, process them later.
                List<GameEntity> locationGameEntities = new ArrayList<>();
                // Add the ground here.
                locationGameEntities.add(alarmLocation.getGround());
                // Add the actor here if there's any. And every item in their inventory.
                if (alarmLocation.containsAnActor()) {
                    Actor alarmedActor = alarmLocation.getActor();
                    locationGameEntities.add(alarmedActor);
                    locationGameEntities.addAll(alarmedActor.getInventory().getItems());
                }
                // Lastly, add every item on the ground at the current location.
                locationGameEntities.addAll(alarmLocation.getItems());
                // Finally, apply the status effect to each entity found here.
                for (GameEntity alarmedEntity : locationGameEntities) {
                    Alarmable alarmable = alarmedEntity.asCapability(Alarmable.class).orElse(null);
                    if (alarmable != null) {
                        alarmedEntity.addStatus(new AlarmStatus(10, alarmTripper, alarmable));
                    }
                }
            }
        }

    }

}
