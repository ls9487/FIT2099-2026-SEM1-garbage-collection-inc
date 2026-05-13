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
        decorateSpecialTiles(new MagicCircleGroup());
    }

    private void decorateSpecialTiles(MagicCircleGroup circles){
        List<Supplier<Actor>> spawnableActors = new ArrayList<>();
        spawnableActors.add(Undead::new);
        spawnableActors.add(Slime::new);

        List<Supplier<Actor>> alienCubeSpawnableActors = new ArrayList<>();
        alienCubeSpawnableActors.add(Undead::new);

        List<Supplier<Item>> magicCircleSpawnableItems = new ArrayList<>();
        magicCircleSpawnableItems.add(Flask::new);

        for (int x : this.getXRange()) {
            for (int y : this.getYRange()) {
                Location here = this.at(x, y);
                char displayChar = here.getGround().getDisplayChar();
                if (displayChar == '◎') {
                    here.setGround(new MagicCircle(circles, magicCircleSpawnableItems));
                    circles.register(here);
                } else if (displayChar == '◈') {
                    here.addItem(new AlienCube(alienCubeSpawnableActors));
                } else if (displayChar == 'o') {
                    here.setGround(new Hole(spawnableActors));
                } else if (displayChar == 'Φ') {
                    tubeLocations.add(here);
                }
            }
        }
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