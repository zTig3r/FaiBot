package de.ztiger.faibot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public interface ICommand {
    CommandData getCommandData();

    void executeSlash(SlashCommandInteractionEvent event);

    default void executeButton(ButtonInteractionEvent event) { }
    default void executeSelect(StringSelectInteractionEvent event) { }
}
