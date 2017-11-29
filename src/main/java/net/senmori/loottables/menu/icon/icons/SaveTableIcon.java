package net.senmori.loottables.menu.icon.icons;

import net.senmori.loottables.loottable.core.LootTable;
import net.senmori.loottables.menu.Menu;
import net.senmori.loottables.menu.icon.Icon;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Created by Senmori on 4/24/2016.
 */
public class SaveTableIcon implements Icon {

    private Menu parent;
    private LootTable table;
    private int slot;
    private ItemStack itemStack;

    public SaveTableIcon(int slot, LootTable table, Menu parent) {
        this.slot = slot;
        this.table = table;
        itemStack = new ItemStack(Material.EMPTY_MAP);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "Save and Close (" + ChatColor.RESET + table.getKey().toString() + ChatColor.AQUA + ")");
        itemStack.setItemMeta(meta);
        this.parent = parent;
    }

    @Override
    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public int getSlot() {
        return this.slot;
    }

    public LootTable getTable() {
        return this.table;
    }

    public Menu getParent() {
        return this.parent;
    }

    @Override
    public void onClick(Player player, int clickedSlot, ItemStack clickedItem, Inventory clickedInventory, ClickType type) {
        player.closeInventory();
        if (saveTable()) {
            player.sendMessage(ChatColor.AQUA + "Successfully updated \'" + ChatColor.RESET + table.getKey().toString() + ChatColor.AQUA + "\'!");
        }
    }

    private synchronized boolean saveTable() {
        return table.save();
    }
}
