package net.senmori.loottables.loottable.utils;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;

import java.io.File;
import java.util.UUID;

public class DataPack {

    private String name;
    private UUID worldUUID;

    public DataPack(String name, World world) {
        this.name = name;
        this.worldUUID = world.getUID();
    }

    public DataPack(String name, UUID worldUUID) {
        this.name = name;
        this.worldUUID = worldUUID;
    }

    public String getName() {
        return this.name;
    }

    public UUID getWorldUID() {
        return worldUUID;
    }

    public World getWorld() {
        return Bukkit.getWorld(this.worldUUID);
    }

    public File getRootDirectory() {
        String path = getWorld().getWorldFolder() + File.separator + "datapacks"
                                                  + File.separator + getName()
                                                  + File.separator + "data";
        return new File(path);
    }

    public File getLootTablePath(NamespacedKey namespace) {
        String path = getRootDirectory() + File.separator + namespace.getNamespace()
                                         + File.separator + "loot_tables";
        return new File(path);
    }
}
