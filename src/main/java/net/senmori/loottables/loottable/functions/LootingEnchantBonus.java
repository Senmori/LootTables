package net.senmori.loottables.loottable.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.senmori.loottables.loottable.conditions.LootCondition;
import net.senmori.loottables.loottable.core.LootContext;
import net.senmori.loottables.loottable.core.RandomValueRange;
import net.senmori.loottables.loottable.utils.JsonUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;


public class LootingEnchantBonus extends LootFunction {

    private RandomValueRange count;
    private int limit;

    /**
     * Adjusts the stack size based on the level of the Looting {@link Enchantment} on the killer entity.
     *
     * @param count      Specifies an exact number of additional items per level of looting. Or it can be a list of two
     *                   numbers which specify the minimum and maximum number of additional items.
     * @param limit      Specifies the maximum amount of items in the stack after caclulation. If value is 0, no limit
     *                   is applied.
     * @param conditions the {@link LootCondition}s that are applied afterwards.
     */
    public LootingEnchantBonus(RandomValueRange count, int limit, List<LootCondition> conditions) {
        super(conditions);
        this.count = count;
        this.limit = limit < 0 ? 0 : limit;
    }

    /**
     * Adjusts the stack size based on the level of the Looting {@link Enchantment} on the killer entity. The limit is
     * defaulted to 0, and the conditions are null-valued with this constructor.
     *
     * @param count Specifies an exact number of additional items per level of looting. Or it can be a list of two
     *              numbers which specify the minimum and maximum number of additional items.
     */
    public LootingEnchantBonus(RandomValueRange count) {
        this(count, 0, null);
    }

    /** Set the minimum amount of items to increase per level of looting */
    public void setMin(float min) {
        count.setMin(min);
    }

    /** Set the maximum amount of items to increase per level of looting */
    public void setMax(float max) {
        count.setMax(max);
    }

    /** Set the min and max of items to increase per level of looting */
    public void setRange(float min, float max) {
        count.setValues(min, max);
    }

    /** Set the maximum amount of items allowed after calculation, if limit is 0 no limit is applied */
    public void setLimit(int limit) {
        this.limit = limit < 0 ? 0 : limit;
    }

    @Override
    public ItemStack apply(ItemStack itemstack, Random rand, LootContext context) {
        Entity entity = context.getKiller();
        int i = 0;
        if (entity instanceof LivingEntity) {
            i = ( (LivingEntity) context.getKiller() ).getEquipment().getItemInMainHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);
        }
        if (i == 0) {
            return itemstack;
        }
        float f = (float) i * this.count.generateFloat(rand);
        itemstack.setAmount(itemstack.getAmount() + Math.round(f));

        if (limit != 0 && itemstack.getAmount() > this.limit) {
            itemstack.setAmount(this.limit);
        }
        return itemstack;
    }

    public RandomValueRange getCount() {
        return this.count;
    }

    public int getLimit() {
        return this.limit;
    }


    public static class Serializer extends LootFunction.Serializer<LootingEnchantBonus> {
        protected Serializer() {
            super(NamespacedKey.minecraft("looting_enchant"), LootingEnchantBonus.class);
        }

        @Override
        public void serialize(JsonObject json, LootingEnchantBonus type, JsonSerializationContext context) {
            json.add("count", context.serialize(type.count));

            if (type.limit > 0) {
                json.add("limit", context.serialize(Integer.valueOf(type.limit)));
            }
        }

        @Override
        public LootingEnchantBonus deserialize(JsonObject json, JsonDeserializationContext context, List<LootCondition> conditions) {
            int i = JsonUtils.getInt(json, "limit", 0);
            return new LootingEnchantBonus(JsonUtils.deserializeClass(json, "count", context, RandomValueRange.class), i, conditions);
        }
    }
}
