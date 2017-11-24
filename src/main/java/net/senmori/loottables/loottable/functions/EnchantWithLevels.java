package net.senmori.loottables.loottable.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.senmori.loottables.loottable.conditions.LootCondition;
import net.senmori.loottables.loottable.core.LootContext;
import net.senmori.loottables.loottable.core.RandomValueRange;
import net.senmori.loottables.loottable.utils.EnchantmentHelper;
import net.senmori.loottables.loottable.utils.JsonUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

/**
 * Enchants the item with the specified enchantment level (roughly equivalent to using an enchantment table at that
 * level).
 */
public class EnchantWithLevels extends LootFunction {

    private RandomValueRange randomLevel;
    private boolean treasure;

    /**
     * Enchants the item with the specified enchantment level (roughly equivalent to using an enchantment table at that
     * level).
     *
     * @param randomLevel the range of levels to pick from. To specify a specific level, use the same number for both
     *                    min & max.
     * @param treasure    if this enchantment can be a treasure enchantment.
     * @param conditions  the list of conditions that must be passed before enchanting.
     */
    public EnchantWithLevels(RandomValueRange randomLevel, boolean treasure, List<LootCondition> conditions) {
        super(conditions);
        this.randomLevel = randomLevel;
        this.treasure = treasure;
    }

    /**
     * Enchants the item with the specified enchantment level (roughly equivalent to using an enchantment table at that
     * level). {@link LootCondition}s are null-valued with this constructor.
     *
     * @param randomLevel the range of levels to pick from.
     * @param treasure    if this enchantment can be a treasure enchantment.
     */
    public EnchantWithLevels(RandomValueRange randomLevel, boolean treasure) {
        this(randomLevel, treasure, null);
    }

    /** Set the minimum enchantment level for the enchantment */
    public void setMin(int min) {
        int max = (int) randomLevel.getMax();
        randomLevel = new RandomValueRange(min, max);
    }

    /** Set the maximum level for the enchantment */
    public void setMax(int max) {
        int min = (int) randomLevel.getMin();
        randomLevel = new RandomValueRange(min, max);
    }

    /** Set new minimum and maximum levels for th enchantment */
    public void setLimits(int min, int max) {
        randomLevel = new RandomValueRange(min, max);
    }

    /** Set if treasure enchantments are allowed as possible enchantments */
    public void setTreasureEnchantsAllowed(boolean allowed) {
        this.treasure = allowed;
    }

    @Override
    public ItemStack apply(ItemStack itemstack, Random rand, LootContext context) {
        return EnchantmentHelper.addRandomEnchant(itemstack, randomLevel.generateInt(rand), treasure);
    }


    public RandomValueRange getRandomLevelRange() {
        return this.randomLevel;
    }

    public boolean getTreasureEnchantmentsAllowed() {
        return this.treasure;
    }


    public static class Serializer extends LootFunction.Serializer<EnchantWithLevels> {
        protected Serializer() {
            super(NamespacedKey.minecraft("enchant_with_levels"), EnchantWithLevels.class);
        }

        @Override
        public void serialize(JsonObject json, EnchantWithLevels type, JsonSerializationContext context) {
            json.add("levels", context.serialize(type.randomLevel));
            json.addProperty("treasure", type.treasure);
        }

        @Override
        public EnchantWithLevels deserialize(JsonObject json, JsonDeserializationContext context, List<LootCondition> conditions) {
            RandomValueRange range = JsonUtils.deserializeClass(json, "levels", context, RandomValueRange.class);
            boolean treasure = JsonUtils.getBoolean(json, "treasure", false);
            return new EnchantWithLevels(range, treasure, conditions);
        }
    }
}
