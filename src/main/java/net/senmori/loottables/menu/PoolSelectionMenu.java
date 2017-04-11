package net.senmori.loottables.menu;

import net.senmori.loottables.LootTables;
import org.bukkit.craftbukkit.loot.core.LootPool;
import org.bukkit.craftbukkit.loot.core.LootTable;
import net.senmori.loottables.menu.icon.icons.PoolIcon;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Senmori on 4/28/2016.
 */
public class PoolSelectionMenu extends Menu {

    private Inventory inventory;
    private LootTable table;

    private List<PoolIcon> poolIcons = new ArrayList<>();

    public PoolSelectionMenu(LootTable table) {
        this.table = table;
        int size = setSize(table.getLootPools().size());
        String title = ChatColor.GOLD + "Table: " + ChatColor.RED + "" + ChatColor.ITALIC + table.getResourceLocation().getResourcePath();
        inventory = Bukkit.createInventory(null, size, title);
        loadIcons();
    }

    private void loadIcons() {
        int slot = 0;
        for(LootPool pool : table.getLootPools()) {
            poolIcons.add(new PoolIcon(slot++, pool));
        }
    }

    @Override
    public Inventory getInventory() { return this.inventory; }
    public LootTable getTable() { return this.table; }

    @Override
    public void show(Player player) {
        player.closeInventory();
        loadIcons();
        new BukkitRunnable() {
            @Override
            public void run() {
                for(PoolIcon icon : poolIcons) {
                    inventory.setItem(icon.getSlot(), icon.getItemStack());
                }
                this.cancel();
            }
        }.runTaskLater(LootTables.getInstance(), 1L);
        player.openInventory(inventory);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setResult(Event.Result.DENY);
        event.setCancelled(true);
        if(poolIcons.get(event.getRawSlot()) != null) {

        }
    }

    @Override
    public void onDrag(InventoryDragEvent event) {
        event.setResult(Event.Result.DENY);
        event.setCancelled(true);
    }

    private int setSize(int poolSize) {
        if(poolSize <= 9) return 9;
        if(poolSize > 9 && poolSize <= 18) return 18;
        if(poolSize > 18 && poolSize <= 27) return 27;
        if(poolSize > 27 && poolSize <= 36) return 36;
        if(poolSize > 36 && poolSize <= 45) return 45;
        return 54;
    }
}
