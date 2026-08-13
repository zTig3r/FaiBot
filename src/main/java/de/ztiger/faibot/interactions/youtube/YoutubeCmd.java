package de.ztiger.faibot.interactions.youtube;

import de.ztiger.faibot.interactions.ICommand;
import de.ztiger.faibot.localization.keys.Youtube;
import de.ztiger.faibot.services.LocalizationService;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

@RequiredArgsConstructor
public class YoutubeCmd implements ICommand {

    private final YoutubeHandler youtubeHandler;
    private final LocalizationService i18n;

    @Override
    public CommandData getCommandData() {
        return Commands.slash("checkyoutube", i18n.get(Youtube.Command.DESCRIPTION))
                .setDefaultPermissions(DefaultMemberPermissions.DISABLED);
    }

    @Override
    public void executeSlash(SlashCommandInteractionEvent event) {
        youtubeHandler.checkVideo();
        event.reply("Videostatus wurde erfolgreich überprüft!").setEphemeral(true).queue();
    }
}
