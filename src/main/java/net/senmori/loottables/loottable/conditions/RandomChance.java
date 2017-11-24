package net.senmori.loottables.loottable.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.senmori.loottables.loottable.core.LootContext;
import net.senmori.loottables.loottable.utils.JsonUtils;
import org.bukkit.NamespacedKey;

import java.util.Random;


public class RandomChance implements LootCondition {
    private float chance;

    /**
     * Test if a random number between 0.0 and 1.0 is less than a specified value
     *
     * @param chance Success rate as a number between 0.0 - 1.0
     */
    public RandomChance(float chance) {
        this.chance = chance;
    }

    /** Set the new success rate */
    public void setChance(float chance) {
        this.chance = chance;
    }

    @Override
    public boolean testCondition(Random rand, LootContext context) {
        return rand.nextFloat() < this.chance;
    }

    public float getChance() {
        return this.chance;
    }


    public static class Serializer extends LootCondition.Serializer<RandomChance> {
        protected Serializer() {
            super(NamespacedKey.minecraft("random_chance"), RandomChance.class);
        }

        @Override
        public void serialize(JsonObject json, RandomChance type, JsonSerializationContext context) {
            json.addProperty("chance", type.getChance());
        }

        @Override
        public RandomChance deserialize(JsonObject json, JsonDeserializationContext context) {
            return new RandomChance(JsonUtils.getFloat(json, "chance"));
        }
    }
}
