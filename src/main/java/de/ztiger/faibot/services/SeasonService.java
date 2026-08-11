package de.ztiger.faibot.services;

import com.j256.ormlite.dao.Dao;
import de.ztiger.faibot.data.Season;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;
import java.time.YearMonth;

@RequiredArgsConstructor
public class SeasonService {

    private final Dao<Season, String> seasonDao;

    public boolean seasonExists(YearMonth yearMonth) throws SQLException {
        return !seasonDao.queryForEq("year_month", yearMonth.toString()).isEmpty();
    }

    public Season getOrCreateSeason(YearMonth yearMonth) throws SQLException {
        Season season = seasonDao.queryBuilder().where().eq("year_month", yearMonth.toString()).queryForFirst();

        if (season == null) {
            season = new Season(0, yearMonth.toString());
            seasonDao.create(season);
        }

        return season;
    }
}
