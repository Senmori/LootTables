package org.bukkit.craftbukkit.loottable.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import org.bukkit.craftbukkit.loottable.utils.JsonUtils;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.UUID;


public class LootAttributeModifier {

    private String modifierName;
    private String attributeName;
    private int operation;
    private RandomValueRange amount;
    private UUID uuid;
    private Set<EquipmentSlot> equipmentSlots = new HashSet<EquipmentSlot>();

    /**
     * Special helper class for serializing/deserializing {@link AttributeModifier}s into loot tables.
     * Created in the same manner as normal {@link AttributeModifier}s.
     *
     * @param modifierName Name of the modifier
     * @param attributeName Name of this modifier to act upon
     * @param operation Must be either "addition", "multiply_base", or "multiply_total"
     * @param randomAmount Specifies the exact amount(or a range) of change to the modifier
     * @param uuid Optional: UUID of the modifier.
     * @param validEquipmentSlots Slots the item must be in for the modifier to take effect.
     */
    public LootAttributeModifier(String modifierName, String attributeName, int operation, RandomValueRange randomAmount, UUID uuid, Set<EquipmentSlot> validEquipmentSlots) {
        this.modifierName = modifierName;
        this.attributeName = attributeName;
        this.operation = operation;
        this.amount = randomAmount;
        this.uuid = uuid == null ? UUID.randomUUID() : uuid;
        this.equipmentSlots = validEquipmentSlots != null ? validEquipmentSlots : new HashSet<>(); // null checks
    }

    /**
     * Create {@link LootAttributeModifier} from an existing {@link AttributeModifier}.
     * Specifies the slots this modifier will be active in.
     *
     * @param modifier the modifier to import.
     * @param validSlots the slots this modifier will be active in.
     */
    public LootAttributeModifier(AttributeModifier modifier, Set<EquipmentSlot> validSlots) {
        this(modifier.getName(), modifier.getName(), modifier.getOperation().ordinal(), new RandomValueRange((float)modifier.getAmount()), modifier.getUniqueId(), validSlots);
    }

    /** Set the new amount(s) of change to the modifier */
    public LootAttributeModifier setAmount(RandomValueRange amount) {
        this.amount = amount;
        return this;
    }

    /** Returns a new instance of {@link org.bukkit.attribute.AttributeModifier} */
    public AttributeModifier getAttributeModifier() {
        AttributeModifier.Operation operation = null;
        switch(this.operation) {
            case 0:
                operation = AttributeModifier.Operation.ADD_NUMBER;
                break;
            case 1:
                operation = AttributeModifier.Operation.ADD_SCALAR;
                break;
            case 2:
                operation = AttributeModifier.Operation.MULTIPLY_SCALAR_1;
                break;
            default:
                operation = AttributeModifier.Operation.ADD_NUMBER;

        }
        return new AttributeModifier(modifierName, amount.generateInt(new Random()), operation);
    }
    public String getModifierName() { return this.modifierName; }
    public String getAttributeName() { return this.attributeName; }
    public int getOperation() { return this.operation; }
    public RandomValueRange getAmount() { return this.amount; }
    public UUID getUuid() { return this.uuid; }
    public Set<EquipmentSlot> getEquipmentSlots() { return this.equipmentSlots; }


    public JsonObject serialize(JsonSerializationContext context) {
        JsonObject json = new JsonObject();
        json.addProperty("name", this.modifierName);
        json.addProperty("attribute", this.attributeName);
        json.addProperty("operation", getOperationFromNum(this.operation));
        json.add("amount", context.serialize(this.amount));
        if (this.uuid != null) {
            json.addProperty("uuid", this.uuid.toString());
        }

        if (this.equipmentSlots.size() > 0) {
            JsonArray array = new JsonArray();
            for (EquipmentSlot slot : equipmentSlots) {
                array.add(new JsonPrimitive(slot.name()));
            }
            json.add("slot", array);
        }
        return json;
    }

    public static LootAttributeModifier deserialize(JsonObject json, JsonDeserializationContext context) {
        String modifierName = JsonUtils.getString(json, "name");
        String attributeName = JsonUtils.getString(json, "attribute");
        int operation = LootAttributeModifier.getOperationFromString(JsonUtils.getString(json, "operation"));
        RandomValueRange randomAmount = JsonUtils.deserializeClass(json, "amount", context, RandomValueRange.class);
        UUID uuid = null;
        EquipmentSlot[] slots;
        Set<EquipmentSlot> validSlots = new HashSet<EquipmentSlot>();
        if (JsonUtils.isString(json, "slot")) {
            slots = new EquipmentSlot[]{EquipmentSlot.valueOf(JsonUtils.getString(json, "slot"))};
        } else {
            if (!(JsonUtils.isJsonArray(json, "slot"))) {
                throw new JsonSyntaxException("Invalid or missing attribute modifier slot; must be either a string or array of strings");
            }

            JsonArray array = JsonUtils.getJsonArray(json, "slot");
            slots = new EquipmentSlot[array.size()];
            JsonElement element;
            int i = 0;
            for (Iterator iter = array.iterator(); iter.hasNext(); i++) {
                element = (JsonElement) iter.next();
                slots[i] = EquipmentSlot.valueOf(JsonUtils.getString(element, "slot"));
            }
            if (slots.length == 0) {
                throw new JsonSyntaxException("Invalid attribute modifier slot; must contain at least one entry");
            }
            for (EquipmentSlot s : slots) {
                validSlots.add(s);
            }
        }
        if (json.has("id")) {
            String id = JsonUtils.getString(json, "id");
            try {
                uuid = UUID.fromString(id);
            } catch (IllegalArgumentException e) {
                throw new JsonSyntaxException("Invalid attribute modifier id \'" + id + "\' (must be UUID format, with dashes)");
            }
        }
        return new LootAttributeModifier(modifierName, attributeName, operation, randomAmount, uuid, validSlots);
    }

    private static String getOperationFromNum(int operation) {
        switch (operation) {
            case 0:
                return "addition";
            case 1:
                return "mutliply_base";
            case 2:
                return "multiply_total";
            default:
                throw new IllegalArgumentException("Unknown attribute modifier operation " + operation);
        }
    }

    private static int getOperationFromString(String operation) {
        switch (operation.toLowerCase()) {
            case "addition":
                return 0;
            case "multiply_base":
                return 1;
            case "multiply_total":
                return 2;
            default:
                throw new JsonSyntaxException("Unknown attribute modifier operation " + operation);
        }
    }
}
