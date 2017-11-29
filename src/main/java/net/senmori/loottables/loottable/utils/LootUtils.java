package net.senmori.loottables.loottable.utils;

import net.minecraft.server.v1_12_R1.EntityInsentient;
import net.minecraft.server.v1_12_R1.EntityMinecartContainer;
import net.minecraft.server.v1_12_R1.Item;
import net.minecraft.server.v1_12_R1.Items;
import net.minecraft.server.v1_12_R1.MinecraftKey;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.TileEntity;
import net.minecraft.server.v1_12_R1.TileEntityLootable;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LootUtils {

    private static List<Material> validBlocks = Collections.synchronizedList(new ArrayList<>());

    static {
        // TODO: Update whenever new containers are added(Looking at you Shulker Boxes!)
        validBlocks.add(Material.CHEST);
        validBlocks.add(Material.DROPPER);
        validBlocks.add(Material.DISPENSER);
        validBlocks.add(Material.HOPPER);
        validBlocks.add(Material.TRAPPED_CHEST);
    }

    private LootUtils() {
    }

    /* ##############################
     * Loot Table functions for blocks & entities
     * ##############################
     */
    public static void setLootTable(Block block, NamespacedKey location, long seed) {
        if (! isValidBlock(block)) return;
        Validate.notNull(location);
        CraftWorld cWorld = (CraftWorld) block.getWorld();
        TileEntity tileEntity = cWorld.getTileEntityAt(block.getX(), block.getY(), block.getZ());
        if (tileEntity != null && tileEntity instanceof TileEntityLootable) {
            ( (TileEntityLootable) tileEntity ).a(new MinecraftKey(location.toString()), seed); // PAIL Rename setLootTable
        }
    }

    public static void setLootTable(Entity entity, NamespacedKey location, long seed) {
        Validate.notNull(location);
        // handle specific minecarts because they are "special"
        if (entity.getType().equals(EntityType.MINECART_CHEST) || entity.getType().equals(EntityType.MINECART_HOPPER)) {
            EntityMinecartContainer container = ( (EntityMinecartContainer) entity );
            container.a(new MinecraftKey(location.toString()), seed); // PAIL Rename setLootTable
        }
        // force update of NBT data since you cannot directly set the LootTable of an entity
        if (entity instanceof EntityInsentient) {
            EntityInsentient insentient = (EntityInsentient) entity;
            NBTTagCompound root = new NBTTagCompound();
            root.setString("DeathLootTable", new MinecraftKey(location.toString()).toString());
            if (seed != 0L) {
                root.setLong("DeathLootTableSeed", seed);
            }
            insentient.a(root); // PAIL Rename readFromNBT
        }
    }


    public static boolean isValidBlock(Block block) {
        return block != null && validBlocks.contains(block.getType());
    }


    public static String getNameForItem(net.minecraft.server.v1_12_R1.ItemStack stack) {
        for (MinecraftKey key : Item.REGISTRY.keySet()) {
            if (Item.REGISTRY.get(key) != Items.a && stack.getName().equals(Item.REGISTRY.get(key).getName())) {
                return key.toString();
            }
        }
        return stack.getName();
    }
}
