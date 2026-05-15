package game.locations;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.GroundCreator;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.Slime;
import game.actors.Undead;
import game.grounds.Hole;
import game.grounds.MagicCircle;
import game.grounds.MagicCircleGroup;
import game.items.AlienCube;
import game.items.Flask;
import game.spawners.SlimeSpawner;
import game.spawners.Spawner;
import game.spawners.UndeadSpawner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * Multi-level flooded factory complex on moon 20-overflow. Hosts magic circles,
 * alien cubes, holes and teleport nodes.
 *
 * @author eche0116
 * @version 1.0
 */
public class Overflow20 extends GameMap {
    private final List<Location> tubeLocations = new ArrayList<>();

    /**
     * Builds the 20-overflow facility from its ASCII layout and decorates
     * special glyphs with magic circles, holes and alien cubes.
     *
     * @param groundCreator engine ground factory
     * @throws GameEngineException when the map cannot be created
     */
    public Overflow20(GroundCreator groundCreator) throws GameEngineException {
        super("20-Overflow", groundCreator, Arrays.asList(
                ".....................≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈",
                "...#######...........≈≈≈≈≈≈≈≈≈≈≈≈≈≈##################≈≈≈≈≈≈≈",
                "...#≡____#...........≈≈≈≈≈≈≈≈≈≈≈≈≈≈#________________#≈≈≈≈≈≈≈",
                "...#__Φ__=...........≈≈≈≈≈≈≈≈#######_______◈________#≈≈≈≈≈≈≈",
                "...#_____#...........≈≈≈≈≈≈≈≈#_____=________________#≈≈≈≈≈≈≈",
                "...#######...≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈#_◎___###########=######≈≈≈≈≈≈≈",
                ".............≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈#_____#≈≈≈≈≈≈≈≈≈#______#≈≈≈≈≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈#########=#####≈≈≈≈≈≈≈≈≈#______#≈≈≈≈≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈#_____________#≈≈≈≈≈≈≈≈≈#___◎__#≈≈≈≈≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈#______o______#≈≈≈≈≈≈≈≈≈#______#≈≈≈≈≈≈≈",
                ".............≈≈≈≈≈≈≈≈######=########≈≈≈≈≈≈≈≈≈####=###≈≈≈≈≈≈≈",
                "...≈≈≈≈≈≈≈≈≈.≈≈≈≈≈≈≈≈≈≈≈≈≈#_#≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈#_#≈≈≈≈≈≈≈≈≈",
                "...≈≈≈≈≈≈≈≈≈.≈≈≈≈≈≈≈≈≈≈≈≈≈#_#≈≈≈≈≈###############_#######≈≈≈",
                ".............≈≈≈≈≈≈≈≈≈≈≈≈≈#_____________________________#≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈≈≈≈≈≈#_______=__________◈__≈≈≈≈____#≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈≈≈≈≈≈#___◎___#_____________≈≈≈≈≈≈__≈≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈≈≈≈≈≈######################≈≈≈≈≈≈≈≈≈≈≈≈",
                ".............≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈",
                ".....................≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈",
                ".....................≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈"
        ));
        MagicCircleGroup circles = new MagicCircleGroup();

        this.setHoles();
        this.setMagicCircles(circles);
        this.setAlienCubes();

        // Pre-reserve a tube location inside the starter ship (bridge corridor).
        this.tubeLocations.add(this.at(6, 3));
    }

    private void setHoles() {
        // First, define the spawners the holes on this map can spawn.
        List<Spawner> spawners = new ArrayList<>();
        spawners.add(new UndeadSpawner());
        spawners.add(new SlimeSpawner());

        this.at(28, 9).setGround(new Hole(spawners));
    }

    private void setMagicCircles(MagicCircleGroup circles) {
        List<Supplier<Item>> spawnableItems = new ArrayList<>();
        spawnableItems.add(Flask::new);

        int[][] coords = {{31, 5}, {49, 8}, {30, 15}};
        for (int[] coord : coords) {
            Location here = this.at(coord[0], coord[1]);
            here.setGround(new MagicCircle(circles, spawnableItems));
            circles.register(here);
        }
    }

    private void setAlienCubes() {
        List<Supplier<Actor>> spawnableActors = new ArrayList<>();
        spawnableActors.add(Undead::new);

        this.at(43, 3).addItem(new AlienCube(spawnableActors));
        this.at(45, 14).addItem(new AlienCube(spawnableActors));
    }

    /**
     * Locations reserved for installing teleportation tubes after all maps are
     * constructed.
     *
     * @return defensive copy of tube locations
     */
    public List<Location> getTubeLocations() {
        List<Location> tubeLocations = new ArrayList<>();
        for (Location location : this.tubeLocations) {
            tubeLocations.add(new Location(location.map(), location.x(), location.y()));
        }
        return tubeLocations;
    }


}