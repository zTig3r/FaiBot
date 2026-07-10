package de.ztiger.faibot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class Youtube implements ICommand{
    @Override
    public CommandData getCommandData() {
        return Commands.slash("checkyoutube", "Sende eine Benachrichtigung, wenn ein neues Video hochgeladen wird")
                .setDefaultPermissions(DefaultMemberPermissions.DISABLED);

    }

    @Override
    public void executeSlash(SlashCommandInteractionEvent event) {
        // TODO: Call YoutubeHandler
    }
}
