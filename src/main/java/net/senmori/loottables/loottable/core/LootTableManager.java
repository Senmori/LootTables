package net.senmori.loottables.loottable.core;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
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
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

/**
 * This class should only be instantiated once, preferably inside CraftBukkit.
 */
public final class LootTableManager {
    private static LootTableManager INSTANCE = new LootTableManager();

    private Gson gson;
    private final String baseDir = Bukkit.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    /* ConcurrentHashMap because it's thread safe, supposedly */
    private HashMap<NamespacedKey, LootTable> registeredLootTables = Maps.newHashMap();
    public final UUID defWorldUUID;


    public static LootTableManager getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new LootTableManager();
        }
        return INSTANCE;
    }

    private LootTableManager() {
        gson = ( new GsonBuilder()
                         .registerTypeHierarchyAdapter(LootEntry.class, new LootEntryAdapter())
                         .registerTypeHierarchyAdapter(LootCondition.class, new LootConditionManager.Serializer())
                         .registerTypeHierarchyAdapter(LootFunction.class, new LootFunctionManager.Serializer())
                         .registerTypeAdapter(RandomValueRange.class, new RandomValueRange.Serializer())
                         .registerTypeAdapter(LootPool.class, new LootPool.Serializer())
                         .registerTypeAdapter(LootTable.class, new LootTable.Serializer())
                         .setPrettyPrinting().create() );
        defWorldUUID = loadDefaultWorldUUID();
    }

    private UUID loadDefaultWorldUUID() {
        CraftServer cServer = (CraftServer)Bukkit.getServer();
        String levelName = cServer.getServer().getPropertyManager().getString("level-name", "world");
        return Bukkit.getWorld(levelName).getUID();
    }

    public Gson getGson() {
        return gson;
    }

    public HashMap<NamespacedKey, LootTable> getRegisteredLootTables() {
        return registeredLootTables;
    }

    /**
     * Get a {@link NamespacedKey} that matches the given path.<br> Do not include the domain. (i.e. "minecraft")
     *
     * @param resourcePath - the path to the loot table, without file extendsions.
     *
     * @return the {@link NamespacedKey} that has the matching resourcePath, or null.
     */
    public NamespacedKey getKey(String resourcePath) {
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
     * @param resourceDomain - the namespace of the plugin(i.e. "minecraft", or the plugin name)
     * @param resourcePath   - the path to the loot table, without file extensions.
     *
     * @return the {@link NamespacedKey} that has the matching arguments, or null.
     */
    public NamespacedKey getKey(String resourceDomain, String resourcePath) {
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
     * @param world the {@link World} this loot table is stored in
     * @param path the {@link NamespacedKey} containing the namespace and the path to the loot table
     * @param datapackName the name of the datapack
     */
    public LootTable getLootTable(World world, NamespacedKey path, String datapackName) {
        return getLootTable(world, path, datapackName, false);
    }

    public LootTable getLootTable(World world, NamespacedKey path, String datapackName, boolean forceReload) {
        if (forceReload) {
            File file = getFile(world, path, datapackName);
            if (file != null && file.exists()) {
                try {
                    LootTable table = load(world, path, datapackName);
                    table.path(path);
                    registeredLootTables.put(path, table);
                    return table;
                } catch (IOException e) {
                    Bukkit.getLogger().log(Level.WARNING, "Couldn't load loot table " + path.toString());
                    return LootTable.emptyLootTable();
                }
            }
        }
        if (registeredLootTables.containsKey(path)) {
            return registeredLootTables.get(path);
        } else {
            File file = getFile(world, path, datapackName);
            if (file != null && file.exists()) {
                // load file
                try {
                    LootTable table = load(world, path, datapackName);
                    registeredLootTables.put(path, table);
                    table.path(path);
                    return table;
                } catch (IOException e) {
                    Bukkit.getLogger().log(Level.WARNING, "Couldn't load loot table " + path.toString());
                    return LootTable.emptyLootTable();
                }
            } else {
                // no LootTable file exists, create new file, and return empty LootTable
                LootTable newTable = LootTable.emptyLootTable();
                registeredLootTables.put(path, newTable);
                newTable.path(path);
                return newTable;
            }
        }
    }

    /**
     *  Get loot table file path from the server datapack location<br>
     *  The location for loot tables is data/<namespace>/loot_tables/<path_to_table> <br>
     *  Do NOT include file extensions.
     */
    private String getFilePath(World world, NamespacedKey path, String datapackName) {
        // <world>/datapacks/<datapack name>/data/<namespace>/loot_tables/<path/to/table>
        if(world == null) {
            world = Bukkit.getWorld(defWorldUUID);
        }
        return world.getWorldFolder() + File.separator + "datapacks"
                                      + File.separator + datapackName
                                      + File.separator + "data"
                                      + File.separator + path.getNamespace()
                                      + File.separator  + "loot_tables"
                                      + File.separator + path.getKey()
                                      + ".json";
    }

    public File getFile(World world, NamespacedKey path, String datapackName) {
        String url = getFilePath(world, path, datapackName);
        String baseUrl = FilenameUtils.getPath(url);
        String fileName = FilenameUtils.getBaseName(url) + "." + FilenameUtils.getExtension(url);
        try {
            File baseFile = new File(baseUrl);
            if (! baseFile.exists()) {
                baseFile.mkdirs();
            }
            File file = new File(baseUrl + fileName);
            if (!file.exists()) {
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
     * @param path the {@link NamespacedKey} that contains the path to the appropriate json file (without file
     *                 extensions)
     *
     * @return the appropriate {@link LootTable} if successful, otherwise an empty {@link LootTable} is returned.
     * @throws IOException         thrown if there is an error getting the json file.
     * @throws JsonSyntaxException thrown if there is a json syntax error within the file.
     */
    public LootTable load(World world, NamespacedKey path, String datapackName) throws IOException, JsonSyntaxException {
        if (!LootTable.VALID_CHARS_PATTERN.matcher(path.getKey()).matches()) {
            Bukkit.getLogger().log(Level.WARNING, "Invalid loot table name \'" + path + ". Can only contain \'a-z0-9-_\'");
            return LootTable.emptyLootTable();
        } else {
            LootTable lootTable = loadLootTable(world, path, datapackName);
            if (lootTable == null) {
                lootTable = loadBuiltInTable(world, path, datapackName);
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
    private LootTable loadLootTable(World world, NamespacedKey path, String datapackName) {
        File file = getFile(world, path, datapackName);
        if (file != null && file.exists()) {
            if (file.isFile()) {
                String s;
                try {
                    s = Files.toString(file, Charsets.UTF_8);
                } catch (IOException e) {
                    Bukkit.getLogger().log(Level.WARNING, "Couldn\'t load the loot table at " + path.toString() + " from " + file, e);
                    return LootTable.emptyLootTable();
                }

                try {
                    return gson.fromJson(s, LootTable.class);
                } catch (JsonParseException e) {
                    Bukkit.getLogger().log(Level.SEVERE, "Couldn\'t load loot table " + path.toString() + " from " + file, e);
                    return LootTable.emptyLootTable();
                }
            } else {
                Bukkit.getLogger().log(Level.WARNING, "Expected to find loot table " + path.toString() + "at " + file + " but it is a folder");
                return LootTable.emptyLootTable();
            }
        } else {
            try {
                file.createNewFile();

                return LootTable.emptyLootTable().path(path);
            } catch (IOException e) {
                return LootTable.emptyLootTable().path(path);
            }
        }
    }

    /* Get the built-in Minecraft resource tables and see if that given path is there.
        NOT used for anything other than default loot tables.
     */
    private LootTable loadBuiltInTable(World world, NamespacedKey path, String datapackName) {
        URL url = Bukkit.class.getResource("/data/minecraft/loot_tables/" + path.getKey() + ".json");
        if (url != null) {
            String s;

            try {
                s = Resources.toString(url, Charsets.UTF_8);
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.WARNING, "Couldn't load loot table " + path + " from " + url, e);
                return LootTable.emptyLootTable();
            }

            try {
                return gson.fromJson(s, LootTable.class);
            } catch (JsonParseException e) {
                Bukkit.getLogger().log(Level.WARNING, "Couldn't load loot table " + path + " from " + url);
                return LootTable.emptyLootTable();
            }
        } else {
            return null;
        }
    }


    /*
     * ###########################################
     * SAVE METHODS
     * ###########################################
     */
    /** Save the LootTable, rewriting all currently written data */
    public boolean save(LootTable table, World world, String datapackName) {
        Validate.notNull(table, "Loot Table cannot be null");
        Validate.notNull(table.getKey(), "NamespacedKey cannot be null");

        NamespacedKey location = table.getKey();
        File file = getFile(world, location, datapackName);
        if (file != null) {
            try(FileWriter writer = new FileWriter(file)) {
                writer.write(""); // clear file of all data
                String json = LootTableManager.getInstance().getGson().toJson(this);
                writer.write(json);
                writer.close();
                updateManager(table);
                return true;
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.WARNING, "Couldn't create loot table \'" + location + "\' at file \'" + location.getKey() + "\'");
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    private void updateManager(LootTable table) {
        registeredLootTables.put(table.getKey(), table);
    }

}
