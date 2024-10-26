package de.ztiger.faibot.stream;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import org.simpleyaml.configuration.file.FileConfiguration;

import java.util.*;

import static de.ztiger.faibot.FaiBot.*;
import static de.ztiger.faibot.stream.SEDataManager.*;

public class SEHandler {

    private static Timer timer;
    private static Boolean isLive = false;

    private static final Set<String> recentChatters = new HashSet<>();

    private static final FileConfiguration config = cfgm.getConfig("config");
    private static final String CHANNEL = config.getString("channel");
    private static final List<String> BLACKLIST = config.getStringList("blacklist");

    public SEHandler() {
        if (config.getString("oauth") == null || config.getString("jwt") == null) {
            logger.error("Twitch OAUTH or Streamelements JWT Token missing");
            return;
        }

        OAuth2Credential credential = new OAuth2Credential("twitch", config.getString("oauth"));

        TwitchClient client = TwitchClientBuilder.builder()
                .withEnableHelix(true)
                .withEnableChat(true)
                .withChatAccount(credential)
                .withDefaultAuthToken(credential)
                .build();

        client.getClientHelper().enableStreamEventListener(CHANNEL);

        client.getEventManager().onEvent(ChannelMessageEvent.class, event -> {
            String name = event.getUser().getName();
            if (!isLive || BLACKLIST.contains(name)) return;
            recentChatters.add(name);
            logger.info("test");
        });

        logger.info("SEHandler initialized");
    }

    public static void startSE() {
        isLive = true;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                addPoints(recentChatters, config.getInt("amount"));
                recentChatters.clear();
                fixLowPoints();
            }
        }, 600000, 600000);
    }

    public static void stopSE() {
        addPoints(recentChatters, config.getInt("amount"));
        backupData();
        isLive = false;
        timer.cancel();
        timer = null;
    }
}