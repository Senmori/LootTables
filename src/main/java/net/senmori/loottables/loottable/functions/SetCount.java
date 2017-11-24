package net.senmori.loottables.loottable.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.senmori.loottables.loottable.conditions.LootCondition;
import net.senmori.loottables.loottable.core.LootContext;
import net.senmori.loottables.loottable.core.RandomValueRange;
import net.senmori.loottables.loottable.utils.JsonUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;


public class SetCount extends LootFunction {
    private RandomValueRange range;

    /**
     * Sets the stack size.
     *
     * @param range      the minimum and maximum range of the stack size
     * @param conditions the {@link LootCondition}s that will be applied after calculation.
     */
    public SetCount(RandomValueRange range, List<LootCondition> conditions) {
        super(conditions);
        this.range = range;
    }

    /**
     * Sets the stack size. {@link LootCondition}s are null-valued with this constructor.
     *
     * @param range the minimum and maximum range of the stack size
     */
    public SetCount(RandomValueRange range) {
        this(range, null);
    }

    /** Set the minimum stack size of the range */
    public void setMin(int min) {
        int max = (int) range.getMax();
        range = new RandomValueRange(min, max);
    }

    /** Set the maximum stack size of the range */
    public void setMax(int max) {
        int min = (int) range.getMin();
        range = new RandomValueRange(min, max);
    }

    /** Set the new minimum & maximum values */
    public void setRange(int min, int max) {
        range = new RandomValueRange(min, max);
    }

    @Override
    public ItemStack apply(ItemStack itemstack, Random rand, LootContext context) {
        itemstack.setAmount(itemstack.getAmount() + range.generateInt(rand));
        return itemstack;
    }

    public RandomValueRange getRange() {
        return this.range;
    }

    public static class Serializer extends LootFunction.Serializer<SetCount> {
        protected Serializer() {
            super(NamespacedKey.minecraft("set_count"), SetCount.class);
        }

        @Override
        public void serialize(JsonObject json, SetCount type, JsonSerializationContext context) {
            json.add("count", context.serialize(type.range));
        }

        @Override
        public SetCount deserialize(JsonObject json, JsonDeserializationContext context, List<LootCondition> conditions) {
            return new SetCount(JsonUtils.deserializeClass(json, "count", context, RandomValueRange.class), conditions);
        }
    }
}
