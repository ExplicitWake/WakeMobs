package ru.awake.wakemobs.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;
import ru.awake.wakemobs.WakeMobs;
import ru.awake.wakemobs.config.Config;
import ru.awake.wakemobs.utils.CommandUtils;
import ru.awake.wakemobs.utils.EventType;
import ru.awake.wakemobs.utils.Utils;
import ru.awake.wakemobs.utils.commands.Command;

import java.util.List;

public class ItemListener implements Listener {

    private final WakeMobs wakeMobs;

    private final Utils utils;

    private final CommandUtils commandUtils;

    private final BukkitScheduler bukkitScheduler;

    private final String[] searchListForEntityItem;

    private final Config config;

    public ItemListener(WakeMobs wakeMobs) {
        this.wakeMobs = wakeMobs;
        this.utils = wakeMobs.getUtils();
        this.commandUtils = wakeMobs.getCommandUtils();
        this.bukkitScheduler = wakeMobs.getServer().getScheduler();
        this.config = wakeMobs.getPluginConfig();
        this.searchListForEntityItem = new String[] {"{mob-name}", "{money}", "{booster}"};
    }

    @EventHandler
    public void onHopperPickupItem(InventoryPickupItemEvent event) {
        ItemStack itemStack = event.getItem().getItemStack();;
        if (utils.isMoneyItem(itemStack))
            event.setCancelled(true);
    }

    @EventHandler
    public void onMerge(ItemMergeEvent event) {
        ItemStack firstItem = event.getEntity().getItemStack();
        ItemStack secondItem = event.getTarget().getItemStack();
        if (firstItem.isSimilar(secondItem) &&
                utils.isMoneyItem(firstItem))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerPickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;
        Player player = (Player) event.getEntity();
        ItemStack itemStack = event.getItem().getItemStack();
        if (utils.isMoneyItem(itemStack)) {
            int customModelData = itemStack.getItemMeta().getCustomModelData();
            switch (customModelData) {
                case 529:
                    String displayName = itemStack.getItemMeta().getDisplayName();
                    String[] results = displayName.split("\\|");
                    double result = Double.parseDouble(results[0]);
                    final String[] replacementListForEntity = {results[3], results[1], results[2]};
                    bukkitScheduler.runTaskAsynchronously(wakeMobs, () -> {
                       wakeMobs.getEconomy().depositPlayer(player, result);
                       commandUtils.runCommands(config.getListeners().get(EventType.PICKUP_ITEM_FROM_ENTITY), searchListForEntityItem, replacementListForEntity, player);
                    });
                    event.getItem().remove();
                    event.setCancelled(true);
                    break;
            }
        }
    }

}
