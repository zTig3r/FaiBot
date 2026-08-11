package de.ztiger.faibot.interactions.halloffame;

import de.ztiger.faibot.config.BotChannel;
import de.ztiger.faibot.interactions.ICommand;
import de.ztiger.faibot.services.ExternalReferenceService;
import de.ztiger.faibot.services.LocalizationService;
import de.ztiger.faibot.services.PlacementService;
import de.ztiger.faibot.utils.ChannelProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@RequiredArgsConstructor
public class HallOfFameCmd implements ICommand {

    private final ChannelProvider channelProvider;
    private final HallOfFameComponents hallOfFameComponents;
    private final PlacementService placementService;
    private final ExternalReferenceService externalReferenceService;
    private final LocalizationService i18n;

    private static final String POST = "post";
    private static final String REFRESH = "refresh";
    private static final String SETMESSAGEID = "setmessageid";

    @Override
    public CommandData getCommandData() {
        return Commands.slash("halloffame", "Zeigt die Hall of Fame an")
                .addSubcommands(
                        new SubcommandData(POST, "Postet die Hall of Fame"),
                        new SubcommandData(REFRESH, "Aktualisiert die Hall of Fame"),
                        new SubcommandData(SETMESSAGEID, "Setzt die Nachrichten-ID der Hall of Fame")
                                .addOption(OptionType.STRING, "messageid", "Die ID der Nachricht", true)
                )
                .setDefaultPermissions(DefaultMemberPermissions.DISABLED);
    }

    @Override
    public void executeSlash(SlashCommandInteractionEvent event) {
        event.deferReply().setEphemeral(true).queue();

        switch (event.getSubcommandName()) {
            case POST -> postHallOfFame(event);
            case REFRESH -> updateHallOfFame(event);
            case SETMESSAGEID -> setHallOfFameMessageId(event);
            default -> event.getHook().sendMessage("Unbekannte Aktion").queue();
        }
    }

    private void postHallOfFame(SlashCommandInteractionEvent event) {
        try {
            List<String> formattedTopList = getFormattedTopList();
            long messageId = channelProvider.sendComponentAndGetId(BotChannel.NIXOS, hallOfFameComponents.getHallOfFame(formattedTopList));

            externalReferenceService.setHallOfFameMessage(String.valueOf(messageId));
            event.getHook().sendMessage("Hall of Fame erfolgreich gepostet").queue();
        } catch (Exception e) {
            log.error("Fehler beim Abrufen der Hall of Fame-Daten", e);
            event.getHook().sendMessage("Fehler beim Abrufen der Hall of Fame-Daten").queue();
        }
    }

    private void updateHallOfFame(SlashCommandInteractionEvent event) {
        try {
            long messageId = externalReferenceService.getHallOfFameMessageId();
            if (messageId == -1) {
                event.getHook().sendMessage("Keine Hall of Fame-Nachricht gefunden. Bitte zuerst die Hall of Fame posten.").queue();
                return;
            }

            List<String> formattedTopList = getFormattedTopList();
            channelProvider.editComponents(BotChannel.NIXOS, messageId, hallOfFameComponents.getHallOfFame(formattedTopList));

            event.getHook().sendMessage("Hall of Fame erfolgreich aktualisiert").queue();
        } catch (Exception e) {
            log.error("Fehler beim Aktualisieren der Hall of Fame-Daten", e);
            event.getHook().sendMessage("Fehler beim Aktualisieren der Hall of Fame-Daten").queue();
        }
    }

    private void setHallOfFameMessageId(SlashCommandInteractionEvent event) {
        try {
            String messageId = event.getOption("messageid").getAsString();
            externalReferenceService.setHallOfFameMessage(messageId);
            event.getHook().sendMessage("Nachrichten-ID erfolgreich gesetzt.").queue();
        } catch (Exception e) {
            log.error("Fehler beim Setzen der Hall of Fame-Nachricht", e);
            event.getHook().sendMessage("Fehler beim Setzen der Hall of Fame-Nachricht").queue();
        }
    }

    private List<String> getFormattedTopList() throws SQLException {
        List<PlacementService.HallOfFameEntry> data = placementService.getHallOfFameData();
        return IntStream.range(0, data.size()).mapToObj(i -> String.format("**%02d\\. **%s (%d)", i + 1, data.get(i).username(), data.get(i).totalScore())).toList();
    }
}
