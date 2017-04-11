package net.senmori.loottables.managers;

import net.senmori.loottables.commands.Subcommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.joptsimple.internal.Strings;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by Senmori on 4/27/2016.
 */
public class CommandManager implements CommandExecutor {

    private JavaPlugin plugin;
    private String commandPrefix;
    private List<Subcommand> commands = new ArrayList<>();

    public CommandManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerCommand(Subcommand command) {
        if(!commands.add(command)) {
            plugin.getLogger().log(Level.WARNING, command.getName() + " is already registered!");
        }
    }

    public void setCommandPrefix(String prefix) {
        this.commandPrefix = prefix;
        plugin.getCommand(prefix).setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        List<String> argsList = new ArrayList<String>();

        if (args.length > 0) {
            String commandName = args[0].toLowerCase();

            for (int i = 1; i < args.length; i++) {
                argsList.add(args[i]);
            }

            for (Subcommand command : commands) {
                if (command.getName().equals(commandName) || command.getAliases().contains(commandName)) {
                    command.execute(sender, argsList.toArray(new String[argsList.size()]));
                    return true;
                }
            }
        } else {
            // display list of subcommand names, along with their descriptions
            StringBuilder sb = new StringBuilder();
            sb.append(ChatColor.GREEN + Strings.repeat('-', 16) + " " + ChatColor.YELLOW + "Hunted commands" + ChatColor.GREEN + " " + Strings.repeat('-', 16));
            for (Subcommand sub : commands) {
                sb.append("\n");
                sb.append(ChatColor.GREEN + sub.getName() + ChatColor.YELLOW + " - " + ChatColor.YELLOW
                                  + sub.getDescription());
            }
            sb.append(ChatColor.GREEN + Strings.repeat('-', 49));
            if (sender instanceof Player) {
                Player player = (Player) sender;
                player.sendMessage(sb.toString());
                return true;
            }
            Bukkit.dispatchCommand(sender, "help " + commandPrefix);
        }

        return true;
    }

    public String getCommandPrefix() { return this.commandPrefix; }
    public List<Subcommand> getCommands() { return this.commands; }
}
