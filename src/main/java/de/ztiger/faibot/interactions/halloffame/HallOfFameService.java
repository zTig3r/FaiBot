package de.ztiger.faibot.interactions.halloffame;

import de.ztiger.faibot.config.BotChannel;
import de.ztiger.faibot.services.ExternalReferenceService;
import de.ztiger.faibot.services.PlacementService;
import de.ztiger.faibot.utils.ChannelProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@RequiredArgsConstructor
public class HallOfFameService {

    private final ExternalReferenceService externalReferenceService;
    private final PlacementService placementService;
    private final ChannelProvider channelProvider;
    private final HallOfFameComponents hallOfFameComponents;

    public boolean updateHallOfFame() throws SQLException {
        long messageId = externalReferenceService.getHallOfFameMessageId();
        if (messageId == -1) {
            log.warn("Cannot update Hall of Fame: message ID not found.");
            return false;
        }

        List<String> formattedTopList = getFormattedTopList();
        channelProvider.editComponents(BotChannel.NIXOS, messageId, hallOfFameComponents.getHallOfFame(formattedTopList));
        return true;
    }

    public List<String> getFormattedTopList() throws SQLException {
        List<PlacementService.HallOfFameEntry> data = placementService.getHallOfFameData();
        return IntStream.range(0, data.size()).mapToObj(i -> String.format("**%02d\\. **%s (%d)", i + 1, data.get(i).username(), data.get(i).totalScore())).toList();
    }
}
