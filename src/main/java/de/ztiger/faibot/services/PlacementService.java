package de.ztiger.faibot.services;

import com.github.twitch4j.helix.domain.User;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;
import com.j256.ormlite.stmt.DeleteBuilder;
import de.ztiger.faibot.data.Placement;
import de.ztiger.faibot.data.Season;
import de.ztiger.faibot.data.TwitchUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class PlacementService {

    private final TwitchUserService twitchUserService;
    private final SeasonService seasonService;
    private final Dao<Placement, Integer> placementDao;
    private final TwitchApiService twitchApiService;

    public void processSeasonResults(YearMonth yearMonth, List<ParsedPlacement> placements) throws Exception {
        if (placements == null || placements.isEmpty()) return;

        List<String> usernames = placements.stream().map(ParsedPlacement::username).toList();

        Map<String, User> twitchUserMap = twitchApiService.getUsersByUsernames(usernames).stream()
                .collect(Collectors.toMap(u -> u.getLogin().toLowerCase(), Function.identity(), (existing, replacement) -> existing));

        Season season = seasonService.getOrCreateSeason(yearMonth);

        DeleteBuilder<Placement, Integer> deleteBuilder = placementDao.deleteBuilder();
        deleteBuilder.where().eq("season_id", season);
        deleteBuilder.delete();

        List<String> unresolvedUsernames = new ArrayList<>();

        for (ParsedPlacement item : placements) {
            User twitchApiUser = twitchUserMap.get(item.username().toLowerCase());

            if (twitchApiUser == null) {
                unresolvedUsernames.add(item.username());
                continue;
            }

            TwitchUser dbUser = twitchUserService.recordUser(twitchApiUser.getId(), twitchApiUser.getDisplayName());

            Placement placement = new Placement(0, season, dbUser, item.rank());
            placementDao.create(placement);
        }

        if (!unresolvedUsernames.isEmpty()) {
            log.error("Completed season {} processing with {} missing Twitch API user(s): {}",
                      yearMonth, unresolvedUsernames.size(), String.join(", ", unresolvedUsernames));
        }
    }

    public List<HallOfFameEntry> getHallOfFameData() {
        String sql = """
                SELECT
                     u.id AS user_id,
                     u.username AS username,
                     COUNT(p.id) * 3 + SUM(11 - p.position) AS total_score,
                     COUNT(p.id) AS appearances
                 FROM placement p
                 INNER JOIN twitchuser u ON p.twitchUser_id = u.id
                 GROUP BY u.id, u.username
                 ORDER BY total_score DESC
                 LIMIT 10
                """;

        RawRowMapper<HallOfFameEntry> mapper = (columnNames, resultColumns) -> new HallOfFameEntry(
                resultColumns[0],
                resultColumns[1],
                (int) Math.round(Double.parseDouble(resultColumns[2]))
        );


        try (GenericRawResults<HallOfFameEntry> results = placementDao.queryRaw(sql, mapper)) {
            return results.getResults();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public UserScoreBreakdown getUserScoreBreakdown(String twitchUserId) throws SQLException {
        List<Placement> userPlacements = placementDao.queryBuilder().where().eq("twitchUser_id", twitchUserId).query();

        List<Integer> positions = userPlacements.stream().map(Placement::getPosition).toList();

        int totalScore = positions.stream().mapToInt(pos -> 14 - pos).sum();

        return new UserScoreBreakdown(twitchUserId, totalScore, positions.size(), positions);
    }

    public record ParsedPlacement(int rank, String username) {
    }

    public record HallOfFameEntry(String userId, String username, int totalScore) {
    }

    public record UserScoreBreakdown(String userId, int totalScore, int appearances, List<Integer> positions) {
    }
}
