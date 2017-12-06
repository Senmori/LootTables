package net.senmori.loottables.loottable.utils;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;

import java.util.UUID;

public class DataPack implements IDataPack {

    private final NamespacedKey name;
    private final UUID worldUUID;
    private IDataPack loadBefore = null;
    private IDataPack loadAfter = null;
    private boolean enabled = false;

    public DataPack(NamespacedKey name, World world) {
        this.name = name;
        this.worldUUID = world.getUID();
        enabled = true;
    }

    public DataPack(NamespacedKey name, UUID worldUUID) {
        this.name = name;
        this.worldUUID = worldUUID;
        enabled = true;
    }

    public IDataPack loadBefore(IDataPack pack) {
        if( (loadAfter != null && pack != null) && pack.getKey().equals(loadAfter.getKey())) {
            throw new UnsupportedOperationException("Cannot load " + pack.getKey() + " before a pack it's supposed to load after ");
        }
        this.loadBefore = pack;
        return this;
    }

    public IDataPack loadAfter(IDataPack pack) {
        if( (loadBefore != null && pack != null) && pack.getKey().equals(loadBefore.getKey())) {
            throw new UnsupportedOperationException("Cannot load " + pack.getKey() + " after a pack it's supposed to load before");
        }
        this.loadAfter = pack;
        return this;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean value) {
        this.enabled = value;
    }

    public NamespacedKey getKey() {
        return this.name;
    }

    public UUID getWorldUID() {
        return worldUUID;
    }

    public IDataPack getLoadBefore() {
        return loadBefore;
    }

    public IDataPack getLoadAfter() {
        return loadAfter;
    }

    public World getWorld() {
        return Bukkit.getWorld(this.worldUUID);
    }

    public boolean equals(Object other) {
        if(other == null || !(other instanceof DataPack)) {
            return false;
        }
        DataPack data = (DataPack)other;
        return data.getWorldUID().equals(this.getWorldUID()) && data.getKey().equals(this.getKey());
    }
}
