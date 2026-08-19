package de.ztiger.faibot.interactions;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.Optional;

public interface ICommand {
    CommandData getCommandData();

    void executeSlash(SlashCommandInteractionEvent event);

    default String getRequiredStringOption(SlashCommandInteractionEvent event, String name) {
        return Optional.ofNullable(event.getOption(name))
                .map(OptionMapping::getAsString)
                .orElseThrow(() -> new IllegalArgumentException("Missing required option: " + name));
    }

    default String getOptionalStringOption(SlashCommandInteractionEvent event, String name) {
        return Optional.ofNullable(event.getOption(name))
                .map(OptionMapping::getAsString)
                .orElse(null);
    }
}
