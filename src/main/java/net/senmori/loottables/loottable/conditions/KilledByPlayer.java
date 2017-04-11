package org.bukkit.craftbukkit.loottable.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.bukkit.craftbukkit.loottable.core.LootContext;
import org.bukkit.craftbukkit.loottable.utils.JsonUtils;
import org.bukkit.util.ResourceLocation;

import java.util.Random;


public class KilledByPlayer implements LootCondition {

    private boolean inverse;

    /**
     * Tests if the {@link org.bukkit.craftbukkit.loottable.core.EntityTarget} was killed by a player.
     *
     * @param inverse If true, the condition passes if the killer is NOT available.
     */
    public KilledByPlayer(boolean inverse) { this.inverse = !inverse; }

    /**
     * Tests the if the {@link org.bukkit.craftbukkit.loottable.core.EntityTarget} was killed by a player.
     *
     * Inverse defaults to false.(Meaning the player killer must be present)
     */
    public KilledByPlayer() { this(false); }

    public boolean getInverse() { return this.inverse; }

    /** Set if the player should be present */
    public void setInverse(boolean inverse) { this.inverse = !inverse; }

    @Override
    public boolean testCondition(Random rand, LootContext context) {
        boolean flag = context.getKillerPlayer() != null;
        return flag == !inverse;
    }

    public static class Serializer extends LootCondition.Serializer<KilledByPlayer> {
        protected Serializer() { super(new ResourceLocation("killed_by_player"), KilledByPlayer.class); }

        @Override
        public void serialize(JsonObject json, KilledByPlayer type, JsonSerializationContext context) {
            json.addProperty("inverse", type.getInverse());
        }

        @Override
        public KilledByPlayer deserialize(JsonObject json, JsonDeserializationContext context) {
            return new KilledByPlayer(JsonUtils.getBoolean(json, "inverse", false));
        }
    }
}
