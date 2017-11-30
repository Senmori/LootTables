package net.senmori.loottables.loottable.utils;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;

import java.io.File;
import java.util.UUID;

public class DataPack {

    private final String name;
    private final UUID worldUUID;
    private DataPack loadBefore = null;
    private DataPack loadAfter = null;
    private boolean enabled = false;

    public DataPack(String name, World world) {
        this.name = name;
        this.worldUUID = world.getUID();
    }

    public DataPack(String name, UUID worldUUID) {
        this.name = name;
        this.worldUUID = worldUUID;
    }

    public DataPack loadBefore(DataPack pack) {
        if( (loadAfter != null && pack != null) && pack.getName().equals(loadAfter.getName())) {
            throw new UnsupportedOperationException("Cannot load " + pack.getName() + " before a pack it's supposed to load after ");
        }
        this.loadBefore = pack;
        return this;
    }

    public DataPack loadAfter(DataPack pack) {
        if( (loadBefore != null && pack != null) && pack.getName().equals(loadBefore.getName())) {
            throw new UnsupportedOperationException("Cannot load " + pack.getName() + " after a pack it's supposed to load before");
        }
        this.loadAfter = pack;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public UUID getWorldUID() {
        return worldUUID;
    }

    public DataPack getLoadBefore() {
        return loadBefore;
    }

    public DataPack getLoadAfter() {
        return loadAfter;
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

    public File getLootTableFile(NamespacedKey namespace) {
        String path = getRootDirectory() + File.separator + namespace.getNamespace()
                                         + File.separator + "loot_tables";
        return new File(path);
    }


    public boolean equals(Object other) {
        if(other == null || !(other instanceof DataPack)) {
            return false;
        }
        DataPack data = (DataPack)other;
        return data.getWorldUID().equals(this.getWorldUID()) && data.getName().equals(this.getName());
    }
}
