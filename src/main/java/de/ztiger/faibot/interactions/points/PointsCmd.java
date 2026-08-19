package de.ztiger.faibot.interactions.points;

import de.ztiger.faibot.interactions.ICommand;
import de.ztiger.faibot.interactions.components.IAutoCompleteHandler;
import de.ztiger.faibot.localization.keys.Points;
import de.ztiger.faibot.services.LocalizationService;
import de.ztiger.faibot.services.PlacementService;
import de.ztiger.faibot.services.TwitchUserService;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PointsCmd implements ICommand, IAutoCompleteHandler {

    private static final String USER_FIELD = "user";

    private final PlacementService placementService;
    private final TwitchUserService twitchUserService;
    private final PointsComponents pointsComponents;
    private final LocalizationService i18n;

    @Override
    public CommandData getCommandData() {
        return Commands.slash("punkte", i18n.get(Points.Command.DESCRIPTION)).
                addOption(OptionType.STRING, USER_FIELD, i18n.get(Points.Command.USER), true, true);
    }

    @Override
    public void executeSlash(SlashCommandInteractionEvent event) {
        event.deferReply().setEphemeral(true).queue();

        String username = getRequiredStringOption(event, USER_FIELD);
        String userId = twitchUserService.getUserIdByName(username);

        if (userId == null) {
            event.getHook().sendMessage(i18n.format(Points.Error.NOTFOUND, "username", username)).queue();
            return;
        }

        try {
            PlacementService.UserScoreBreakdown pointsInfo = placementService.getUserScoreBreakdown(userId);

            String formattedPositions = pointsInfo.positions().stream()
                    .collect(Collectors.groupingBy(Function.identity(), TreeMap::new, Collectors.counting()))
                    .entrySet().stream()
                    .map(e -> e.getValue() + "x " + e.getKey() + ". Platz")
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("Keine Positionen");

            event.getHook().sendMessageComponents(
                    pointsComponents.getPointsContainer(username, pointsInfo.totalScore(), pointsInfo.hallOfFamePosition(),
                                                        pointsInfo.appearances(),
                                                        formattedPositions)).useComponentsV2().queue();
        } catch (Exception e) {
            event.getHook().sendMessage(i18n.get(Points.Error.FETCH)).queue();
        }
    }

    @Override
    public void handleAutoComplete(CommandAutoCompleteInteractionEvent event) {
        if (event.getFocusedOption().getName().equals(USER_FIELD)) {
            String input = event.getFocusedOption().getValue();
            event.replyChoices(twitchUserService.searchUsers(input)).queue();
        }
    }
}
