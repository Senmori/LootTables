package org.bukkit.craftbukkit.loottable.functions;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import org.bukkit.craftbukkit.loottable.adapter.InheritanceAdapter;
import org.bukkit.craftbukkit.loottable.conditions.LootCondition;
import org.bukkit.craftbukkit.loottable.utils.JsonUtils;
import org.bukkit.util.ResourceLocation;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;


public class LootFunctionManager {
    private static Map<ResourceLocation, LootFunction.Serializer<?>> nameToSerializerMap = Maps.newHashMap();
    private static Map<Class<? extends LootFunction>, LootFunction.Serializer<?>> classToSerializerMap = Maps.newHashMap();

    public LootFunctionManager() {
    }

    static {
        registerFunction(new EnchantRandomly.Serializer());
        registerFunction(new EnchantWithLevels.Serializer());
        registerFunction(new LootingEnchantBonus.Serializer());
        registerFunction(new SetAttributes.Serializer());
        registerFunction(new SetCount.Serializer());
        registerFunction(new SetDamage.Serializer());
        registerFunction(new SetMetadata.Serializer());
        registerFunction(new SetNBT.Serializer());
        registerFunction(new Smelt.Serializer());
    }

    public static <T extends LootFunction> void registerFunction(LootFunction.Serializer<? extends T> function) {
        ResourceLocation resourcelocation = function.getName();
        Class funcClass = function.getFunctionClass();
        if (nameToSerializerMap.containsKey(resourcelocation)) {
            throw new IllegalArgumentException("Can\'t re-register item function name " + resourcelocation);
        } else if (classToSerializerMap.containsKey(funcClass)) {
            throw new IllegalArgumentException("Can\'t re-register item function class " + funcClass.getName());
        } else {
            nameToSerializerMap.put(function.getName(), function);
            classToSerializerMap.put(function.getFunctionClass(), function);
        }
    }

    public static LootFunction.Serializer<?> getSerializerForName(ResourceLocation location) {
        LootFunction.Serializer serializer = null;
        for (ResourceLocation name : nameToSerializerMap.keySet()) {
            if (name.equals(location)) {
                serializer = nameToSerializerMap.get(name);
                break;
            }
        }
        if (serializer == null) {
            throw new IllegalArgumentException("Unknown loot item function \'" + location + "\'");
        } else {
            return serializer;
        }
    }

    public static <T extends LootFunction> LootFunction.Serializer<T> getSerializerFor(T functionClass) {
        LootFunction.Serializer serializer = (LootFunction.Serializer) classToSerializerMap.get(functionClass.getClass());
        if (serializer == null) {
            throw new IllegalArgumentException("Unknown loot item function " + functionClass);
        } else {
            return serializer;
        }
    }


    public static class Serializer extends InheritanceAdapter<LootFunction> {
        public Serializer() {}

        @Override
        public JsonElement serialize(LootFunction lootFunction, Type type, JsonSerializationContext context) {
            LootFunction.Serializer serializer = LootFunctionManager.getSerializerFor(lootFunction);
            JsonObject json = new JsonObject();
            json.addProperty("function", serializer.getName().toString());
            serializer.serialize(json, lootFunction, context);
            if (lootFunction.getConditions() != null && !lootFunction.getConditions().isEmpty()) {
                json.add("conditions", context.serialize(lootFunction.getConditions()));
            }
            return json;
        }

        int i = 0;

        @Override
        public LootFunction deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = JsonUtils.getJsonObject(jsonElement, "function");
            ResourceLocation location = new ResourceLocation(JsonUtils.getString(jsonObject, "function"));
            LootFunction.Serializer serializer;
            try {
                serializer = LootFunctionManager.getSerializerForName(location);
            } catch (IllegalArgumentException e) {
                throw new JsonSyntaxException("Unknown loot function \'" + location + "\'");
            }
            return serializer.deserialize(jsonObject, context, Arrays.asList(JsonUtils.deserializeClass(jsonObject, "conditions", new LootCondition[0], context, LootCondition[].class)));
        }
    }
}
