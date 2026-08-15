package de.ztiger.faibot.interactions.twitch;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.events.ChannelChangeGameEvent;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import com.github.twitch4j.helix.domain.Stream;
import de.ztiger.faibot.config.BotChannel;
import de.ztiger.faibot.config.BotRole;
import de.ztiger.faibot.localization.keys.Twitch;
import de.ztiger.faibot.services.LocalizationService;
import de.ztiger.faibot.services.TwitchApiService;
import de.ztiger.faibot.utils.ChannelProvider;
import de.ztiger.faibot.utils.RoleProvider;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TwitchStreamHandler {

    private final String channelName;
    private final TwitchApiService twitchApiService;
    private final ChannelProvider channelProvider;
    private final RoleProvider roleProvider;
    private final TwitchComponents twitchComponents;
    private final LocalizationService i18n;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private ScheduledFuture<?> updateTask;
    private long messageID;
    private String profileImageUrl;
    private String currentUserId;
    private String offlineImageUrl;

    public TwitchStreamHandler(TwitchApiService twitchApiService, String channelName, ChannelProvider channelProvider,
                               RoleProvider roleProvider, TwitchComponents twitchComponents, LocalizationService i18n) {
        this.twitchApiService = twitchApiService;
        this.channelName = channelName;
        this.channelProvider = channelProvider;
        this.roleProvider = roleProvider;
        this.twitchComponents = twitchComponents;
        this.i18n = i18n;

        fetchChannelMetaData();
        registerEvents();

        log.info("TwitchHandler initialized for channel: {}", channelName);
    }

    private void fetchChannelMetaData() {
        twitchApiService.getUserByUsername(channelName).ifPresent(user -> {
            this.currentUserId = user.getId();
            this.profileImageUrl = user.getProfileImageUrl();
            this.offlineImageUrl = user.getOfflineImageUrl();
        });
    }

    private void registerEvents() {
        TwitchClient client = twitchApiService.getClient();
        client.getClientHelper().enableStreamEventListener(channelName);
        client.getEventManager().onEvent(ChannelGoLiveEvent.class, event -> streamStart());
        client.getEventManager().onEvent(ChannelChangeGameEvent.class, event -> updateEmbed());
        client.getEventManager().onEvent(ChannelGoOfflineEvent.class, event -> streamEnd());
    }

    private MessageEmbed createEmbed() {
        try {
            Optional<Stream> streamOpt = twitchApiService.getLiveStream(channelName);
            Stream stream = streamOpt.orElse(null);

            String previewURL = "https://static-cdn.jtvnw.net/previews-ttv/live_user_" + channelName + "-1280x720.jpg" + "?t=" + System.currentTimeMillis();
            String title = stream != null ? stream.getTitle() : "Stream Title";
            String game = stream != null ? stream.getGameName() : "Game Name";
            int viewers = stream != null ? stream.getViewerCount() : 0;
            String duration = i18n.formatDuration(stream != null ? stream.getUptime() : Duration.ZERO);

            return twitchComponents.getNotificationEmbed(channelName, previewURL, "https://twitch.tv/" + channelName, profileImageUrl,
                                                         title, game, viewers, duration);
        } catch (Exception e) {
            log.error("Error creating live stream embed", e);
            return null;
        }
    }

    public synchronized void updateEmbed() {
        if (messageID == 0) {
            log.warn("TwitchHandler: No message ID set for updating embed. Skipping update.");
            return;
        }

        MessageEmbed embed = createEmbed();
        if (embed != null) {
            channelProvider.editEmbed(BotChannel.TWITCH, messageID, embed);
        }
    }

    public synchronized void streamStart() {
        log.info("TwitchHandler: Stream live detected for {}", channelName);

        stopPeriodicUpdates();

        MessageEmbed initialEmbed = createEmbed();
        if (initialEmbed != null) {
            String twitchRoleMention = roleProvider.getRole(BotRole.TWITCH).map(IMentionable::getAsMention).orElse("@twitch");

            messageID = channelProvider.sendEmbedAndGetId(BotChannel.TWITCH,
                                                          i18n.format(Twitch.NOTIFICATION, "twitchrole", twitchRoleMention), initialEmbed);
        }

        updateTask = scheduler.scheduleAtFixedRate(this::updateEmbed, 5, 15, TimeUnit.MINUTES);
    }

    public synchronized void streamEnd() {
        log.info("TwitchHandler: Stream offline detected for {}", channelName);
        stopPeriodicUpdates();

        Duration vodDuration = currentUserId != null ? twitchApiService.getLatestVodDuration(currentUserId) : Duration.ZERO;

        MessageEmbed embed = twitchComponents.getEndNotificationEmbed(channelName, offlineImageUrl, "https://twitch.tv/" + channelName,
                                                                      profileImageUrl, i18n.formatDuration(vodDuration));

        channelProvider.editMessageWithEmbed(BotChannel.TWITCH, messageID, " ", embed);
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