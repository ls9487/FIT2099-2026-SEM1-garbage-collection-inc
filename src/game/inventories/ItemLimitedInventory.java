package game.inventories;

import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.items.Item;

public class ItemLimitedInventory extends Inventory {
    private final int itemNumberLimit;
    private int itemNumber;

    public ItemLimitedInventory(int itemNumberLimit) {
        this.itemNumberLimit = itemNumberLimit;
        this.itemNumber = 0;
    }
    /**
     * Add an item to the inventory.
     * Adding an item may fail due to certain conditions, e.g., the item to be added doesn't have the required statistic
     *
     * @param item The Item to add.
     * @return true if the item is successfully added, false otherwise
     */
    @Override
    public boolean add(Item item) {
        Display display = new Display();

        if(itemNumber < itemNumberLimit) {
            items.add(item);
            itemNumber++;
            display.println(String.format("%s added successfully. Current inventory size (%d/%d)", item, itemNumber, itemNumberLimit));
            return true;
        } else {
            display.println(String.format("Fails to add %s. Item number limit will be exceeded (%d/%d)", item, 1 + itemNumber, itemNumberLimit));
            return false;
        }
    }

    /**
     * Remove an item from the inventory.
     * Removing an item may fail due to certain conditions, e.g., the item to be removed doesn't have the required statistic
     *
     * @param item The Item to remove.
     * @return true if the item is successfully removed, false otherwise
     */
    @Override
    public boolean remove(Item item) {
        items.remove(item);
        this.itemNumber--;
        return true;
    }
}
