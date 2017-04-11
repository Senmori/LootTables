package org.bukkit.craftbukkit.loottable.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.bukkit.craftbukkit.loottable.core.LootContext;
import org.bukkit.util.ResourceLocation;

import java.util.Random;


public interface LootCondition {

    boolean testCondition(Random rand, LootContext context);

    abstract class Serializer<T extends LootCondition> {
        private ResourceLocation name;
        private Class<T> conditionClass;

        protected Serializer(ResourceLocation location, Class<T> clazz) {
            this.name = location;
            this.conditionClass = clazz;
        }

        public ResourceLocation getName() { return this.name; }

        public Class<T> getConditionClass() { return this.conditionClass; }

        public abstract void serialize(JsonObject json, T type, JsonSerializationContext context);

        public abstract T deserialize(JsonObject json, JsonDeserializationContext context);
    }
}
