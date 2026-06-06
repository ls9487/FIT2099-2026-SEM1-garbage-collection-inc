package game.locations;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.World;
import game.actors.ContractedWorker;
import game.actors.Muckraker;
import game.actors.PhantasmWisp;
import game.grounds.*;
import game.inventories.WeightLimitedInventory;
import game.items.AlienArtifact;
import game.items.AluminiumScrap;
import game.items.Flask;
import game.atmosphere.AtmosphericApiClient;
import game.atmosphere.AtmosphericMonitor;
import game.atmosphere.AtmosphericServicesFactory;
import game.atmosphere.EnvironmentalMonitorBehaviour;
import game.items.IndustrialFan;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * This class handles the miracle of creation, translating a bunch of periods
 * and hashtags into a sprawling, functional sci-fi facility.
 */
public class EclipseNebula extends World {

    /**
     * Creates the game world bound to the given display.
     *
     * @param display display used for game output
     */
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
        groundCreator.registerGround('=', AluminiumDoor::new);
        groundCreator.registerGround('N', IronDoor::new);
        groundCreator.registerGround('M', TitaniumDoor::new);
        groundCreator.registerGround('≈', ToxicWaste::new);
//        groundCreator.registerGround('≡', () -> new SuperComputer(SuperComputer.defaultCatalogue()));
        // Placeholder mappings for special glyphs that are decorated later

        // NOTE: We cannot use the default ground creator to create holes,
        // as holes take a parameter of what they can spawn.

        List<Supplier<Item>> depositable = new ArrayList<>();
        depositable.add(AluminiumScrap::new);
        depositable.add(IndustrialFan::new);
        depositable.add(AlienArtifact::new);

        // Produce the maps...
        Deprecated99 moon99DeprecatedMap = new Deprecated99(groundCreator, depositable);
        Overflow20 overflow20Map = new Overflow20(groundCreator, depositable);
        this.addGameMap(moon99DeprecatedMap);
        this.addGameMap(overflow20Map);

        // Install teleportation tubes with mixed intra- and inter-map destinations.
        for (Location tubeLocation : moon99DeprecatedMap.getTubeLocations()) {
            List<Location> tubeTeleportableLocation = new ArrayList<>();
            tubeTeleportableLocation.add(moon99DeprecatedMap.at(6, 3));
            tubeTeleportableLocation.add(overflow20Map.at(6, 3));
            tubeLocation.setGround(new TeleportationTube(tubeTeleportableLocation));
        }

        for (Location tubeLocation : overflow20Map.getTubeLocations()) {
            List<Location> tubeTeleportableLocation = new ArrayList<>();
            tubeTeleportableLocation.add(overflow20Map.at(10, 15));
            tubeTeleportableLocation.add(overflow20Map.at(30, 4));
            tubeTeleportableLocation.add(overflow20Map.at(6, 3));
            tubeTeleportableLocation.add(moon99DeprecatedMap.at(6, 3));
            tubeLocation.setGround(new TeleportationTube(tubeTeleportableLocation));
        }

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

        /*
         * TESTING HELPER for any REQ involving credit amount:
         * Uncomment this line when testing REQ involving purchases
         * (FirstAidKit, SterilisationBox, AccessCards, Creatures, etc.)
         * since workers normally start with 0 credits.
         *
         * Comment it out again for normal gameplay/evaluation (start as a broke).
         */
         //contractedWorker1.getEclipseStatistics.CREDITS(1000);

         // Give Bob a CrtMonitor to sell.
         //contractedWorker1.getInventory().add(new FloppyDisk());

        moon99DeprecatedMap.at(10, 10).addActor(new Muckraker());
        moon99DeprecatedMap.at(11, 11).addActor(new PhantasmWisp());

        // Passive atmospheric monitor for A3 REQ5: sits near the SuperComputer
        // and periodically scans the real-world air quality.
        AtmosphericServicesFactory servicesFactory = new AtmosphericServicesFactory();
        EnvironmentalMonitorBehaviour monitorBehaviour = new EnvironmentalMonitorBehaviour(
                servicesFactory.createParser(),
                new AtmosphericApiClient(),
                servicesFactory
        );
        AtmosphericMonitor monitor = new AtmosphericMonitor(monitorBehaviour);
        moon99DeprecatedMap.at(15, 2).setGround(monitor);
    }

    /**
     * This method handles creating a new worker, with its own separate weighted inventory. Starts with a flask.
     * This exists because I don't want to keep creating new inventories per worker.
     * @param name        the name of the worker
     * @param displayChar the display character of the worker
     * @param hitPoints   the health value of the worker
     * @return a new contracted worker with a weighted inventory and starter flask
     */
    public ContractedWorker initialiseNewWorker(String name, char displayChar, int hitPoints) {
        // Inventory is weighted, with a limit of 50.
        Inventory inventory = new WeightLimitedInventory(50);
        inventory.add(new Flask());
        return new ContractedWorker(name, displayChar, hitPoints, inventory);
    }

}