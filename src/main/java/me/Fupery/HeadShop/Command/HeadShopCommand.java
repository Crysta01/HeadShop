package me.Fupery.HeadShop.Command;

import me.Fupery.HeadShop.HeadShop;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.Fupery.HeadShop.Command.ReturnMessage.errorMessage;

interface AbstractCommand {
    boolean runCommand(CommandSender sender, String[] args, ReturnMessage msg);
}

public abstract class HeadShopCommand implements AbstractCommand {

    String usage;
    HeadShop plugin;
    private AbstractCommand ArtMapCommand = this;
    private String permission;
    private int minArgs;
    private int maxArgs;
    private boolean consoleAllowed;

    HeadShopCommand(HeadShop plugin, String permission, String usage, boolean consoleAllowed) {
        this.permission = permission;
        this.consoleAllowed = consoleAllowed;

        if (usage == null) {
            throw new IllegalArgumentException("Usage must not be null");
        }
        String[] args = usage.replace("/headshop ", "").split("\\s+");
        maxArgs = args.length;
        minArgs = maxArgs - StringUtils.countMatches(usage, "[");
        this.usage = usage;
        this.plugin = plugin;
    }

    void runPlayerCommand(final CommandSender sender, final String args[]) {

        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {

            @Override
            public void run() {

                ReturnMessage returnMsg = new ReturnMessage(sender, null);

                if (permission != null && !sender.hasPermission(permission)) {
                    returnMsg.message = errorMessage("You don't have permission to do that!");

                } else if (!consoleAllowed && !(sender instanceof Player)) {
                    returnMsg.message = errorMessage("Only players can use this command!");

                } else if (args.length < minArgs || args.length > maxArgs) {
                    returnMsg.message = errorMessage(usage);

                } else if (ArtMapCommand.runCommand(sender, args, returnMsg)) {
                    return;
                }

                if (returnMsg.message != null) {
                    Bukkit.getScheduler().runTask(plugin, returnMsg);
                }
            }
        });
    }
}

