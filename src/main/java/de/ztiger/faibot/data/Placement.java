package de.ztiger.faibot.data;

import com.j256.ormlite.field.DatabaseField;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class Placement {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    @Getter
    private Season season;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private TwitchUser twitchUser;

    @DatabaseField
    @Getter
    private int position;
}
