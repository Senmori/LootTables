package net.senmori.loottables.loottable.entry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import net.senmori.loottables.loottable.adapter.InheritanceAdapter;
import net.senmori.loottables.loottable.conditions.LootCondition;
import net.senmori.loottables.loottable.utils.JsonUtils;

import java.lang.reflect.Type;
import java.util.Arrays;


public class LootEntryAdapter extends InheritanceAdapter<LootEntry> {

    @Override
    public JsonElement serialize(LootEntry lootEntry, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        if (lootEntry instanceof LootEntryItem) {
            object.addProperty("type", "item");
        } else if (lootEntry instanceof LootEntryTable) {
            object.addProperty("type", "loot_table");
        } else {
            if (! ( lootEntry instanceof LootEntryEmpty )) {
                throw new IllegalArgumentException("Unknown loot entry type \'" + lootEntry + "\'");
            }
            object.addProperty("type", "empty");
        }
        object.addProperty("weight", lootEntry.weight);
        object.addProperty("quality", lootEntry.quality);
        lootEntry.serialize(object, context);
        if (lootEntry.conditions != null && ! lootEntry.conditions.isEmpty()) {
            object.add("conditions", context.serialize(lootEntry.conditions));
        }
        return object;
    }

    @Override
    public LootEntry deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = JsonUtils.getJsonObject(jsonElement, "loot item");
        String s = JsonUtils.getString(jsonObject, "type");
        int w = JsonUtils.getInt(jsonObject, "weight", 1);
        int q = JsonUtils.getInt(jsonObject, "quality", 0);
        LootCondition[] conditions;
        if (jsonObject.has("conditions")) {
            conditions = JsonUtils.deserializeClass(jsonObject, "conditions", context, LootCondition[].class);
        } else {
            conditions = new LootCondition[0];
        }

        switch (s) {
            case "item":
                return LootEntryItem.deserialize(jsonObject, context, w, q, Arrays.asList(conditions));
            case "loot_table":
                return LootEntryTable.deserialize(jsonObject, context, w, q, Arrays.asList(conditions));
            case "empty":
                return LootEntryEmpty.deserialize(jsonObject, context, w, q, Arrays.asList(conditions));
            default:
                throw new JsonSyntaxException("Unknown loot entry type \'" + s + "\'");
        }
    }
}
