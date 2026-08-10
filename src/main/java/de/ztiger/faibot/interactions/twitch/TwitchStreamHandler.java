package de.ztiger.faibot.interactions.twitch;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.events.ChannelChangeGameEvent;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import com.github.twitch4j.helix.domain.Stream;
import de.ztiger.faibot.config.BotChannel;
import de.ztiger.faibot.services.TwitchApiService;
import de.ztiger.faibot.utils.ChannelProvider;
import de.ztiger.faibot.services.LocalizationService;
import net.dv8tion.jda.api.components.container.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TwitchStreamHandler {

    private static final Logger logger = LoggerFactory.getLogger(TwitchStreamHandler.class);

    private final String channelName;
    private final TwitchApiService twitchApiService;
    private final ChannelProvider channelProvider;
    private final LocalizationService i18n;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private ScheduledFuture<?> updateTask;
    private String messageID;
    private String profileImageUrl;
    private String currentUserId;

    public TwitchStreamHandler(TwitchApiService twitchApiService, String channelName, ChannelProvider channelProvider, LocalizationService i18n) {
        this.twitchApiService = twitchApiService;
        this.channelName = channelName;
        this.channelProvider = channelProvider;
        this.i18n = i18n;

        fetchChannelMetaData();
        registerEvents();

        logger.info("TwitchHandler initialized for channel: {}", channelName);
    }

    private void fetchChannelMetaData() {
        twitchApiService.getUserByUsername(channelName).ifPresent(user -> {
            this.currentUserId = user.getId();
            this.profileImageUrl = user.getProfileImageUrl();
        });
    }

    private void registerEvents() {
        TwitchClient client = twitchApiService.getClient();
        client.getClientHelper().enableStreamEventListener(channelName);
        client.getEventManager().onEvent(ChannelGoLiveEvent.class, event -> streamStart());
        client.getEventManager().onEvent(ChannelChangeGameEvent.class, event -> updateEmbed());
        client.getEventManager().onEvent(ChannelGoOfflineEvent.class, event -> streamEnd());
    }

    private Container createEmbed() {
        try {
            Optional<Stream> streamOpt = twitchApiService.getLiveStream(channelName);
            Stream stream = streamOpt.orElse(null);

            String previewURL = "https://static-cdn.jtvnw.net/previews-ttv/live_user_" + channelName + "-1280x720.jpg";
            String title = stream != null ? stream.getTitle() : "Stream Title";
            String game = stream != null ? stream.getGameName() : "Game Name";
            int viewers = stream != null ? stream.getViewerCount() : 0;
            String duration = i18n.formatDuration(stream != null ? stream.getUptime() : Duration.ZERO);

            return TwitchComponents.notification(i18n, previewURL, channelName, profileImageUrl, title, game, viewers, duration);

        } catch (Exception e) {
            logger.error("Error creating live stream embed", e);
            return null;
        }
    }

    public synchronized void updateEmbed() {
        if (messageID == null) return;

        Container embed = createEmbed();
        if (embed != null) {
            channelProvider.editComponents(BotChannel.TWITCH, messageID, embed);
        }
    }

    public synchronized void streamStart() {
        logger.info("TwitchHandler: Stream live detected for {}", channelName);

        stopPeriodicUpdates();

        Container initialEmbed = createEmbed();
        if (initialEmbed != null) {
            channelProvider.sendComponent(BotChannel.TWITCH, initialEmbed);
        }

        updateTask = scheduler.scheduleAtFixedRate(this::updateEmbed, 5, 15, TimeUnit.MINUTES);
    }

    public synchronized void streamEnd() {
        logger.info("TwitchHandler: Stream offline detected for {}", channelName);
        stopPeriodicUpdates();

        Duration vodDuration = currentUserId != null
                ? twitchApiService.getLatestVodDuration(currentUserId)
                : Duration.ZERO;

        Container embed = TwitchComponents.endNotification(i18n, channelName, profileImageUrl, i18n.formatDuration(vodDuration));

        channelProvider.sendComponent(BotChannel.TWITCH, embed);
    }

    private void stopPeriodicUpdates() {
        if (updateTask != null && !updateTask.isCancelled()) {
            updateTask.cancel(true);
            updateTask = null;
        }
    }

    public void shutdown() {
        stopPeriodicUpdates();
        scheduler.shutdown();
    }
}