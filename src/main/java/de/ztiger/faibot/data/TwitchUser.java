package de.ztiger.faibot.data;

import com.j256.ormlite.field.DatabaseField;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class TwitchUser {

    @DatabaseField(id = true)
    @Getter
    private String id;

    @DatabaseField
    @Getter
    @Setter
    private String username;
}
