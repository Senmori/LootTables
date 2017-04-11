package org.bukkit.craftbukkit.loottable.entry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.bukkit.craftbukkit.loottable.conditions.LootCondition;
import org.bukkit.craftbukkit.loottable.core.LootContext;
import org.bukkit.craftbukkit.loottable.core.LootTable;
import org.bukkit.craftbukkit.loottable.core.LootTableManager;
import org.bukkit.craftbukkit.loottable.utils.JsonUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.ResourceLocation;

import java.util.Collection;
import java.util.List;
import java.util.Random;


public class LootEntryTable extends LootEntry {

    private ResourceLocation lootTableLocation;

    public LootEntryTable(ResourceLocation location, int weight, int quality, List<LootCondition> conditions) {
        super(weight, quality, conditions);
        this.lootTableLocation = location;
    }

    public LootEntryTable(ResourceLocation location, int weight, int quality) {
        this(location, weight, quality, null);
    }

    public ResourceLocation getLootTableLocation() { return this.lootTableLocation; }

    @Override
    public void addLoot(Collection<ItemStack> itemStacks, Random rand, LootContext context) {
        LootTable table = LootTableManager.getLootTable(lootTableLocation);
        List coll = table.generateLootForPools(rand, context);
        itemStacks.addAll(coll);
    }


    @Override
    protected void serialize(JsonObject json, JsonSerializationContext context) {
        json.addProperty("name", this.lootTableLocation.toString());
    }


    public static LootEntryTable deserialize(JsonObject jsonObject, JsonDeserializationContext context, int weight, int quality, List<LootCondition> conditions) {
        ResourceLocation rLocation = new ResourceLocation(JsonUtils.getString(jsonObject, "name"));
        return new LootEntryTable(rLocation, weight, quality, conditions);
    }
}
