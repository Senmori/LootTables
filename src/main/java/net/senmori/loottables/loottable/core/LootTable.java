package net.senmori.loottables.loottable.core;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.senmori.loottables.loottable.adapter.InheritanceAdapter;
import net.senmori.loottables.loottable.utils.JsonUtils;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LootTable {
    public static final Pattern VALID_CHARS_PATTERN = Pattern.compile("^[a-z0-9-_]*$");

    private NamespacedKey location;
    private File file;
    private List<LootPool> pools = new ArrayList<>();

    public LootTable(List<LootPool> pools) {
        this.pools = pools;
    }

    public LootTable() {
        this.pools = new ArrayList<>();
    }

    /** Add another LootPool to this LootTable */
    public LootTable addLootPool(LootPool pool) {
        this.pools.add(pool);
        return this;
    }

    /** Set a new {@link NamespacedKey} for this LootTable */
    public LootTable path(NamespacedKey path) {
        Validate.notNull(path, "Path cannot be null");
        Validate.notEmpty(path.getKey(), "Path key cannot be empty ");
        Validate.isTrue(VALID_CHARS_PATTERN.matcher(path.getKey()).matches(), "Loot Table can only contain a-z, ");
        this.location = path;
        return this;
    }

    public List<ItemStack> generateLootForPools(Random rand, LootContext context) {
        List<ItemStack> items = new ArrayList<>();
        if (context.addLootTable(this)) {
            List<LootPool> poolList = this.pools;
            for (LootPool p : poolList) {
                p.generateLoot(items, rand, context);
            }
            context.removeLootTable(this);
        } else {
            Bukkit.getLogger().log(Level.WARNING, "Detected infinite loop in loot tables");
        }
        return items;
    }

    public static LootTable emptyLootTable() {
        return new LootTable(new ArrayList<>());
    }

    public List<LootPool> getLootPools() {
        return pools;
    }

    public NamespacedKey getResourceLocation() {
        return this.location;
    }

    /** Get the pool with the given name, if it doesn't exist return null */
    public LootPool getLootPool(String name) {
        for (LootPool p : pools) {
            if (p.getName().equals(name)) {
                return p;
            }
        }
        return null;
    }

    /** Save the LootTable */
    public boolean save() {
        return save(false);
    }

    /** Save the LootTable, rewriting all currently written data */
    public boolean save(boolean forceUpdate) {
        if (forceUpdate) {
            if (file != null) {
                try {
                    FileWriter writer = new FileWriter(file);
                    writer.write(""); // clear file of all data
                    String json = LootTableManager.getGson().toJson(this);
                    writer.write(json);
                    writer.close();
                } catch (IOException e) {
                    Bukkit.getLogger().log(Level.WARNING, "Couldn't create loot table \'" + location + "\' at file \'" + location.getKey() + "\'");
                    e.printStackTrace();
                    return false;
                }
            }
            updateManager();
            return true;
        }
        if (file != null && file.exists()) {
            try {
                FileWriter writer = new FileWriter(file);
                String json = LootTableManager.getGson().toJson(this);
                writer.write(json);
                writer.close();
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.WARNING, "Couldn't create loot table \'" + location + "\' at file \'" + location.getKey() + "\'");
                e.printStackTrace();
                return false;
            }
        } else { // file doesn't exist
            file = LootTableManager.getFile(location);
            try {
                FileWriter writer = new FileWriter(file);
                file.createNewFile();
                String json = LootTableManager.getGson().toJson(this);
                writer.write(json);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        updateManager();
        return true;
    }


    private void updateManager() {
        LootTableManager.getRegisteredLootTables().put(getResourceLocation(), this);
    }

    public static class Serializer extends InheritanceAdapter<LootTable> {

        public Serializer() {
        }

        public LootTable deserialize(JsonElement json, Type type, JsonDeserializationContext context) {
            JsonObject jsonObject = JsonUtils.getJsonObject(json, "loot table");
            LootPool[] lootPool = JsonUtils.deserializeClass(jsonObject, "pools", new LootPool[0], context, LootPool[].class);
            return new LootTable(Arrays.asList(lootPool));
        }

        public JsonElement serialize(LootTable table, Type type, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("pools", context.serialize(table.getLootPools()));
            return jsonObject;
        }
    }
}
