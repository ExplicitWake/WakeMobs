package ru.awake.wakemobs.utils.commands;

import lombok.Getter;


@Getter
public class Command {

    private final CommandType commandType;

    private final String context;

    public Command(CommandType commandType, String context) {
        this.commandType = commandType;
        this.context = context;
    }

}
