package me.Fupery.HeadShop;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logging {

    private static final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy|kk:mm");
    private static final String log = "transactions.log";

    public static void log(final JavaPlugin plugin, final Player player, final PlayerHead head) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                File file = new File(plugin.getDataFolder(), log);

                if (!file.exists()) {

                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        plugin.getLogger().severe("Could not create log file: " + e.getMessage());
                        return;
                    }
                }
                FileWriter fileWriter;
                PrintWriter logger;
                try {
                    fileWriter = new FileWriter(file, true);
                    logger = new PrintWriter(fileWriter);
                    logger.println(String.format("[%s] %s BOUGHT \'%s\' FOR $%s",
                            dateFormat.format(new Date()), player.getName(), head.getName(), head.getCost()));
                    logger.flush();
                    logger.close();
                } catch (IOException e) {
                    plugin.getLogger().severe("Could not log to " + log + ", " + e.getMessage());
                    return;
                }
            }
        });
    }
}
