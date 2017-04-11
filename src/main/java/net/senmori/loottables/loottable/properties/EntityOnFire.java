package org.bukkit.craftbukkit.loottable.properties;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import org.bukkit.craftbukkit.loottable.utils.JsonUtils;
import org.bukkit.entity.Entity;
import org.bukkit.util.ResourceLocation;

import java.util.Random;


public class EntityOnFire implements EntityProperty {

    private boolean onFire;

    /**
     * Tests whether the {@link org.bukkit.craftbukkit.loottable.core.EntityTarget} is on fire.
     * @param onFire
     */
    public EntityOnFire(boolean onFire) { this.onFire = onFire; }

    /** Set if this entity should be on fire or not */
    public void setOnFire(boolean value) { this.onFire = value; }

    @Override
    public boolean testProperty(Random rand, Entity entity) {
        return onFire ? entity.getFireTicks() > 0 : entity.getFireTicks() <= 0;
    }

    public boolean getOnFire() { return this.onFire; }

    public static class Serializer extends EntityProperty.Serializer<EntityOnFire> {
        protected Serializer() { super(new ResourceLocation("on_fire"), EntityOnFire.class); }

        @Override
        public JsonElement serialize(EntityOnFire type, JsonSerializationContext context) {
            return new JsonPrimitive(type.onFire);
        }

        @Override
        public EntityOnFire deserialize(JsonElement json, JsonDeserializationContext context) {
            return new EntityOnFire(JsonUtils.getBoolean(json, "on_fire"));
        }
    }
}
