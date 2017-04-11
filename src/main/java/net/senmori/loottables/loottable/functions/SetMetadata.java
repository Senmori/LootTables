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


public class SetMetadata extends LootFunction {
    private RandomValueRange range;

    /**
     * Set the data value of the item.
     *
     * @param range Specify the minimum and maximum range of the random data value.
     * @param conditions the {@link LootCondition}(s) that must be passed before calculation.
     */
    public SetMetadata(RandomValueRange range, List<LootCondition> conditions) {
        super(conditions);
        this.range = range;
    }

    /**
     * Set the data value of the item.
     *
     * @param range Specify the minimum and maximum range of the random data value.
     */
    public SetMetadata(RandomValueRange range) {
        this(range, null);
    }

    /** Set the minimum data value */
    public void setMin(int min) {
        int max = (int) range.getMax();
        range = new RandomValueRange(min, max);
    }

    /** Set the maximum data value */
    public void setMax(int max) {
        int min = (int) range.getMin();
        range = new RandomValueRange(min, max);
    }

    /** Set the new minimum and maximum data values possible */
    public void setRange(int min, int max) {
        range = new RandomValueRange(min, max);
    }

    @SuppressWarnings("deprecation")
    @Override
    public ItemStack apply(ItemStack itemstack, Random rand, LootContext context) {
        itemstack.getData().setData((byte) range.generateInt(rand));
        return itemstack;
    }

    public RandomValueRange getRange() { return this.range; }


    public static class Serializer extends LootFunction.Serializer<SetMetadata> {
        protected Serializer() { super(new ResourceLocation("set_data"), SetMetadata.class); }

        @Override
        public void serialize(JsonObject json, SetMetadata type, JsonSerializationContext context) {
            json.add("data", context.serialize(type.range));
        }

        @Override
        public SetMetadata deserialize(JsonObject json, JsonDeserializationContext context, List<LootCondition> conditions) {
            return new SetMetadata(JsonUtils.deserializeClass(json, "data", context, RandomValueRange.class), conditions);
        }
    }
}
