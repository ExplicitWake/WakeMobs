package ru.awake.wakemobs.config;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import ru.awake.wakemobs.WakeMobs;
import ru.awake.wakemobs.objects.EntityHolder;
import ru.awake.wakemobs.utils.EventType;
import ru.awake.wakemobs.utils.TypeDrop;
import ru.awake.wakemobs.utils.color.LegacyColorize;
import ru.awake.wakemobs.utils.commands.Command;
import ru.awake.wakemobs.utils.commands.CommandType;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class Config {

    private final WakeMobs wakeMobs;

    public Config(WakeMobs wakeMobs) {
        this.wakeMobs = wakeMobs;
    }

    private TypeDrop typeDrop;

    private final Map<String, Double> boosters = new HashMap<>();;

    private int percent;

    private String itemId;

    private boolean isEnchantEffect;

    private String helpMessage;

    private String reloadMessage;

    private String noMoneyMessage;

    private final Map<EventType, List<Command>> listeners = new HashMap<>();

    private final Map<String, EntityHolder> entities = new HashMap<>();

    public void setupSettings(FileConfiguration configuration) {
        ConfigurationSection settings = configuration.getConfigurationSection("settings");
        typeDrop = settings.getString("type-drop").equals("COLLECTING") ? TypeDrop.COLLECTING : TypeDrop.INSTANT;
        ConfigurationSection boosters = settings.getConfigurationSection("boosters");
        for (String string : boosters.getKeys(false)) {
            double booster = boosters.getDouble(string);
            this.boosters.put(string, booster);
        }
        percent = settings.getInt("percent");
    }

    public void setupItemSettings(FileConfiguration configuration) {
        ConfigurationSection itemSettings = configuration.getConfigurationSection("item-settings");
        itemId = itemSettings.getString("item-id");
        isEnchantEffect = itemSettings.getBoolean("enchant-effect");
    }

    public void setupMessages(FileConfiguration configuration) {
        ConfigurationSection messages = configuration.getConfigurationSection("messages");
        helpMessage = LegacyColorize.colorize(messages.getString("help"));
        reloadMessage = LegacyColorize.colorize(messages.getString("reload"));
        noMoneyMessage = LegacyColorize.colorize(messages.getString("no-money"));
    }

    public void setupListeners(FileConfiguration configuration) {
        if (!(listeners.isEmpty()))
            listeners.clear();
        ConfigurationSection listeners = configuration.getConfigurationSection("listeners");
        for (String string : listeners.getKeys(false)) {
            EventType eventType = EventType.getEventType(string);
            List<Command> commands = getCommandList(listeners.getStringList(string + ".commands"));
            this.listeners.put(eventType, commands);
        }
    }

    public void setupEntities(FileConfiguration configuration) {
        if (!(entities.isEmpty()))
            entities.clear();
        ConfigurationSection mobsSettings = configuration.getConfigurationSection("mobs-settings");
        for (String string : mobsSettings.getKeys(false)) {
            ConfigurationSection entity = mobsSettings.getConfigurationSection(string);
            EntityHolder holder = getEntityHolder(entity);
            entities.put(string, holder);
        }
    }

    private EntityHolder getEntityHolder(ConfigurationSection section) {
        String name = section.getString("name");
        int chance = section.getInt("chance-drop");
        int minDrop = section.getInt("min-drop");
        int maxDrop = section.getInt("max-drop");

        return new EntityHolder(name, chance, minDrop, maxDrop);
    }

    public EntityHolder getEntityHolder(LivingEntity entity) {
        String entityName = entity.getType().toString();
        if (entities.containsKey(entityName))
            return entities.get(entityName);
        return entities.get("DEFAULT");
    }


    private List<Command> getCommandList(List<String> commands) {
        if (commands.isEmpty())
            return List.of();
        ImmutableList.Builder<Command> commandBuilder = ImmutableList.builder();
        for (String commandString : commands) {
            Command command = getFromString(commandString);
            commandBuilder.add(command);
   }
        return commandBuilder.build();
    }

    private final Pattern COMMAND_PATTERN = Pattern.compile("\\[(\\w+)] ?(.*)");

    private Command getFromString(String str) {
        Matcher matcher = COMMAND_PATTERN.matcher(str);
        if (!matcher.matches()) {
            return new Command(CommandType.MESSAGE, str);
        }
        CommandType type;
        try {
            type = CommandType.valueOf(matcher.group(1).toUpperCase());
        } catch (IllegalArgumentException e) {
            type = CommandType.MESSAGE;
            return new Command(type, str);
        }
        return new Command(type, matcher.group(2).trim());
    }

    public FileConfiguration getFile(String  path, String fileName) {
        File file = new File(path, fileName);
        if (!file.exists()) {
            wakeMobs.saveResource(fileName, false);
        }
        return YamlConfiguration.loadConfiguration(file);
    }

}
