package net.senmori.loottables.menu.icon.icons;

import net.senmori.loottables.loottable.entry.LootEntryItem;
import net.senmori.loottables.menu.Menu;
import net.senmori.loottables.menu.icon.Icon;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

/**
 * Created by Senmori on 4/24/2016.
 */
public class EntryIcon implements Icon {

    protected Menu parent;
    protected LootEntryItem entry;
    protected ItemStack itemStack;
    protected int slot;

    /**
     * This {@link Icon} is used to display the related {@link ItemStack} a {@link LootEntryItem} will be.<br> Clicking
     * this {@link Icon} will display options to increase the {@link LootEntryItem#weight} and/or {@link
     * LootEntryItem#quality} of an entry.<br> You cannot add/remove conditions or functions. Only the entry itself.<br>
     * Click options:<br> LEFT: increase weight<br> SHIFT_LEFT: decrease weight<br> RIGHT: increase quality<br>
     * SHIFT_RIGHT: decrease quality<br> MIDDLE: reset weight & quality to 1 and 0, respectively
     *
     * @param slot   - the slot index
     * @param entry  - the {@link LootEntryItem} this Icon relates to.
     * @param parent - the {@link Menu} that holds this Icon
     */
    public EntryIcon(int slot, LootEntryItem entry, Menu parent) {
        this.slot = slot;
        this.entry = entry;
        itemStack = new ItemStack(entry.getMaterial());
        itemStack = updateItem();
        this.parent = parent;
    }

    @Override
    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public int getSlot() {
        return this.slot;
    }

    public LootEntryItem getEntry() {
        return this.entry;
    }

    public Menu getHolder() {
        return this.parent;
    }


    @Override
    public void onClick(Player player, int clickedSlot, ItemStack clickedItem, Inventory clickedInventory, ClickType type) {
        adjustWeightOrQuality(type);
        player.setItemOnCursor(null);
        clickedInventory.setItem(slot, updateItem());
    }

    private void adjustWeightOrQuality(ClickType type) {
        switch (type) {
            case MIDDLE:
                entry.setWeight(1);
                entry.setQuality(0);
                break;
            case LEFT:
                entry.setWeight(entry.getWeight() + 1);
                break;
            case SHIFT_LEFT:
                entry.setWeight(entry.getWeight() - 1);
                break;
            case RIGHT:
                entry.setQuality(entry.getQuality() + 1);
                break;
            case SHIFT_RIGHT:
                entry.setQuality(entry.getQuality() - 1);
                break;
            default:
                break;
        }
    }

    private ItemStack updateItem() {
        ItemMeta meta = itemStack.getItemMeta();
        // weight + quality
        String firstLore = ChatColor.GOLD + "Weight: (" + ChatColor.AQUA + entry.getWeight() + ChatColor.GOLD + ") - Quality (" + ChatColor.AQUA + entry.getQuality() + ChatColor.GOLD + ")";
        String conditions = ChatColor.GOLD + "Conditions: (" + ChatColor.AQUA + entry.getConditions().size() + ChatColor.GOLD + ")";
        String functions = ChatColor.GOLD + "Functions: (" + ChatColor.AQUA + entry.getFunctions().size() + ChatColor.GOLD + ")";
        meta.setLore(Arrays.asList(firstLore, conditions, functions));
        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
