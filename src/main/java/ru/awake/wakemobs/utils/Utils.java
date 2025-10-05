package ru.awake.wakemobs.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.awake.wakemobs.WakeMobs;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private final Config config;
    private final Random random = new Random();

    public static String SUB_VERSION = Bukkit.getBukkitVersion().split("\\.")[1];
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([a-fA-F\\d]{6})");

    public Utils(WakeMobs plugin) {
        this.config = plugin.getPluginConfig();
    }

    public static String color(String message) {
        if (SUB_VERSION.contains("-")) {
            SUB_VERSION = SUB_VERSION.split("-")[0];
        }
        if (Integer.parseInt(SUB_VERSION) >= 16) {
            Matcher matcher = HEX_PATTERN.matcher(message);
            StringBuilder builder = new StringBuilder(message.length() + 32);
            while (matcher.find()) {
                String group = matcher.group(1);
                matcher.appendReplacement(builder, "§x§" +
                        group.charAt(0) + "§" + group.charAt(1) + "§" +
                        group.charAt(2) + "§" + group.charAt(3) + "§" + group.charAt(4) + "§" +
                        group.charAt(5));
            }
            message = matcher.appendTail(builder).toString();
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String decimalFormat(double formatted) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.ROOT);
        DecimalFormat format = new DecimalFormat("#.#", symbols);
        return format.format(formatted);
    }

    public boolean isGroupContains(String group) {
        return this.config.boosters.contains(group);
    }

    public boolean isMoneyDrop() {
        int randomValue = random.nextInt(100);
        return randomValue <= this.config.chanceDrop;
    }

    public boolean isMoneyItem(ItemStack itemStack) {
        return itemStack.hasItemMeta()
                && itemStack.getItemMeta().hasCustomModelData()
                && itemStack.getItemMeta().getCustomModelData() == 529;
    }

    public boolean isInstant() {
        return this.config.typeDrop.equals("Instant");
    }

    public boolean isCollecting() {
        return this.config.typeDrop.equals("Collecting");
    }

    public void sendMessages(Player player, String result, double booster) {
        if (this.config.isSoundEnabled) {
            player.playSound(player.getLocation(), Sound.valueOf(this.config.soundId), this.config.yawVolume, this.config.pitchVolume);
        }

        if (this.config.isActionBarEnabled) {
            String replaced = this.config.actionbarMessage.replace("{money}", result)
                    .replace("{booster}", String.valueOf(booster));
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(replaced));
        }
        if (this.config.isChatEnabled) {
            String replaced = this.config.chatMessage.replace("{money}", result)
                    .replace("{booster}", String.valueOf(booster));
            player.sendMessage(replaced);
        }
    }

    public boolean notNull(Player player, LivingEntity entity) {
        return !(entity instanceof Player) && player != null;
    }
}
