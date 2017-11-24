package net.senmori.loottables.loottable.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.senmori.loottables.loottable.core.EntityTarget;
import net.senmori.loottables.loottable.core.LootContext;
import net.senmori.loottables.loottable.properties.EntityProperty;
import net.senmori.loottables.loottable.properties.EntityPropertyManager;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;

import java.util.List;
import java.util.Random;


public class EntityHasProperty implements LootCondition {
    private List<EntityProperty> properties;
    private EntityTarget target;

    /**
     * Tests properties of an entity.
     *
     * @param properties the {@link EntityProperty}(s) to check.
     * @param target     the {@link EntityTarget} to check against.
     */
    public EntityHasProperty(List<EntityProperty> properties, EntityTarget target) {
        this.properties = properties;
        this.target = target;
    }

    /** Add a new {@link EntityProperty} to check for */
    public void addProperty(EntityProperty property) {
        properties.add(property);
    }

    /** Set a new {@link EntityTarget} to check against */
    public void setTarget(EntityTarget target) {
        this.target = target;
    }


    public List<EntityProperty> getProperties() {
        return this.properties;
    }

    public EntityTarget getTarget() {
        return this.target;
    }

    @Override
    public boolean testCondition(Random rand, LootContext context) {
        Entity entity = context.getEntity(this.target);
        if (entity == null) {
            return false;
        } else {
            for (EntityProperty property : this.properties) {
                if (! property.testProperty(rand, entity)) return false;
            }
        }
        return true;
    }

    public static class Serializer extends LootCondition.Serializer<EntityHasProperty> {
        protected Serializer() {
            super(NamespacedKey.minecraft("entity_properties"), EntityHasProperty.class);
        }

        @Override
        public void serialize(JsonObject json, EntityHasProperty type, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            for (EntityProperty property : type.getProperties()) {
                EntityProperty.Serializer serializer = EntityPropertyManager.getSerializerFor(property);
                jsonObject.add(serializer.getName().getKey(), serializer.serialize(property, context));
            }
            json.add("properties", jsonObject);
            json.addProperty("entity", type.getTarget().toString().toLowerCase());
        }

        @Override
        public EntityHasProperty deserialize(JsonObject json, JsonDeserializationContext context) {
            return null;
        }
    }
}
