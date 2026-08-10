package de.ztiger.faibot.services;

import com.j256.ormlite.dao.Dao;
import de.ztiger.faibot.data.TwitchUser;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TwitchUserService {

    private final Dao<TwitchUser, String> userDao;

    public TwitchUser recordUser(String twitchId, String currentUsername) throws Exception {
        TwitchUser existingUser = userDao.queryForId(twitchId);

        if (existingUser != null) {
            existingUser.setUsername(currentUsername);
            userDao.update(existingUser);
            return existingUser;
        }

        TwitchUser newUser = new TwitchUser(twitchId, currentUsername);
        userDao.create(newUser);
        return newUser;
    }
}
