package me.Fupery.HeadShop;

import me.Fupery.HeadShop.Command.HeadShopCommandExecutor;
import me.Fupery.HeadShop.Menu.CategoryMenu;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Set;

public class HeadShop extends JavaPlugin {

    public static CategoryMenu menu;
    private static ConfigurationSection categories;
    private static Economy economy = null;

    private static int defaultCost;

    private static final String ownerKey = "owner";
    private static final String costKey = "cost";
    public static final String prefix = "§5[HeadShop] ";

    private MenuListener listener;

    @Override
    public void onEnable() {
        super.onEnable();
        menu = new CategoryMenu(this);

        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
            saveDefaultConfig();
        }
        FileConfiguration config = getConfig();
        defaultCost = config.getInt("defaultCost");
        categories = config.getConfigurationSection("categories");
        listener = new MenuListener(this);
        getServer().getPluginManager().registerEvents(listener, this);
        getCommand("headshop").setExecutor(new HeadShopCommandExecutor(this));

        if (!setupEconomy()) {
            getLogger().warning("Economy not found, disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    private boolean setupEconomy() {

        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer()
                .getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    public static HashMap<String, String> getCategories() {
        HashMap<String, String> categories = new HashMap<>();
        Set<String> keys = HeadShop.categories.getKeys(false);

        for (String categoryKey : keys) {
            ConfigurationSection category = HeadShop.categories.getConfigurationSection(categoryKey);
            Set<String> headKeys = category.getKeys(false);

            if (headKeys.size() < 1) {
                continue;
            }
            String[] heads = headKeys.toArray(new String[headKeys.size()]);
            String icon = category.getConfigurationSection(heads[0]).getString(ownerKey);
            categories.put(categoryKey, icon);
        }
        return categories;
    }

    public static PlayerHead[] getHeads(String category) {

        ConfigurationSection categorySection = categories.getConfigurationSection(category);

        if (categorySection == null) {
            return null;
        }
        Set<String> keys = categorySection.getKeys(false);
        PlayerHead[] heads = new PlayerHead[keys.size()];

        int i = 0;
        for (String skullName : categorySection.getKeys(false)) {
            ConfigurationSection skullData = categorySection.getConfigurationSection(skullName);
            Integer cost = skullData.contains(costKey) ? skullData.getInt(costKey) : null;
            heads[i] = new PlayerHead(skullData.getString(ownerKey), skullName, cost);
            i ++;
        }

        return heads;
    }

    public void addHead(final PlayerHead head, String category) {
        if (!categories.contains(category)) {
            categories.createSection(category);
        }
        ConfigurationSection categorySection = categories.getConfigurationSection(category);
        ConfigurationSection skullData = categorySection.createSection(head.getName());
        skullData.set(ownerKey, head.getOwner());

        if (head.hasCost()) {
            skullData.set(costKey, head.getCost());
        }
        updateConfig();
    }

    public boolean removeHead(String head) {

        if (!categories.getKeys(true).contains(head)) {

            for (String categoryName : categories.getKeys(false)) {

                ConfigurationSection category = categories.getConfigurationSection(categoryName);

                if (category.contains(head)) {
                    category.set(head, null);
                    updateConfig();
                    return true;
                }
            }
        }
        return false;
    }

    void updateConfig() {
        menu.flagDirty();
        FileConfiguration config = getConfig();
        config.set("categories", categories);
        saveConfig();
        reloadConfig();
    }

    public static Economy getEconomy() {
        return economy;
    }

    public static int getDefaultCost() {
        return defaultCost;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        listener.closeMenus();
    }
}
