package game.locations;

import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.World;
import game.actors.ContractedWorker;
import game.grounds.Dirt;
import game.grounds.Door;
import game.grounds.Floor;
import game.grounds.Puddle;
import game.grounds.Wall;
import game.inventories.WeightLimitedInventory;
import game.items.Flask;

/**
 * This class handles the miracle of creation, translating a bunch of periods
 * and hashtags into a sprawling, functional sci-fi facility.
 */
public class EclipseNebula extends World {

    public EclipseNebula(Display display) {
        super(display);
    }

    /**
     * Initialise maps, actors, items, and grounds of the game world.
     * @throws Exception in case if anything goes wrong...
     */
    public void initialise() throws Exception {
        // Set up the groundCreator to be using.
        DefaultGroundCreator groundCreator = new DefaultGroundCreator();
        groundCreator.registerGround('.', Dirt::new);
        groundCreator.registerGround('#', Wall::new);
        groundCreator.registerGround('~', Puddle::new);
        groundCreator.registerGround('_', Floor::new);
        groundCreator.registerGround('=', Door::new);
        // NOTE: We cannot use the default ground creator to create holes,
        // as holes take a parameter of what they can spawn.

        // Produce the map...
        GameMap moon99DeprecatedMap = new Deprecated99(groundCreator);
        this.addGameMap(moon99DeprecatedMap);

        // BEHOLD, LOCAL MULTIPLAYER!!! ...comment some guys out for easier testing.
        ContractedWorker contractedWorker1 = initialiseNewWorker("#1 Bob", 'ඞ', 10);
        //ContractedWorker contractedWorker2 = initialiseNewWorker("#2 Tom", 'ඞ', 10);
        //ContractedWorker contractedWorker3 = initialiseNewWorker("#3 Sarah", 'ඞ', 10);
        //ContractedWorker contractedWorker4 = initialiseNewWorker("#4 Julie", 'ඞ', 10);
        //ContractedWorker contractedWorker5 = initialiseNewWorker("#5 Rick", 'ඞ', 10);
        this.addPlayer(contractedWorker1, moon99DeprecatedMap.at(6, 2));
        //this.addPlayer(contractedWorker2, moon99DeprecatedMap.at(7, 2));
        //this.addPlayer(contractedWorker3, moon99DeprecatedMap.at(8, 2));
        //this.addPlayer(contractedWorker4, moon99DeprecatedMap.at(6, 4));
        //this.addPlayer(contractedWorker5, moon99DeprecatedMap.at(8, 4));
    }

    /**
     * This method handles creating a new worker, with its own separate weighted inventory. Starts with a flask.
     * This exists because I don't want to keep creating new inventories per worker.
     * @param name The name of the worker.
     * @param displayChar The display character of the worker.
     * @param hitPoints The health value of the worker.
     */
    public ContractedWorker initialiseNewWorker(String name, char displayChar, int hitPoints) {
        // Inventory is weighted, with a limit of 50.
        Inventory inventory = new WeightLimitedInventory(50);
        inventory.add(new Flask());
        return new ContractedWorker(name, displayChar, hitPoints, inventory);
    }

}
