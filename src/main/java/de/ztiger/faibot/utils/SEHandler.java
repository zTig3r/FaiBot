package de.ztiger.faibot.utils;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.simpleyaml.configuration.file.FileConfiguration;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import static de.ztiger.faibot.FaiBot.*;

public class SEHandler {

    private static Timer timer;
    private static Boolean isLive = false;
    private static final List<String> recentChatters = new ArrayList<>();
    private static final FileConfiguration config = cfgm.getConfig("config");

    private static final String BODYFORMAT = "{\"username\": \"%s\", \"current\":" + config.getInt("amount") + "}";

    public SEHandler() {
        if(config.getString("oauth") == null || config.getString("jwt") == null) {
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

        client.getClientHelper().enableStreamEventListener(config.getString("channel"));

        client.getEventManager().onEvent(ChannelMessageEvent.class, event -> {
            if(isLive) return;
            recentChatters.add(event.getUser().getName());
        });

        startSE();
        logger.info("SEHandler initialized");
    }

    public static void startSE() {
        isLive = true;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updatePoints();
            }
        }, 15000, 15000);

        // TODO: Add the real values   600000, 600000);
    }

    public static void stopSE() {
        isLive = false;
        timer.cancel();
        timer = null;
    }

    private static void updatePoints() {
        if (recentChatters.isEmpty()) return;

        String requestBody = "{\"users\": [" +
                recentChatters.stream().map(user -> String.format(BODYFORMAT, user)).collect(Collectors.joining(",")) +
                "], \"mode\": \"add\"}";

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            try (CloseableHttpResponse response = httpClient.execute(getHttpPut(requestBody))) {
                if (EntityUtils.toString(response.getEntity()).contains("Created")) logger.info("Successfully added points: {}", requestBody);
                else logger.error("Failed to add points: {}", requestBody);
            }
        } catch (IOException e) {
            logger.error("Error updating points", e);
        } finally {
            recentChatters.clear();
        }
    }

    private static @NotNull HttpPut getHttpPut(String requestBody) throws UnsupportedEncodingException {
        HttpPut request = new HttpPut("https://api.streamelements.com/kappa/v2/points/" + config.getString("channelID"));
        request.addHeader("Accept", "application/json");
        request.addHeader("Authorization", "Bearer " + config.get("jwt"));
        request.addHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(requestBody));
        return request;
    }
}