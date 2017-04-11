package org.bukkit.craftbukkit.loottable.properties;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import org.bukkit.entity.Entity;
import org.bukkit.util.ResourceLocation;

import java.util.Random;


public interface EntityProperty {

    boolean testProperty(Random rand, Entity entity);


    abstract class Serializer<T extends EntityProperty> {
        private ResourceLocation name;
        private Class<T> propertyClass;

        protected Serializer(ResourceLocation name, Class<T> propertyClass) {
            this.name = name;
            this.propertyClass = propertyClass;
        }

        public ResourceLocation getName() { return this.name; }

        public Class<T> getPropertyClass() { return this.propertyClass; }

        public abstract JsonElement serialize(T type, JsonSerializationContext context);

        public abstract T deserialize(JsonElement json, JsonDeserializationContext context);
    }
}
