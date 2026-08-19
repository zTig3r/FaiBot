package de.ztiger.faibot.interactions.halloffame;

import de.ztiger.faibot.config.BotChannel;
import de.ztiger.faibot.services.ExternalReferenceService;
import de.ztiger.faibot.services.PlacementService;
import de.ztiger.faibot.utils.ChannelProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
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
            log.error("Cannot update Hall of Fame: message ID not found.");
            return false;
        }

        int year = getEffectiveYear();
        List<String> formattedTopList = getFormattedTopList();
        List<String> formattedTopListForYear = getFormattedTopListForYear(year);
        channelProvider.editComponents(BotChannel.NIXOS, messageId, hallOfFameComponents.getHallOfFame(formattedTopList, formattedTopListForYear, year));
        return true;
    }

    public List<String> getFormattedTopList() {
        List<PlacementService.HallOfFameEntry> data = placementService.getHallOfFameData();
        return IntStream.range(0, data.size())
                .mapToObj(i -> String.format("**%02d\\. **%s (%d)", i + 1, data.get(i).username(), data.get(i).totalScore())).toList();
    }

    public List<String> getFormattedTopListForYear(int year) {
        List<PlacementService.HallOfFameEntry> data = placementService.getHallOfFameDataForYear(year);
        return IntStream.range(0, data.size())
                .mapToObj(i -> String.format("**%02d\\. **%s (%d)", i + 1, data.get(i).username(), data.get(i).totalScore())).toList();
    }

    public int getEffectiveYear() {
        LocalDate now = LocalDate.now();

        if (now.getMonth() == Month.JANUARY && now.getDayOfMonth() < 25) {
            return now.getYear() - 1;
        }
        return now.getYear();
    }
}
