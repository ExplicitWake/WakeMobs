package ru.awake.wakemobs;

import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.awake.wakemobs.config.Config;
import ru.awake.wakemobs.listeners.EntityListener;
import ru.awake.wakemobs.utils.CommandUtils;
import ru.awake.wakemobs.utils.Utils;

@Getter
public final class WakeMobs extends JavaPlugin {

    private Economy economy;

    private Permission permission;

    private Config pluginConfig;

    private FileConfiguration mobsSettings;

    private Utils utils;

    private CommandUtils commandUtils;

    public void onEnable() {
        saveDefaultConfig();
        pluginConfig = new Config(this);
        mobsSettings = getPluginConfig().getFile(getDataFolder().getAbsolutePath(), "mobs-settings.yml");
        setupConfig();
        utils = new Utils(this);
        commandUtils = new CommandUtils(this);
        PluginManager pluginManager = getServer().getPluginManager();
        if (pluginManager.isPluginEnabled("Vault")) {
            setupEconomy(getServer().getServicesManager());
            setupPerms(getServer().getServicesManager());
        }
        pluginManager.registerEvents(new EntityListener(this), this);
    }

    public void onDisable() {}

    private void setupEconomy(ServicesManager servicesManager) {
        economy = getProvider(servicesManager, Economy.class);
    }

    private void setupPerms(ServicesManager servicesManager) {
        permission = getProvider(servicesManager, Permission.class);
    }

    private <T> T getProvider(ServicesManager servicesManager, Class<T> clazz) {
        final RegisteredServiceProvider<T> provider = servicesManager.getRegistration(clazz);
        return provider != null ? provider.getProvider() : null;
    }

    private void setupConfig() {
        getPluginConfig().setupSettings(getConfig());
        getPluginConfig().setupItemSettings(getConfig());
        getPluginConfig().setupMessages(getConfig());
        getPluginConfig().setupListeners(getConfig());
        getPluginConfig().setupEntities(getMobsSettings());
    }

}
