package ru.awake.wakemobs.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitScheduler;
import ru.awake.wakemobs.WakeMobs;
import ru.awake.wakemobs.config.Config;
import ru.awake.wakemobs.utils.CommandUtils;
import ru.awake.wakemobs.utils.EventType;
import ru.awake.wakemobs.utils.TypeDrop;
import ru.awake.wakemobs.utils.Utils;

public class PlayerListener implements Listener {

    private final WakeMobs wakeMobs;

    private final Config config;

    private final CommandUtils commandUtils;

    private final Utils utils;

    private final BukkitScheduler bukkitScheduler;

    private final String[] searchListNotKiller;

    private final String[] searchListKiller;

    private final String[] searchListForKiller;

    public PlayerListener(WakeMobs wakeMobs) {
        this.wakeMobs = wakeMobs;
        this.config = wakeMobs.getPluginConfig();
        this.commandUtils = wakeMobs.getCommandUtils();
        this.utils = wakeMobs.getUtils();
        this.bukkitScheduler = wakeMobs.getServer().getScheduler();
        this.searchListNotKiller = new String[] {"{world}", "{money}", "{x}", "{y}", "{z}"};
        this.searchListKiller = new String[] {"{world}", "{money}", "{killer}", "{x}", "{y}", "{z}"};
        this.searchListForKiller = new String[] {"{money}", "{player}"};
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();
        double balance = wakeMobs.getEconomy().getBalance(player);
        double percent = config.getPercent() / 100.0D;
        double result = balance * percent;
        String formatResult = utils.decimalFormat(result);
        Location loc = player.getLocation();
        TypeDrop typeDrop = config.getTypeDrop();
        String[] replacementListForKiller = {formatResult, player.getName()};
        switch (typeDrop) {
            case INSTANT:
                wakeMobs.getEconomy().withdrawPlayer(player, result);
                if (killer != null) {
                    String[] replacementListKiller = {loc.getWorld().getName(), formatResult, killer.getName(), String.valueOf(loc.getBlockX()), String.valueOf(loc.getBlockY()), String.valueOf(loc.getBlockZ())};
                    bukkitScheduler.runTaskAsynchronously(wakeMobs, () -> {
                        wakeMobs.getEconomy().depositPlayer(killer, result);
                        commandUtils.runCommands(config.getListeners().get(EventType.KILLING_PLAYER), searchListKiller, replacementListKiller, player);
                        commandUtils.runCommands(config.getListeners().get(EventType.MONEY_FROM_PLAYER), searchListForKiller, replacementListForKiller, killer);
                    });
                } else {
                    String[] replacementListNotKiller = {loc.getWorld().getName(), formatResult, String.valueOf(loc.getBlockX()), String.valueOf(loc.getBlockY()), String.valueOf(loc.getBlockZ())};
                    bukkitScheduler.runTaskAsynchronously(wakeMobs, () ->
                        commandUtils.runCommands(config.getListeners().get(EventType.PLAYER_DEATH_ENVIRONMENT), searchListNotKiller, replacementListNotKiller, player));
                }
                break;
            case COLLECTING:
                ItemStack itemStack = new ItemStack(Material.valueOf(config.getItemId()));
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setCustomModelData(530);
                itemMeta.setDisplayName(result + "|" + formatResult + "|" + player.getName());
                if (config.isEnchantEffect())
                    itemMeta.addEnchant(Enchantment.LURE, 1, true);
                itemStack.setItemMeta(itemMeta);
                event.getDrops().add(itemStack);
                wakeMobs.getEconomy().withdrawPlayer(player, result);
                if (killer != null) {
                    String[] replacementListKiller = {loc.getWorld().getName(), formatResult, killer.getName(), String.valueOf(loc.getBlockX()), String.valueOf(loc.getBlockY()), String.valueOf(loc.getBlockZ())};
                    bukkitScheduler.runTaskAsynchronously(wakeMobs, () ->
                        commandUtils.runCommands(config.getListeners().get(EventType.KILLING_PLAYER), searchListKiller, replacementListKiller, player));
                } else {
                    String[] replacementListNotKiller = {loc.getWorld().getName(), formatResult, String.valueOf(loc.getBlockX()), String.valueOf(loc.getBlockY()), String.valueOf(loc.getBlockZ())};
                    bukkitScheduler.runTaskAsynchronously(wakeMobs, () ->
                            commandUtils.runCommands(config.getListeners().get(EventType.PLAYER_DEATH_ENVIRONMENT), searchListNotKiller, replacementListNotKiller, player));
                }
        }
    }

}
