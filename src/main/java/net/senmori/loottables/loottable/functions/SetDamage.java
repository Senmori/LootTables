package org.bukkit.craftbukkit.loottable.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.bukkit.craftbukkit.loottable.conditions.LootCondition;
import org.bukkit.craftbukkit.loottable.core.LootContext;
import org.bukkit.craftbukkit.loottable.core.RandomValueRange;
import org.bukkit.craftbukkit.loottable.utils.JsonUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.ResourceLocation;

import java.util.List;
import java.util.Random;


public class SetDamage extends LootFunction {
    private RandomValueRange range;

    /**
     * Sets the item's damage value(durability) for tools.
     *
     * @param range Specifies a random damage amount from 0.0 to 1.0.
     *              Where 0.0 is zero durability, and 1.0 is undamaged.
     * @param conditions the {@link LootCondition}s that must be passed before calculation
     */
    public SetDamage(RandomValueRange range, List<LootCondition> conditions) {
        super(conditions);
        this.range = range;
    }

    /**
     * Sets the item's damage value(durability) for tools.
     * {@link LootCondition}s are null-valued with this constructor.
     *
     * @param range Specifies a random damage amount from 0.0 to 1.0.
     *              Where 0.0 is zero durability, and 1.0 is undamaged.
     */
    public SetDamage(RandomValueRange range) {
        this(range, null);
    }

    /** Set the minimum damage amount */
    public void setMin(int min) {
        int max = (int) range.getMax();
        range = new RandomValueRange(min, max);
    }

    /** Set the maximum damage amount */
    public void setMax(int max) {
        int min = (int) range.getMin();
        range = new RandomValueRange(min, max);
    }

    /** Set the new minimum and maximum damage amounts */
    public void setRange(int min, int max) {
        range = new RandomValueRange(min, max);
    }

    @Override
    public ItemStack apply(ItemStack itemstack, Random rand, LootContext context) {
        itemstack.setDurability((short) range.generateInt(rand));
        return itemstack;
    }

    public RandomValueRange getRange() { return this.range; }


    public static class Serializer extends LootFunction.Serializer<SetDamage> {
        protected Serializer() { super(new ResourceLocation("set_count"), SetDamage.class); }

        @Override
        public void serialize(JsonObject json, SetDamage type, JsonSerializationContext context) {
            json.add("damage", context.serialize(type.range));
        }

        @Override
        public SetDamage deserialize(JsonObject json, JsonDeserializationContext context, List<LootCondition> conditions) {
            return new SetDamage(JsonUtils.deserializeClass(json, "damage", context, RandomValueRange.class), conditions);
        }
    }
}
