package net.senmori.loottables.loottable.conditions;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import net.senmori.loottables.loottable.adapter.InheritanceAdapter;
import net.senmori.loottables.loottable.core.LootContext;
import net.senmori.loottables.loottable.utils.JsonUtils;
import org.bukkit.NamespacedKey;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class LootConditionManager {

    private static Map<NamespacedKey, LootCondition.Serializer<?>> nameToSerializerMap = Maps.newHashMap();
    private static Map<Class<? extends LootCondition>, LootCondition.Serializer<?>> classToSerializerMap = Maps.newHashMap();

    public LootConditionManager() {
    }

    static {
        registerCondition(new KilledByPlayer.Serializer());
        registerCondition(new EntityHasScore.Serializer());
        registerCondition(new EntityHasProperty.Serializer());
        registerCondition(new RandomChance.Serializer());
        registerCondition(new RandomChanceWithLooting.Serializer());
    }

    // register condition
    protected static <T extends LootCondition> void registerCondition(LootCondition.Serializer<? extends T> condition) {
        if (nameToSerializerMap.containsKey(condition.getName())) {
            throw new IllegalArgumentException("Can't re-register item condition \'" + condition.getName().toString() + "\'");
        } else if (classToSerializerMap.containsKey(condition.getConditionClass())) {
            throw new IllegalArgumentException("Can't re-register item condition class \'" + condition.getConditionClass().getName() + "\'");
        } else {
            nameToSerializerMap.put(condition.getName(), condition);
            classToSerializerMap.put(condition.getConditionClass(), condition);
        }
    }

    // test all conditions
    public static boolean testAllConditions(List<LootCondition> conditions, Random rand, LootContext context) {
        if (conditions == null || conditions.isEmpty()) return true;

        for (LootCondition c : conditions) {
            if (! c.testCondition(rand, context)) return false;
        }
        return true;
    }


    public static LootCondition.Serializer<?> getSerializerForName(NamespacedKey location) {
        LootCondition.Serializer serializer = null;
        for (NamespacedKey name : nameToSerializerMap.keySet()) {
            if (name.equals(location)) {
                serializer = nameToSerializerMap.get(name);
                break;
            }
        }
        if (serializer == null) {
            throw new IllegalArgumentException("Unknown loot condition \'" + location + "\'");
        }
        return serializer;
    }

    public static <T extends LootCondition> LootCondition.Serializer<T> getSerializerFor(T conditionClass) {
        LootCondition.Serializer serializer = classToSerializerMap.get(conditionClass.getClass());
        if (serializer == null) {
            throw new IllegalArgumentException("Unknown loot condition class \'" + conditionClass.getClass().getName() + "\'");
        }
        return serializer;
    }


    public static class Serializer extends InheritanceAdapter<LootCondition> {
        public Serializer() {
        }

        @Override
        public JsonElement serialize(LootCondition lootCondition, Type type, JsonSerializationContext context) {
            LootCondition.Serializer serializer = LootConditionManager.getSerializerFor(lootCondition);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("condition", serializer.getName().toString());
            serializer.serialize(jsonObject, lootCondition, context);
            return jsonObject;
        }

        @Override
        public LootCondition deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject json = JsonUtils.getJsonObject(jsonElement, "condition");
            String name = JsonUtils.getString(json, "condition");
            NamespacedKey location = NamespacedKey.minecraft(name);
            LootCondition.Serializer serializer;
            try {
                serializer = LootConditionManager.getSerializerForName(location);
            } catch (IllegalArgumentException e) {
                throw new JsonSyntaxException("Unknown condition \'" + location + "\'");
            }
            return serializer.deserialize(json, context);
        }
    }

}
