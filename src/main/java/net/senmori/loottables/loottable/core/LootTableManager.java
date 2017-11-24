package net.senmori.loottables.loottable.core;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import net.senmori.loottables.loottable.conditions.LootCondition;
import net.senmori.loottables.loottable.conditions.LootConditionManager;
import net.senmori.loottables.loottable.entry.LootEntry;
import net.senmori.loottables.loottable.entry.LootEntryAdapter;
import net.senmori.loottables.loottable.functions.LootFunction;
import net.senmori.loottables.loottable.functions.LootFunctionManager;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * This class should only be instantiated once, preferably inside CraftBukkit.
 */
public final class LootTableManager {

    private static Gson gson;
    /* ConcurrentHashMap because it's thread safe, supposedly */
    private static HashMap<NamespacedKey, LootTable> registeredLootTables = Maps.newHashMap();
    //private static Set<String> registeredDomains = new HashSet<>();
    private final File baseFolder;

    static {
        gson = ( new GsonBuilder()
                         .registerTypeHierarchyAdapter(LootEntry.class, new LootEntryAdapter())
                         .registerTypeHierarchyAdapter(LootCondition.class, new LootConditionManager.Serializer())
                         .registerTypeHierarchyAdapter(LootFunction.class, new LootFunctionManager.Serializer())
                         .registerTypeAdapter(RandomValueRange.class, new RandomValueRange.Serializer())
                         .registerTypeAdapter(LootPool.class, new LootPool.Serializer())
                         .registerTypeAdapter(LootTable.class, new LootTable.Serializer())
                         .setPrettyPrinting().create() );
    }


    public LootTableManager(File folder) {
        this.baseFolder = folder;
        // reload all loot tables for re-validation.
        registeredLootTables.clear();
        for (NamespacedKey location : registeredLootTables.keySet()) {
            LootTableManager.getLootTable(location);
        }
    }

    public static Gson getGson() {
        return gson;
    }

    public static HashMap<NamespacedKey, LootTable> getRegisteredLootTables() {
        return registeredLootTables;
    }

    /**
     * Get a {@link NamespacedKey} that matches the given path.<br> Do not include the domain. (i.e. "minecraft")
     *
     * @param resourcePath - the path to the loot table, without file extendsions.
     *
     * @return the {@link NamespacedKey} that has the matching resourcePath, or null.
     */
    public static NamespacedKey getKey(String resourcePath) {
        for (NamespacedKey location : registeredLootTables.keySet()) {
            if (location.getKey().equals(resourcePath)) {
                return location;
            }
        }
        return null;
    }

    /**
     * Get a {@link NamespacedKey} that matches the given domain and path.
     *
     * @param resourceDomain - the namespace of the plugin(i.e. "minecraft")
     * @param resourcePath   - the path to the loot table, without file extensions.
     *
     * @return the {@link NamespacedKey} that has the matching arguments, or null.
     */
    public static NamespacedKey getResourceLocation(String resourceDomain, String resourcePath) {
        for (NamespacedKey loc : registeredLootTables.keySet()) {
            if (loc.getKey().equals(resourceDomain) && loc.getKey().equals(resourcePath)) {
                return loc;
            }
        }
        return null;
    }

    /**
     * Add a {@link NamespacedKey} which will generate the .json file. <br> If a .json file is not already created, it
     * will create one and return it. <br> If the {@link LootTable} is already loaded, it will return that. <br>
     *
     * @param resource the {@link NamespacedKey} that has the matching arguments, or null.
     */
    public static LootTable getLootTable(NamespacedKey resource) {
        return getLootTable(resource, false);
    }

    public static LootTable getLootTable(NamespacedKey resource, boolean forceReload) {
        if (forceReload) {
            File file = getFile(resource);
            if (file != null && file.exists()) {
                try {
                    LootTable table = load(resource);
                    table.setPath(resource);
                    registeredLootTables.put(resource, table);
                    return table;
                } catch (IOException e) {
                    Bukkit.getLogger().log(Level.WARNING, "Couldn't load loot table " + resource.toString());
                    return LootTable.emptyLootTable();
                }
            }
        }
        if (registeredLootTables.containsKey(resource)) {
            return registeredLootTables.get(resource);
        } else {
            File file = getFile(resource);
            if (file != null && file.exists()) {
                // load file
                try {
                    LootTable table = load(resource);
                    registeredLootTables.put(resource, table);
                    table.setPath(resource);
                    return table;
                } catch (IOException e) {
                    Bukkit.getLogger().log(Level.WARNING, "Couldn't load loot table " + resource.toString());
                    return LootTable.emptyLootTable();
                }
            } else {
                // no LootTable file exists, create new file, and return empty LootTable
                LootTable newTable = LootTable.emptyLootTable();
                registeredLootTables.put(resource, newTable);
                newTable.setPath(resource);
                return newTable;
            }
        }
    }

    // Get appropriate file location, only works with "world" for now.
    private static String getFilePath(NamespacedKey location) {
        //TODO: add world selection capability
        return Bukkit.getWorld("world").getWorldFolder() + File.separator + "data" + File.separator + "loot_tables" + File.separator + location.getNamespace() + File.separator + location.getKey() + ".json";
    }

    public static File getFile(NamespacedKey location) {
        String url = getFilePath(location);
        String baseUrl = FilenameUtils.getPath(url);
        String fileName = FilenameUtils.getBaseName(url) + "." + FilenameUtils.getExtension(url);
        try {
            File path = new File(baseUrl);
            if (! path.exists()) {
                path.mkdirs();
            }
            File file = new File(baseUrl + fileName);
            if (! file.exists()) {
                file.createNewFile();
            }
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Load a loot able from a given {@link NamespacedKey} Will return an empty {@link LootTable} if any errors occur.
     *
     * @param location the {@link NamespacedKey} that contains the path to the appropriate json file (without file
     *                 extensions)
     *
     * @return the appropriate {@link LootTable} if successful, otherwise an empty {@link LootTable} is returned.
     * @throws IOException         thrown if there is an error getting the json file.
     * @throws JsonSyntaxException thrown if there is a json syntax error within the file.
     */
    public static LootTable load(NamespacedKey location) throws IOException, JsonSyntaxException {
        if (location.getKey().contains(".")) {
            Bukkit.getLogger().log(Level.WARNING, "Invalid loot table name \'" + location + "\' (can\'t contain periods)");
            return LootTable.emptyLootTable();
        } else {
            LootTable lootTable = loadLootTable(location);
            if (lootTable == null) {
                lootTable = loadBuiltInTable(location);
            }

            if (lootTable == null) {
                return LootTable.emptyLootTable();
            }
            return lootTable;
        }
    }

    /* Get a loot table by a ResourceLocation
    *  Not used to get the built-in loot tables (in assets/...)
    * */
    private static LootTable loadLootTable(NamespacedKey resource) {
        File file = getFile(resource);
        if (file.exists()) {
            if (file.isFile()) {
                String s;
                try {
                    s = Files.toString(file, Charsets.UTF_8);
                } catch (IOException e) {
                    Bukkit.getLogger().log(Level.WARNING, "Couldn\'t load the loot table at " + resource + " from " + file, e);
                    return LootTable.emptyLootTable();
                }

                try {
                    return gson.fromJson(s, LootTable.class);
                } catch (JsonParseException e) {
                    Bukkit.getLogger().log(Level.SEVERE, "Couldn\'t load loot table " + resource + " from " + file, e);
                    return LootTable.emptyLootTable();
                }
            } else {
                Bukkit.getLogger().log(Level.WARNING, "Expected to find loot table " + resource + "at " + file + " but it is a folder");
                return LootTable.emptyLootTable();
            }
        } else {
            try {
                file.createNewFile();
                return LootTable.emptyLootTable();
            } catch (IOException e) {
                return LootTable.emptyLootTable();
            }
        }
    }

    /* Get the built-in Minecraft resource tables and see if that given ResourceLocation is there.
        NOT used for anything other than default loot tables.
     */
    private static LootTable loadBuiltInTable(NamespacedKey location) {
        URL url = Bukkit.class.getResource("/assets/" + location.getNamespace() + "/loot_tables/" + location.getKey() + ".json");
        if (url != null) {
            String s;

            try {
                s = Resources.toString(url, Charsets.UTF_8);
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.WARNING, "Couldn't load loot table " + location + " from " + url, e);
                return LootTable.emptyLootTable();
            }

            try {
                return gson.fromJson(s, LootTable.class);
            } catch (JsonParseException e) {
                Bukkit.getLogger().log(Level.WARNING, "Couldn't load loot table " + location + " from " + url);
                return LootTable.emptyLootTable();
            }
        } else {
            return null;
        }
    }
}
