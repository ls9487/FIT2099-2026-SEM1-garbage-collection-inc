package game.locations;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.GroundCreator;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.Undead;
import game.grounds.Hole;
import game.grounds.MagicCircle;
import game.grounds.MagicCircleGroup;
import game.grounds.Vent;
import game.items.AlienCube;
import game.items.Flask;
import game.spawners.*;
import game.trees.*;

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
    private final List<Location> tubeLocations;

    /**
     * Builds the 20-overflow facility from its ASCII layout and decorates
     * special glyphs with magic circles, holes and alien cubes.
     *
     * @param groundCreator engine ground factory
     * @throws GameEngineException when the map cannot be created
     */
    public Overflow20(GroundCreator groundCreator, List<Supplier<Item>> depositable) throws GameEngineException {
        super("20-Overflow", groundCreator, Arrays.asList(
                ".....................≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈",
                "...#######...........≈≈≈≈≈≈≈≈≈≈≈≈≈≈##################≈≈≈≈≈≈≈",
                "...#≡____#...........≈≈≈≈≈≈≈≈≈≈≈≈≈≈#________________#≈≈≈≈≈≈≈",
                "...#_____=...........≈≈≈≈≈≈≈≈#######________________#≈≈≈≈≈≈≈",
                "...#_____#...........≈≈≈≈≈≈≈≈#_____=________________#≈≈≈≈≈≈≈",
                "...#######...≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈#_____###########=######≈≈≈≈≈≈≈",
                ".............≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈#_____#≈≈≈≈≈≈≈≈≈#______#≈≈≈≈≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈#########=#####≈≈≈≈≈≈≈≈≈#______#≈≈≈≈≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈#_____________#≈≈≈≈≈≈≈≈≈#______#≈≈≈≈≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈#_____________#≈≈≈≈≈≈≈≈≈#______#≈≈≈≈≈≈≈",
                ".............≈≈≈≈≈≈≈≈######=########≈≈≈≈≈≈≈≈≈####=###≈≈≈≈≈≈≈",
                "...≈≈≈≈≈≈≈≈≈.≈≈≈≈≈≈≈≈≈≈≈≈≈#_#≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈#_#≈≈≈≈≈≈≈≈≈",
                "...≈≈≈≈≈≈≈≈≈.≈≈≈≈≈≈≈≈≈≈≈≈≈#_#≈≈≈≈≈###############_#######≈≈≈",
                ".............≈≈≈≈≈≈≈≈≈≈≈≈≈#_____________________________#≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈≈≈≈≈≈#_______=_____________≈≈≈≈____#≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈≈≈≈≈≈#_______#_____________≈≈≈≈≈≈__≈≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈≈≈≈≈≈######################≈≈≈≈≈≈≈≈≈≈≈≈",
                ".............≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈",
                ".....................≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈",
                ".....................≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈"
        ));
        this.tubeLocations = new ArrayList<>();
        this.setHoles(depositable);
        this.setVents(depositable);
        this.setMagicCircles();
        this.setAlienCubes();
        this.setTrees();

        // Pre-reserve a tube location inside the starter ship (bridge corridor).
        this.tubeLocations.add(this.at(6, 3));
    }

    private void setHoles(List<Supplier<Item>> depositable) {
        // First, define the spawners the holes on this map can spawn.
        List<Spawner> spawners = new ArrayList<>();
        spawners.add(new UndeadSpawner());
        spawners.add(new ParasiteSpawner());

        this.at(28, 9).setGround(new Hole(spawners));
    }

    private void setVents(List<Supplier<Item>> depositable) {
        // Define the spawners the vents on this map can spawn.
        List<Spawner> spawners = new ArrayList<>();
        spawners.add(new SlimeSpawner());
        spawners.add(new ParasiteSpawner());
        spawners.add(new ScrapSnatcherSpawner(depositable));

        this.at(51, 2).setGround(new Vent(spawners));
    }

    private void setMagicCircles() {
        MagicCircleGroup circles = new MagicCircleGroup();
        List<Supplier<Item>> spawnableItems = new ArrayList<>();
        spawnableItems.add(Flask::new);

        this.at(31, 5).setGround(new MagicCircle(circles, spawnableItems));
        circles.register(this.at(31, 5));

        this.at(49, 8).setGround(new MagicCircle(circles, spawnableItems));
        circles.register(this.at(49, 8));

        this.at(30, 15).setGround(new MagicCircle(circles, spawnableItems));
        circles.register(this.at(30, 15));
    }

    private void setAlienCubes() {
        List<Supplier<Actor>> spawnableActors = new ArrayList<>();
        spawnableActors.add(Undead::new);

        List<Spawner> spawners = new ArrayList<>();
        spawners.add(new UndeadSpawner());

        this.at(43, 3).addItem(new AlienCube(spawners));
        this.at(45, 14).addItem(new AlienCube(spawners));
    }

    private void setTrees() {
        List<Spawner> fleshyTreeSproutSpawners = new ArrayList<>();
        fleshyTreeSproutSpawners.add(new SlimeSpawner());

        List<Spawner> fleshyTreeMatureSpawners = new ArrayList<>();
        fleshyTreeMatureSpawners.add(new UndeadSpawner());

        this.at(1, 16).setGround(new FleshyTreeSprout(fleshyTreeSproutSpawners, new FleshyTreeSapling(new FleshyTreeMature(fleshyTreeMatureSpawners))));

        this.at(4, 16).setGround(new WarperTreeSapling(new WarperTreeMature()));
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
            tubeLocations.add(this.at(location.x(), location.y()));
        }
        return tubeLocations;
    }


}