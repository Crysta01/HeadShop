package me.Fupery.HeadShop.Menu;

import me.Fupery.HeadShop.Menu.InventoryMenu.ListMenu;
import me.Fupery.HeadShop.Menu.InventoryMenu.MenuButton;
import me.Fupery.HeadShop.HeadShop;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class CategoryMenu extends ListMenu {

    private boolean dirty;

    public CategoryMenu(JavaPlugin plugin) {
        super(null, ChatColor.DARK_GREEN + "Choose a Category");
        dirty = true;
        new BukkitRunnable() {
            @Override
            public void run() {
                loadMenus();
                dirty = false;
            }
        }.runTaskAsynchronously(plugin);
    }

    private MenuButton[] generateButtons() {
        HashMap<String, String> categories = HeadShop.getCategories();
        MenuButton[] buttons;

        if (categories.size() > 0) {
            buttons = new MenuButton[categories.size()];

            int i = 0;
            for (String category : categories.keySet()) {
                HeadMenu menu = new HeadMenu(category);
                MenuButton icon = new MenuButton.LinkedButton(menu, Material.SKULL_ITEM, category);
                icon.setDurability((short) 3);
                SkullMeta meta = (SkullMeta) icon.getItemMeta();

                meta.setOwner(categories.get(category));
                icon.setItemMeta(meta);
                buttons[i] = icon;
                i ++;
            }

        } else {
            buttons = new MenuButton[0];
        }
        return buttons;
    }

    @Override
    public void open(final JavaPlugin plugin, final Player player) {

        new BukkitRunnable() {
            @Override
            public void run() {

                if (dirty) {
                    loadMenus();
                }
                openMenu(plugin, player);
            }
        }.runTaskAsynchronously(plugin);
    }

    public void openMenu(JavaPlugin plugin, Player player) {
        super.open(plugin, player);
    }

    private void loadMenus() {
        listItems = generateButtons();
        dirty = false;
    }

    public void flagDirty() {
        dirty = true;
    }
}
