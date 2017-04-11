package net.senmori.loottables.menu.icon;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Senmori on 4/24/2016.
 */
public interface Icon {

    void onClick(Player player, int clickedSlot, ItemStack clickedItem, Inventory clickedInventory, ClickType type);

    ItemStack getItemStack();
}
