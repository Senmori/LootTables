package org.bukkit.craftbukkit.loottable.utils;

import net.minecraft.server.EntityInsentient;
import net.minecraft.server.EntityMinecartContainer;
import net.minecraft.server.Item;
import net.minecraft.server.MinecraftKey;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.TileEntity;
import net.minecraft.server.TileEntityLootable;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.ResourceLocation;

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
	
	private LootUtils() {}
	
	/* ##############################
	 * Loot Table functions for blocks & entities
	 * ##############################
	 */
	// TODO: Replace inside appropriate blocks.
	public static void setLootTable(Block block, ResourceLocation location, long seed) {
        if(!isValidBlock(block)) return;
        Validate.notNull(location);
        BlockState state = block.getState();
        TileEntity tileEntity = ((CraftBlockState)state).getTileEntity();
        if(tileEntity instanceof TileEntityLootable) {
            ((TileEntityLootable)tileEntity).a(new MinecraftKey(location.toString()), seed); // PAIL Rename setLootTable
        }
    }

    // TODO: Replace inside appropriate entities.
    public static void setLootTable(Entity entity, ResourceLocation location, long seed) {
        Validate.notNull(location);
        // handle specific minecarts because they are "special"
        if(entity.getType().equals(EntityType.MINECART_CHEST) || entity.getType().equals(EntityType.MINECART_HOPPER)) {
            EntityMinecartContainer container = ((EntityMinecartContainer)entity);
            container.a(new MinecraftKey(location.toString()), seed); // PAIL Rename readFromNBT
        }
        // force update of NBT data since you cannot directly set the LootTable of an entity
        EntityInsentient insentient = (EntityInsentient)entity;
        NBTTagCompound root = new NBTTagCompound();
        root.setString("DeathLootTable", new MinecraftKey(location.toString()).toString());
        if(seed != 0L) {
            root.setLong("DeathLootTableSeed", seed);
        }
        insentient.a(root); // PAIL Rename readFromNBT
    }
	
	
	public static boolean isValidBlock(Block block) {
		return block != null && validBlocks.contains(block.getType());
	}


    public static String getNameForItem(net.minecraft.server.ItemStack stack) {
        for (MinecraftKey key : Item.REGISTRY.keySet()) {
            if (Item.REGISTRY.get(key).getName().equals(stack.getItem().getName())) {
                return key.toString();
            }
        }
        return stack.getName();
    }
}
