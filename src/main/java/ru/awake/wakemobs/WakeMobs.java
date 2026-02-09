package ru.awake.wakemobs;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import ru.awake.wakemobs.config.Config;
import ru.awake.wakemobs.utils.CommandUtils;
import ru.awake.wakemobs.utils.Utils;

@Getter
public final class WakeMobs extends JavaPlugin {

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
    }

    public void onDisable() {}

    private void setupConfig() {
        getPluginConfig().setupSettings(getConfig());
        getPluginConfig().setupItemSettings(getConfig());
        getPluginConfig().setupMessages(getConfig());
        getPluginConfig().setupListeners(getConfig());
        getPluginConfig().setupEntities(getMobsSettings());
    }

}
