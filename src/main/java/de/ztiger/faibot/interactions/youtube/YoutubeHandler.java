package de.ztiger.faibot.interactions.youtube;

import de.ztiger.faibot.config.BotChannel;
import de.ztiger.faibot.config.BotRole;
import de.ztiger.faibot.localization.keys.Youtube;
import de.ztiger.faibot.services.ExternalReferenceService;
import de.ztiger.faibot.services.LocalizationService;
import de.ztiger.faibot.utils.ChannelProvider;
import de.ztiger.faibot.utils.RoleProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.IMentionable;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
public class YoutubeHandler {

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final String PLAYLIST_ID = "UU2YqG8Bc1RAncad0AFKuhtA";

    private final ChannelProvider channelProvider;
    private final ExternalReferenceService externalReferenceService;
    private final RoleProvider roleProvider;
    private final String youtubeApiKey;
    private final LocalizationService i18n;

    public Runnable checkVideo() {
        return () -> {
            try {
                String playlistUrl = String.format(
                        "https://youtube.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=1&playlistId=%s&key=%s",
                        PLAYLIST_ID, youtubeApiKey);

                JSONObject json = fetchJson(playlistUrl);
                if (json == null) return;

                String videoId = json.getJSONArray("items")
                        .getJSONObject(0)
                        .getJSONObject("snippet")
                        .getJSONObject("resourceId")
                        .getString("videoId");

                if (videoId.equals(externalReferenceService.getLastVideoId())) return;

                String videoUrl = String.format("https://youtube.googleapis.com/youtube/v3/videos?part=contentDetails&id=%s&key=%s",
                                                videoId, youtubeApiKey);

                JSONObject videoInfo = fetchJson(videoUrl);
                if (videoInfo == null) return;

                String duration = videoInfo.getJSONArray("items").getJSONObject(0).getJSONObject("contentDetails").getString("duration");

                if (Duration.parse(duration).getSeconds() < 60) return;

                externalReferenceService.setLastVideoId(videoId);
                sendVideoEmbed(videoId);

            } catch (Exception e) {
                log.error("Error while checking for new video: {}", e.getMessage(), e);
            }
        };
    }

    private JSONObject fetchJson(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            return new JSONObject(response.body());
        } catch (Exception e) {
            log.error("Failed to fetch JSON from {}: {}", url, e.getMessage());
            return null;
        }
    }

    private void sendVideoEmbed(String videoId) {
        String youtubeRoleMention = roleProvider.getRole(BotRole.YOUTUBE).map(IMentionable::getAsMention).orElse("@youtube");

        channelProvider.sendMessage(BotChannel.YOUTUBE, i18n.format(Youtube.NOTIFICATION, "youtuberole", youtubeRoleMention, "link",
                                                                    "https://youtu.be/" + videoId));
        log.info("New video posted: {}", videoId);
    }
}