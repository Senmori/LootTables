package net.senmori.loottables.menu.icon.icons;

import net.senmori.loottables.loottable.core.LootPool;
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
public class PoolIcon implements Icon {

    protected LootPool pool;
    protected int slot;
    protected ItemStack itemStack;

    public PoolIcon(int slot, LootPool pool) {
        this.slot = slot;
        this.pool = pool;
        this.itemStack = new ItemStack(Material.BOOK);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + pool.getName());
        itemStack.setItemMeta(meta);
    }

    public LootPool getLootPool() {
        return this.pool;
    }

    public int getSlot() {
        return this.slot;
    }

    @Override
    public ItemStack getItemStack() {
        return this.itemStack;
    }

    @Override
    public void onClick(Player player, int clickedSlot, ItemStack clickedItem, Inventory clickedInventory, ClickType type) {

    }
}
