package net.senmori.loottables.menu.icon.icons;

import net.senmori.loottables.loottable.core.LootPool;
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
public class RollsIcon implements Icon {

    private LootPool pool;
    private int slot;
    private ItemStack stack;

    /**
     * The {@link Icon} that displays the {@link LootPool#rolls}.<br> Clicking this item will do the following: <br>
     * <indent>LEFT: increase Minimum by 1</indent><br> <indent>SHIFT_LEFT: decrease Minimum by 1</indent><br>
     * <indent>RIGHT: increase Maximum by 1</indent><br> <indent>SHIFT_RIGHT: decrease Maximum by 1</indent><br>
     * <indent>MIDDLE: reset values to 0</indent>
     *
     * @param slot   - slot this Icon is in
     * @param pool   - the {@link LootPool} which this Icon relates to
     * @param parent - the {@link Menu} this Icon is stored in.
     */
    public RollsIcon(int slot, LootPool pool, Menu parent) {
        this.slot = slot;
        this.pool = pool;
        stack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        stack = updateItem();
    }

    public LootPool getPool() {
        return this.pool;
    }

    public int getSlot() {
        return this.slot;
    }

    @Override
    public ItemStack getItemStack() {
        return this.stack;
    }


    @Override
    public void onClick(Player player, int clickedSlot, ItemStack clickedItem, Inventory clickedInventory, ClickType type) {
        adjustRolls(type);
        clickedInventory.setItem(slot, updateItem());
    }

    private void adjustRolls(ClickType type) {
        switch (type) {
            case MIDDLE:
                pool.getRolls().setValues(0, 0);
                break;
            case LEFT:
                pool.getRolls().setMin(pool.getRolls().getMin() + 1);
                break;
            case SHIFT_LEFT:
                pool.getRolls().setMin(pool.getRolls().getMin() - 1);
                break;
            case RIGHT:
                pool.getRolls().setMax(pool.getRolls().getMax() + 1);
                break;
            case SHIFT_RIGHT:
                pool.getRolls().setMax(pool.getRolls().getMax() - 1);
                break;
            default:
                break;
        }
    }

    private ItemStack updateItem() {
        String display = ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Rolls: (" + ChatColor.WHITE + pool.getRolls().toString() + ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + ")";
        SkullMeta meta = (SkullMeta) stack.getItemMeta();
        meta.setOwner("MHF_Question");
        meta.setDisplayName(display);
        stack.setItemMeta(meta);
        return stack;
    }
}
