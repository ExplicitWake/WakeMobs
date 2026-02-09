package ru.awake.wakemobs.objects;

import lombok.Getter;
import ru.awake.wakemobs.utils.color.LegacyColorize;

@Getter
public class EntityHolder {

    private final String entityName;

    private final int chance;

    private final int minDrop;

    private final int maxDrop;

    public EntityHolder(String entityName, int chance, int minDrop, int maxDrop) {
        this.entityName = LegacyColorize.colorize(entityName);
        this.chance = chance;
        this.minDrop = minDrop;
        this.maxDrop = maxDrop;
    }

}
