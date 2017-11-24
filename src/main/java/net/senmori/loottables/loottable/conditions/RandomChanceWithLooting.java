package net.senmori.loottables.loottable.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.senmori.loottables.loottable.core.LootContext;
import net.senmori.loottables.loottable.utils.JsonUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;

import java.util.Random;


public class RandomChanceWithLooting implements LootCondition {

    private float chance;
    private float lootingMultiplier;

    /**
     * Test if a random number between 0.0 and 1.0 is less than a specified value, affected by the Looting level on the
     * killer entity.
     *
     * @param chance            Success rate between 0.0 and 1.0
     * @param lootingMultiplier Looting adjustment to the base success rate. Formula: chance + (looting_level *
     *                          looting_multiplier)
     */
    public RandomChanceWithLooting(float chance, float lootingMultiplier) {
        this.chance = chance;
        this.lootingMultiplier = lootingMultiplier;
    }

    /** Set the new base success rate */
    public void setChance(float newChance) {
        this.chance = newChance;
    }

    /** Set the new looting mulitplier */
    public void setLootingMultiplier(float multiplier) {
        this.lootingMultiplier = multiplier;
    }

    @Override
    public boolean testCondition(Random rand, LootContext context) {
        int i = 0;
        if (context.getKiller() instanceof LivingEntity) {
            i = ( (LivingEntity) context.getKiller() ).getEquipment().getItemInMainHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);
        }
        return rand.nextFloat() < this.chance + ( (float) i * this.lootingMultiplier );
    }

    public float getChance() {
        return this.chance;
    }

    public float getLootingMultiplier() {
        return this.lootingMultiplier;
    }

    public static class Serializer extends LootCondition.Serializer<RandomChanceWithLooting> {
        protected Serializer() {
            super(NamespacedKey.minecraft("random_chance_with_looting"), RandomChanceWithLooting.class);
        }

        @Override
        public void serialize(JsonObject json, RandomChanceWithLooting type, JsonSerializationContext context) {
            json.addProperty("chance", type.getChance());
            json.addProperty("looting_multiplier", type.getLootingMultiplier());
        }

        @Override
        public RandomChanceWithLooting deserialize(JsonObject json, JsonDeserializationContext context) {
            return new RandomChanceWithLooting(JsonUtils.getFloat(json, "chance"), JsonUtils.getFloat(json, "looting_multiplier"));
        }
    }
}
