package net.senmori.loottables.managers;

import net.senmori.loottables.LootTables;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

/**
 * Created by Senmori on 4/27/2016.
 */
public class ConfigManager {

    private LootTables plugin;


    private File file;
    private FileConfiguration config;


    public ConfigManager(LootTables plugin) {
        this.plugin = plugin;
        init();
    }

    private void init() {
        if (! plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        file = new File(plugin.getDataFolder(), "config.yml");
        if (! file.exists()) {
            file.getParentFile().mkdirs();
            plugin.saveDefaultConfig();
        }

        config = YamlConfiguration.loadConfiguration(file);
        loadConfig();
    }


    private void loadConfig() {

    }
}
