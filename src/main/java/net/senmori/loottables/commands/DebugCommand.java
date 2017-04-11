package net.senmori.loottables.commands;

import org.bukkit.craftbukkit.loot.storage.ResourceLocation;
import org.bukkit.craftbukkit.loot.utils.LootUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Set;

/**
 * Created by Senmori on 4/27/2016.
 */
public class DebugCommand extends Subcommand {

    public DebugCommand() {
        this.needsPlayer = true;
        this.permission = "lt.commands.debug";

    }

    @Override
    protected void perform() {
        Block block = getPlayer().getTargetBlock((Set<Material>)null, 5);
        if(block.getType() != Material.AIR) {
            LootUtils.setLootTable(block, new ResourceLocation("hunted", "debug"), 0L);
        }
    }
}
