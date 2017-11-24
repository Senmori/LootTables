package net.senmori.loottables.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Senmori on 4/27/2016.
 */
public abstract class Subcommand {

    protected CommandSender sender;
    protected Player player;
    protected String[] args;

    protected String name;
    protected String description;

    protected String permission;

    protected boolean needsPlayer;

    protected List<String> requiredArgs = new ArrayList<>();
    protected List<String> optionalArgs = new ArrayList<>();
    protected List<String> aliases = new ArrayList<>();

    protected abstract void perform();


    public void execute(CommandSender sender, String[] args) {
        this.sender = sender;
        this.args = args;

        if (sender instanceof Player) {
            this.player = (Player) sender;
        }

        if (needsPlayer && ! isPlayer()) return;

        if (args.length < requiredArgs.size()) return;
        if (! hasPermission()) return;

        try {
            perform();
        } catch (Throwable e) {

        }
    }


    public String getUsageTemplate(boolean displayHelp) {
        StringBuilder ret = new StringBuilder();

        ret.append(ChatColor.GREEN + name + " ");
        for (String s : requiredArgs) {
            ret.append(ChatColor.GREEN + String.format("<%s> ", s));
        }

        for (String s : optionalArgs) {
            ret.append(ChatColor.GREEN + String.format("[%s]", s));
        }

        if (displayHelp) {
            ret.append(ChatColor.YELLOW + " - " + ChatColor.GREEN + description);
        }
        return ret.toString();
    }

    public CommandSender getSender() {
        return sender;
    }

    public Player getPlayer() {
        return player;
    }

    public String[] getArgs() {
        return args;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPermission() {
        return permission;
    }

    public List<String> getRequiredArgs() {
        return requiredArgs;
    }

    public List<String> getOptionalArgs() {
        return optionalArgs;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public boolean hasPermission() {
        return sender.hasPermission(permission);
    }

    public boolean isPlayer() {
        return player != null;
    }
}
