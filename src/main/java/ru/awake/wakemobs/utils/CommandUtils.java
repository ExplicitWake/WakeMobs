package ru.awake.wakemobs.utils;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.awake.wakemobs.WakeMobs;
import ru.awake.wakemobs.utils.color.LegacyColorize;
import ru.awake.wakemobs.utils.commands.Command;
import ru.awake.wakemobs.utils.commands.CommandType;

import java.util.Arrays;
import java.util.List;

public class CommandUtils {

    private final WakeMobs wakeMobs;

    public CommandUtils(WakeMobs wakeMobs) {
        this.wakeMobs = wakeMobs;
    }

    private String formatContext(Command command, String[] searchList, String[] replacementList) {
        return replaceEach(LegacyColorize.colorize(command.getContext()), searchList, replacementList);
    }

    public void runCommands(List<Command> commands, String[] searchList, String[] replacementList, Player player) {
        for (Command command : commands) {
            CommandType commandType = command.getCommandType();
            switch (commandType) {
                case MESSAGE -> sendMessage(player, command, searchList, replacementList);
                case TITLE -> sendTitle(player, command, searchList, replacementList);
                case SOUND -> sendSound(player, command);
                case ACTIONBAR -> sendActionbar(player, command, searchList, replacementList);
            }
        }
    }

    private void sendActionbar(Player player, Command command, String[] searchList, String[] replacementList) {
        String formatted = formatContext(command, searchList, replacementList);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(formatted));
    }

    private void sendSound(Player player, Command command) {
        String[] soundArgs = command.getContext().split(";");
        sendSound(soundArgs, player);
    }

    private void sendTitle(Player player, Command command, String[] searchList, String[] replacementList) {
        String formatted = formatContext(command, searchList, replacementList);
        String[] titleMessages = formatted.split(";");
        sendTitleMessage(titleMessages, player);
    }

    private void sendMessage(Player player, Command command, String[] searchList, String[] replacementList) {
        String formatted = formatContext(command, searchList, replacementList);
        player.sendMessage(formatted);
    }

    public void sendTitleMessage(@NotNull String[] titleMessages, @NotNull Player p) {
        if (titleMessages[0].isEmpty()) {
            return;
        }
        if (titleMessages.length > 5) {
            wakeMobs.getServer().getConsoleSender().sendMessage("Unable to send title. " + Arrays.toString(titleMessages));
            return;
        }
        String title = titleMessages[0];
        String subtitle = titleMessages.length >= 2 ? titleMessages[1] : "";
        int fadeIn = titleMessages.length >= 3 ? Integer.parseInt(titleMessages[2]) : 10;
        int stay = titleMessages.length >= 4 ? Integer.parseInt(titleMessages[3]) : 70;
        int fadeOut = titleMessages.length == 5 ? Integer.parseInt(titleMessages[4]) : 20;
        p.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }

    public void sendSound(@NotNull String[] soundArgs, @NotNull Player p) {
        if (soundArgs[0].isEmpty()) {
            return;
        }
        if (soundArgs.length > 3) {
            wakeMobs.getServer().getConsoleSender().sendMessage("Unable to send sound. " + Arrays.toString(soundArgs));
            return;
        }
        Sound sound = Sound.valueOf(soundArgs[0]);
        float volume = soundArgs.length >= 2 ? Float.parseFloat(soundArgs[1]) : 1.0F;
        float pitch = soundArgs.length == 3 ? Float.parseFloat(soundArgs[2]) : 1.0F;
        p.playSound(p.getLocation(), sound, volume, pitch);
    }

    public String replaceEach(String text, String[] searchList, String[] replacementList) {
        if (text.isEmpty() || searchList.length == 0 || replacementList.length == 0) {
            return text;
        }

        if (searchList.length != replacementList.length) {
            throw new IllegalArgumentException("Search and replacement arrays must have the same length.");
        }

        final StringBuilder result = new StringBuilder(text);

        for (int i = 0; i < searchList.length; i++) {
            final String search = searchList[i];
            final String replacement = replacementList[i];

            int start = 0;

            while ((start = result.indexOf(search, start)) != -1) {
                result.replace(start, start + search.length(), replacement);
                start += replacement.length();
            }
        }

        return result.toString();
    }

}
