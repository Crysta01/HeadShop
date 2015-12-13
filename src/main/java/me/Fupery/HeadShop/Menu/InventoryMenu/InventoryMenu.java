package me.Fupery.HeadShop.Menu.InventoryMenu;

import me.Fupery.HeadShop.HeadShop;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ConcurrentHashMap;

public class InventoryMenu {

    public static final ConcurrentHashMap<Player, InventoryMenu> openMenus = new ConcurrentHashMap<>();

    protected InventoryType type;
    protected InventoryMenu parent;
    protected String title;
    protected MenuButton[] buttons;

    protected InventoryMenu(InventoryMenu parent, String title, InventoryType type) {
        this.buttons = new MenuButton[type.getDefaultSize()];
        this.type = type;
        this.title = title;
        this.parent = parent;
    }

    protected void addButtons(MenuButton... buttons) {

        if (buttons != null && buttons.length > 0) {
            System.arraycopy(buttons, 0, this.buttons, 0, buttons.length);
        }
    }

    void clearButtons() {
        buttons = new MenuButton[type.getDefaultSize()];
    }

    protected void updateInventory(JavaPlugin plugin, final Player player) {
        Bukkit.getScheduler().runTask(plugin, new Runnable() {
            @Override
            public void run() {
                Inventory inventory = player.getOpenInventory().getTopInventory();

                for (int slot = 0; slot < inventory.getSize(); slot++) {

                    if (getButton(slot) != null) {
                        inventory.setItem(slot, getButton(slot));

                    } else {
                        inventory.setItem(slot, new ItemStack(Material.AIR));
                    }
                }
                player.updateInventory();
            }
        });
    }

    public MenuButton getButton(int slot) {
        return buttons[slot];
    }

    public void open(JavaPlugin plugin, final Player player) {
        final InventoryMenu menu = this;
        Bukkit.getScheduler().runTask(plugin, new Runnable() {
            @Override
            public void run() {

                if (openMenus.containsKey(player)) {
                    openMenus.get(player).close(player);
                }

                Inventory inventory = Bukkit.createInventory(player, type, HeadShop.prefix + title);

                for (int slot = 0; slot < inventory.getSize(); slot++) {

                    if (getButton(slot) != null) {
                        inventory.setItem(slot, getButton(slot));
                    }
                }
                openMenus.put(player, menu);
                player.openInventory(inventory);
            }
        });

    }

    public void close(Player player) {
        openMenus.remove(player);
        player.closeInventory();
    }

    public String getTitle() {
        return title;
    }
}

