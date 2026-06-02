package game.projectiles;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.items.ItemStatistics;

/**
 * Projectile is an abstract base class representing a fired projectile.
 * All projectiles have a defined destination that they'll travel towards.
 * These also have a velocity statistic that affects how fast they travel.
 * Projectiles will be stopped if they reach their destination, but can also
 * be intercepted by any actor or any ground that blocks it.
 * Regardless, upon hitting, projectiles activate their own defined effect.
 *
 * @author echu0057
 */
public abstract class Projectile extends Item {

    private Location destination;

    /**
     * NOTE: This exists to denote whether tick() should be skipped for it.
     * Because this is a moving item, and how GameMap ticks its locations,
     * moving an item right/down would cause GameMap to tick it again in the same turn.
     * This is to make sure it only acts once per turn.
     */
    private boolean locked;

    /**
     * Constructor for the Projectile class.
     * All projectiles have a velocity statistic.
     * @param name The name of the projectile.
     * @param displayChar The display char of the projectile on the map.
     * @param velocity How many tiles the projectile can travel through per turn.
     * @param destination The location where the projectile will travel toward.
     */
    protected Projectile(String name, char displayChar, int velocity, Location destination) {
        super(name, displayChar);
        this.addNewStatistic(ItemStatistics.VELOCITY, new BaseStatistic(velocity));
        this.destination = destination;
        this.locked = false;
    }

    /**
     * Projectiles will travel towards their destination when the tick method is called.
     * @param location The location of the projectile.
     */
    @Override
    public void tick(Location location) {
        // If it's locked, it means it's already done something in this turn, so skip it.
        if (locked) {
            locked = false;
            return;
        }

        Location currentLocation = location;
        // Update the location (no. of times based on velocity) of the projectile since it's moving.
        for (int i = 0; i < this.getStatistic(ItemStatistics.VELOCITY); i++) {
            // Update the location by moving one step closer to the destination.
            currentLocation = getCloserDestination(currentLocation);
            // Check if the projectile is going to be stopped and activate the hit effect.
            if (isStopped(currentLocation)) {
                onHitEffect(currentLocation);
                // Remove the projectile from the ORIGINAL location (since we didn't actually move it).
                location.removeItem(this);
            }
        }
        // Since the projectile was not stopped, move the projectile to the new location.
        location.removeItem(this);
        currentLocation.addItem(this);
        // BUT, due to how the GameMap works it could call tick() on this item again in the same turn.
        // This will happen if the projectile was moved somewhere down-left, down, down-right or right.
        // Hence the projectile needs to be locked.
        if (currentLocation.y() > location.y() ||
                (currentLocation.y() == location.y() && currentLocation.x() > location.x())) {
            locked = true;
        }
    }

    /**
     * Get an adjacent location that's closer to the destination.
     * Tries to get an adjacent location that shortens the Manhattan distance as much as possible.
     * Diagonal movement is allowed.
     * Adapted from the mars example.
     * @param location The current location of the projectile.
     * @return A location the projectile should move towards.
     */
    protected Location getCloserDestination(Location location) {
        // This method only moves the projectile one tile each time it's called.
        // Keep track of the current distance and where the projectile might be going.
        int currentDistance = getDistance(location, destination);
        // Note the currentTowards will be overwritten unless you're already at the destination.
        Location currentTowards = location;

        // Check each adjacent tile and compute their Manhattan distance.
        for (Exit exit : location.getExits()) {
            Location surrounding = exit.getDestination();
            int newDistance = getDistance(surrounding, destination);
            // If this surrounding has a shorter distance, it should be taken.
            // currentDistance/currentTowards could be reassigned multiple times in this loop.
            if (newDistance < currentDistance) {
                currentDistance = newDistance;
                currentTowards = surrounding;
            }
        }
        // currentTowards will have a location with the shortest Manhattan distance of those tiles.
        return currentTowards;
    }

    /**
     * Indicates whether the projectile, at this location, will be stopped.
     * Either the projectile reached its destination, or was intercepted by an actor
     * or ground that blocks it.
     * Could override this if you want different conditions for stopping (e.g. ignoring walls).
     * @return A boolean indicating whether the projectile stops at the location.
     */
    public boolean isStopped(Location location) {
        // Note the == comparison with location and destination. This should be fine (instead
        // of comparing their x,y values) since each position is represented by the same location object.
        return location.containsAnActor() || location.getGround().blocksThrownObjects()
                || location == destination;
    }

    /**
     * Called when the projectile hits its final destination or gets blocked on the way.
     * Subclasses of Projectile are to define what happens when a projectile impacts.
     * @param hitLocation The location of the projectile's impact.
     */
    protected abstract void onHitEffect(Location hitLocation);

    /**
     * Compute the Manhattan (sum of x-difference + y-difference) distance between two locations.
     * To be used internally within this class only, as it's used to gauge distance.
     * Taken from the mars example.
     * @param start The start location.
     * @param end The end location.
     * @return The Manhattan distance between the two points.
     */
    private int getDistance(Location start, Location end) {
        return Math.abs(start.x() - end.x()) + Math.abs(start.y() - end.y());
    }


}
