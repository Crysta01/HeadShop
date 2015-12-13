package me.Fupery.HeadShop.Command;

import me.Fupery.HeadShop.HeadShop;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

class ReturnMessage implements Runnable {

    CommandSender sender;
    String message;

    ReturnMessage(CommandSender sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    @Override
    public void run() {
        sender.sendMessage(message);
    }

    public static String errorMessage(String message) {
        return HeadShop.prefix + ChatColor.RED + message;
    }
}

