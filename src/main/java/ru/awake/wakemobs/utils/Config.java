package ru.awake.wakemobs.utils;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import ru.awake.wakemobs.WakeMobs;
import java.io.File;

public class Config {

    private final WakeMobs plugin;

    public Config(WakeMobs plugin) {
        this.plugin = plugin;
    }

    public String itemId;

    public boolean isEnchantEffect;

    public int amountItems;

    public String typeDrop;

    public int chanceDrop;

    public int minDrop;

    public int maxDrop;

    public ConfigurationSection boosters;

    public boolean isSoundEnabled;

    public boolean isActionBarEnabled;

    public boolean isChatEnabled;

    public String soundId;

    public float yawVolume;

    public float pitchVolume;

    public String actionbarMessage;

    public String chatMessage;

    public String helpMessage;

    public String reloadMessage;

    public String noMoneyMessage;

    public ConfigurationSection mobsSettings;

    public void setupItemSettings(FileConfiguration configuration) {
        ConfigurationSection itemSettings = configuration.getConfigurationSection("items-settings");
        this.itemId = itemSettings.getString("item-id");
        this.isEnchantEffect = itemSettings.getBoolean("enchant-effect");
        this.amountItems = itemSettings.getInt("amount-items");
    }

    public void setupSettings(FileConfiguration configuration) {
        ConfigurationSection settings = configuration.getConfigurationSection("settings");
        this.typeDrop = settings.getString("type-drop");
        this.boosters = settings.getConfigurationSection("boosters");
    }

    public void setupNotifications(FileConfiguration configuration) {
        ConfigurationSection notifications = configuration.getConfigurationSection("notification-settings");
        ConfigurationSection soundSettings = notifications.getConfigurationSection("sound");
        ConfigurationSection actionbarSettings = notifications.getConfigurationSection("actionbar");
        ConfigurationSection chatSettings = notifications.getConfigurationSection("chat");
        this.isSoundEnabled = soundSettings.getBoolean("enabled");
        this.yawVolume = (float) soundSettings.getDouble("yaw-volume");
        this.pitchVolume = (float) soundSettings.getDouble("pitch-volume");
        this.isActionBarEnabled = actionbarSettings.getBoolean("enabled");
        this.isChatEnabled = chatSettings.getBoolean("enabled");
        this.soundId = soundSettings.getString("sound-id");
        this.actionbarMessage = Utils.color(actionbarSettings.getString("message"));
        this.chatMessage = Utils.color(chatSettings.getString("message"));
    }

    public void setupMessages(FileConfiguration configuration) {
        ConfigurationSection messagesSettings = configuration.getConfigurationSection("messages");
        this.helpMessage = Utils.color(messagesSettings.getString("help"));
        this.reloadMessage = Utils.color(messagesSettings.getString("reload"));
        this.noMoneyMessage = Utils.color(messagesSettings.getString("no-money"));
    }

    public void setupMobsSettings(FileConfiguration configuration) {
        this.mobsSettings = configuration.getConfigurationSection("mobs-settings");
    }

    public void newSettings(LivingEntity entity) {
        String entityType = entity.getType().toString();
        if (this.mobsSettings.contains(entityType)) {
            this.chanceDrop = this.mobsSettings.getInt(entityType + ".chance-drop");
            this.minDrop = this.mobsSettings.getInt(entityType +  ".min-drop");
            this.maxDrop = this.mobsSettings.getInt(entityType + ".max-drop");
        } else {
            this.chanceDrop = this.mobsSettings.getInt("default.chance-drop");
            this.minDrop = this.mobsSettings.getInt("default.min-drop");
            this.maxDrop = this.mobsSettings.getInt("default.max-drop");
        }
    }

    public FileConfiguration getFileConfiguration(String path, String fileName) {
        File file = new File(path, fileName);
        if (!file.exists()) {
            this.plugin.saveResource(fileName, false);
        }
        return YamlConfiguration.loadConfiguration(file);
    }

}
