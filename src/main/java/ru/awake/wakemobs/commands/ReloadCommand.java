package ru.awake.wakemobs.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.awake.wakemobs.WakeMobs;
import ru.awake.wakemobs.config.Config;

import java.io.File;
import java.util.List;

public class ReloadCommand implements TabExecutor {

    private final WakeMobs wakeMobs;

    private final Config config;

    public ReloadCommand(WakeMobs wakeMobs) {
        this.wakeMobs = wakeMobs;
        this.config = wakeMobs.getPluginConfig();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (commandSender.isOp()) {
            if (strings.length == 1 && strings[0].equals("reload")) {
                wakeMobs.getServer().getScheduler().runTaskAsynchronously(wakeMobs, () -> {
                    wakeMobs.reloadConfig();
                    final FileConfiguration config = wakeMobs.getConfig();
                    this.config.setupConfig(config);
                    this.config.setupMobsSettings();
                    commandSender.sendMessage(this.config.getReloadMessage());
                });
            } else {
                commandSender.sendMessage(this.config.getHelpMessage());
            }
            return true;
        } else {
            commandSender.sendMessage("§a§lWakeMobs v" + wakeMobs.getDescription().getVersion() + "§fis running");
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (commandSender.isOp() && strings.length == 1)
            return List.of("reload");

        return List.of();
    }
}
