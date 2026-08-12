package de.ztiger.faibot.interactions.halloffame;

import de.ztiger.faibot.config.BotChannel;
import de.ztiger.faibot.interactions.ICommand;
import de.ztiger.faibot.localization.keys.HallOfFame;
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
    private static final String UPDATE = "update";
    private static final String SETMESSAGEID = "setmessageid";
    private static final String MESSAGEID_FIELD = "messageid";

    @Override
    public CommandData getCommandData() {
        return Commands.slash("halloffame", i18n.get(HallOfFame.Command.DESCRIPTION))
                .addSubcommands(
                        new SubcommandData(POST, i18n.get(HallOfFame.Command.POST)),
                        new SubcommandData(UPDATE, i18n.get(HallOfFame.Command.UPDATE)),
                        new SubcommandData(SETMESSAGEID, i18n.get(HallOfFame.Command.SETMESSAGEID))
                                .addOption(OptionType.STRING, MESSAGEID_FIELD, i18n.get(HallOfFame.Command.MESSAGEID), true)
                )
                .setDefaultPermissions(DefaultMemberPermissions.DISABLED);
    }

    @Override
    public void executeSlash(SlashCommandInteractionEvent event) {
        event.deferReply().setEphemeral(true).queue();

        switch (event.getSubcommandName()) {
            case POST -> postHallOfFame(event);
            case UPDATE -> updateHallOfFame(event);
            case SETMESSAGEID -> setHallOfFameMessageId(event);
            default -> event.getHook().sendMessage(i18n.get(HallOfFame.Error.UNKNOWN)).queue();
        }
    }

    private void postHallOfFame(SlashCommandInteractionEvent event) {
        try {
            List<String> formattedTopList = getFormattedTopList();
            long messageId = channelProvider.sendComponentAndGetId(BotChannel.NIXOS, hallOfFameComponents.getHallOfFame(formattedTopList));

            externalReferenceService.setHallOfFameMessage(String.valueOf(messageId));
            event.getHook().sendMessage(i18n.get(HallOfFame.Success.POST)).queue();
        } catch (Exception e) {
            log.error("Error while fetching Hall of Fame data", e);
            event.getHook().sendMessage(i18n.get(HallOfFame.Error.FETCH)).queue();
        }
    }

    private void updateHallOfFame(SlashCommandInteractionEvent event) {
        try {
            long messageId = externalReferenceService.getHallOfFameMessageId();
            if (messageId == -1) {
                event.getHook().sendMessage(i18n.get(HallOfFame.Error.NOTFOUND)).queue();
                return;
            }

            List<String> formattedTopList = getFormattedTopList();
            channelProvider.editComponents(BotChannel.NIXOS, messageId, hallOfFameComponents.getHallOfFame(formattedTopList));

            event.getHook().sendMessage(i18n.get(HallOfFame.Success.UPDATE)).queue();
        } catch (Exception e) {
            log.error("Error while updating Hall of Fame data", e);
            event.getHook().sendMessage(i18n.get(HallOfFame.Error.UPDATE)).queue();
        }
    }

    private void setHallOfFameMessageId(SlashCommandInteractionEvent event) {
        try {
            String messageId = event.getOption(MESSAGEID_FIELD).getAsString();
            externalReferenceService.setHallOfFameMessage(messageId);
            event.getHook().sendMessage(i18n.get(HallOfFame.Success.SETMESSAGEID)).queue();
        } catch (Exception e) {
            log.error("Error while setting Hall of Fame message", e);
            event.getHook().sendMessage(i18n.get(HallOfFame.Error.SETMESSAGEID)).queue();
        }
    }

    private List<String> getFormattedTopList() throws SQLException {
        List<PlacementService.HallOfFameEntry> data = placementService.getHallOfFameData();
        return IntStream.range(0, data.size()).mapToObj(i -> String.format("**%02d\\. **%s (%d)", i + 1, data.get(i).username(), data.get(i).totalScore())).toList();
    }
}
