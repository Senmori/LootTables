package net.senmori.loottables.commands;

import net.senmori.loottables.LootTables;
import net.senmori.loottables.loottable.utils.LootUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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
        Block block = getPlayer().getTargetBlock((Set<Material>) null, 5);
        if (block.getType() != Material.AIR) {
            LootUtils.setLootTable(block, new NamespacedKey(LootTables.getInstance(), "debug"), 0L);
        }
    }
}
