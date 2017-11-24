package net.senmori.loottables.loottable.functions;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import net.senmori.loottables.loottable.conditions.LootCondition;
import net.senmori.loottables.loottable.core.LootAttributeModifier;
import net.senmori.loottables.loottable.core.LootContext;
import net.senmori.loottables.loottable.utils.JsonUtils;
import net.senmori.loottables.loottable.utils.NBTUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;


public class SetAttributes extends LootFunction {

    private List<LootAttributeModifier> modifiers;

    /**
     * Add {@link LootAttributeModifier}(s) to an item.
     *
     * @param modifiers  the {@link LootAttributeModifier}(s) to add.
     * @param conditions the {@link LootCondition}(s) that must be passed before modification.
     */
    public SetAttributes(List<LootAttributeModifier> modifiers, List<LootCondition> conditions) {
        super(conditions);
        this.modifiers = modifiers;
    }

    /**
     * Add {@link LootAttributeModifier}(s) to an items.
     *
     * @param modifiers the {@link LootAttributeModifier}(s) to add.
     */
    public SetAttributes(List<LootAttributeModifier> modifiers) {
        this(modifiers, null);
    }


    /** Add a {@link LootAttributeModifier} */
    public void addModifier(LootAttributeModifier modifier) {
        if (modifiers.contains(modifier)) return;
        modifiers.add(modifier);
    }

    @Override
    public ItemStack apply(ItemStack itemstack, Random rand, LootContext context) {
        for (LootAttributeModifier modifier : modifiers) {
            UUID uuid = modifier.getUuid();
            if (uuid == null) {
                uuid = UUID.randomUUID();
            }
            AttributeModifier attrib = new AttributeModifier(uuid, modifier.getAttributeName(), modifier.getAmount().generateInt(new Random()), getOperation(modifier.getOperation()));
            itemstack = NBTUtils.addAttribute(attrib, itemstack, modifier.getEquipmentSlots());
        }
        return itemstack;
    }

    private AttributeModifier.Operation getOperation(int operation) {
        switch (operation) {
            case 0:
                return AttributeModifier.Operation.ADD_NUMBER;
            case 1:
                return AttributeModifier.Operation.ADD_SCALAR;
            case 2:
                return AttributeModifier.Operation.MULTIPLY_SCALAR_1;
            default:
                throw new IllegalArgumentException("Unknown Attribute Modifier operation " + operation);
        }
    }


    public List<LootAttributeModifier> getModifiers() {
        return this.modifiers;
    }


    public static class Serializer extends LootFunction.Serializer<SetAttributes> {
        protected Serializer() {
            super(NamespacedKey.minecraft("set_attributes"), SetAttributes.class);
        }

        @Override
        public void serialize(JsonObject json, SetAttributes type, JsonSerializationContext context) {
            JsonArray array = new JsonArray();
            for (LootAttributeModifier mod : type.modifiers) {
                array.add(mod.serialize(context));
            }
            json.add("modifiers", array);
        }

        @Override
        public SetAttributes deserialize(JsonObject json, JsonDeserializationContext context, List<LootCondition> conditions) {
            JsonArray array = JsonUtils.getJsonArray(json, "modifiers");
            LootAttributeModifier[] modifiers = new LootAttributeModifier[array.size()];

            JsonElement element;
            int i = 0;
            for (Iterator iter = array.iterator(); iter.hasNext(); i++) {
                element = (JsonElement) iter.next();
                modifiers[i] = LootAttributeModifier.deserialize(JsonUtils.getJsonObject(element, "modifier"), context);
            }
            if (modifiers.length == 0) {
                throw new JsonSyntaxException("Invalid attribute modifiers array; cannot be empty");
            } else {
                return new SetAttributes(Arrays.asList(modifiers), conditions);
            }
        }
    }
}
