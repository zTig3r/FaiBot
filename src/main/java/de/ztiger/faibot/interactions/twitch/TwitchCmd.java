package de.ztiger.faibot.interactions.twitch;

import de.ztiger.faibot.interactions.ICommand;
import de.ztiger.faibot.localization.keys.Twitch;
import de.ztiger.faibot.services.LocalizationService;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

@RequiredArgsConstructor
public class TwitchCmd implements ICommand {

    private static final String START = "start";
    private static final String STOP = "stop";

    private final TwitchStreamHandler twitchStreamHandler;
    private final LocalizationService i18n;

    @Override
    public CommandData getCommandData() {
        return Commands.slash("twitch", i18n.get(Twitch.Command.DESCRIPTION))
                .addSubcommands(
                        new SubcommandData(START, i18n.get(Twitch.Command.START)),
                        new SubcommandData(STOP, i18n.get(Twitch.Command.STOP))
                ).setDefaultPermissions(DefaultMemberPermissions.DISABLED);
    }

    @Override
    public void executeSlash(SlashCommandInteractionEvent event) {
        if(event.getSubcommandName().equals(START)) {
            twitchStreamHandler.streamStart();
            event.reply(i18n.get(Twitch.Success.START)).setEphemeral(true).queue();
            return;
        }

        twitchStreamHandler.streamEnd();
        event.reply(i18n.get(Twitch.Success.STOP)).setEphemeral(true).queue();
    }
}
