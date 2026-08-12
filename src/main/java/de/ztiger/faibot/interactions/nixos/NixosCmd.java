package de.ztiger.faibot.interactions.nixos;

import de.ztiger.faibot.config.BotChannel;
import de.ztiger.faibot.interactions.ICommand;
import de.ztiger.faibot.interactions.components.IButtonHandler;
import de.ztiger.faibot.interactions.components.IModalHandler;
import de.ztiger.faibot.localization.keys.General;
import de.ztiger.faibot.localization.keys.Nixos;
import de.ztiger.faibot.services.LocalizationService;
import de.ztiger.faibot.services.PlacementService;
import de.ztiger.faibot.services.SeasonService;
import de.ztiger.faibot.utils.ChannelProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
public class NixosCmd implements ICommand, IButtonHandler, IModalHandler {

    private static final String WINNER_OPTION = "winners";

    private final PlacementService placementService;
    private final SeasonService seasonService;
    private final ChannelProvider channelProvider;
    private final NixosComponents nixosComponents;
    private final LocalizationService i18n;

    private final Map<String, List<String>> winnerCache = new ConcurrentHashMap<>();
    private final Map<String, PendingSeasonData> pendingOverrideCache = new ConcurrentHashMap<>();

    private record PendingSeasonData(YearMonth season, String localizedMonth, String yearStr, List<String> winners,
                                     String rawTop10, List<Message.Attachment> attachments) {
    }

    @Override
    public String getComponentId() {
        return NixosComponents.COMPONENT_ID;
    }

    @Override
    public CommandData getCommandData() {
        return Commands.slash("nixos", i18n.get(Nixos.Command.DESCRIPTION))
                .addOption(OptionType.STRING, WINNER_OPTION, i18n.get(Nixos.Command.WINNERS), true)
                .setDefaultPermissions(DefaultMemberPermissions.DISABLED);
    }

    @Override
    public void executeSlash(SlashCommandInteractionEvent event) {
        OptionMapping option = event.getOption(WINNER_OPTION);
        if (option == null) return;

        List<String> winnerNames = new ArrayList<>();
        Matcher matcher = Pattern.compile("<@!?\\d+>|\\S+").matcher(option.getAsString());

        while (matcher.find()) {
            winnerNames.add(matcher.group());
        }

        winnerCache.put(event.getUser().getId(), winnerNames);
        event.replyModal(nixosComponents.getNixoModal(winnerNames)).queue();
    }

    @Override
    public void modalInteraction(ModalInteractionEvent event) {
        event.deferReply(true).setEphemeral(true).queue();

        String monthValue = event.getValue(NixosComponents.FIELD_MONTH).getAsStringList().getFirst();
        String yearStr = event.getValue(NixosComponents.FIELD_YEAR).getAsString();

        Month month = Month.valueOf(monthValue);
        YearMonth season = YearMonth.of(Integer.parseInt(yearStr), month);
        String localizedMonth = month.getDisplayName(TextStyle.FULL_STANDALONE, i18n.getLocale());

        List<String> winners = winnerCache.remove(event.getUser().getId());
        if (winners == null) winners = List.of();

        String rawTop10 = event.getValue(NixosComponents.TOP_LIST).getAsString();
        List<Message.Attachment> attachments = event.getValue(NixosComponents.WINNER_IMAGES).getAsAttachmentList();

        try {
            if (seasonService.seasonExists(season)) {
                pendingOverrideCache.put(event.getUser().getId(), new PendingSeasonData(season, localizedMonth, yearStr, winners, rawTop10, attachments));

                event.getHook().sendMessageComponents(nixosComponents.getConfirmOverride(localizedMonth, yearStr)).useComponentsV2().setEphemeral(true).queue();
                return;
            }
        } catch (Exception e) {
            log.error("Failed to check if a season already exists: {}", season, e);
            event.getHook().sendMessage(i18n.get(General.ERROR)).setEphemeral(true).queue();
            return;
        }

        processSeasonData(event.getHook(), season, localizedMonth, yearStr, rawTop10, winners, attachments);
    }

    @Override
    public void handleButton(ButtonInteractionEvent event) {
        event.deferReply(true).setEphemeral(true).queue();

        String userId = event.getUser().getId();

        if (event.getButton().getCustomId().contains(NixosComponents.CANCEL_OVERRIDE)) {
            pendingOverrideCache.remove(userId);
            event.getHook().sendMessage(i18n.get(Nixos.Override.CANCELLED)).setEphemeral(true).queue();
            return;
        }

        PendingSeasonData pendingData = pendingOverrideCache.remove(userId);
        if (pendingData == null) {
            event.getHook().sendMessage(i18n.get(General.ERROR)).setEphemeral(true).queue();
            return;
        }

        processSeasonData(event.getHook(), pendingData.season(), pendingData.localizedMonth(), pendingData.yearStr(),
                pendingData.rawTop10(), pendingData.winners(), pendingData.attachments());
    }

    private void processSeasonData(InteractionHook hook, YearMonth season, String localizedMonth, String yearStr,
                                   String rawTop10, List<String> winners, List<Message.Attachment> attachments) {
        List<String> formattedTopList = new ArrayList<>();
        List<PlacementService.ParsedPlacement> placementsToSave = new ArrayList<>();

        Matcher matcher = Pattern.compile("#(\\d+)\\s+(.+?)\\s*\\(([\\d.]+)\\)").matcher(rawTop10);

        while (matcher.find()) {
            int rank = Integer.parseInt(matcher.group(1));
            String username = matcher.group(2).trim();
            long points = Long.parseLong(matcher.group(3).replace(".", ""));

            formattedTopList.add(String.format("**%02d\\. **%s (%d)", rank, username, points));
            placementsToSave.add(new PlacementService.ParsedPlacement(rank, username));
        }

        try {
            placementService.processSeasonResults(season, placementsToSave);
        } catch (Exception e) {
            log.error("Failed to save Nixo season results to database for month: {}", season, e);
            hook.sendMessage(i18n.get(General.ERROR)).setEphemeral(true).queue();
            return;
        }

        channelProvider.sendComponentAndCreateThread(
                BotChannel.NIXOS,
                nixosComponents.getWinnerComponent(localizedMonth, yearStr, formattedTopList, winners, attachments),
                i18n.format(Nixos.Message.THREAD, "month", localizedMonth, "year", yearStr)
        );

        hook.sendMessage(i18n.get(Nixos.Modal.SUCCESS)).setEphemeral(true).queue();
    }
}