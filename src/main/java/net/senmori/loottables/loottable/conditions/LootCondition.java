package net.senmori.loottables.loottable.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.senmori.loottables.loottable.core.LootContext;
import org.bukkit.NamespacedKey;

import java.util.Random;


public interface LootCondition {

    boolean testCondition(Random rand, LootContext context);

    abstract class Serializer<T extends LootCondition> {
        private NamespacedKey name;
        private Class<T> conditionClass;

        protected Serializer(NamespacedKey location, Class<T> clazz) {
            this.name = location;
            this.conditionClass = clazz;
        }

        public NamespacedKey getName() {
            return this.name;
        }

        public Class<T> getConditionClass() {
            return this.conditionClass;
        }

        public abstract void serialize(JsonObject json, T type, JsonSerializationContext context);

        public abstract T deserialize(JsonObject json, JsonDeserializationContext context);
    }
}
