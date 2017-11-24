package net.senmori.loottables.loottable.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.server.v1_12_R1.MojangsonParseException;
import net.minecraft.server.v1_12_R1.MojangsonParser;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.senmori.loottables.loottable.conditions.LootCondition;
import net.senmori.loottables.loottable.core.LootContext;
import net.senmori.loottables.loottable.utils.JsonUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;


public class SetNBT extends LootFunction {

    private String tag;
    private NBTTagCompound root;

    /**
     * Adds NBT data to an item.
     *
     * @param tag        NBT data to add.
     * @param conditions the {@link LootCondition}(s) that must be passed before adding of NBT data.
     */
    public SetNBT(String tag, List<LootCondition> conditions) {
        super(conditions);
        setNBTTag(tag);
    }

    /**
     * Adds NBT data to an item.
     *
     * @param tag NBT data to add.
     */
    public SetNBT(String tag) {
        this(tag, null);
    }

    /**
     * Set the new NBT data tag.
     *
     * @param tag the NBT data, in string version.
     *
     * @return true if the string is formatted correctly.
     */
    public boolean setNBTTag(String tag) {
        this.tag = tag;
        try {
            root = MojangsonParser.parse(tag);
        } catch (MojangsonParseException e) {
            // reset tag to empty string to prevent accidentally calling an incorrect JSON string.
            this.tag = "";
            return false;
        }
        return true;
    }

    @Override
    public ItemStack apply(ItemStack itemstack, Random rand, LootContext context) {
        net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemstack);
        nmsStack.setTag(root);
        return CraftItemStack.asBukkitCopy(nmsStack);
    }

    public static class Serializer extends LootFunction.Serializer<SetNBT> {
        protected Serializer() {
            super(NamespacedKey.minecraft("set_nbt"), SetNBT.class);
        }

        @Override
        public void serialize(JsonObject json, SetNBT type, JsonSerializationContext context) {
            json.addProperty("tag", type.tag);
        }

        @Override
        public SetNBT deserialize(JsonObject json, JsonDeserializationContext context, List<LootCondition> conditions) {
            return new SetNBT(JsonUtils.getString(json, "tag"), conditions);
        }
    }
}
