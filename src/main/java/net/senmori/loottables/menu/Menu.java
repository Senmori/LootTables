package net.senmori.loottables.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

/**
 * Created by Senmori on 4/23/2016.
 */
public abstract class Menu implements IMenu {

    public abstract void show(Player player);

    public abstract void onClick(InventoryClickEvent event);

    public abstract void onDrag(InventoryDragEvent event);

    public abstract Inventory getInventory();
}
