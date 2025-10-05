package ru.awake.wakemobs;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import ru.awake.wakemobs.utils.Config;

public class CommandClass implements CommandExecutor {

    private final Config config;
    private final WakeMobs plugin;

    public CommandClass(WakeMobs plugin) {
        this.config = plugin.getPluginConfig();
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender.isOp())) {
            commandSender.sendMessage("§f§lRunning §a§lWakeMobs " + this.plugin.getDescription().getVersion());
        }

        if (commandSender.isOp() && strings.length == 0) {
            commandSender.sendMessage(this.config.helpMessage);
            return true;
        }

        if (commandSender.isOp() && strings[0].equals("reload")) {
            final FileConfiguration mobsSettings = this.config.getFileConfiguration(this.plugin.getDataFolder().getAbsolutePath(), "mobs-settings.yml");
            this.config.setupMobsSettings(mobsSettings);
            this.plugin.reloadPlugin(commandSender);
            return true;
        }

        return false;
    }
}
