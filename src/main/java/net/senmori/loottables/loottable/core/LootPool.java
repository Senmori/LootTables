package org.bukkit.craftbukkit.loottable.core;

import com.google.common.base.Preconditions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.loottable.adapter.InheritanceAdapter;
import org.bukkit.craftbukkit.loottable.conditions.LootCondition;
import org.bukkit.craftbukkit.loottable.conditions.LootConditionManager;
import org.bukkit.craftbukkit.loottable.entry.LootEntry;
import org.bukkit.craftbukkit.loottable.entry.LootEntryEmpty;
import org.bukkit.craftbukkit.loottable.entry.LootEntryItem;
import org.bukkit.craftbukkit.loottable.entry.LootEntryTable;
import org.bukkit.craftbukkit.loottable.functions.SetCount;
import org.bukkit.craftbukkit.loottable.functions.SetNBT;
import org.bukkit.craftbukkit.loottable.utils.JsonUtils;
import org.bukkit.craftbukkit.loottable.utils.MathHelper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.ResourceLocation;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class LootPool {

    /** Default {@link RandomValueRange} that specifies no BonusRolls (min & max of 0.0f)*/
	public static RandomValueRange noBonusRollsRange = new RandomValueRange(0.0f, 0.0f);

    protected List<LootEntry> entries = new ArrayList<>();
    protected List<LootCondition> conditions = new ArrayList<>();
    protected RandomValueRange rolls;
	protected RandomValueRange bonusRolls;
    protected String name;

    /**
     * LootPool is a collection of {@link LootEntry}s and {@link LootCondition}s, which will generate a random set of loot.
     * @param name the name of this LootPool. (Only used internally to identify LootPools)
     * @param entries a list of {@link LootEntry} that will be used to generate a loot item.
     * @param poolConditions a list of {@link LootCondition} that must pass for a loot item to be generated.
     * @param rolls how many times this LootPool will attempt to generate a loot item
     * @param bonusRolls how many <i>extra</i> times this LootPool will attempt to generate a loot item
     */
    public LootPool(String name, List<LootEntry> entries, List<LootCondition> poolConditions, RandomValueRange rolls, RandomValueRange bonusRolls) {
        if (entries != null) { this.entries.addAll(entries); }
        if (poolConditions != null) { this.conditions.addAll(conditions); }
        this.rolls = rolls;
		this.bonusRolls = bonusRolls;
        this.name = name;
    }

    /**
     * LootPool is a collection of {@link LootEntry}s and {@link LootCondition}s, which will generate a random set of loot.
     * This constructor null-values all {@link LootEntry}s, and {@link LootCondition}s, as well as default BonusRolls to 0.0f
     *
     * @param name the name of this LootPool (only used internally to identify LootPools)
     * @param rolls how many times this LootPool will attempt to generate a loot item.
     */
    public LootPool(String name, float rolls) {
        new LootPool(name, null, null, new RandomValueRange(rolls), noBonusRollsRange);
    }

    public void generateLoot(Collection<ItemStack> stacks, Random rand, LootContext context) {
        if (LootConditionManager.testAllConditions(this.getConditions(), rand, context)) {
            int r = this.rolls.generateInt(rand) + MathHelper.floorFloat(this.bonusRolls.generateFloat(rand) * context.getLuck());

            for (int i = 0; i < r; i++) {
                this.createLootRoll(stacks, rand, context);
            }
        }
    }

    protected void createLootRoll(Collection<ItemStack> stacks, Random rand, LootContext context) {
        ArrayList list = new ArrayList<>();
        int i = 0;
        for (LootEntry e : this.entries) {
            if (LootConditionManager.testAllConditions(e.getConditions(), rand, context)) {
                int qual = e.getEffectiveQuality(context.getLuck());
                if (qual > 0) {
                    list.add(e);
                    i += qual;
                }
            }
        }

        if (i != 0 && !list.isEmpty()) {
            int num = rand.nextInt(i);
            Iterator iter = list.iterator();

            while (iter.hasNext()) {
                LootEntry entry = (LootEntry) iter.next();
                num -= entry.getEffectiveQuality(context.getLuck());
                if (num < 0) {
                    entry.addLoot(stacks, rand, context);
                    return;
                }
            }
        }
    }

    /** Add a new {@link LootEntry} to this pool */
    public void addLootEntry(LootEntry entry) {
        this.entries.add(entry);
    }

    /**
     * Add a new {@link ItemStack} to this LootPool.
     *
     * @param stack the {@link ItemStack} to add.
     * @param weight Determines how often this entry will be chosen out of all entries in this pool.
     * @param quality Modifies the {@param weight} based on the killing/opening/fishing player's luck attribute.
     *                Formula: floor(weight + (quality * generic.luck) )
     */
    public void addLootEntry(ItemStack stack, int weight, int quality) {
        LootEntryItem item = new LootEntryItem(stack.getType(), weight, quality, null, null);
        if (stack.getAmount() > 1) {
            item.addFunction(new SetCount(new RandomValueRange(stack.getAmount()), null));
        }

        if (CraftItemStack.asNMSCopy(stack).hasTag()) { // because this will override every other function anyways
            SetNBT function = new SetNBT(CraftItemStack.asNMSCopy(stack).getTag().toString(), null);
            item.addFunction(function);
        }
        addLootEntry(item);
    }


    /**
     * Add a new {@link ItemStack} from the {@link Material} given.
     *
     * @param material the {@link ItemStack} that will be made from this {@link Material}
     * @param weight Determines how often this entry will be chosen out of all entries in this pool.
     * @param quality Modifies the {@param weight} based on the killing/opening/fishing player's luck attribute.
     *                Formula: floor(weight + (quality * generic.luck) )
     */
    public void addLootEntry(Material material, int weight, int quality) {
        addLootEntry(new LootEntryItem(material, weight, quality, null, null));
    }

    /**
     * Add a {@link LootTable} as another valid location to generate rewards from.
     *
     * @param location the {@link ResourceLocation} of another {@link LootTable} to pick rewards from.
     * @param weight Determines how often this entry will be chosen out of all entries in this pool.
     * @param quality Modifies the {@param weight} based on the killing/opening/fishing player's luck attribute.
     *                Formula: floor(weight + (quality * generic.luck) )
     */
    public void addLootTableEntry(ResourceLocation location, int weight, int quality) {
        addLootEntry(new LootEntryTable(location, weight, quality, null));
    }

    /**
     * Add an emtpy {@link LootEntry}.
     * This is valid because players <i>can</i> receive nothing as a valid reward.
     *
     * @param weight Determines how often this entry will be chosen out of all entries in this pool.
     * @param quality Modifies the {@param weight} based on the killing/opening/fishing player's luck attribute.
     *                Formula: floor(weight + (quality * generic.luck) )
     */
    public void addEmptyLootEntry(int weight, int quality) {
        addLootEntry(new LootEntryEmpty(weight, quality, null));
    }

    public void addLootCondition(LootCondition condition) {
        Preconditions.checkArgument(condition != null);
        conditions.add(condition);
    }

    /**
     * Returns a {@link RandomValueRange} set that can contain a minimum, and maximum value. <br>
     * If minimum < maximum, then the loot table will pick a random number between them, and will <br>
     * generate that many loot items, if all associated conditions pass.
     * @return
     */
    public RandomValueRange getRolls() {return rolls == null ? RandomValueRange.emptyRange : rolls;}

    /**
     * Returns a {@link RandomValueRange} set that can contain a minimum and maximum value.<br>
     * If minimum < maximum, then the loot table will pick a random number between them, and will <br>
     * attempt to generate that many extra loot item(s), if all associated conditions pass.
     * @return bonusRolls if valid, otherwise an empty RandomValueRange is returned.
     */
    public RandomValueRange getBonusRolls() {
        return bonusRolls == null ? RandomValueRange.emptyRange : bonusRolls;
    }

	public List<LootEntry> getEntries() { return entries; }
	public List<LootCondition> getConditions() { return conditions; }
    public String getName() { return this.name; }


	public static class Serializer extends InheritanceAdapter<LootPool> {
		public Serializer() {}

		@Override
		public LootPool deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
			JsonObject jsonObject = JsonUtils.getJsonObject(jsonElement, "loot pool");
            String name = JsonUtils.getString(jsonObject, "name");
            LootEntry[] lootEntries = JsonUtils.deserializeClass(jsonObject, "entries", context, LootEntry[].class);
			LootCondition[] lootConditions = JsonUtils.deserializeClass(jsonObject, "conditions", new LootCondition[0], context, LootCondition[].class);
			RandomValueRange rolls = JsonUtils.deserializeClass(jsonObject, "rolls", context, RandomValueRange.class);
			RandomValueRange bonusRolls = JsonUtils.deserializeClass(jsonObject, "bonus_rolls", new RandomValueRange(0.0F, 0.0F), context, RandomValueRange.class);
            return new LootPool(name, Arrays.asList(lootEntries), Arrays.asList(lootConditions), rolls, bonusRolls);
        }

		@Override
		public JsonElement serialize(LootPool pool, Type type, JsonSerializationContext context) {
			JsonObject json = new JsonObject();
            json.add("name", new JsonPrimitive(pool.name));
            json.add("rolls", context.serialize(pool.rolls));
			if (pool.bonusRolls != null && pool.bonusRolls.getMin() != 0.0F && pool.bonusRolls.getMax() != 0.0F) {
				json.add("bonus_rolls", context.serialize(pool.bonusRolls));
			}
			json.add("entries", context.serialize(pool.entries));
			if (pool.conditions != null && !pool.conditions.isEmpty()) {
				context.serialize(pool.conditions);
			}
			return json;
		}
	}

}
