package net.senmori.loottables.loottable.entry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import net.senmori.loottables.loottable.conditions.LootCondition;
import net.senmori.loottables.loottable.core.LootContext;
import net.senmori.loottables.loottable.core.LootTable;
import net.senmori.loottables.loottable.core.LootTableManager;
import net.senmori.loottables.loottable.utils.JsonUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.List;
import java.util.Random;


public class LootEntryTable extends LootEntry {

    private NamespacedKey lootTableLocation;

    public LootEntryTable(NamespacedKey location, int weight, int quality, List<LootCondition> conditions) {
        super(weight, quality, conditions);
        this.lootTableLocation = location;
    }

    public LootEntryTable(NamespacedKey location, int weight, int quality) {
        this(location, weight, quality, null);
    }

    public NamespacedKey getLootTableLocation() {
        return this.lootTableLocation;
    }

    @Override
    public void addLoot(Collection<ItemStack> itemStacks, Random rand, LootContext context) {
        LootTable table = LootTableManager.getLootTable(lootTableLocation);
        List<ItemStack> coll = table.generateLootForPools(rand, context);
        itemStacks.addAll(coll);
    }


    @Override
    protected void serialize(JsonObject json, JsonSerializationContext context) {
        json.addProperty("name", this.lootTableLocation.toString());
    }


    public static LootEntryTable deserialize(JsonObject jsonObject, JsonDeserializationContext context, int weight, int quality, List<LootCondition> conditions) {
        String name = JsonUtils.getString(jsonObject, "name");
        NamespacedKey rLocation = null;
        if (name.contains("minecraft")) {
            rLocation = NamespacedKey.minecraft(name);
        } else {
            Plugin plugin = Bukkit.getPluginManager().getPlugin(name);
            if (plugin != null) {
                rLocation = new NamespacedKey(plugin, name.substring(52));
            } else {
                throw new JsonSyntaxException(String.format("Invalid syntax. Required <namespace>:<id>. Found %s", name));
            }
        }
        return new LootEntryTable(rLocation, weight, quality, conditions);
    }
}
