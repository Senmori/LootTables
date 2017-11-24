package net.senmori.loottables.loottable.utils;

import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagList;
import net.minecraft.server.v1_12_R1.NBTTagString;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Set;


public class NBTUtils {

    public static ItemStack addAttribute(AttributeModifier modifier, ItemStack stack, Set<EquipmentSlot> validSlots) {
        net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);

        NBTTagCompound root = nmsStack.hasTag() ? nmsStack.getTag() : new NBTTagCompound();
        NBTTagList modifiers = new NBTTagList();

        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("AttributeName", modifier.getName());
        tag.setString("Name", modifier.getName());
        tag.setDouble("Amount", modifier.getAmount());
        tag.setInt("Operation", modifier.getOperation().ordinal());
        tag.a("UUID", modifier.getUniqueId());

        NBTTagList slotList = new NBTTagList();
        for (EquipmentSlot s : validSlots) {
            slotList.add(new NBTTagString(s.name()));
        }
        tag.set("Slot", slotList);
        modifiers.add(tag);
        assert root != null;
        root.set("AttributeModifiers", modifiers);
        nmsStack.setTag(root);

        stack = CraftItemStack.asBukkitCopy(nmsStack);
        return stack;
    }
}
