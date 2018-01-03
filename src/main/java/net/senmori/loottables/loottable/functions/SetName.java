package net.senmori.loottables.loottable.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.senmori.loottables.loottable.conditions.LootCondition;
import net.senmori.loottables.loottable.core.LootContext;
import net.senmori.loottables.loottable.utils.JsonUtils;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Random;

public class SetName extends LootFunction {
    private String customName;
    protected SetName(String customName, List<LootCondition> conditions) {
        super(conditions);
        setCustomName(customName);
    }

    public void setCustomName(String customName) {
        this.customName = ChatColor.translateAlternateColorCodes('&', customName);
    }

    public String getCustomName() {
        return this.customName;
    }

    @Override
    public ItemStack apply(ItemStack itemstack, Random rand, LootContext context) {
        ItemMeta meta = itemstack.getItemMeta();
        meta.setDisplayName(getCustomName());
        itemstack.setItemMeta(meta);
        return itemstack;
    }


    public static class Serializer extends LootFunction.Serializer<SetName> {
        protected Serializer() {
            super(NamespacedKey.minecraft("set_name"), SetName.class);
        }

        @Override
        public void serialize(JsonObject json, SetName type, JsonSerializationContext context) {
            String customName = type.getCustomName().replaceAll(String.valueOf(ChatColor.COLOR_CHAR), "&"); // replace weird colors
            json.addProperty("name", customName);
        }

        @Override
        public SetName deserialize(JsonObject json, JsonDeserializationContext context, List<LootCondition> conditions) {
            return new SetName(JsonUtils.getString(json, "name"), conditions);
        }
    }
}
