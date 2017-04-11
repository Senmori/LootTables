package net.senmori.loottables.menu.icon.icons;

import org.bukkit.craftbukkit.loot.core.LootPool;
import org.bukkit.craftbukkit.loot.entry.LootEntry;
import org.bukkit.craftbukkit.loot.entry.LootEntryItem;
import org.bukkit.craftbukkit.loot.entry.LootEntryTable;
import net.senmori.loottables.menu.Menu;
import net.senmori.loottables.menu.icon.Icon;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

/**
 * Created by Senmori on 4/24/2016.
 */
public class DeleteIcon implements Icon {

    private Menu parent;
    private LootPool pool;
    private int slot;
    private ItemStack stack;

    /**
     * This Icon will delete an entry dropped onto it.<br>
     *     It will only delete items that are from the parent Menu.<br>
     * @param slot - the slot index this Icon is in
     * @param pool - the {@link LootPool} this Icon relates to.
     * @param parent - the {@link Menu} that holds this Icon
     */
    public DeleteIcon(int slot, LootPool pool, Menu parent) {
        this.parent = parent;
        this.slot = slot;
        this.pool = pool;
        stack = new ItemStack(Material.BARRIER);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Delete Entry");
        String lore = ChatColor.GOLD + "" + ChatColor.ITALIC + "Drag an item here to delete it.";
        meta.setLore(Arrays.asList(lore));
        stack.setItemMeta(meta);
    }

    @Override
    public ItemStack getItemStack() { return this.stack; }
    public int getSlot() { return this.slot; }
    public Menu getParent() { return this.parent; }

    @Override
    public void onClick(Player player, int clickedSlot, ItemStack clickedItem, Inventory clickedInventory, ClickType type) {
        if (player.getItemOnCursor() != null && clickedItem.equals(stack)) {
            if (containsItemStack(player.getItemOnCursor()) && removeEntry(player.getItemOnCursor())) {
                player.sendMessage(ChatColor.AQUA + "Successfully deleted \'" + stack.getType() + "\' from Loot Pool \'" + pool.getName() + "\'");
                player.setItemOnCursor(null);
                clickedInventory.setItem(slot, this.stack);
            }
        }
    }

    private boolean containsItemStack(ItemStack stack) {
        if (stack == null || stack.getType().equals(Material.AIR)) return false;
        for (LootEntry entry : pool.getEntries()) {
            if (entry instanceof LootEntryItem) {
                if (((LootEntryItem) entry).getMaterial().equals(stack.getType())) return true;
            }
            if (entry instanceof LootEntryTable) {
                if (stack.getType().equals(Material.PAPER)) return true;
            }
        }
        return false;
    }

    private boolean removeEntry(ItemStack stack) {
        if (stack == null || stack.getType().equals(Material.AIR)) return true;
        for (LootEntry entry : pool.getEntries()) {
            if (entry instanceof LootEntryItem) {
                LootEntryItem itemEntry = (LootEntryItem) entry;
                if (itemEntry.getMaterial().equals(stack.getType())) {
                    return pool.getEntries().remove(itemEntry);
                }
            }
            if (entry instanceof LootEntryTable) {
                LootEntryTable tableEntry = (LootEntryTable) entry;
                if (stack.getType().equals(Material.PAPER)) {
                    return pool.getEntries().remove(tableEntry);
                }
            }
        }
        return true; // ItemStack was not represented in this LootPool
    }
}
