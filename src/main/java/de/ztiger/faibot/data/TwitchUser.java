package de.ztiger.faibot.data;

import com.j256.ormlite.field.DatabaseField;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class TwitchUser {

    @DatabaseField(id = true)
    private String id;

    @DatabaseField
    @Setter
    private String username;
}
