package net.senmori.loottables.menu.icon.icons;

import net.senmori.loottables.menu.Menu;
import net.senmori.loottables.menu.icon.Icon;
import org.apache.commons.lang.Validate;
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
public class ParentIcon implements Icon {

    private Menu parent;
    private int slot;
    private ItemStack itemStack;

    public ParentIcon(int slot, Menu menu) {
        Validate.notNull(menu);
        this.slot = slot;
        this.parent = menu;
        itemStack = new ItemStack(Material.EMPTY_MAP);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(ChatColor.ITALIC + "Previous Menu");
        itemStack.setItemMeta(meta);
    }

    @Override
    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public int getSlot() {
        return this.slot;
    }

    public Menu getParent() {
        return this.parent;
    }

    @Override
    public void onClick(Player player, int clickedSlot, ItemStack clickedItem, Inventory clickedInventory, ClickType type) {
        player.setItemOnCursor(null);
        parent.show(player);
    }
}
