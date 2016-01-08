package me.Fupery.HeadShop.Command;
import me.Fupery.HeadShop.HeadShop;
import me.Fupery.HeadShop.PlayerHead;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

import static me.Fupery.HeadShop.Command.ReturnMessage.errorMessage;

public class HeadShopCommandExecutor implements CommandExecutor {

    private static final String nameKey = "name:";
    private static final String categoryKey = "category:";
    private static final String costKey = "cost:";

    private String[] playerHelp;
    private String[] adminHelp;

    private HashMap<String, HeadShopCommand> commands;

    public HeadShopCommandExecutor(final HeadShop plugin) {
        commands = new HashMap<>();
        //Commands go here - note that they are run on an async thread
        commands.put("shop", new HeadShopCommand(plugin, null, "/headshop [shop]", false) {
            @Override
            public boolean runCommand(CommandSender sender, String[] args, ReturnMessage msg) {
                HeadShop.menu.open(plugin, ((Player) sender));
                return true;
            }
        });

        commands.put("add", new HeadShopCommand(plugin, "headshop.admin", String.format(
                "/headshop add <headOwner> [%s<displayName>] [%s<categoryName>] [%s<amount>])",
                nameKey, categoryKey, costKey), true) {
            @Override
            public boolean runCommand(CommandSender sender, String[] args, ReturnMessage msg) {
                OfflinePlayer owner = Bukkit.getOfflinePlayer(args[1]);

                if (owner == null) {
                    msg.message = errorMessage("PlayerHead '%s' could not be found!");
                    return false;
                }
                String displayName = owner.getName();
                String category = "uncategorized";
                Integer cost = null;

                if (args.length > 2) {

                    for (int i = 2; i < args.length; i ++) {

                        if (args[i].contains(nameKey)) {
                            displayName = args[i].replace(nameKey, "");

                        } else if (args[i].contains(categoryKey)) {
                            category = args[i].replace(categoryKey, "");

                        } else if (args[i].contains("cost")) {

                            try {
                                cost = Integer.parseInt(args[i].replace(costKey, ""));
                            } catch (NumberFormatException e) {
                                cost = null;
                            }
                        }
                    }
                }
                PlayerHead head = new PlayerHead(owner.getName(), displayName, cost);
                plugin.addHead(head, category);
                sender.sendMessage(HeadShop.prefix + ChatColor.DARK_GREEN
                        + String.format("Successfully added %s to category %s for $%s.",
                        displayName, category, head.getCost()));
                return true;
            }
        });

        commands.put("remove", new HeadShopCommand(plugin, "headshop.admin", "/headshop remove <headOwner>", true) {
            @Override
            public boolean runCommand(CommandSender sender, String[] args, ReturnMessage msg) {

                if (!plugin.removeHead(args[1])) {
                    msg.message = errorMessage(String.format("Player head '%s' could not be found!", args[1]));
                    return false;
                }
                sender.sendMessage(HeadShop.prefix + ChatColor.DARK_GREEN
                        + String.format("Successfully removed %s.", args[1]));
                return true;
            }
        });

        commands.put("categories", new HeadShopCommand(plugin, "headshop.admin", "/headshop categories", true) {
            @Override
            public boolean runCommand(CommandSender sender, String[] args, ReturnMessage msg) {
                String categories = HeadShop.prefix + ChatColor.LIGHT_PURPLE + "Current Categories: " + ChatColor.DARK_GREEN;

                for (String string : HeadShop.getCategories().keySet()) {
                    categories += string + ", ";
                }
                sender.sendMessage(categories);
                return true;
            }
        });

        commands.put("help", new HeadShopCommand(plugin, null, "/headshop help", true) {
            @Override
            public boolean runCommand(CommandSender sender, String[] args, ReturnMessage msg) {

                sender.sendMessage(playerHelp);

                if (sender.hasPermission("headshop.admin")) {
                    sender.sendMessage(adminHelp);
                }
                return true;
            }

        });

        playerHelp = new String[] {
                HeadShop.prefix + ChatColor.LIGHT_PURPLE + "Help:",
                formatLine(commands.get("shop"), "Open the head shop", ChatColor.GREEN),
        };
        adminHelp = new String[] {
                formatLine(commands.get("add"), "Add a head", ChatColor.DARK_GREEN),
                formatLine(commands.get("remove"), "Remove a head", ChatColor.GREEN),
                formatLine(commands.get("categories"), "List current categories", ChatColor.DARK_GREEN),
        };
    };

    private String formatLine(HeadShopCommand cmd, String description, ChatColor rowColour) {
        return ChatColor.GOLD + " • " + rowColour + cmd.usage
                + ChatColor.GOLD + " | " + ChatColor.GRAY + description;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length > 0) {

            if (commands.containsKey(args[0].toLowerCase())) {
                commands.get(args[0].toLowerCase()).runPlayerCommand(sender, args);

            } else {
                sender.sendMessage(ReturnMessage.errorMessage("/headshop help"));
            }

        } else {
            commands.get("shop").runPlayerCommand(sender, args);
        }
        return true;
    }
}