package net.senmori.loottables.menu.icon.icons;

import net.senmori.loottables.loottable.entry.LootEntryTable;
import net.senmori.loottables.menu.Menu;
import net.senmori.loottables.menu.icon.Icon;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Senmori on 4/24/2016.
 */
public class TableIcon implements Icon {

    protected Menu parent;
    protected LootEntryTable entry;
    protected ItemStack stack;
    protected int slot;

    /**
     * This {@link Icon} is used to display a link to the related {@link LootEntryTable}.<br> Clicking this {@link Icon}
     * will <b>NOT</b> open the related {@link LootEntryTable}.<br> You cannot add/remove conditions. Only the entry
     * itself.
     *
     * @param slot   - the slot this Icon is in
     * @param table  - the {@link net.senmori.loottables.loottable.core.LootTable} this Icon relates to.
     * @param parent - The {@link Menu} that holds this Icon
     */
    public TableIcon(int slot, LootEntryTable table, Menu parent) {
        this.slot = slot;
        this.entry = table;
        this.parent = parent;
    }

    @Override
    public ItemStack getItemStack() {
        return this.stack;
    }

    public int getSlot() {
        return this.slot;
    }

    public LootEntryTable getEntry() {
        return this.entry;
    }

    public Menu getParent() {
        return this.parent;
    }

    @Override
    public void onClick(Player player, int clickedSlot, ItemStack clickedItem, Inventory clickedInventory, ClickType type) {

    }
}
