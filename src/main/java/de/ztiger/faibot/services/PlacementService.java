package de.ztiger.faibot.services;

import com.github.twitch4j.helix.domain.User;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import de.ztiger.faibot.data.Placement;
import de.ztiger.faibot.data.Season;
import de.ztiger.faibot.data.TwitchUser;
import lombok.RequiredArgsConstructor;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PlacementService {

    private final TwitchUserService twitchUserService;
    private final SeasonService seasonService;
    private final Dao<Placement, Integer> placementDao;
    private final TwitchApiService twitchApiService;

    public record ParsedPlacement(int rank, String username) {
    }

    public void processSeasonResults(YearMonth yearMonth, List<ParsedPlacement> placements) throws Exception {
        if (placements == null || placements.isEmpty()) return;

        List<String> usernames = placements.stream().map(ParsedPlacement::username).toList();

        Map<String, User> twitchUserMap = twitchApiService.getUsersByUsernames(usernames).stream()
                .collect(Collectors.toMap(User::getLogin, Function.identity(), (existing, replacement) -> existing));

        Season season = seasonService.getOrCreateSeason(yearMonth);

        DeleteBuilder<Placement, Integer> deleteBuilder = placementDao.deleteBuilder();
        deleteBuilder.where().eq("season_id", season);
        deleteBuilder.delete();

        for (ParsedPlacement item : placements) {
            User twitchApiUser = twitchUserMap.get(item.username().toLowerCase());
            if (twitchApiUser == null) {
                continue;
            }

            TwitchUser dbUser = twitchUserService.recordUser(twitchApiUser.getId(), twitchApiUser.getDisplayName());

            Placement placement = new Placement(0, season, dbUser, item.rank());
            placementDao.create(placement);
        }
    }
}