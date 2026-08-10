package de.ztiger.faibot.services;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.helix.domain.Stream;
import com.github.twitch4j.helix.domain.StreamList;
import com.github.twitch4j.helix.domain.User;
import com.github.twitch4j.helix.domain.Video;
import com.github.twitch4j.helix.domain.VideoList;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TwitchApiService {

    private static final Logger logger = LoggerFactory.getLogger(TwitchApiService.class);

    @Getter
    private final TwitchClient client;

    public TwitchApiService(String clientId, String clientSecret) {
        this.client = TwitchClientBuilder.builder()
                .withClientId(clientId)
                .withClientSecret(clientSecret)
                .withEnableHelix(true)
                .build();
    }

    public Optional<User> getUserByUsername(String username) {
        List<User> users = getUsersByUsernames(List.of(username));
        return users.isEmpty() ? Optional.empty() : Optional.of(users.getFirst());
    }

    public List<User> getUsersByUsernames(List<String> usernames) {
        if (usernames == null || usernames.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return client.getHelix().getUsers(null, null, usernames).execute().getUsers();
        } catch (Exception e) {
            logger.error("Failed to fetch Twitch users for list: {}", usernames, e);
            return Collections.emptyList();
        }
    }

    public Optional<Stream> getLiveStream(String channelName) {
        try {
            StreamList streamList = client.getHelix()
                    .getStreams(null, null, null, null, null, null, null, List.of(channelName))
                    .execute();

            return streamList.getStreams().stream().findFirst();
        } catch (Exception e) {
            logger.error("Failed to fetch stream status for {}", channelName, e);
            return Optional.empty();
        }
    }

    public Duration getLatestVodDuration(String userId) {
        try {
            VideoList list = client.getHelix()
                    .getVideos(null, null, userId, null, null, null, null, Video.Type.ARCHIVE, 1, null, null)
                    .execute();

            if (!list.getVideos().isEmpty()) {
                String rawDuration = list.getVideos().getFirst().getDuration();
                return parseTwitchIsoDuration(rawDuration);
            }
        } catch (Exception e) {
            logger.error("Failed to fetch VOD duration for userId {}", userId, e);
        }
        return Duration.ZERO;
    }

    private Duration parseTwitchIsoDuration(String durationStr) {
        try {
            return Duration.parse("PT" + durationStr.toUpperCase());
        } catch (Exception e) {
            logger.warn("Failed to parse duration string: {}", durationStr);
            return Duration.ZERO;
        }
    }

    public void shutdown() {
        client.close();
    }
}