package net.senmori.loottables.loottable.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.senmori.loottables.loottable.conditions.LootCondition;
import net.senmori.loottables.loottable.core.LootContext;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public abstract class LootFunction {
    private List<LootCondition> conditions = new ArrayList<>();

    protected LootFunction(List<LootCondition> conditions) {
        this.conditions = conditions == null ? new ArrayList<>() : conditions;
    }

    public void addCondition(LootCondition condition) {
        this.conditions.add(condition);
    }

    public abstract ItemStack apply(ItemStack itemstack, Random rand, LootContext context);

    public List<LootCondition> getConditions() {
        return this.conditions;
    }


    public abstract static class Serializer<T extends LootFunction> {
        private NamespacedKey lootTableLocation;
        private Class<T> functionClass;

        protected Serializer(NamespacedKey location, Class<T> clazz) {
            this.lootTableLocation = location;
            this.functionClass = clazz;
        }

        public NamespacedKey getName() {
            return this.lootTableLocation;
        }

        public Class<T> getFunctionClass() {
            return this.functionClass;
        }

        public abstract void serialize(JsonObject json, T type, JsonSerializationContext context);

        public abstract T deserialize(JsonObject json, JsonDeserializationContext context, List<LootCondition> conditions);

    }
}
