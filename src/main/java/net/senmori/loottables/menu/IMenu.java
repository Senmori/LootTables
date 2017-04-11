package net.senmori.loottables.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

/**
 * Created by Senmori on 4/23/2016.
 */
public interface IMenu {

    void show(Player player);

    void onClick(InventoryClickEvent event);

    void onDrag(InventoryDragEvent event);
}
