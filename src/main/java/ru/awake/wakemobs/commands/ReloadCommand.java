package ru.awake.wakemobs.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.awake.wakemobs.WakeMobs;
import ru.awake.wakemobs.config.Config;

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
            if (strings[0].equals("reload")) {
                wakeMobs.getServer().getScheduler().runTaskAsynchronously(wakeMobs, () -> {
                    final FileConfiguration config = this.config.getFile(wakeMobs.getDataFolder().getAbsolutePath(), "config.yml");
                    final FileConfiguration mobsSettings = this.config.getFile(wakeMobs.getDataFolder().getAbsolutePath(), "mobs-settings.yml");
                    this.config.setupSettings(config);
                    this.config.setupItemSettings(config);
                    this.config.setupMessages(config);
                    this.config.setupListeners(config);
                    this.config.setupEntities(mobsSettings);
                    commandSender.sendMessage(this.config.getReloadMessage());
                });
            } else {
                commandSender.sendMessage(this.config.getHelpMessage());
            }
            return true;
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
