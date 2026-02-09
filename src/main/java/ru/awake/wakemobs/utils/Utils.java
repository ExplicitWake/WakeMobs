package ru.awake.wakemobs.utils;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.awake.wakemobs.WakeMobs;
import ru.awake.wakemobs.config.Config;
import ru.awake.wakemobs.objects.EntityHolder;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Random;

public class Utils {

    private final Config config;

    public Utils(WakeMobs wakeMobs) {
        this.config = wakeMobs.getPluginConfig();
    }

    private final Random RANDOM = new Random();

    public String decimalFormat(double formatted) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.ROOT);
        DecimalFormat format = new DecimalFormat("#.#", symbols);
        return format.format(formatted);
    }

    public double getMoney(int minDrop, int maxDrop) {
        return RANDOM.nextDouble(minDrop, maxDrop);
    }

    public boolean isMoneyDrop(EntityHolder entityHolder) {
        int randomValue = RANDOM.nextInt(100);
        int chanceDrop = entityHolder.getChance();
        return (randomValue <= chanceDrop);
    }

    public boolean isMoneyItem(ItemStack itemStack) {
        return (itemStack.hasItemMeta() && itemStack
                .getItemMeta().hasCustomModelData() && itemStack
                .getItemMeta().getCustomModelData() == 529 || itemStack
                .getItemMeta().hasCustomModelData() && itemStack
                .getItemMeta().getCustomModelData() == 530);
    }

    public boolean notNull(Player player, LivingEntity entity) {
        return (!(entity instanceof Player) && player != null);
    }

    public double getGroupBooster(String group) {
        if (config.getBoosters().containsKey(group))
            return config.getBoosters().get(group);
        return 1.0D;
    }

}
