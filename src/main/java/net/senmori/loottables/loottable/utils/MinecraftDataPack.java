package net.senmori.loottables.loottable.utils;

import net.senmori.loottables.loottable.core.LootTableManager;
import org.bukkit.NamespacedKey;

import java.util.UUID;

public class MinecraftDataPack implements IDataPack {
    private IDataPack after;
    private IDataPack before;
    private UUID worldUUID;
    private NamespacedKey key;
    private boolean enabled = false;

    public MinecraftDataPack() {
        this.after = null;
        this.before = null;
        this.worldUUID = LootTableManager.getInstance().defWorldUUID;
        this.key = NamespacedKey.minecraft("minecraft");
    }

    @Override
    public IDataPack loadBefore(IDataPack before) {
        this.before = before;
        return this;
    }

    @Override
    public IDataPack getLoadBefore() {
        return before;
    }

    @Override
    public IDataPack loadAfter(IDataPack after) {
        this.after = after;
        return this;
    }

    @Override
    public IDataPack getLoadAfter() {
        return after;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean value) {
        this.enabled = value;
    }

    @Override
    public UUID getWorldUID() {
        return worldUUID;
    }

    @Override
    public NamespacedKey getKey() {
        return key;
    }
}
