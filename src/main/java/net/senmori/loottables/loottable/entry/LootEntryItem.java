package net.senmori.loottables.loottable.entry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.senmori.loottables.loottable.conditions.LootCondition;
import net.senmori.loottables.loottable.conditions.LootConditionManager;
import net.senmori.loottables.loottable.core.LootContext;
import net.senmori.loottables.loottable.functions.LootFunction;
import net.senmori.loottables.loottable.utils.JsonUtils;
import net.senmori.loottables.loottable.utils.LootUtils;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;


public class LootEntryItem extends LootEntry {

    private List<LootFunction> functions = new ArrayList<>();
    private Material material;

    public LootEntryItem(Material material, int weight, int quality, List<LootFunction> functions, List<LootCondition> conditions) {
        super(weight, quality, conditions);
        this.functions = functions == null ? new ArrayList<>() : functions;
        this.material = material;
    }

    public LootEntryItem(Material material, int weight, int quality) {
        this(material, weight, quality, null, null);
    }

    public void addFunction(LootFunction function) {
        functions.add(function);
    }

    @Override
    public void addLoot(Collection<ItemStack> itemstacks, Random rand, LootContext context) {
        ItemStack stack = new ItemStack(getMaterial());

        for (LootFunction f : this.functions) {
            if (LootConditionManager.testAllConditions(f.getConditions(), rand, context)) {
                stack = f.apply(stack, rand, context);
            }
        }

        if (stack.getAmount() > 0) {
            if (stack.getAmount() < stack.getMaxStackSize()) {
                itemstacks.add(stack);
            } else {
                int size = stack.getAmount();

                while (size > 0) {
                    ItemStack copy = stack.clone();
                    copy.setAmount(Math.min(stack.getMaxStackSize(), size));
                    size -= copy.getAmount();
                    itemstacks.add(copy);
                }
            }
        }
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material newMaterial) {
        this.material = newMaterial;
    }

    private String toMinecraftName() {
        return LootUtils.getNameForItem(CraftItemStack.asNMSCopy(new ItemStack(material)));
    }

    public List<LootFunction> getFunctions() {
        return this.functions;
    }


    @Override
    protected void serialize(JsonObject json, JsonSerializationContext context) {
        json.addProperty("name", this.toMinecraftName()); // replace just in case wonkiness
        if (this.functions != null && ! this.functions.isEmpty()) {
            json.add("functions", context.serialize(this.functions));
        }
    }


    public static LootEntryItem deserialize(JsonObject object, JsonDeserializationContext context, int weight, int quality, List<LootCondition> conditions) {
        ItemStack stack = JsonUtils.getItem(object, "name");
        LootFunction[] functions;
        if (object.has("functions")) {
            functions = JsonUtils.deserializeClass(object, "functions", context, LootFunction[].class);
        } else {
            functions = new LootFunction[0];
        }
        return new LootEntryItem(stack.getType(), weight, quality, Arrays.asList(functions), conditions);
    }
}
