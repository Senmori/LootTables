package org.bukkit.craftbukkit.loottable.utils;

import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.NBTTagString;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Set;


public class NBTUtils {

    // TODO: Replace inside appropriate ItemStack implementation.
    public static ItemStack addAttribute(AttributeModifier modifier, ItemStack stack, Set<EquipmentSlot> validSlots) {
        net.minecraft.server.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);

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
        root.set("AttributeModifiers", modifiers);
        nmsStack.setTag(root);

        stack = CraftItemStack.asBukkitCopy(nmsStack);
        return stack;
    }
}
