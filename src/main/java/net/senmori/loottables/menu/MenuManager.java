package net.senmori.loottables.menu;

import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Senmori on 4/27/2016.
 */
public class MenuManager {

    private List<Menu> menuList = Collections.synchronizedList(new ArrayList<>());
    private boolean shouldPause = false;

    public MenuManager() {}

    public boolean addMenu(Menu menu) { return menuList.add(menu); }
    public boolean removeMenu(Menu menu) { return menuList.remove(menu); }

    public void onClick(InventoryClickEvent e) {
        if(shouldPause) return;
        for(Menu m : menuList) {
            m.onClick(e);
        }
    }

    /**
     * Clear all inactive menus. Inactive means it has no active viewers.
     * @param displayAmount - send chat to console.
     */
    public void clearInactiveMenus(boolean displayAmount) {
        shouldPause = true;
        synchronized(menuList) {
            for(Menu m : menuList) {
                if(m.getInventory().getViewers().isEmpty() || m.getInventory().getViewers().size() < 1 || m.getInventory() == null) {
                    menuList.remove(m);
                }
            }
        }
        shouldPause = false;
    }
}
