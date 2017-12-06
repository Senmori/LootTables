package net.senmori.loottables.loottable.utils;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;

import java.io.File;
import java.util.UUID;

/**
 * Represents a DataPack used to store information about where a plugin stores recipes, advancements, loot tables, etc.
 */
public interface IDataPack {

    /**
     * Set the {@link DataPack} this DataPack should load before
     * @param before the DataPack to load before this one
     * @return this DataPack to chain calls
     */
    IDataPack loadBefore(IDataPack before);

    /**
     * Get the DataPack that should be loaded before this DataPack
     * @return the DataPack that should be loaded before this DataPack
     */
    IDataPack getLoadBefore();

    /**
     * Set the {@link DataPack} this DataPack should load after.
     * @param after the DataPack to load after this DataPack
     * @return this DataPack to chain calls
     */
    IDataPack loadAfter(IDataPack after);

    /**
     * Get the DataPack that should be loaded after this DataPack
     * @return the DataPack that should be loaded after this DataPack
     */
    IDataPack getLoadAfter();

    /**
     * Get if this DatPack is enabled.
     * @return true if the DataPack is enabled
     */
    boolean isEnabled();

    /**
     *
     * @param value
     */
    void setEnabled(boolean value);

    UUID getWorldUID();

    NamespacedKey getKey();

    default File getRootDirectory() {
        String path = Bukkit.getWorld(getWorldUID()).getWorldFolder()
                            + File.separator + "datapacks"
                            + File.separator + getKey().getNamespace()
                            + File.separator + "data";
        return new File(path);
    }

    default File getLootTableFile(NamespacedKey path) {
        String dir = getRootDirectory() + File.separator + path.getNamespace()
                                        + File.separator + "loot_tables";
        return new File(dir);
    }
}
