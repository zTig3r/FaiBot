package de.ztiger.faibot.data;

import com.j256.ormlite.field.DatabaseField;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class Season {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = "year_month")
    private String yearMonth;
}
