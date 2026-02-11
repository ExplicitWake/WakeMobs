package ru.awake.wakemobs.runnables;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import ru.awake.wakemobs.WakeMobs;
import ru.awake.wakemobs.config.Config;
import ru.awake.wakemobs.utils.CommandUtils;
import ru.awake.wakemobs.utils.EventType;
import ru.awake.wakemobs.utils.Utils;

public class NearEntitiesRunnable extends BukkitRunnable {

    private final WakeMobs wakeMobs;

    private final Config config;

    private final Utils utils;

    private final CommandUtils commandUtils;

    private final String[] searchListForEntityItem;

    private final String[] searchListForPlayerItem;

    private final BukkitScheduler bukkitScheduler;

    public NearEntitiesRunnable(WakeMobs wakeMobs) {
        this.wakeMobs = wakeMobs;
        this.config = wakeMobs.getPluginConfig();
        this.utils = wakeMobs.getUtils();
        this.commandUtils = wakeMobs.getCommandUtils();
        this.searchListForEntityItem = new String[] {"{mob-name}", "{money}", "{booster}"};
        this.searchListForPlayerItem = new String[] {"{money}", "{player}"};
        this.bukkitScheduler = wakeMobs.getServer().getScheduler();
    }

    @Override
    public void run() {
        for (Player player : wakeMobs.getServer().getOnlinePlayers()) {
            if (player.getInventory().firstEmpty() == -1) {
                for (Entity entity : player.getNearbyEntities(1.0, 1.0, 1.0)) {
                    if (entity instanceof Item) {
                        Item item = (Item) entity;
                        ItemStack itemStack = item.getItemStack();
                        if (!(utils.isMoneyItem(itemStack)))
                            continue;
                        int customModelData = itemStack.getItemMeta().getCustomModelData();
                        String displayName = itemStack.getItemMeta().getDisplayName();
                        String[] results = displayName.split("\\|");
                        double result = Double.parseDouble(results[0]);
                        switch (customModelData) {
                            case 529:
                                final String[] replacementListForEntity = {results[3], results[1], results[2]};
                                bukkitScheduler.runTaskAsynchronously(wakeMobs, () -> {
                                    wakeMobs.getEconomy().depositPlayer(player, result);
                                    commandUtils.runCommands(config.getListeners().get(EventType.PICKUP_ITEM_FROM_ENTITY), searchListForEntityItem, replacementListForEntity, player);
                                });
                                break;
                            case 530:
                                final String[] replacementListForPlayer = {results[1], results[2]};
                                bukkitScheduler.runTaskAsynchronously(wakeMobs, () -> {
                                    wakeMobs.getEconomy().depositPlayer(player, result);
                                    commandUtils.runCommands(config.getListeners().get(EventType.PICKUP_ITEM_FROM_PLAYER), searchListForPlayerItem, replacementListForPlayer, player);
                                });
                        }
                        item.remove();
                    }
                }
            }
        }
    }
}
