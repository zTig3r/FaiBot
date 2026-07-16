package de.ztiger.faibot.interactions.twitch;

import de.ztiger.faibot.interactions.ICommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class TwitchCmd implements ICommand {
    @Override
    public CommandData getCommandData() {
        return Commands.slash("twitch", "Starte / Stoppe eine Live-Benachrichtigung").setDefaultPermissions(DefaultMemberPermissions.DISABLED);
    }

    @Override
    public void executeSlash(SlashCommandInteractionEvent event) {
        // TODO: Call twitchHandler
    }
}
