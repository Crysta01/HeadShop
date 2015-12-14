package me.Fupery.HeadShop.Menu;

import me.Fupery.HeadShop.Logging;
import me.Fupery.HeadShop.Menu.InventoryMenu.ListMenu;
import me.Fupery.HeadShop.Menu.InventoryMenu.MenuButton;
import me.Fupery.HeadShop.PlayerHead;
import me.Fupery.HeadShop.HeadShop;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;


public class HeadMenu extends ListMenu {

    String category;

    public HeadMenu(String category) {
        super(HeadShop.menu, ChatColor.DARK_GREEN + "Click to Purchase");
        this.category = category;
        listItems = generateButtons();
    }

    private MenuButton[] generateButtons() {
        PlayerHead[] heads = HeadShop.getHeads(category);
        MenuButton[] buttons;

        if (heads != null && heads.length > 0) {
            buttons = new MenuButton[heads.length];

            for (int i = 0; i < heads.length; i++) {
                buttons[i] = new HeadbuyButton(heads[i]);
            }

        } else {
            buttons = new MenuButton[0];
        }
        return buttons;
    }

    @Override
    public void open(JavaPlugin plugin, Player player) {
        super.open(plugin, player);
    }

    private class HeadbuyButton extends MenuButton {

        PlayerHead head;

        public HeadbuyButton(PlayerHead head) {
            super(Material.SKULL_ITEM);
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(head.getOwner());

            setDurability((short) 3);
            SkullMeta meta = (SkullMeta) getItemMeta();

            meta.setOwner(offlinePlayer.getName());
            meta.setDisplayName(head.getName());
            meta.setLore(Arrays.asList(
                    ChatColor.GOLD + "Price: $" + head.getCost(),
                    ChatColor.GREEN + "Click to Buy"));
            setItemMeta(meta);
            this.head = head;
        }

        @Override
        public void onClick(JavaPlugin plugin, final Player player) {

            if (HeadShop.getEconomy().getBalance(player) < head.getCost()
                    && !player.hasPermission("headshop.admin")) {
                player.sendMessage(ChatColor.RED + "Sorry, you don't have enough money to purchase this Head!");
                player.playSound(player.getLocation(), Sound.CLICK, 1, 1);

            } else {

                if (!player.hasPermission("headshop.admin")) {
                    HeadShop.getEconomy().withdrawPlayer(player, head.getCost());
                    player.sendMessage(String.format(ChatColor.GREEN + "Purchased %s for $%s",
                            head.getName(), head.getCost()));
                    Logging.log(plugin, player, head);
                }

                Bukkit.getScheduler().runTask(plugin, new Runnable() {
                    @Override
                    public void run() {

                        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                        ItemMeta meta = getItemMeta();
                        meta.setDisplayName(null);
                        meta.setLore(Collections.<String>emptyList());
                        skull.setItemMeta(meta);

                        HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(skull);

                        if (leftover != null && leftover.get(0) != null) {
                            player.getWorld().dropItemNaturally(player.getLocation(), leftover.get(0));
                        }
                    }
                });
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
            }
        }
    }
}
