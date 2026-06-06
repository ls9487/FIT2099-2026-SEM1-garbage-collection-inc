package game.grounds;

import edu.monash.fit2099.engine.positions.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Shared registry of linked magic circle sites used for random within-map jumps.
 *
 * @author eche0116
 * @version 1.0
 */
public final class MagicCircleGroup {

    private static final Random random = new Random();

    private final List<Location> sites;

    /**
     * Creates an empty group of linked magic circle sites.
     */
    public MagicCircleGroup() {
        sites = new ArrayList<>();
    }
    /**
     * Registers a circle location after it has been placed on a map.
     *
     * @param site circle tile
     */
    public void register(Location site) {
        sites.add(site);
    }

    /**
     * Chooses a different circle on the same map as the source, if possible.
     *
     * @param from the circle currently being used
     * @return a destination circle tile, or null when no peer exists
     */
    public Location randomPeerExcluding(Location from) {
        List<Location> peers = new ArrayList<>();
        for (Location candidate : sites) {
            if (candidate != from && candidate.map() == from.map() && !candidate.containsAnActor()) {
                peers.add(candidate);
            }
        }
        if (peers.isEmpty()) {
            return null;
        }

        return peers.get(random.nextInt(peers.size()));
    }
}