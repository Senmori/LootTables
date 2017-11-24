package net.senmori.loottables.loottable.entry;

import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.senmori.loottables.loottable.conditions.LootCondition;
import net.senmori.loottables.loottable.core.LootContext;
import net.senmori.loottables.loottable.utils.MathHelper;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;


public abstract class LootEntry {

    protected int weight;
    protected int quality;
    protected List<LootCondition> conditions = new ArrayList<>();


    protected LootEntry(int weight, int quality, List<LootCondition> conditions) {
        this.weight = weight;
        this.quality = quality;
        this.conditions = conditions == null ? new ArrayList<>() : conditions;
    }

    public void addCondition(LootCondition condition) {
        conditions.add(condition);
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public int getEffectiveQuality(float luck) {
        return Math.max(MathHelper.floorFloat((float) this.weight + (float) this.quality * luck), 0);
    }

    public int getWeight() {
        return this.weight;
    }

    public int getQuality() {
        return this.quality;
    }

    public List<LootCondition> getConditions() {
        return this.conditions;
    }

    public abstract void addLoot(Collection<ItemStack> itemStacks, Random rand, LootContext context);

    protected abstract void serialize(JsonObject json, JsonSerializationContext context);
}
