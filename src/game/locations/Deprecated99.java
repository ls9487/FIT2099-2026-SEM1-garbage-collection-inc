package game.locations;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.GroundCreator;
import game.actors.Slime;
import game.actors.Undead;
import game.grounds.Hole;
import game.items.AccessCardLevelOne;
import game.items.Apple;
import game.items.CookiePack;
import game.items.Lantern;
import game.items.FloppyDisk;
import game.items.CrtMonitor;
import game.items.Alarm;
//import game.items.FirstAidKit;
//import game.items.SterilisationBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * The starter moon of the Eclipse Nebula, and this game.
 * This class defines its own map structure and includes loose items and spawners.
 *
 * @author echu0057
 */
public class Deprecated99 extends GameMap
{

    /**
     * Constructor for the moon in Eclipse Nebula, 99-Deprecated.
     * @param groundCreator The ground creator object.
     * @throws Exception in case if anything goes wrong...
     */
    public Deprecated99(GroundCreator groundCreator) throws Exception {
        super("99-Deprecated", groundCreator, Arrays.asList(
                "....................########################################",
                "...#######..........#__________________#___________________#",
                "...#_____#..........=__________________=___________________#",
                "...#≡____=...~......#__________________#___________________#",
                "...#_____#..~~~.....########=#####=#####___#############___#",
                "...#######.~~~~.....#______#_#_________#___#___________#___#",
                ".........~~~~.......#______#_#_________#####___________#####",
                "....................#______=_#_________#___________________#",
                "......~.............#______#_#_________#___________________#",
                ".....~~~............#______#_###########___#############___#",
                ".....~..............#______#___________#___#___________#___#",
                "....................=______#___________=___=___________=___#",
                "....................#______#############___#############___#",
                ".........~~~~.......#______#___________#####################",
                "........~~~~~~......#______#___________=___________________#",
                ".........~~~~.......#______#___________#___________________#",
                "....................#______#############___#############___#",
                "....................#______#___________#___#___________#___#",
                "..~.................#______=___________=___=___________=___#",
                "....................########################################")
        );
        this.addLooseItems();
        this.setHoles();
    }

    /**
     * Populates the map with items scattered around.
     * To be used internally within this class only.
     */
    private void addLooseItems() {
        // Add the loose items onto the ship.
        this.at(7, 2).addItem(new AccessCardLevelOne());
        //this.at(6, 2).addItem(new FirstAidKit()); removed since it should only be bought / cannot get for FREE
        //this.at(5, 2).addItem(new SterilisationBox()); removed since it should only be bought / cannot get for FREE
        // Add other loose items around as well.
        this.at(6, 13).addItem(new Apple());
        this.at(48, 10).addItem(new Apple());
        this.at(50, 10).addItem(new Apple());
        this.at(57, 18).addItem(new CookiePack());
        this.at(21, 3).addItem(new CookiePack());
        this.at(32, 17).addItem(new CookiePack());
        this.at(13, 5).addItem(new Lantern());
        this.at(14, 11).addItem(new Lantern());
        this.at(25, 10).addItem(new FloppyDisk());
        this.at(33, 6).addItem(new FloppyDisk());
        this.at(47, 14).addItem(new FloppyDisk());
        this.at(33, 8).addItem(new CrtMonitor());
        this.at(34, 8).addItem(new CrtMonitor());
        this.at(35, 8).addItem(new CrtMonitor());
        this.at(56, 5).addItem(new CrtMonitor());
        this.at(43, 15).addItem(new CrtMonitor());
        this.at(21, 1).addItem(new Alarm());
        this.at(21, 12).addItem(new Alarm());
    }

    /**
     * Replace designated spots with holes in the ground, along with the creatures spawned from it.
     * To be used internally within this class only.
     */
    private void setHoles() {
        // First, define what actors the holes on this map can spawn.
        List<Supplier<Actor>> spawnableActors = new ArrayList<>();
        spawnableActors.add(Undead::new);
        spawnableActors.add(Slime::new);
        // Then, replace the designated locations with holes.
        this.at(56, 1).setGround(new Hole(spawnableActors));
        this.at(49, 11).setGround(new Hole(spawnableActors));
        this.at(33, 17).setGround(new Hole(spawnableActors));
        this.at(57, 17).setGround(new Hole(spawnableActors));
    }

}
