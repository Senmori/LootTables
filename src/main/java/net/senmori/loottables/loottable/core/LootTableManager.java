package org.bukkit.craftbukkit.loottable.core;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.loottable.conditions.LootCondition;
import org.bukkit.craftbukkit.loottable.conditions.LootConditionManager;
import org.bukkit.craftbukkit.loottable.entry.LootEntry;
import org.bukkit.craftbukkit.loottable.entry.LootEntryAdapter;
import org.bukkit.craftbukkit.loottable.functions.LootFunction;
import org.bukkit.craftbukkit.loottable.functions.LootFunctionManager;
import org.bukkit.util.ResourceLocation;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.logging.Level;

/*
   TODO: handle per-world loot tables. (is this already a thing?)
   TODO: handle plugins registering their specific namespaces.
*/

/**
 * This class should only be instantiated once, preferably inside CraftBukkit.
 */
public final class LootTableManager {

    private static Gson gson;
    /* ConcurrentHashMap because it's thread safe, supposedly */
	private static HashMap<ResourceLocation, LootTable> registeredLootTables = new HashMap<ResourceLocation, LootTable>();
    //private static Set<String> registeredDomains = new HashSet<>();
    private final File baseFolder;

    static {
        gson = (new GsonBuilder()
                        .registerTypeHierarchyAdapter(LootEntry.class, new LootEntryAdapter())
                        .registerTypeHierarchyAdapter(LootCondition.class, new LootConditionManager.Serializer())
                        .registerTypeHierarchyAdapter(LootFunction.class, new LootFunctionManager.Serializer())
                        .registerTypeAdapter(RandomValueRange.class, new RandomValueRange.Serializer())
                        .registerTypeAdapter(LootPool.class, new LootPool.Serializer())
                        .registerTypeAdapter(LootTable.class, new LootTable.Serializer())
                        .setPrettyPrinting().create());
    }


    public LootTableManager(File folder) {
        this.baseFolder = folder;
        // reload all loot tables for re-validation.
        registeredLootTables.clear();
        for(ResourceLocation location : registeredLootTables.keySet()) {
            LootTableManager.getLootTable(location);
        }
    }

    public static Gson getGson() { return gson; }

    public static HashMap<ResourceLocation, LootTable> getRegisteredLootTables() { return registeredLootTables; }

    /**
     * Get a {@link ResourceLocation} that matches the given path.<br>
     *     Do not include the domain. (i.e. "minecraft")
     * @param resourcePath - the path to the loot table, without file extendsions.
     * @return the {@link ResourceLocation} that has the matching resourcePath, or null.
     */
    public static ResourceLocation getResourceLocation(String resourcePath) {
        for(ResourceLocation location : registeredLootTables.keySet()) {
            if(location.getResourcePath().equals(resourcePath)) {
                return location;
            }
        }
        return null;
    }

    /**
     * Get a {@link ResourceLocation} that matches the given domain and path.
     * @param resourceDomain - the namespace of the plugin(i.e. "minecraft")
     * @param resourcePath - the path to the loot table, without file extensions.
     * @return the {@link ResourceLocation} that has the matching arguments, or null.
     */
    public static ResourceLocation getResourceLocation(String resourceDomain, String resourcePath) {
        for(ResourceLocation loc : registeredLootTables.keySet()) {
            if(loc.getResourceDomain().equals(resourceDomain) && loc.getResourcePath().equals(resourcePath)) {
                return loc;
            }
        }
        return null;
    }

    /**
     * Add a {@link ResourceLocation} which will generate the .json file. <br>
     * If a .json file is not already created, it will create one and return it. <br>
     * If the {@link LootTable} is already loaded, it will return that. <br>
     * @param resource the {@link ResourceLocation} that has the matching arguments, or null.
     */
    public static LootTable getLootTable(ResourceLocation resource) {
        return getLootTable(resource, false);
    }

    public static LootTable getLootTable(ResourceLocation resource, boolean forceReload) {
        if (forceReload) {
            File file = getFile(resource);
            if (file != null && file.exists()) {
                try {
                    LootTable table = load(resource);
                    table.setResourceLocation(resource);
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
                    table.setResourceLocation(resource);
                    return table;
                } catch (IOException e) {
                    Bukkit.getLogger().log(Level.WARNING, "Couldn't load loot table " + resource.toString());
                    return LootTable.emptyLootTable();
                }
            } else {
                // no LootTable file exists, create new file, and return empty LootTable
                LootTable newTable = LootTable.emptyLootTable();
                registeredLootTables.put(resource, newTable);
                newTable.setResourceLocation(resource);
                return newTable;
            }
        }
    }

    // Get appropriate file location, only works with "world" for now.
    private static String getFilePath(ResourceLocation location) {
        return Bukkit.getWorld(location.getResourceWorld()).getWorldFolder() + File.separator + "data" + File.separator + "loot_tables" + File.separator + location.getResourceDomain() + File.separator + location.getResourcePath() + ".json";
    }

    public static File getFile(ResourceLocation location) {
        String url = getFilePath(location);
        String baseUrl = FilenameUtils.getPath(url);
        String fileName = FilenameUtils.getBaseName(url) + "." + FilenameUtils.getExtension(url);
        try {
            File path = new File(baseUrl);
            if (!path.exists()) { path.mkdirs(); }
            File file = new File(baseUrl + fileName);
            if (!file.exists()) { file.createNewFile(); }
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Load a loot able from a given {@link ResourceLocation}
     * Will return an empty {@link LootTable} if any errors occur.
     *
     * @param location the {@link ResourceLocation} that contains the path to the appropriate json file (without file extensions)
     * @return the appropriate {@link LootTable} if successful, otherwise an empty {@link LootTable} is returned.
     * @throws IOException thrown if there is an error getting the json file.
     * @throws JsonSyntaxException thrown if there is a json syntax error within the file.
     */
    public static LootTable load(ResourceLocation location) throws IOException, JsonSyntaxException {
        if(location.getResourcePath().contains(".")) {
            Bukkit.getLogger().log(Level.WARNING, "Invalid loot table name \'" + location + "\' (can\'t contain periods)");
            return LootTable.emptyLootTable();
        } else {
            LootTable lootTable = loadLootTable(location);
            if(lootTable == null) {
                lootTable = loadBuiltInTable(location);
            }

            if(lootTable == null) {
                return LootTable.emptyLootTable();
            }
            return lootTable;
        }
    }

    /* Get a loot table by a ResourceLocation
    *  Not used to get the built-in loot tables (in assets/...)
    * */
    private static LootTable loadLootTable(ResourceLocation resource) {
        File file = getFile(resource);
        if(file.exists()) {
            if(file.isFile()) {
                String s;
                try {
                    s = Files.toString(file, Charsets.UTF_8);
                } catch(IOException e) {
                    Bukkit.getLogger().log(Level.WARNING, "Couldn\'t load the loot table at " + resource + " from " + file, e);
                    return LootTable.emptyLootTable();
                }

                try {
                    return gson.fromJson(s, LootTable.class);
                } catch(JsonParseException e) {
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
            }catch(IOException e) {
                return LootTable.emptyLootTable();
            }
        }
    }

    /* Get the built-in Minecraft resource tables and see if that given ResourceLocation is there.
        NOT used for anything other than default loot tables.
     */
    private static LootTable loadBuiltInTable(ResourceLocation location) {
        URL url = Bukkit.class.getResource("/assets/" + location.getResourceDomain() + "/loot_tables/" + location.getResourcePath() + ".json");
        if(url != null) {
            String s;

            try {
                s = Resources.toString(url, Charsets.UTF_8);
            } catch(IOException e) {
                Bukkit.getLogger().log(Level.WARNING, "Couldn't load loot table " + location + " from " + url, e);
                return LootTable.emptyLootTable();
            }

            try {
                return gson.fromJson(s, LootTable.class);
            }catch(JsonParseException e) {
                Bukkit.getLogger().log(Level.WARNING, "Couldn't load loot table " + location + " from " + url);
                return LootTable.emptyLootTable();
            }
        } else {
            return null;
        }
    }
}
