package net.senmori.loottables.loottable.properties;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;

import java.util.Random;


public interface EntityProperty {

    boolean testProperty(Random rand, Entity entity);


    abstract class Serializer<T extends EntityProperty> {
        private NamespacedKey name;
        private Class<T> propertyClass;

        protected Serializer(NamespacedKey name, Class<T> propertyClass) {
            this.name = name;
            this.propertyClass = propertyClass;
        }

        public NamespacedKey getName() {
            return this.name;
        }

        public Class<T> getPropertyClass() {
            return this.propertyClass;
        }

        public abstract JsonElement serialize(T type, JsonSerializationContext context);

        public abstract T deserialize(JsonElement json, JsonDeserializationContext context);
    }
}
