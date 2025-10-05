package ru.awake.wakemobs;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import ru.awake.wakemobs.listeners.MobListener;
import ru.awake.wakemobs.utils.Config;

public final class WakeMobs extends JavaPlugin {

    private final Config pluginConfig = new Config(this);

    public Permission perms;
    public Economy economy;

    BukkitTask bukkitTask;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        setupPerms();
        final FileConfiguration mobsSettings = pluginConfig.getFileConfiguration(getDataFolder().getAbsolutePath(), "mobs-settings.yml");
        this.pluginConfig.setupMobsSettings(mobsSettings);
        setupConfig();
        setupBukkitTask();
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new MobListener(this), this);
        getCommand("wakemobs").setExecutor(new CommandClass(this));
        if (!setupEconomy() ) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
    }

    public Config getPluginConfig() {
        return pluginConfig;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return true;
    }

    private void setupPerms() {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager()
                .getRegistration(Permission.class);
        if (permissionProvider != null)
            this.perms = permissionProvider.getProvider();
    }

    private void setupConfig() {
        FileConfiguration configuration = getConfig();
        this.pluginConfig.setupItemSettings(configuration);
        this.pluginConfig.setupSettings(configuration);
        this.pluginConfig.setupNotifications(configuration);
        this.pluginConfig.setupMessages(configuration);
    }

    private void setupBukkitTask() {
        this.bukkitTask = (new NearEntitiesRunnable(this).runTaskTimer(this, 5, 5));
    }

    public void reloadPlugin(CommandSender commandSender) {
        reloadConfig();
        setupConfig();
        commandSender.sendMessage(this.pluginConfig.reloadMessage);
    }
}
