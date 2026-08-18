package de.ztiger.faibot.interactions.halloffame;

import de.ztiger.faibot.config.BotChannel;
import de.ztiger.faibot.interactions.ICommand;
import de.ztiger.faibot.localization.keys.HallOfFame;
import de.ztiger.faibot.services.ExternalReferenceService;
import de.ztiger.faibot.services.LocalizationService;
import de.ztiger.faibot.utils.ChannelProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class HallOfFameCmd implements ICommand {

    private static final String POST = "post";
    private static final String YEAR_FIELD = "year";
    private static final String UPDATE = "update";
    private static final String SETMESSAGEID = "setmessageid";
    private static final String MESSAGEID_FIELD = "messageid";

    private final ChannelProvider channelProvider;
    private final HallOfFameComponents hallOfFameComponents;
    private final ExternalReferenceService externalReferenceService;
    private final HallOfFameService hallOfFameService;
    private final LocalizationService i18n;

    @Override
    public CommandData getCommandData() {
        return Commands.slash("halloffame", i18n.get(HallOfFame.Command.DESCRIPTION))
                .addSubcommands(
                        new SubcommandData(POST, i18n.get(HallOfFame.Command.POST))
                                .addOption(OptionType.STRING, YEAR_FIELD, i18n.get(HallOfFame.Command.YEAR), false),
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
            case null, default -> event.getHook().sendMessage(i18n.get(HallOfFame.Error.UNKNOWN)).setEphemeral(true).queue();
        }
    }

    private void postHallOfFame(SlashCommandInteractionEvent event) {
        try {
            String yearOption = getOptionalStringOption(event, YEAR_FIELD);

            if (yearOption != null) {
                int year;

                try {
                    year = Integer.parseInt(yearOption);
                } catch (NumberFormatException e) {
                    event.getHook().sendMessage(i18n.get(HallOfFame.Error.INVALIDYEAR)).queue();
                    return;
                }

                List<String> formattedYearlyTopList = hallOfFameService.getFormattedTopListForYear(year);
                if (formattedYearlyTopList.isEmpty()) {
                    event.getHook().sendMessage(i18n.get(HallOfFame.Error.NOTFOUND)).queue();
                    return;
                }

                channelProvider.sendComponent(BotChannel.NIXOS, hallOfFameComponents.getYearlyHallOfFame(formattedYearlyTopList, year));
                event.getHook().sendMessage(i18n.get(HallOfFame.Success.POST)).queue();
                return;
            }

            int displayYear = hallOfFameService.getEffectiveYear();
            List<String> formattedTopList = hallOfFameService.getFormattedTopList();
            List<String> formattedCurrentYearTopList = hallOfFameService.getFormattedTopListForYear(displayYear);

            long messageId = channelProvider.sendComponentAndGetId(BotChannel.NIXOS, hallOfFameComponents.getHallOfFame(formattedTopList,
                                                                                                                        formattedCurrentYearTopList,
                                                                                                                        displayYear));

            externalReferenceService.setHallOfFameMessage(String.valueOf(messageId));
            event.getHook().sendMessage(i18n.get(HallOfFame.Success.POST)).queue();
        } catch (Exception e) {
            log.error("Error while fetching Hall of Fame data", e);
            event.getHook().sendMessage(i18n.get(HallOfFame.Error.FETCH)).queue();
        }
    }

    private void updateHallOfFame(SlashCommandInteractionEvent event) {
        try {
            boolean updated = hallOfFameService.updateHallOfFame();
            if (!updated) {
                event.getHook().sendMessage(i18n.get(HallOfFame.Error.NOTFOUND)).queue();
                return;
            }
            event.getHook().sendMessage(i18n.get(HallOfFame.Success.UPDATE)).queue();
        } catch (Exception e) {
            log.error("Error while updating Hall of Fame data", e);
            event.getHook().sendMessage(i18n.get(HallOfFame.Error.UPDATE)).queue();
        }
    }

    private void setHallOfFameMessageId(SlashCommandInteractionEvent event) {
        try {
            String messageId = getRequiredStringOption(event, MESSAGEID_FIELD);

            if (!messageId.matches("\\d+") || messageId.isBlank() || messageId.length() < 10) {
                event.getHook().sendMessage(i18n.get(HallOfFame.Error.INVALIDMESSAGEID)).queue();
                return;
            }

            externalReferenceService.setHallOfFameMessage(messageId);
            event.getHook().sendMessage(i18n.get(HallOfFame.Success.SETMESSAGEID)).queue();
        } catch (Exception e) {
            log.error("Error while setting Hall of Fame message", e);
            event.getHook().sendMessage(i18n.get(HallOfFame.Error.SETMESSAGEID)).queue();
        }
    }
}
