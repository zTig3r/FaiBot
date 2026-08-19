package de.ztiger.faibot.services;

import com.j256.ormlite.dao.Dao;
import de.ztiger.faibot.data.TwitchUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
public class TwitchUserService {

    private final Dao<TwitchUser, String> userDao;

    private final Map<String, TwitchUser> idToUserCache = new ConcurrentHashMap<>();
    private final Map<String, String> usernameToIdCache = new ConcurrentHashMap<>();

    public void initCache() {
        try {
            List<TwitchUser> users = userDao.queryForAll();
            users.forEach(this::cacheUserInternal);
            log.info("Initialized Twitch user cache with {} entries.", idToUserCache.size());
        } catch (Exception e) {
            log.error("Failed to initialize Twitch user cache", e);
        }
    }

    public TwitchUser recordUser(String twitchId, String currentUsername) throws Exception {
        TwitchUser cachedUser = idToUserCache.get(twitchId);

        if (cachedUser != null && cachedUser.getUsername().equals(currentUsername)) {
            return cachedUser;
        }

        if (cachedUser != null && !cachedUser.getUsername().equalsIgnoreCase(currentUsername)) {
            usernameToIdCache.remove(cachedUser.getUsername().toLowerCase());
        }

        TwitchUser updatedUser = new TwitchUser(twitchId, currentUsername);

        userDao.createOrUpdate(updatedUser);

        cacheUserInternal(updatedUser);
        return updatedUser;
    }

    public String getUserIdByName(String username) {
        if (username == null) return null;
        return usernameToIdCache.get(username.toLowerCase());
    }

    public List<Command.Choice> searchUsers(String input) {
        if (input == null || input.isBlank()) return Collections.emptyList();

        String query = input.toLowerCase().trim();
        if (query.length() < 2) return Collections.emptyList();

        return idToUserCache.values().stream().filter(u -> u.getUsername().toLowerCase().contains(query))
                .limit(25).map(u -> new Command.Choice(u.getUsername(), u.getUsername())).toList();
    }

    private void cacheUserInternal(TwitchUser user) {
        idToUserCache.put(user.getId(), user);
        usernameToIdCache.put(user.getUsername().toLowerCase(), user.getId());
    }
}