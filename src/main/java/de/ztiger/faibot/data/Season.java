package de.ztiger.faibot.data;

import com.j256.ormlite.field.DatabaseField;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.YearMonth;

@AllArgsConstructor
@NoArgsConstructor
public class Season {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = "year_month")
    private String yearMonth;

    public YearMonth toYearMonth() {
        return YearMonth.parse(yearMonth);
    }
}
