package game.items;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.statistics.BaseStatistic;

/**
 * An abstract class representing an item for this game (Garbage Collection Inc.)
 * All items here are weighted and portable by default.
 *
 * @author echu0057
 */
public abstract class EclipseItem extends Item {

    /**
     * Constructor for the EclipseItem class.
     * All items are portable by default and have a weight statistic.
     * @param name The name of this item.
     * @param displayChar The character representation of this item.
     * @param weight Unit weight of this item.
     */
    public EclipseItem(String name, char displayChar, int weight) {
        super(name, displayChar);
        this.makePortable();
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(weight));
    }

}
