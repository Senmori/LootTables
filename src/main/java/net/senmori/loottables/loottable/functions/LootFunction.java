package org.bukkit.craftbukkit.loottable.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.bukkit.craftbukkit.loottable.conditions.LootCondition;
import org.bukkit.craftbukkit.loottable.core.LootContext;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public abstract class LootFunction {
    private List<LootCondition> conditions = new ArrayList<>();

    protected LootFunction(List<LootCondition> conditions) {
        this.conditions = conditions == null ? new ArrayList<>() : conditions;
    }

    public void addCondition(LootCondition condition) { this.conditions.add(condition); }

    public abstract ItemStack apply(ItemStack itemstack, Random rand, LootContext context);

    public List<LootCondition> getConditions() { return this.conditions; }


    public abstract static class Serializer<T extends LootFunction> {
        private ResourceLocation lootTableLocation;
        private Class<T> functionClass;

        protected Serializer(ResourceLocation location, Class<T> clazz) {
            this.lootTableLocation = location;
            this.functionClass = clazz;
        }

        public ResourceLocation getName() { return this.lootTableLocation; }

        public Class<T> getFunctionClass() { return this.functionClass; }

        public abstract void serialize(JsonObject json, T type, JsonSerializationContext context);

        public abstract T deserialize(JsonObject json, JsonDeserializationContext context, List<LootCondition> conditions);

    }
}
