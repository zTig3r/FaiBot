package de.ztiger.faibot.interactions.youtube;

import de.ztiger.faibot.config.BotChannel;
import de.ztiger.faibot.utils.ChannelProvider;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.Duration;

// TODO: Put strings into configs
@RequiredArgsConstructor
public class YoutubeHandler {

    private static final Logger logger = LoggerFactory.getLogger(YoutubeHandler.class);

    private final ChannelProvider channelProvider;
    private final Dotenv env;

    public void checkVideo() {
        JSONObject json = readFromUrl("https://youtube.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=1&playlistId=UU2YqG8Bc1RAncad0AFKuhtA&key=" + env.get("YOUTUBE_KEY") + "&maxResults=1&order=date&type=video");
        if(json == null) return;

        try {
            String videoId = json.getJSONArray("items").getJSONObject(0).getJSONObject("snippet").getJSONObject("resourceId").getString("videoId");

            String lastVideo = null;
            if (lastVideo == null || lastVideo.equals(videoId)) return;
            //setter.setLastVideo(videoId);

            JSONObject videoInfo = readFromUrl("https://youtube.googleapis.com/youtube/v3/videos?part=contentDetails&maxResults=1&id=" + videoId + "&key=" + env.get("YOUTUBE_KEY"));
            if (videoInfo == null) return;

            String duration = videoInfo.getJSONArray("items").getJSONObject(0).getJSONObject("contentDetails").getString("duration");
            if(Duration.parse(duration).getSeconds() < 60) return;

            sendVideoEmbed(videoId);
        } catch (JSONException e) {
            logger.error(e.getMessage());
        }
    }

    private JSONObject readFromUrl(String url) {
        try {
            URL u = new URL(url);
            HttpsURLConnection connection = (HttpsURLConnection) u.openConnection();

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();

            return new JSONObject(sb.toString());
        } catch (IOException | JSONException e) {
            logger.error(e.getMessage());
        }

        return null;
    }

    private void sendVideoEmbed(String videoId) {
        channelProvider.sendMessage(BotChannel.YOUTUBE, "@everyone Neues Video von **Izi Fit:** \n\rhttps://youtu.be/" + videoId);
        logger.info("New video posted: {}", videoId);
    }

    public void triggerVideoCheck(SlashCommandInteractionEvent event) {
        checkVideo();
        event.reply("Videostatus wurde erfolgreich überprüft!").setEphemeral(true).queue();
    }
}
