package de.ztiger.faibot.interactions;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public interface ICommand {
    CommandData getCommandData();

    void executeSlash(SlashCommandInteractionEvent event);
}
