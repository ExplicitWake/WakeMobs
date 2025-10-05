package ru.awake.wakemobs.listeners;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.awake.wakemobs.WakeMobs;
import ru.awake.wakemobs.utils.Config;
import ru.awake.wakemobs.utils.Utils;

import java.util.Random;

public class MobListener implements Listener {

    private final WakeMobs plugin;
    private final Config config;
    private final Utils utils;
    private final Random RANDOM = new Random();

    public MobListener(WakeMobs plugin) {
        this.plugin = plugin;
        this.config = plugin.getPluginConfig();
        this.utils = new Utils(plugin);
    }


    @EventHandler
    public void MobDeathEvent (EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player player = entity.getKiller();

        this.config.newSettings(entity);
        if (this.utils.notNull(player, entity)) {
            if (!(this.utils.isCollecting()) && !(this.utils.isInstant())) {
                player.sendMessage("Вы неправильно настроили тип выпадания монет, проверьте значение!");
                return;
            }
        }
        if (this.utils.isMoneyDrop()) {
            if (this.utils.notNull(player, entity)) {
                int difference = this.config.maxDrop - this.config.minDrop;
                double money = RANDOM.nextDouble(difference) + this.config.minDrop;
                double booster = 1.0D;
                String group = this.plugin.perms.getPrimaryGroup(player);
                if (this.utils.isGroupContains(group)) {
                    booster = this.config.boosters.getDouble(group);
                }
                money *= booster;
                String result = this.utils.decimalFormat(money);
                if (this.utils.isInstant()) {
                    this.plugin.economy.depositPlayer(player, money);
                    this.utils.sendMessages(player, result, booster);
                } else {
                    if (this.utils.isCollecting()) {
                        ItemStack itemStack = new ItemStack(Material.valueOf(this.config.itemId));
                        ItemMeta itemMeta = itemStack.getItemMeta();
                        itemMeta.setCustomModelData(529);
                        itemMeta.setDisplayName(result + "|" + money + "|" + booster);
                        if (this.config.isEnchantEffect) {
                            itemMeta.addEnchant(Enchantment.LURE, 1, true);
                        }
                        itemStack.setItemMeta(itemMeta);
                        event.getDrops().add(itemStack);
                    }
                }
            }

        } else {
            if (this.utils.notNull(player, entity)) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(this.config.noMoneyMessage));
            }
        }
    }
    @EventHandler
    public void onHopperPickupItem(InventoryPickupItemEvent event) {
        ItemStack itemStack = event.getItem().getItemStack();
        if (this.utils.isMoneyItem(itemStack)) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onMerge(ItemMergeEvent event) {
        ItemStack firstItem = event.getEntity().getItemStack();
        ItemStack secondItem = event.getTarget().getItemStack();
        if (firstItem.isSimilar(secondItem)) {
            if (this.utils.isMoneyItem(firstItem)) {
                event.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void PlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem().getItemStack();
        if (this.utils.isMoneyItem(itemStack)) {
            String[] strings = itemStack.getItemMeta().getDisplayName().split("\\|");
            this.plugin.economy.depositPlayer(player, Double.parseDouble(strings[1]));
            this.utils.sendMessages(player, strings[0], Double.parseDouble(strings[2]));
            event.getItem().remove();
            event.setCancelled(true);
        }
    }
}
