package ru.awake.wakemobs.listeners;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitScheduler;
import ru.awake.wakemobs.WakeMobs;
import ru.awake.wakemobs.config.Config;
import ru.awake.wakemobs.objects.EntityHolder;
import ru.awake.wakemobs.utils.CommandUtils;
import ru.awake.wakemobs.utils.EventType;
import ru.awake.wakemobs.utils.TypeDrop;
import ru.awake.wakemobs.utils.Utils;
import ru.awake.wakemobs.utils.commands.Command;

import java.util.List;

public class EntityListener implements Listener {

    private final WakeMobs wakeMobs;

    private final Utils utils;

    private final CommandUtils commandUtils;

    private final Config config;

    private final BukkitScheduler bukkitScheduler;

    private final List<Command> commands;

    private final String[] searchList;

    public EntityListener(WakeMobs wakeMobs) {
        this.wakeMobs = wakeMobs;
        this.utils = wakeMobs.getUtils();
        this.commandUtils = wakeMobs.getCommandUtils();
        this.config = wakeMobs.getPluginConfig();
        this.bukkitScheduler = wakeMobs.getServer().getScheduler();
        this.commands = config.getListeners().get(EventType.ENTITY_DEATH);
        this.searchList = new String[] {"{mob-name}", "{money}", "{booster}"};
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {

        LivingEntity livingEntity = event.getEntity();
        Player player = livingEntity.getKiller();

        if (utils.notNull(player, livingEntity)) {
            EntityHolder entityHolder = config.getEntities().get(livingEntity.getType().toString());
            if (utils.isMoneyDrop(entityHolder)) {
                double money = utils.getMoney(entityHolder.getMinDrop(), entityHolder.getMaxDrop());
                double booster = utils.getGroupBooster(wakeMobs.getPermission().getPrimaryGroup(player));
                double result = money * booster;
                String formatResult = utils.decimalFormat(result);
                final String[] replacementList = {entityHolder.getEntityName(), formatResult, String.valueOf(result)};
                TypeDrop typeDrop = config.getTypeDrop();
                switch (typeDrop) {
                    case INSTANT:
                        bukkitScheduler.runTaskAsynchronously(wakeMobs, () -> {
                            wakeMobs.getEconomy().depositPlayer(player, result);
                            commandUtils.runCommands(commands, searchList, replacementList, player);
                        });
                        break;
                    case COLLECTING:
                        ItemStack itemStack = new ItemStack(Material.valueOf(config.getItemId()));
                        ItemMeta itemMeta = itemStack.getItemMeta();
                        itemMeta.setCustomModelData(529);
                        itemMeta.setDisplayName(result + "|" + formatResult + "|" + booster + "|" + entityHolder.getEntityName());
                        if (config.isEnchantEffect())
                            itemMeta.addEnchant(Enchantment.LURE, 1, true);
                        itemStack.setItemMeta(itemMeta);
                        event.getDrops().add(itemStack);
                }
            } else {
                bukkitScheduler.runTaskAsynchronously(wakeMobs, () -> player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(config.getNoMoneyMessage())));
            }
        }

    }

}
