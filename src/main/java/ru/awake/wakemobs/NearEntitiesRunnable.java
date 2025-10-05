package ru.awake.wakemobs;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import ru.awake.wakemobs.utils.Config;
import ru.awake.wakemobs.utils.Utils;

public class NearEntitiesRunnable extends BukkitRunnable {

    private final Config config;
    private final WakeMobs plugin;
    private final Utils utils;

    public NearEntitiesRunnable(WakeMobs plugin) {
        this.plugin = plugin;
        this.utils = new Utils(plugin);
        this.config = plugin.getPluginConfig();
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getInventory().firstEmpty() == -1) {
                for (Entity entity : player.getNearbyEntities(1, 1, 1)) {
                    if (entity instanceof Item) {
                        Item item = (Item)entity;
                        ItemStack itemStack = item.getItemStack();
                        if (!this.utils.isMoneyItem(itemStack))
                            continue;
                        String[] strings = itemStack.getItemMeta().getDisplayName().split("\\|");
                        this.plugin.economy.depositPlayer(player, Double.parseDouble(strings[1]));
                        if (this.config.isSoundEnabled) {
                            player.playSound(player.getLocation(), Sound.valueOf(this.config.soundId), this.config.yawVolume, this.config.pitchVolume);
                        }
                        if (this.config.isActionBarEnabled) {
                            String message = this.config.actionbarMessage.replace("{money}", strings[0]).replace("{booster}", strings[2]);
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
                        }
                        if (this.config.isActionBarEnabled) {
                            String message = this.config.chatMessage.replace("{money}", strings[0]).replace("{booster}", strings[2]);
                            player.sendMessage(message);
                        }
                        item.remove();
                    }
                }
            }
        }
    }
}
