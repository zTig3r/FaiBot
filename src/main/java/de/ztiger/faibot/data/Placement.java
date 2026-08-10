package de.ztiger.faibot.data;

import com.j256.ormlite.field.DatabaseField;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class Placement {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Season season;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private TwitchUser twitchUser;

    @DatabaseField
    private int position;
}
