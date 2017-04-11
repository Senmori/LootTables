package net.senmori.loottables.menu.icon.icons;

import org.bukkit.craftbukkit.loot.core.LootPool;
import net.senmori.loottables.menu.Menu;
import net.senmori.loottables.menu.icon.Icon;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

/**
 * Created by Senmori on 4/24/2016.
 */
public class BonusRollsIcon implements Icon {

    private Menu parent;
    private LootPool pool;
    private int slot;
    private ItemStack stack;

    public BonusRollsIcon(int slot, LootPool pool, Menu parent) {
        this.slot = slot;
        this.pool = pool;
        stack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        this.stack = updateItem();
        this.parent = parent;
    }

    @Override
    public ItemStack getItemStack() { return this.stack; }
    public LootPool getPool() { return this.pool; }
    public int getSlot() { return this.slot; }
    public Menu getParent() { return this.parent; }


    @Override
    public void onClick(Player player, int clickedSlot, ItemStack clickedItem, Inventory inventory, ClickType clickType) {
        adjustRolls(clickType);
        inventory.setItem(slot, updateItem());
    }

    private void adjustRolls(ClickType type) {
        switch (type) {
            case MIDDLE:
                pool.getBonusRolls().setValues(0, 0);
                return;
            case LEFT:
                pool.getBonusRolls().setMin(pool.getBonusRolls().getMin() + 1);
                return;
            case SHIFT_LEFT:
                pool.getBonusRolls().setMin(pool.getBonusRolls().getMin() - 1);
                return;
            case RIGHT:
                pool.getBonusRolls().setMax(pool.getBonusRolls().getMax() + 1);
                return;
            case SHIFT_RIGHT:
                pool.getBonusRolls().setMax(pool.getBonusRolls().getMax() - 1);
                return;
            default:
                return;
        }
    }

    private ItemStack updateItem() {
        String display = ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Bonus Rolls: (" + ChatColor.WHITE + pool.getBonusRolls().toString() + ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + ")";
        SkullMeta meta = (SkullMeta) stack.getItemMeta();
        meta.setOwner("MHF_Question");
        meta.setDisplayName(display);
        stack.setItemMeta(meta);
        return stack;
    }
}
