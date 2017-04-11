package net.senmori.loottables;

import net.senmori.loottables.commands.DebugCommand;
import net.senmori.loottables.managers.CommandManager;
import net.senmori.loottables.menu.MenuManager;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Senmori on 4/27/2016.
 */
public class LootTables extends JavaPlugin {

    private static LootTables instance;

    private PluginDescriptionFile pdf;
    public static String name;

    // managers
    private CommandManager commandManager;
    private MenuManager menuManager;


    @Override
    public void onEnable() {
        pdf = getDescription();
        name = pdf.getName();

        instance = this;
        // Config


        // Commands
        commandManager = new CommandManager(this);
        commandManager.setCommandPrefix("lt");

        commandManager.registerCommand(new DebugCommand());


        menuManager = new MenuManager();
    }



    @Override
    public void onDisable() {

    }


    public static LootTables getInstance() { return instance; }
    public CommandManager getCommandManager() { return this.commandManager; }
    public MenuManager getMenuManager() { return this.menuManager; }
}
