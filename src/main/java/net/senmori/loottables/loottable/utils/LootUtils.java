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
        validBlocks.add(Material.CHEST);
        validBlocks.add(Material.DROPPER);
        validBlocks.add(Material.DISPENSER);
        validBlocks.add(Material.HOPPER);
        validBlocks.add(Material.TRAPPED_CHEST);
        validBlocks.add(Material.WHITE_SHULKER_BOX);
        validBlocks.add(Material.ORANGE_SHULKER_BOX);
        validBlocks.add(Material.MAGENTA_SHULKER_BOX);
        validBlocks.add(Material.LIGHT_BLUE_SHULKER_BOX);
        validBlocks.add(Material.YELLOW_SHULKER_BOX);
        validBlocks.add(Material.LIME_SHULKER_BOX);
        validBlocks.add(Material.PINK_SHULKER_BOX);
        validBlocks.add(Material.GRAY_SHULKER_BOX);
        validBlocks.add(Material.SILVER_SHULKER_BOX);
        validBlocks.add(Material.CYAN_SHULKER_BOX);
        validBlocks.add(Material.PURPLE_SHULKER_BOX);
        validBlocks.add(Material.BLUE_SHULKER_BOX);
        validBlocks.add(Material.BROWN_SHULKER_BOX);
        validBlocks.add(Material.GREEN_SHULKER_BOX);
        validBlocks.add(Material.RED_SHULKER_BOX);
        validBlocks.add(Material.BLACK_SHULKER_BOX);
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
            ( (TileEntityLootable) tileEntity ).a(new MinecraftKey(location.toString()), seed);
        }
    }

    public static void setLootTable(Entity entity, NamespacedKey location, long seed) {
        Validate.notNull(location);
        // handle specific minecarts because they are "special"
        if (entity.getType().equals(EntityType.MINECART_CHEST) || entity.getType().equals(EntityType.MINECART_HOPPER)) {
            EntityMinecartContainer container = ( (EntityMinecartContainer) entity );
            container.a(new MinecraftKey(location.toString()), seed);
        }
        // force update of NBT data since you cannot directly set the LootTable of an entity
        if (entity instanceof EntityInsentient) {
            EntityInsentient insentient = (EntityInsentient) entity;
            NBTTagCompound root = new NBTTagCompound();
            insentient.b(root); // load nbt from entity
            root.setString("DeathLootTable", new MinecraftKey(location.toString()).toString());
            if (seed != 0L) {
                root.setLong("DeathLootTableSeed", seed);
            }
            insentient.a(root);
        }
    }


    public static boolean isValidBlock(Block block) {
        return block != null && validBlocks.contains(block.getType());
    }
}
